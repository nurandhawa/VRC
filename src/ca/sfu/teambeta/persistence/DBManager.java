package ca.sfu.teambeta.persistence;

import ca.sfu.teambeta.logic.VrcScorecardGenerator;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;

import ca.sfu.teambeta.core.Game;
import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Penalty;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.logic.GameSession;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class that reads and writes data to the database
 */
public class DBManager {
    private static final String DEFAULT_FILENAME = "ladder.csv";
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
        return config;
    }

    public static SessionFactory getHSQLSession() {
        Configuration config = getDefaultConfiguration();
        config.setProperty("hibernate.hbm2ddl.auto", "update");
        config.setProperty("hibernate.connection.username", "");
        config.setProperty("hibernate.connection.password", "");
        config.setProperty("hibernate.connection.pool_size", "1");
        config.setProperty("hibernate.connection.url", "jdbc:hsqldb:file:/home/freeman/prj/resources/database/test");
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
        config.setProperty("hibernate.connection.url", "jdbc:mysql://cmpt373-beta.csil.sfu.ca:3306/test?serverTimezone=America/Vancouver");
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        try {
            return config.buildSessionFactory();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static SessionFactory getDockerSession(boolean create) {
        Configuration config = getDefaultConfiguration();
        if (create) {
            config.setProperty("hibernate.hbm2ddl.auto", "create");
        } else {
            config.setProperty("hibernate.hbm2ddl.auto", "update");
        }
        config.setProperty("hibernate.connection.username", "root");
        config.setProperty("hibernate.connection.password", "b3ta");
        config.setProperty("hibernate.connection.pool_size", "1");
        config.setProperty("hibernate.connection.url", "jdbc:mysql://mysql:3306/test?serverTimezone=America/Vancouver");
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
//        Player p1 = new Player("Bobby", "Chan", "");
//        Player p2 = new Player("Wing", "Man", "");
//        dbMan.persistEntity(new Pair(p1, p2));
//
//        Player p3 = new Player("Hello", "World!", "");
//        dbMan.persistEntity(new Pair(new Player("Bobby", "Chan", ""), p3));
//
//        Player test = dbMan.getPlayerFromID(5);

//        System.out.println(test.getFirstName());

        Ladder lad = dbMan.getLatestLadder();

        System.out.println(lad);
    }

    public static List<Player> getInformationAboutPlayers() throws Exception{
        List<Player> players;
        try (CSVReader reader = new CSVReader(new FileReader(DEFAULT_FILENAME))) {
            List<String[]> entries = reader.readAll();
            Iterator<String[]> iterator = entries.iterator();
            players = new ArrayList<>();

            String[] pairInfo;
            while (iterator.hasNext()) {
                pairInfo = iterator.next();

                String lastName = pairInfo[0];
                String firstName = pairInfo[1];
                String id = pairInfo[2];

                Player player = new Player(lastName, firstName, id);
                players.add(player);
            }
            reader.close();
        } catch (IOException e) {
            throw new Exception("Error reading file " + DEFAULT_FILENAME);
        }

        return players;
    }

    public int persistEntity(Persistable entity) {
        Transaction tx = null;
        int key = 0;
        try {
            tx = session.beginTransaction();
            key = (int) session.save(entity);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
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

    public Player getPlayerFromID(int id) {
        Player player = null;
        try {
            player = (Player) getEntityFromID(Player.class, id);
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return player;
    }

    public Pair getPairFromID(int id) {
        Pair pair = null;
        try {
            pair = (Pair) getEntityFromID(Pair.class, id);
        } catch (HibernateException e) {
            e.printStackTrace();
        }
        return pair;
    }

    public Ladder getLatestLadder() {
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

    public void addPenaltyToPairToLatestGameSession(int pairId, Penalty penalty) {
        Transaction tx = null;
        GameSession gameSession = null;
        try {
            tx = session.beginTransaction();
            Pair pair = session.get(Pair.class, pairId);
            DetachedCriteria maxId = DetachedCriteria.forClass(GameSession.class)
                    .setProjection(Projections.max("id"));
            gameSession = (GameSession) session.createCriteria(GameSession.class)
                    .add(Property.forName("id").eq(maxId))
                    .uniqueResult();
            gameSession.setPenaltyToPair(pair, penalty);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
    }

    public void addPairToLatestLadder(Pair pair) {
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

    public void inputMatchResults(int pairId, String[][] results) throws Exception{
        GameSession gameSession = getGameSessionLatest();

        int rows = results.length;
        int cols = results[0].length;

        Pair pair = getPairFromID(pairId);
        int index = pair.getPosition() - 1;

        List<Scorecard> scorecards = gameSession.getScorecards();
        Scorecard group = scorecards.get(index);
        int numTeams = group.getReorderedPairs().size();

        boolean isValidResult = (rows == numTeams) && (cols == numTeams);
        if (!isValidResult) {
            throw new Exception();
        }

        List<Pair> teams = group.getReorderedPairs();
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
                group.setGameResults(teamWon, teamLost);
            }
            winCount = 0;
            teamLost = null;
            teamWon = null;
        }

        submitGameSession(gameSession);
    }

    public void addPair(Pair pair, int position) {
        GameSession gameSession = getGameSessionLatest();
        gameSession.addNewPairAtIndex(pair, position);
        submitGameSession(gameSession);
    }

    public void addPair(Pair pair) {
        GameSession gameSession = getGameSessionLatest();
        gameSession.addNewPairAtEnd(pair);
        submitGameSession(gameSession);
    }

    public boolean removePair(int pairId) {
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

    public boolean hasPairID(int id) {
        return getPairFromID(id) != null;
    }

    public void movePair(int pairId, int newPosition){
        GameSession gameSession = getGameSessionLatest();
        Pair pair = getPairFromID(pairId);

        removePair(pairId);
        gameSession.addNewPairAtIndex(pair, newPosition);
        submitGameSession(gameSession);
    }

    public Player getAlreadyActivePlayer(int id) throws Exception {
        GameSession gameSession = getGameSessionLatest();
        Pair pair = getPairFromID(id);
        Player player;
        try {
            player = gameSession.getAlreadyActivePlayer(pair);
        } catch (Exception e){
            throw e;
        }
        return player;
    }

    public boolean setPairActive(int pairId) {
        GameSession gameSession = getGameSessionLatest();
        Pair pair = getPairFromID(pairId);
        boolean activated = gameSession.setPairActive(pair);
        submitGameSession(gameSession);
        return activated;
    }

    public void setPairInactive(int pairId) {
        GameSession gameSession = getGameSessionLatest();
        Pair pair = getPairFromID(pairId);
        gameSession.setPairInactive(pair);
        gameSession.createGroups(new VrcScorecardGenerator());
        submitGameSession(gameSession);
    }

    public boolean isActivePair(int pairId){
        GameSession gameSession = getGameSessionLatest();
        Pair pair = getPairFromID(pairId);

        boolean status = gameSession.isActivePair(pair);
        submitGameSession(gameSession);
        return status;
    }

    public int getLadderSize() {
        GameSession gameSession = getGameSessionLatest();
        List<Pair> ladder = gameSession.getActivePairs();
        return ladder.size();
    }

    public String getJSONLadder() {
        GameSession gameSession = getGameSessionLatest();
        List<Pair> ladder = gameSession.getAllPairs();
        Gson gson = new Gson();

        String json = gson.toJson(ladder);
        return json;
    }

    public String getJSONScorecards() {
        GameSession gameSession = getGameSessionLatest();
        List<Scorecard> scorecards = gameSession.getScorecards();
        Gson gson = new Gson();

        String json = gson.toJson(scorecards);
        return json;
    }

    private GameSession getGameSession(int gameSessionId){
        Transaction tx = null;
        GameSession gameSession = null;
        try {
            tx = session.beginTransaction();
            gameSession = (GameSession) session.createCriteria(Ladder.class)
                    .add(Property.forName("id").eq(gameSessionId))
                    .uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        return gameSession;
    }

    private GameSession getGameSessionLatest(){
        Transaction tx = null;
        GameSession gameSession = null;
        try {
            tx = session.beginTransaction();
            DetachedCriteria maxId = DetachedCriteria.forClass(Ladder.class)
                    .setProjection(Projections.max("id"));
            gameSession = (GameSession) session.createCriteria(Ladder.class)
                    .add(Property.forName("id").eq(maxId))
                    .uniqueResult();
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
        return gameSession;
    }

    private void submitGameSession(GameSession newSession){
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            session.saveOrUpdate(newSession);
            tx.commit();
        } catch (HibernateException e) {
            tx.rollback();
        }
    }
}
