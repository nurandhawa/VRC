package ca.sfu.teambeta.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;


public class PersistenceTest {
    Session session;

    @Before
    public void setupDatabase() {
        SessionFactory factory = DBManager.getTestingSession(true);
        this.session = factory.openSession();
    }

    protected Session getSession() {
        return session;
    }
}
