package ca.sfu.teambeta.persistence;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.logic.GameSession;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by David on 2016-06-19.
 */
public class DBManagerTest {
    private DBManager dbManager;

    @Before
    public void setUp() throws Exception {
        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        this.dbManager = new DBManager(sessionFactory);
    }

    @Test
    public void testGetPlayerFromID() {
        Player playerExpected = new Player("Zara", "Ali");

        dbManager.persistEntity(playerExpected);
        Player playerActual = dbManager.getPlayerFromID(1);

        Assert.assertEquals(playerExpected, playerActual);
    }

    @Test
    public void testGetPlayerFromIDNotFound() {
        Player playerActual = dbManager.getPlayerFromID(99);
        Assert.assertNull(playerActual);
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

        dbManager.persistEntity(ladderExpected);
        Ladder ladderActual = dbManager.getLatestLadder();

        Assert.assertEquals(ladderExpected, ladderActual);
    }

    private GameSession generateGameSession(long timestamp) {
        List<Pair> ladderPairs = Arrays.asList(
                new Pair(new Player("Bobby", "Chan"), new Player("Wing", "Man"), false),
                new Pair(new Player("Ken", "Hazen"), new Player("Brian", "Fraser"), false),
                new Pair(new Player("Simon", "Fraser"), new Player("Dwight", "Howard"), false),
                new Pair(new Player("Bobby", "Chan"), new Player("Big", "Head"), false)
        );
        Collections.shuffle(ladderPairs);
        Ladder ladder = new Ladder(ladderPairs);
        return new GameSession(ladder, timestamp);
    }

    @Test
    public void testGetPreviousGameSession() {
        LocalDateTime dateTime = LocalDateTime.now().minusWeeks(1);

        GameSession expectedPrevious = generateGameSession(
                dateTime.toEpochSecond(ZoneOffset.ofTotalSeconds(0)));
        GameSession latest = generateGameSession(Instant.now().getEpochSecond());

        dbManager.persistEntity(expectedPrevious);
        dbManager.persistEntity(latest);

        GameSession resultPrevious = dbManager.getGameSessionPrevious();
        assertEquals(expectedPrevious.getID(), resultPrevious.getID());
    }

    @Test
    public void testGetLatestGameSession() {
        LocalDateTime dateTime = LocalDateTime.now().minusWeeks(1);

        GameSession previous = generateGameSession(
                dateTime.toEpochSecond(ZoneOffset.ofTotalSeconds(0)));
        GameSession expectedLatest = generateGameSession(Instant.now().getEpochSecond());

        dbManager.persistEntity(previous);
        dbManager.persistEntity(expectedLatest);

        GameSession resultLatest = dbManager.getGameSessionLatest();
        assertEquals(expectedLatest.getID(), resultLatest.getID());
    }

    @Test
    public void testGetLatestGameSessionManySessions() {
        List<GameSession> sessionsList = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            GameSession currSession = generateGameSession(i);
            sessionsList.add(currSession);
            dbManager.persistEntity(currSession);
        }

        GameSession expectedLatest = sessionsList.get(sessionsList.size() - 1);
        GameSession resultLatest = dbManager.getGameSessionLatest();

        assertEquals(expectedLatest.getID(), resultLatest.getID());
    }

    @Test
    public void testAddPair() throws Exception {
        GameSession gameSession = generateGameSession(Instant.now().getEpochSecond());
        dbManager.persistEntity(gameSession);

        Pair expectedPair = new Pair(new Player("Jin", "Yang"),
                new Player("Erlich", "Bachman"));
        dbManager.addPair(gameSession, expectedPair);

        List<Pair> allPairs = gameSession.getAllPairs();

        Pair resultsPair = allPairs.get(allPairs.size() - 1);

        assertEquals(expectedPair.getID(), resultsPair.getID());
    }

    @Test
    public void testAddPairAlreadyExists() throws Exception {
        GameSession gameSession = generateGameSession(Instant.now().getEpochSecond());
        dbManager.persistEntity(gameSession);

        Pair pair = new Pair(new Player("Jin", "Yang"),
                new Player("Erlich", "Bachman"));
        dbManager.addPair(gameSession, pair);

        List<Pair> expectedPairs = gameSession.getAllPairs();

        dbManager.addPair(gameSession, pair);

        List<Pair> resultPairs = gameSession.getAllPairs();

        assertEquals(expectedPairs, resultPairs);
    }

    @Test
    public void testAddPairAtPosition() throws Exception {
        GameSession gameSession = generateGameSession(Instant.now().getEpochSecond());
        dbManager.persistEntity(gameSession);

        Pair expectedPair = new Pair(new Player("Jin", "Yang"),
                new Player("Erlich", "Bachman"));
        dbManager.addPair(gameSession, expectedPair, 0);

        List<Pair> allPairs = gameSession.getAllPairs();

        Pair resultsPair = allPairs.get(0);

        assertEquals(expectedPair.getID(), resultsPair.getID());
    }

    @Test
    public void testAddPairAtPositionEnd() throws Exception {
        GameSession gameSession = generateGameSession(Instant.now().getEpochSecond());
        dbManager.persistEntity(gameSession);

        Pair expectedPair = new Pair(new Player("Jin", "Yang"),
                new Player("Erlich", "Bachman"));
        dbManager.addPair(gameSession, expectedPair, gameSession.getAllPairs().size());

        List<Pair> allPairs = gameSession.getAllPairs();

        Pair resultsPair = allPairs.get(gameSession.getAllPairs().size() - 1);

        assertEquals(expectedPair.getID(), resultsPair.getID());
    }

    @Test
    public void testAddPairAtInvalidPosition() throws Exception {
        GameSession gameSession = generateGameSession(Instant.now().getEpochSecond());
        dbManager.persistEntity(gameSession);

        Pair pair = new Pair(new Player("Jin", "Yang"),
                new Player("Erlich", "Bachman"));
        dbManager.addPair(gameSession, pair, -1);

        List<Pair> allPairs = gameSession.getAllPairs();

        assertTrue(!allPairs.contains(pair));
    }

    @Test
    public void testRemoveUser() {
        Ladder ladder = null;
        try {
            ladder = CSVReader.setupTestingLadder(dbManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        GameSession gameSession = new GameSession(ladder);
        dbManager.persistEntity(gameSession);

        List<User> expected = dbManager.getAllUsers();
        assertEquals(expected.size(), 0);

        User newUser = new User("testuser@vrc.ca", "testing");
        Player randomPlayer = ladder.getPairs().get(0).getPlayers().get(0);
        newUser.associatePlayer(randomPlayer);
        try {
            dbManager.addNewUser(newUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        expected = dbManager.getAllUsers();
        assertEquals(expected.size(), 1);

        try {
            dbManager.deleteUser(newUser.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
        }

        expected = dbManager.getAllUsers();
        assertEquals(expected.size(), 0);
    }
}
