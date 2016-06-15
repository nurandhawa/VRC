package ca.sfu.teambeta.logic;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;

/**
 * Utility class that reads and writes data to the database
 */
public class DBManager {
    private static SessionFactory factory;

    public static void main(String[] args) {
        try {
            factory = new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Failed to create sessionFactory object." + ex);
            throw new ExceptionInInitializerError(ex);
        }
        DBManager ME = new DBManager();

      /* Add few employee records in database */
        Integer empID1 = ME.addPlayer("Zara", "Ali", 1000);
        Integer empID2 = ME.addPlayer("Daisy", "Das", 5000);
        Integer empID3 = ME.addPlayer("Daisy", "Dass", 5000);
        Pair p1 = ME.addPair(empID1, empID2);
        Pair p2 = ME.addPair(empID1, empID3);
        Pair p3 = ME.addPair(empID2, empID3);

        ME.addLadder(Arrays.asList(p1, p2, p3));
        ME.addLadder(Arrays.asList(p3, p1, p2));

//        Integer empID3 = ME.addPlayer("John", "Paul", 10000);
//
//      /* List down all the employees */
//        ME.listPlayers();
//
//      /* Update employee's records */
//        ME.updatePlayer(empID1, "12345678");
//
//      /* Delete an employee from the database */
//        ME.deletePlayer(empID2);

      /* List down new list of the employees */
        ME.listPlayers();
    }

    public Pair addPair(int id1, int id2) {
        Session session = factory.openSession();
        Transaction tx = null;
        Pair pair = null;
        try {
            tx = session.beginTransaction();
            Player p1 = session.load(Player.class, id1);
            Player p2 = session.load(Player.class, id2);
            pair = new Pair(p1, p2);
            session.save(pair);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return pair;
    }

    /* Method to CREATE an employee in the database */
    public Integer addPlayer(String fname, String lname, int salary) {
        Session session = factory.openSession();
        Transaction tx = null;
        Integer employeeID = null;
        try {
            tx = session.beginTransaction();
            Player employee = new Player(fname, lname, "1234");
            employeeID = (Integer) session.save(employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return employeeID;
    }

    public Integer addLadder(List<Pair> ladder) {
        Session session = factory.openSession();
        Transaction tx = null;
        Integer employeeID = null;
        try {
            tx = session.beginTransaction();
            Ladder l = new Ladder(ladder);
            employeeID = (Integer) session.save(l);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return employeeID;
    }

    /* Method to  READ all the employees */
    public void listPlayers() {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            List employees = session.createQuery("FROM Player").list();
            for (Iterator iterator =
                 employees.iterator(); iterator.hasNext(); ) {
                Player employee = (Player) iterator.next();
                System.out.print("First Name: " + employee.getId());
            }
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /* Method to UPDATE salary for an employee */
    public void updatePlayer(Integer PlayerID, String phNum) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Player employee =
                    (Player) session.get(Player.class, PlayerID);
            employee.setPhoneNumber(phNum);
            session.update(employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    /* Method to DELETE an employee from the records */
    public void deletePlayer(Integer PlayerID) {
        Session session = factory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Player employee =
                    (Player) session.get(Player.class, PlayerID);
            session.delete(employee);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

}
