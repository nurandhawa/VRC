package ca.sfu.teambeta.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ca.sfu.teambeta.accounts.UserRole;
import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Penalty;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.Time;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.AccountRegistrationException;
import ca.sfu.teambeta.core.exceptions.IllegalDatabaseOperation;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;
import ca.sfu.teambeta.logic.GameSession;
import ca.sfu.teambeta.logic.VrcLadderReorderer;
import ca.sfu.teambeta.logic.VrcScorecardGenerator;
import ca.sfu.teambeta.logic.VrcTimeSelection;
import ca.sfu.teambeta.notifications.Announcement;

/**
 * Utility class that reads and writes data to the database
 */
public class DBManager {
    private static final String LOCAL_TESTING_CFG_XML = "hibernate.testing.cfg.xml";
    private static final String HIBERNATE_CLASSES_XML = "hibernate.classes.xml";
    private static final String PRODUCTION_CFG_XML = "hibernate.production.cfg.xml";
    private static final String DOCKER_CFG_XML = "hibernate.docker.cfg.xml";
    private static final String H2_CFG_XML = "hibernate.h2.cfg.xml";
    private static String TESTING_ENV_VAR = "TESTING";
    private SessionFactory sessionFactory;
    private TransactionManager transactionManager;
    private Session currentSession;

    public DBManager(SessionFactory factory) {
        this.sessionFactory = factory;
        this.transactionManager = new TransactionManager(this::refreshSession);
    }

    // Use me if the database is down
    public static SessionFactory getHSQLSession() {
        Configuration config = new Configuration();
        config.configure(H2_CFG_XML);
        config.configure(HIBERNATE_CLASSES_XML);
        try {
            return config.buildSessionFactory();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static SessionFactory getMySQLSession(boolean create) {
        Configuration config = new Configuration();
        config.configure(LOCAL_TESTING_CFG_XML);
        config.configure(HIBERNATE_CLASSES_XML);
        if (create) {
            config.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        } else {
            config.setProperty("hibernate.hbm2ddl.auto", "update");
        }
        try {
            return config.buildSessionFactory();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static SessionFactory getProductionSession() {
        Configuration config = new Configuration();
        config.configure(PRODUCTION_CFG_XML);
        config.configure(HIBERNATE_CLASSES_XML);
        try {
            return config.buildSessionFactory();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static SessionFactory getTestingSession(boolean create) {
        boolean isTesting = System.getenv(TESTING_ENV_VAR) != null;
        Configuration config = new Configuration();
        if (isTesting) {
            config.configure(DOCKER_CFG_XML);
        } else {
            config.configure(LOCAL_TESTING_CFG_XML);
        }
        config.configure(HIBERNATE_CLASSES_XML);
        if (create) {
            config.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        } else {
            config.setProperty("hibernate.hbm2ddl.auto", "update");
        }
        try {
            return config.buildSessionFactory();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    private Session refreshSession() {
        return this.sessionFactory.openSession();
    }

    public synchronized void persistEntity(Persistable entity) {
        transactionManager.executeTransaction(((session, transaction) -> {
            session.saveOrUpdate(entity);
            return true;
        }));
    }

    private Persistable getEntityFromID(Class persistable, int id) throws HibernateException {
        return transactionManager.executeTransaction((session, transaction) -> (Persistable) session.get(persistable, id));
    }

    public synchronized Player getPlayerFromID(int id) {
        Player player = null;
        try {
            player = (Player) getEntityFromID(Player.class, id);
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return player;
    }

    public synchronized Pair getPairFromID(int id) {
        Pair pair = null;
        try {
            pair = (Pair) getEntityFromID(Pair.class, id);
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return pair;
    }

    public synchronized Ladder getLatestLadder() {
        return transactionManager.executeTransaction((session, transaction) -> {
            DetachedCriteria maxId = DetachedCriteria.forClass(Ladder.class)
                    .setProjection(Projections.max("id"));
            return (Ladder) session.createCriteria(Ladder.class)
                    .add(Property.forName("id").eq(maxId))
                    .uniqueResult();
        });
    }

    private synchronized GameSession getGameSessionByVersion(GameSessionVersion version) {
        final List gameSessions = new ArrayList();
        GameSession gameSession;
        try {
            transactionManager.executeTransaction((session, transaction) -> {
                DetachedCriteria idCriteria = DetachedCriteria.forClass(GameSession.class)
                        .setProjection(Projections.id());
                List results = (session.createCriteria(GameSession.class)
                        .add(Property.forName("id").in(idCriteria))
                        .addOrder(Order.desc("timestamp"))
                        .list());
                return gameSessions.addAll(results);
            });

            int gameSessionIndex = -1;

            if (version == null || version == GameSessionVersion.CURRENT) {
                gameSessionIndex = 0;
            } else if (version == GameSessionVersion.PREVIOUS) {
                gameSessionIndex = 1;
            }

            gameSession = (GameSession) gameSessions.get(gameSessionIndex);

        } catch (IndexOutOfBoundsException e) {
            return null;
        }
        return gameSession;
    }

    public synchronized GameSession getGameSessionLatest() {
        return getGameSessionByVersion(GameSessionVersion.CURRENT);
    }

    public synchronized GameSession getGameSessionPrevious() {
        return getGameSessionByVersion(GameSessionVersion.PREVIOUS);
    }

    public void addPenaltyToPair(GameSession gameSession, int pairId, Penalty penalty) {
        Pair pair = getPairFromID(pairId);
        gameSession.setPenaltyToPair(pair, penalty);
    }

    public synchronized void addPairToLatestLadder(Pair pair) {
        Ladder ladder = transactionManager.executeTransaction((session, transaction) -> {
            DetachedCriteria maxId = DetachedCriteria.forClass(Ladder.class)
                    .setProjection(Projections.max("id"));
            return (Ladder) session.createCriteria(Ladder.class)
                    .add(Property.forName("id").eq(maxId))
                    .uniqueResult();

        });
        ladder.insertAtEnd(pair);
    }

    public synchronized void addPair(GameSession gameSession, Pair pair, int position) {
        gameSession.addNewPairAtIndex(pair, position);
        persistEntity(gameSession);
    }

    public synchronized void addPair(GameSession gameSession, Pair pair) {
        gameSession.addNewPairAtEnd(pair);
        persistEntity(gameSession);
    }

    public synchronized boolean removePair(int pairId) {
        GameSession gameSession = getGameSessionLatest();
        Pair pair = transactionManager.executeTransaction((session, transaction) -> session.get(Pair.class, pairId));

        boolean removed = gameSession.removePairFromLadder(pair);

        persistEntity(gameSession);

        return removed;
    }

    public synchronized boolean hasPairID(int id) {
        List<Pair> pairs = getLatestLadder().getPairs();
        for (Pair pair : pairs) {
            if (pair.getID() == id) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean hasPlayerID(int id) {
        List<Pair> pairs = getLatestLadder().getPairs();
        for (Pair pair : pairs) {
            List<Player> players = pair.getPlayers();
            for (Player player : players) {
                if (player.getID() == id) {
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized void movePair(GameSession gameSession, int pairId, int newPosition) {
        Pair pair = getPairFromID(pairId);
        boolean isPairPlaying = isActivePair(gameSession, pairId);
        removePair(pairId);
        gameSession.addNewPairAtIndex(pair, newPosition);
        if (isPairPlaying) {
            setPairActive(gameSession, pair.getID());
        }
        persistEntity(gameSession);
    }

    public synchronized Player getAlreadyActivePlayer(
            GameSession gameSession, int id) throws Exception {
        Pair pair = getPairFromID(id);
        Player player;
        try {
            player = gameSession.getAlreadyActivePlayer(pair);
        } catch (Exception e) {
            throw e;
        }
        return player;
    }

    public synchronized boolean setPairActive(GameSession gameSession, int pairId) {
        Pair pair = getPairFromID(pairId);
        boolean activated = gameSession.setPairActive(pair);
        gameSession.setTimeSlot(pair, Time.NO_SLOT);
        gameSession.createGroups(new VrcScorecardGenerator(), new VrcTimeSelection());
        persistEntity(gameSession);
        return activated;
    }

    public synchronized void setPairInactive(GameSession gameSession, int pairId) {
        Pair pair = getPairFromID(pairId);
        gameSession.setPairInactive(pair);
        gameSession.createGroups(new VrcScorecardGenerator(), new VrcTimeSelection());
        persistEntity(gameSession);
    }

    public synchronized boolean isActivePair(GameSession gameSession, int pairId) {
        Pair pair = getPairFromID(pairId);

        boolean status = gameSession.isActivePair(pair);
        persistEntity(gameSession);
        return status;
    }

    public synchronized boolean isPlayerInPair(Player player, int pairId) {
        Pair pair = getPairFromID(pairId);
        List<Player> players = pair.getPlayers();
        if (players.contains(player)) {
            return true;
        }
        return false;
    }

    public synchronized int getLadderSize(GameSession gameSession) {
        List<Pair> ladder = gameSession.getAllPairs();
        return ladder.size();
    }

    private GameSession getGameSession(int gameSessionId) {
        return transactionManager.executeTransaction((session, transaction) -> {
            return (GameSession) session.createCriteria(GameSession.class)
                    .add(Property.forName("id").eq(gameSessionId))
                    .uniqueResult();
        });
    }

    public synchronized List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        transactionManager.executeTransaction((session, transaction) -> {
            return players.addAll(session.createCriteria(Player.class).list());
        });
        return players;
    }

    public synchronized User getUser(String email) {
        return transactionManager.executeTransaction((session, transaction) -> {
            return (User) session.createCriteria(User.class)
                    .add(Restrictions.eq("email", email))
                    .uniqueResult();
        });
    }

    public synchronized List<User> getAllUsersOfRole(UserRole role) {
        List<User> anonymousUsers = new ArrayList<>();
        transactionManager.executeTransaction((session, transaction) -> {
            return anonymousUsers.addAll(session.createCriteria(User.class).add(Restrictions.eq("role", role)).list());
        });
        return anonymousUsers;
    }

    public synchronized List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        transactionManager.executeTransaction((session, transaction) -> {
            return users.addAll(session.createCriteria(User.class).list());

        });
        return users;
    }

    public synchronized void deleteUser(String userEmail) throws NoSuchUserException, IllegalDatabaseOperation {

        User user = getUser(userEmail);

        if (user == null) {
            throw new NoSuchUserException("No user exists for email: " + userEmail);
        }

        if (user.getUserRole() == UserRole.ADMINISTRATOR) {
            throw new IllegalDatabaseOperation("Cannot delete an administrator");
        }

        transactionManager.executeTransaction((session, transaction) -> {
            session.delete(user);
            return true;
        });
    }

    public synchronized void addNewUser(User user) throws AccountRegistrationException {
        String email = user.getEmail();
        boolean uniqueEmail = (getUser(email) == null);
        if (!uniqueEmail) {
            throw new AccountRegistrationException("The email '" + email + "' is already in use");
        }

        persistEntity(user);
    }

    public synchronized void updateExistingUser(User user) {
        persistEntity(user);
    }

    public synchronized void addNewPlayer(Player player) throws AccountRegistrationException {
        persistEntity(player);
    }

    // Rankings are in a format of pairID -> position in scorecard
    public synchronized void setMatchResults(int scorecardId, Map<Integer, Integer> rankings) {
        Scorecard sc = (Scorecard) getEntityFromID(Scorecard.class, scorecardId);
        for (Map.Entry<Integer, Integer> entry : rankings.entrySet()) {
            Pair pair = getPairFromID(entry.getKey());
            sc.setGameResults(pair, entry.getValue());
        }
        persistEntity(sc);
    }

    public synchronized void reorderLadder(GameSession gameSession) {
        gameSession.reorderLadder(new VrcLadderReorderer(), new VrcTimeSelection());
    }

    public synchronized GameSession createNewGameSession(GameSession sourceGameSession) {
        return new GameSession(sourceGameSession);
    }

    public synchronized void saveGameSession(GameSession gameSession) {
        persistEntity(gameSession);
    }

    public synchronized void setTimeSlot(int pairId, Time time) {
        GameSession gameSession = getGameSessionLatest();
        Pair pair = getPairFromID(pairId);
        gameSession.setTimeSlot(pair, time);
        persistEntity(gameSession);
    }

    public boolean writeToCsvFile(OutputStream outputStream, GameSession gameSession) {
        try {
            CSVReader.exportCsv(outputStream, gameSession.getAllPairs(), this);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public synchronized boolean importLadderFromCsv(InputStreamReader inputStreamReader) {
        Ladder ladder;
        try {
            ladder = CSVReader.importCsv(inputStreamReader, this);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        GameSession gameSession = getGameSessionLatest();
        gameSession.replaceLadder(ladder);
        persistEntity(gameSession);
        return true;
    }

    public enum GameSessionVersion {
        CURRENT,
        PREVIOUS
    }

    public synchronized List<Announcement> getAnnouncements() {
        List<Announcement> announcements = new ArrayList<>();
        transactionManager.executeTransaction(((session, transaction) -> {
            return announcements.addAll(session.createCriteria(Announcement.class).list());
        }));
        return announcements;
    }
}
