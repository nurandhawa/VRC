package ca.sfu.teambeta.persistence;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;

import ca.sfu.teambeta.core.Player;

/**
 * Created by David on 2016-06-19.
 */
public class DBManagerTest {

    @Test
    public void testGetPlayerFromID() {
        Player playerExpected = new Player("Zara", "Ali", "1234");

        SessionFactory sessionFactory = DBManager.getMySQLSession();
        DBManager dbManager = new DBManager(sessionFactory);
        Player playerActual = dbManager.getPlayerFromID(1);

        Assert.assertEquals(playerExpected, playerActual);
    }
}
