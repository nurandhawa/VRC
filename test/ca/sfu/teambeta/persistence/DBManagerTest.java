package ca.sfu.teambeta.persistence;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
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
        Player playerExpected = null;

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

        GameSession expectedPrevious = generateGameSession(dateTime.toEpochSecond(ZoneOffset.ofTotalSeconds(0)));
        GameSession latest = generateGameSession(Instant.now().getEpochSecond());

        dbManager.persistEntity(expectedPrevious);
        dbManager.persistEntity(latest);

        GameSession resultPrevious = dbManager.getGameSessionPrevious();
        assertEquals(expectedPrevious.getID(), resultPrevious.getID());
    }

    @Test
    public void testGetLatestGameSession() {
        LocalDateTime dateTime = LocalDateTime.now().minusWeeks(1);

        GameSession previous = generateGameSession(dateTime.toEpochSecond(ZoneOffset.ofTotalSeconds(0)));
        GameSession expectedLatest = generateGameSession(Instant.now().getEpochSecond());

        dbManager.persistEntity(previous);
        dbManager.persistEntity(expectedLatest);

        GameSession resultLatest = dbManager.getGameSessionLatest();
        assertEquals(expectedLatest.getID(), resultLatest.getID());
    }
}
