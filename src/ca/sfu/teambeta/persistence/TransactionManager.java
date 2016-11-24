package ca.sfu.teambeta.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Performs transactions and handles the creation and closing of sessions.
 */
class TransactionManager {
    private Session session;
    private Supplier<Session> refreshSession;
    private boolean hasRecursed = false;

    TransactionManager(Supplier<Session> refreshSession) {
        this.refreshSession = refreshSession;
        this.session = refreshSession.get();
    }

    <T> T executeTransaction(BiFunction<Session, Transaction, T> function) {
        T returnValue;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            returnValue = function.apply(session, tx);
            tx.commit();
        } catch (TransactionException e) {
            if (tx == null && !hasRecursed) {
                // The session expired, so refresh it and try again.
                this.session = refreshSession.get();
                hasRecursed = true;
                return executeTransaction(function);
            } else {
                hasRecursed = false;
                throw e;
            }
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            throw e;
        }
        return returnValue;
    }
}
