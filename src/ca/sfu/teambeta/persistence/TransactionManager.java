package ca.sfu.teambeta.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.function.BiFunction;

/**
 * Performs transactions and handles the creation and closing of sessions.
 */
public class TransactionManager {
    private SessionFactory sessionFactory;

    public TransactionManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public <T> T executeTransaction(BiFunction<Session, Transaction, T> function) {
        T returnValue;
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            returnValue = function.apply(session, tx);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
        return returnValue;
    }
}
