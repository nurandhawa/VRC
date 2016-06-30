package ca.sfu.teambeta.persistence;


import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import ca.sfu.teambeta.core.Player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PlayerDBTest extends PersistenceTest {

    @Test
    public void testPlayerCreation() {
        Transaction tx;
        Session session = getSession();
        tx = session.beginTransaction();
        session.save(new Player("Big", "Head"));
        tx.commit();
    }

    @Test
    public void testUniquePlayerID() {
        Transaction tx;
        Session session = getSession();
        tx = session.beginTransaction();
        int id1 = (int) session.save(new Player("Big", "Head"));
        int id2 = (int) session.save(new Player("Big", "Head"));
        tx.commit();
        assertNotEquals(id1, id2);
    }

    @Test
    public void testSamePlayerID() {
        Transaction tx;
        Session session = getSession();
        tx = session.beginTransaction();
        Player player = new Player("Big", "Head");
        int id1 = (int) session.save(player);
        int id2 = (int) session.save(player);
        tx.commit();
        assertEquals(id1, id2);
    }


}
