package ca.sfu.teambeta.logic;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;

/**
 * Utility class that reads and writes data to the database
 */
public class DBManager {
    private SessionFactory factory;

    DBManager(SessionFactory factory) {
        this.factory = factory;
    }

    private static Configuration getDefaultConfiguration() {
        Configuration config = new Configuration();
        config.addAnnotatedClass(Player.class);
        config.addAnnotatedClass(Pair.class);
        config.addAnnotatedClass(Ladder.class);
        return config;
    }

    public static SessionFactory getTestingSession() {
        Configuration config = getDefaultConfiguration();
        config.setProperty("hibernate.hbm2ddl.auto", "create");
        config.setProperty("hibernate.connection.url", "jdbc:h2:file:/home/freeman/prj/resources/database.db");
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        config.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
        return config.buildSessionFactory();
    }

    public static SessionFactory getHSQLSession() {
        Configuration config = getDefaultConfiguration();
        config.setProperty("hibernate.hbm2ddl.auto", "create");
        config.setProperty("hibernate.connection.username", "");
        config.setProperty("hibernate.connection.password", "");
        config.setProperty("hibernate.connection.pool_size", "1");
        config.setProperty("hibernate.connection.url", "jdbc:hsqldb:file:/home/freeman/prj/resources/database/test");
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        config.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
        return config.buildSessionFactory();
    }

    public static SessionFactory getMySQLSession() {
        Configuration config = getDefaultConfiguration();
        config.setProperty("hibernate.hbm2ddl.auto", "update");
        config.setProperty("hibernate.connection.username", "sql3124016");
        config.setProperty("hibernate.connection.password", "kTZ23wYIQq");
        config.setProperty("hibernate.connection.pool_size", "1");
        config.setProperty("hibernate.connection.url", "jdbc:mysql://sql3.freemysqlhosting.net:3306/sql3124016");
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        config.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        return config.buildSessionFactory();
    }

    public static void main(String[] args) {
        SessionFactory factory = getTestingSession();
        DBManager dbMan = new DBManager(factory);
        dbMan.addPlayer(new Player("Bobby", "Chan", ""));
    }

    public int addPair(Pair pair) {
        Session session = factory.openSession();
        Transaction tx = null;
        int key = 0;
        try {
            tx = session.beginTransaction();
            key = (int) session.save(pair);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return key;
    }

    public int addPlayer(Player player) {
        Session session = factory.openSession();
        Transaction tx = null;
        int key = 0;
        try {
            tx = session.beginTransaction();
            key = (int) session.save(player);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return key;
    }
}
