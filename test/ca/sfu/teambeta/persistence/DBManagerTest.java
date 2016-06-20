package ca.sfu.teambeta.persistence;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
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

    @Test
    public void testGetPlayerFromIDNotFound() {
        Player playerExpected = null;

        SessionFactory sessionFactory = DBManager.getMySQLSession();
        DBManager dbManager = new DBManager(sessionFactory);
        Player playerActual = dbManager.getPlayerFromID(99);

        Assert.assertEquals(playerExpected, playerActual);
    }

    @Test
    @Ignore
    public void testGetLatestLadder() {
        List<Pair> ladderPairs = Arrays.asList(
                new Pair(new Player("Bobby", "Chan", null), new Player("Wing", "Man", null), false),
                new Pair(new Player("Ken", "Hazen", null), new Player("Brian", "Fraser", null), false),
                new Pair(new Player("Simon", "Fraser", null), new Player("Dwight", "Howard", null), false),
                new Pair(new Player("Bobby", "Chan", null), new Player("Big", "Head", null), false)
        );
        Ladder ladderExpected = new Ladder(ladderPairs);
        SessionFactory sessionFactory = DBManager.getMySQLSession();
        DBManager dbManager = new DBManager(sessionFactory);
        Ladder ladderActual = dbManager.getLatestLadder();

        Assert.assertEquals(ladderExpected, ladderActual);
    }
}
