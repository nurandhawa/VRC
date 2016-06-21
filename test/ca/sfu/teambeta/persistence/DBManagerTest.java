package ca.sfu.teambeta.persistence;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by David on 2016-06-19.
 */
public class DBManagerTest {

    @Test
    public void testGetPlayerFromID() {
        Player playerExpected = new Player("Zara", "Ali", "1234");

        SessionFactory sessionFactory = DBManager.getMySQLSession(true);
        DBManager dbManager = new DBManager(sessionFactory);
        dbManager.persistEntity(playerExpected);
        Player playerActual = dbManager.getPlayerFromID(1);

        Assert.assertEquals(playerExpected, playerActual);
    }

    @Test
    public void testGetPlayerFromIDNotFound() {
        Player playerExpected = null;

        SessionFactory sessionFactory = DBManager.getMySQLSession(true);
        DBManager dbManager = new DBManager(sessionFactory);
        Player playerActual = dbManager.getPlayerFromID(99);

        Assert.assertEquals(playerExpected, playerActual);
    }

    @Test
    public void testNotNullLadder() {
        List<Pair> ladderPairs = Arrays.asList(
                new Pair(new Player("Bobby", "Chan", ""), new Player("Wing", "Man", ""), false),
                new Pair(new Player("Ken", "Hazen", ""), new Player("Brian", "Fraser", ""), false),
                new Pair(new Player("Simon", "Fraser", ""), new Player("Dwight", "Howard", ""), false),
                new Pair(new Player("Bobby", "Chan", ""), new Player("Big", "Head", ""), false)
        );
        Ladder ladderExpected = new Ladder(ladderPairs);

        SessionFactory sessionFactory = DBManager.getMySQLSession(true);
        DBManager dbManager = new DBManager(sessionFactory);

        dbManager.persistEntity(ladderExpected);
        Ladder ladderActual = dbManager.getLatestLadder();

        for (Pair pair : ladderActual.getPairs()) {
            assertNotNull(pair);
        }
    }

    @Test
    public void testGetLatestLadder() {
        List<Pair> ladderPairs = Arrays.asList(
                new Pair(new Player("Bobby", "Chan", ""), new Player("Wing", "Man", ""), false),
                new Pair(new Player("Ken", "Hazen", ""), new Player("Brian", "Fraser", ""), false),
                new Pair(new Player("Simon", "Fraser", ""), new Player("Dwight", "Howard", ""), false),
                new Pair(new Player("Bobby", "Chan", ""), new Player("Big", "Head", ""), false)
        );
        Ladder ladderExpected = new Ladder(ladderPairs);

        SessionFactory sessionFactory = DBManager.getMySQLSession(true);
        DBManager dbManager = new DBManager(sessionFactory);

        dbManager.persistEntity(ladderExpected);
        Ladder ladderActual = dbManager.getLatestLadder();

        Assert.assertEquals(ladderExpected, ladderActual);
    }
}
