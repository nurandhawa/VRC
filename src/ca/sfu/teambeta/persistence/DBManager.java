package ca.sfu.teambeta.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import java.util.List;

import ca.sfu.teambeta.core.Game;
import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Penalty;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.AccountRegistrationException;
import ca.sfu.teambeta.logic.GameSession;
import ca.sfu.teambeta.logic.VrcLadderReorderer;
import ca.sfu.teambeta.logic.VrcScorecardGenerator;

/**
 * Utility class that reads and writes data to the database
 */
public class DBManager {
    private static String TESTING_ENV_VAR = "TESTING";
    private SessionFactory factory;
    private Session session;

    public DBManager(SessionFactory factory) {
        this.factory = factory;
        this.session = factory.openSession();
    }

    private static Configuration getDefaultConfiguration() {
        Configuration config = new Configuration();
        config.addAnnotatedClass(Player.class);
        config.addAnnotatedClass(Pair.class);
        config.addAnnotatedClass(Ladder.class);
        config.addAnnotatedClass(Scorecard.class);
        config.addAnnotatedClass(Game.class);
        config.addAnnotatedClass(GameSession.class);
        config.addAnnotatedClass(Penalty.class);
        config.addAnnotatedClass(User.class);
        return config;
    }

    public static SessionFactory getHSQLSession() {
        Configuration config = getDefaultConfiguration();
        config.setProperty("hibernate.hbm2ddl.auto", "update");
        config.setProperty("hibernate.connection.username", "");
        config.setProperty("hibernate.connection.password", "");
        config.setProperty("hibernate.connection.pool_size", "1");
        config.setProperty("hibernate.connection.url",
                "jdbc:hsqldb:file:/home/freeman/prj/resources/database/test");
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        config.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        return config.buildSessionFactory();
    }

    public static SessionFactory getMySQLSession(boolean create) {
        Configuration config = getDefaultConfiguration();
        if (create) {
            config.setProperty("hibernate.hbm2ddl.auto", "create");
        } else {
            config.setProperty("hibernate.hbm2ddl.auto", "update");
        }
        config.setProperty("hibernate.connection.username", "beta-test");
        config.setProperty("hibernate.connection.password", "b3ta");
        config.setProperty("hibernate.connection.pool_size", "1");
        config.setProperty("hibernate.connection.url",
                "jdbc:mysql://vrcproject.duckdns.org:3306/gshieh?serverTimezone=America/Vancouver");
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        try {
            return config.buildSessionFactory();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static SessionFactory getProductionSession() {
        Configuration config = getDefaultConfiguration();
        config.setProperty("hibernate.hbm2ddl.auto", "update");
        config.setProperty("hibernate.connection.username", "beta-test");
        config.setProperty("hibernate.connection.password", "b3ta");
        config.setProperty("hibernate.connection.pool_size", "1");
        config.setProperty("hibernate.connection.url",
                "jdbc:mysql://vrcproject.duckdns.org:"
                + "3306/production?serverTimezone=America/Vancouver");
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        try {
            return config.buildSessionFactory();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    private static SessionFactory getDockerSession(boolean create) {
        Configuration config = getDefaultConfiguration();
        if (create) {
            config.setProperty("hibernate.hbm2ddl.auto", "create");
        } else {
            config.setProperty("hibernate.hbm2ddl.auto", "update");
        }
        config.setProperty("hibernate.connection.username", "root");
        config.setProperty("hibernate.connection.password", "b3ta");
        config.setProperty("hibernate.connection.pool_size", "1");
        config.setProperty("hibernate.connection.url",
                "jdbc:mysql://mysql:3306/test?serverTimezone=America/Vancouver");
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        try {
            return config.buildSessionFactory();
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public static SessionFactory getTestingSession(boolean create) {
        boolean isTesting = System.getenv(TESTING_ENV_VAR) != null;
        if (isTesting) {
            return getDockerSession(create);
        } else {
            return getMySQLSession(create);
        }
    }

    public static void main(String[] args) {
        SessionFactory factory = getMySQLSession(false);
        DBManager dbMan = new DBManager(factory);
        /*
        Player p1 = new Player("Bobby", "Chan", "");
        Player p2 = new Player("Wing", "Man", "");
        dbMan.persistEntity(new Pair(p1, p2));

        Player p3 = new Player("Hello", "World!", "");
        dbMan.persistEntity(new Pair(new Player("Bobby", "Chan", ""), p3));

        Player test = dbMan.getPlayerFromID(5);

        System.out.println(test.getFirstName());
        */
        Ladder lad = dbMan.getLatestLadder();

        System.out.println(lad);
    }

    public synchronized int persistEntity(Persistable entity) {
        Transaction tx = null;
        int key = 0;
        try {
            tx = session.beginTransaction();
            key = (int) session.save(entity);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
        return key;
    }

    private Persistable getEntityFromID(Class persistable, int id) throws HibernateException {
        Transaction tx = null;
        Persistable entity = null;
        try {
            tx = session.beginTransaction();
            entity = (Persistable) session.get(persistable, id);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        return entity;
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
        Transaction tx = null;
        Ladder ladder = null;
        try {
            tx = session.beginTransaction();
            DetachedCriteria maxId = DetachedCriteria.forClass(Ladder.class)
                    .setProjection(Projections.max("id"));
            ladder = (Ladder) session.createCriteria(Ladder.class)
                    .add(Property.forName("id").eq(maxId))
                    .uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        return ladder;
    }

    public synchronized GameSession getGameSessionLatest() {
        Transaction tx = null;
        GameSession gameSession = null;
        try {
            tx = session.beginTransaction();
            DetachedCriteria maxId = DetachedCriteria.forClass(GameSession.class)
                    .setProjection(Projections.max("id"));
            gameSession = (GameSession) session.createCriteria(GameSession.class)
                    .add(Property.forName("id").eq(maxId))
                    .uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        return gameSession;
    }

    // TODO: Actually get previous game session
    public synchronized GameSession getGameSessionPrevious() {
        Transaction tx = null;
        GameSession gameSession = null;
        try {
            tx = session.beginTransaction();
            DetachedCriteria maxId = DetachedCriteria.forClass(GameSession.class)
                    .setProjection(Projections.max("id"));
            gameSession = (GameSession) session.createCriteria(GameSession.class)
                    .add(Property.forName("id").eq(maxId))
                    .uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        return gameSession;
    }

    public void addPenaltyToPair(GameSession gameSession, int pairId, Penalty penalty) {
        Pair pair = getPairFromID(pairId);
        gameSession.setPenaltyToPair(pair, penalty);
    }

    public synchronized void addPairToLatestLadder(Pair pair) {
        Transaction tx = null;
        Ladder ladder = null;
        try {
            tx = session.beginTransaction();
            DetachedCriteria maxId = DetachedCriteria.forClass(Ladder.class)
                    .setProjection(Projections.max("id"));
            ladder = (Ladder) session.createCriteria(Ladder.class)
                    .add(Property.forName("id").eq(maxId))
                    .uniqueResult();
            ladder.insertAtEnd(pair);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
    }

    public synchronized void inputMatchResults(GameSession gameSession, Scorecard s, String[][] results) {
        List<Pair> teams = s.getReorderedPairs();
        int rows = results.length;
        int cols = teams.size();

        Pair teamWon = null;
        Pair teamLost = null;
        int winCount = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (results[i][j].equals("W")) {
                    teamWon = teams.get(j);
                    winCount++;
                } else if (results[i][j].equals("L")) {
                    teamLost = teams.get(j);
                    winCount--;
                }
            }
            if (winCount == 0 && teamWon != null && teamLost != null) {
                s.setGameResults(teamWon,teamLost);
            }
            winCount = 0;
            teamLost = null;
            teamWon = null;
        }
        persistEntity(gameSession);
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
        Transaction tx = null;
        Pair pair = null;
        Ladder ladder = null;
        boolean removed = false;
        try {
            tx = session.beginTransaction();
            pair = session.get(Pair.class, pairId);
            DetachedCriteria maxId = DetachedCriteria.forClass(Ladder.class)
                    .setProjection(Projections.max("id"));
            ladder = (Ladder) session.createCriteria(Ladder.class)
                    .add(Property.forName("id").eq(maxId))
                    .uniqueResult();
            removed = ladder.removePair(pair);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        return removed;
    }

    public synchronized boolean hasPairID(int id) {
        return getPairFromID(id) != null;
    }

    public synchronized void movePair(GameSession gameSession, int pairId, int newPosition) {
        Pair pair = getPairFromID(pairId);

        removePair(pairId);
        gameSession.addNewPairAtIndex(pair, newPosition);
        persistEntity(gameSession);
    }

    public synchronized Player getAlreadyActivePlayer(GameSession gameSession, int id) throws Exception {
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
        gameSession.createGroups(new VrcScorecardGenerator());
        persistEntity(gameSession);
        return activated;
    }

    public synchronized void setPairInactive(GameSession gameSession, int pairId) {
        Pair pair = getPairFromID(pairId);
        gameSession.setPairInactive(pair);
        gameSession.createGroups(new VrcScorecardGenerator());
        persistEntity(gameSession);
    }

    public synchronized boolean isActivePair(GameSession gameSession, int pairId) {
        Pair pair = getPairFromID(pairId);

        boolean status = gameSession.isActivePair(pair);
        persistEntity(gameSession);
        return status;
    }

    public synchronized int getLadderSize(GameSession gameSession) {
        List<Pair> ladder = gameSession.getAllPairs();
        return ladder.size();
    }

    public synchronized String getJSONLadder(GameSession gameSession) {
        List<Pair> ladder = gameSession.getAllPairs();
        JSONSerializer serializer = new LadderJSONSerializer(ladder,
                gameSession.getActivePairSet());
        return serializer.toJson();
    }

    public synchronized String getJSONScorecards(GameSession gameSession) {
        List<Scorecard> scorecards = gameSession.getScorecards();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        String json = gson.toJson(scorecards);
        return json;
    }

    public synchronized void setGameResults(GameSession gameSession, int winningPairId, int losingPairId) {
        int sessionId = gameSession.getID();
        Scorecard scorecard = (Scorecard) session.createQuery(
                "from Scorecard sc \n"
                        + "join session_Scorecard s_sc on (s_sc.scorecards_id = sc.id) "
                        + "join Scorecard_Pair sc_pwin on (sc_pwin.Scorecard_id = sc.id) "
                        + "join Scorecard_Pair sc_plose on (sc_plose.Scorecard_id = sc.id) "
                        + "where sc_pwin.pairs_id = :winning_pair_id "
                        + "and sc_plose.pairs_id = :losing_pair_id "
                        + "and s_sc.session_id = :session_id")
                .setInteger("winning_pair_id", winningPairId)
                .setInteger("losing_pair_id", winningPairId)
                .setInteger("session_id", sessionId)
                .uniqueResult();
        Pair winningPair = session.load(Pair.class, winningPairId);
        Pair losingPair = session.load(Pair.class, losingPairId);
        scorecard.setGameResults(winningPair, losingPair);

        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(scorecard);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
    }

    private GameSession getGameSession(int gameSessionId) {
        Transaction tx = null;
        GameSession gameSession = null;
        try {
            tx = session.beginTransaction();
            gameSession = (GameSession) session.createCriteria(GameSession.class)
                    .add(Property.forName("id").eq(gameSessionId))
                    .uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        return gameSession;
    }

    public synchronized User getUser(String email) {
        Transaction tx = null;
        User user = null;
        try {
            tx = session.beginTransaction();
            user = (User) session.createCriteria(User.class)
                    .add(Restrictions.eq("email", email))
                    .uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        return user;
    }

    public synchronized void addNewUser(User user) throws AccountRegistrationException {
        String email = user.getEmail();
        boolean uniqueEmail = (getUser(email) == null);
        if (!uniqueEmail) {
            throw new AccountRegistrationException("The email '" + email + "' is already in use");
        }

        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(user);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
    }

    public synchronized void addNewPlayer(Player player) throws AccountRegistrationException {
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(player);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
    }

    public synchronized Scorecard getScorecardFromGame(GameSession gameSession, int index) {
        persistEntity(gameSession);
        return gameSession.getScorecardByIndex(index);
    }

    public synchronized void reorderLadder(GameSession gameSession) {
        gameSession.reorderLadder(new VrcLadderReorderer());
        List<Pair> reorderedPairs = gameSession.getReorderedLadder();
        Ladder nextWeekLadder = new Ladder(reorderedPairs);
        GameSession nextWeekGameSession = new GameSession(nextWeekLadder);
        persistEntity(gameSession);
        persistEntity(nextWeekGameSession);
    }
}
