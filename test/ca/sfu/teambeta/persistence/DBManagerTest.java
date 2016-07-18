package ca.sfu.teambeta.persistence;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.logic.GameSession;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by David on 2016-06-19.
 */
public class DBManagerTest {

    @Test
    public void testGetPlayerFromID() {
        Player playerExpected = new Player("Zara", "Ali");

        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        DBManager dbManager = new DBManager(sessionFactory);
        dbManager.persistEntity(playerExpected);
        Player playerActual = dbManager.getPlayerFromID(1);

        Assert.assertEquals(playerExpected, playerActual);
    }

    @Test
    public void testGetPlayerFromIDNotFound() {
        Player playerExpected = null;

        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        DBManager dbManager = new DBManager(sessionFactory);
        Player playerActual = dbManager.getPlayerFromID(99);

        Assert.assertEquals(playerExpected, playerActual);
    }

    @Test
    public void testNotNullLadder() {
        List<Pair> ladderPairs = Arrays.asList(
                new Pair(new Player("Bobby", "Chan"), new Player("Wing", "Man"), false),
                new Pair(new Player("Ken", "Hazen"), new Player("Brian", "Fraser"), false),
                new Pair(new Player("Simon", "Fraser"), new Player("Dwight", "Howard"), false),
                new Pair(new Player("Bobby", "Chan"), new Player("Big", "Head"), false)
        );
        Ladder ladderExpected = new Ladder(ladderPairs);

        SessionFactory sessionFactory = DBManager.getTestingSession(true);
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
                new Pair(new Player("Bobby", "Chan"), new Player("Wing", "Man"), false),
                new Pair(new Player("Ken", "Hazen"), new Player("Brian", "Fraser"), false),
                new Pair(new Player("Simon", "Fraser"), new Player("Dwight", "Howard"), false),
                new Pair(new Player("Bobby", "Chan"), new Player("Big", "Head"), false)
        );
        Ladder ladderExpected = new Ladder(ladderPairs);

        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        DBManager dbManager = new DBManager(sessionFactory);

        dbManager.persistEntity(ladderExpected);
        Ladder ladderActual = dbManager.getLatestLadder();

        Assert.assertEquals(ladderExpected, ladderActual);
    }

    private GameSession generateGameSession() {
        List<Pair> ladderPairs = Arrays.asList(
                new Pair(new Player("Bobby", "Chan"), new Player("Wing", "Man"), false),
                new Pair(new Player("Ken", "Hazen"), new Player("Brian", "Fraser"), false),
                new Pair(new Player("Simon", "Fraser"), new Player("Dwight", "Howard"), false),
                new Pair(new Player("Bobby", "Chan"), new Player("Big", "Head"), false)
        );
        Collections.shuffle(ladderPairs);
        Ladder ladder = new Ladder(ladderPairs);
        return new GameSession(ladder);
    }


    @Test
    public void testGetPreviousGameSession() {
        GameSession expectedPrevious = generateGameSession();
        GameSession latest = generateGameSession();

        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        DBManager dbManager = new DBManager(sessionFactory);

        dbManager.persistEntity(expectedPrevious);
        dbManager.persistEntity(latest);

        GameSession resultPrevious = dbManager.getGameSessionPrevious();
        assertEquals(expectedPrevious.getID(), resultPrevious.getID());
    }

    @Test
    public void testGetPreviousGameSessionManySessions() {
        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        DBManager dbManager = new DBManager(sessionFactory);

        List<GameSession> sessionsList = new ArrayList<>(10);
        GameSession currSession;
        for (int i = 0; i < 10; i++) {
            currSession = generateGameSession();
            sessionsList.add(currSession);
            dbManager.persistEntity(currSession);
        }

        GameSession expectedPrevious = sessionsList.get(sessionsList.size() - 2);
        GameSession resultPrevious = dbManager.getGameSessionPrevious();

        assertEquals(expectedPrevious.getID(), resultPrevious.getID());
    }

    @Test
    public void testGetLatestGameSession() {
        GameSession previous = generateGameSession();
        GameSession expectedLatest = generateGameSession();

        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        DBManager dbManager = new DBManager(sessionFactory);

        dbManager.persistEntity(previous);
        dbManager.persistEntity(expectedLatest);

        GameSession resultLatest = dbManager.getGameSessionLatest();
        assertEquals(expectedLatest.getID(), resultLatest.getID());
    }

    @Test
    public void testGetLatestGameSessionManySessions() {
        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        DBManager dbManager = new DBManager(sessionFactory);

        List<GameSession> sessionsList = new ArrayList<>(10);
        GameSession currSession;
        for (int i = 0; i < 10; i++) {
            currSession = generateGameSession();
            sessionsList.add(currSession);
            dbManager.persistEntity(currSession);
        }

        GameSession expectedLatest = sessionsList.get(sessionsList.size() - 1);
        GameSession resultLatest = dbManager.getGameSessionLatest();

        assertEquals(expectedLatest.getID(), resultLatest.getID());
    }
}
