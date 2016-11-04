package ca.sfu.teambeta.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.BiFunction;

/**
 * Performs transactions and handles the creation and closing of sessions.
 */
public class TransactionManager {
    private Session session;

    public void startSession(Session session) {
        this.session = session;
    }

    public void finishSession() {
        this.session = null;
    }

    public <T> T executeTransaction(BiFunction<Session, Transaction, T> function) {
        T returnValue;
        Transaction tx = null;
        try {
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
