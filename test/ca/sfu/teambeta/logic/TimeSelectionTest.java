package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.Time;

import ca.sfu.teambeta.persistence.CSVReader;
import ca.sfu.teambeta.persistence.DBManager;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by constantin on 11/07/16.
 */

public class TimeSelectionTest {
    private static final int AMOUNT_TIME_SLOTS = Time.values().length - 1;
    private static final int MAX_NUM_PAIRS_PER_SLOT = 24;

    @Test
    public void getPairsByTime() {
        List<Pair> pairs = new ArrayList<Pair>() {
            {
                add(new Pair(new Player("a", "b"), new Player("c", "d")));
                add(new Pair(new Player("e", "f"), new Player("g", "h")));
                add(new Pair(new Player("i", "j"), new Player("k", "l")));
            }
        };

        Scorecard sc1 = new Scorecard(pairs, null);
        Scorecard sc2 = new Scorecard(pairs, null);

        TimeSelection selector = new VrcTimeSelection();
        sc1.setTimeSlot(Time.SLOT_1);
        sc2.setTimeSlot(Time.SLOT_2);
        List<Scorecard> scorecards = new ArrayList<Scorecard>() {
            {
                add(sc1);
                add(sc2);
            }
        };

        int pairsFirstTimeSlot = selector.getAmountPairsByTime(scorecards, Time.SLOT_1);
        int pairsSecondTimeSlot = selector.getAmountPairsByTime(scorecards, Time.SLOT_2);

        Assert.assertEquals(3, pairsFirstTimeSlot);
        Assert.assertEquals(3, pairsSecondTimeSlot);
    }

    @Test
    public void clearTimeSlots() {
        Player firstPlayer = new Player("Kate", "Smith");
        Player secondPlayer = new Player("Nick", "Smith");
        Pair pair1 = new Pair(firstPlayer, secondPlayer, true);
        pair1.setTimeSlot(Time.SLOT_1);

        firstPlayer = new Player("Anna", "Fraser");
        secondPlayer = new Player("Camden", "Fraser");
        Pair pair2 = new Pair(firstPlayer, secondPlayer, true);
        pair2.setTimeSlot(Time.SLOT_1);

        firstPlayer = new Player("Maria", "Johnson");
        secondPlayer = new Player("Isaac", "johnson");
        Pair pair3 = new Pair(firstPlayer, secondPlayer, true);
        pair3.setTimeSlot(Time.SLOT_1);

        Ladder ladder = new Ladder();
        ladder.insertAtEnd(pair1);
        ladder.insertAtEnd(pair2);
        ladder.insertAtEnd(pair3);

        List<Scorecard> scorecards = new ArrayList<Scorecard>() {
            {
                add(new Scorecard(ladder.getPairs(), null));
            }
        };

        TimeSelection selector = new VrcTimeSelection();
        selector.clearTimeSlots(ladder);
        int size = selector.getAmountPairsByTime(scorecards, Time.SLOT_1);
        Assert.assertEquals(size, 0);
    }

    @Test
    public void checkCommonTimeForGroup() {

        Player firstPlayer = new Player("Kate", "Smith");
        Player secondPlayer = new Player("Nick", "Smith");
        Pair pair1 = new Pair(firstPlayer, secondPlayer, true);
        pair1.setTimeSlot(Time.SLOT_1);

        firstPlayer = new Player("Anna", "Fraser");
        secondPlayer = new Player("Camden", "Fraser");
        Pair pair2 = new Pair(firstPlayer, secondPlayer, true);
        pair2.setTimeSlot(Time.SLOT_1);

        firstPlayer = new Player("Emma", "Johnson");
        secondPlayer = new Player("William", "Johnson");
        Pair pair3 = new Pair(firstPlayer, secondPlayer, true);
        pair3.setTimeSlot(Time.SLOT_1);

        List<Pair> pairs = new ArrayList<Pair>() {
            {
                add(pair1);
                add(pair2);
                add(pair3);
            }
        };
        Scorecard scorecard = new Scorecard(pairs, null);
        List<Scorecard> scorecards = new ArrayList<Scorecard>() {
            {
                add(scorecard);
            }
        };

        TimeSelection selector = new VrcTimeSelection();
        selector.distributePairs(scorecards);
        Time time = scorecard.getTimeSlot();
        Time expectedTime = Time.SLOT_1;

        Assert.assertEquals(expectedTime, time);
    }

    @Test
    public void distributePairsCase_1() throws Exception {
        TimeSelection selector = new VrcTimeSelection();
        ScorecardGenerator generator = new VrcScorecardGenerator();

        List<Pair> pairs = setUpBig();
        List<Scorecard> scorecards =
                generator.generateScorecards(pairs);

        selector.distributePairs(scorecards);

        //Check logic when too many pairs are playing
        //It should distribute pairs equally between time slots
        checkLogic(selector, scorecards);
    }

    private List<Pair> setUpBig() throws Exception {
        List<Pair> pairs;

        try {
            pairs = createBigAmountPairs();
        } catch (Exception e) {
            throw e;
        }

        int count = 0;
        for (Pair pair : pairs) {
            if (count % 5 == 0) {
                pair.setTimeSlot(Time.SLOT_2);
            } else {
                pair.setTimeSlot(Time.SLOT_1);
            }
            count++;
        }

        return pairs;
    }

    private List<Pair> createBigAmountPairs() throws Exception {
        List<Pair> pairs;

        try {
            Ladder ladder = CSVReader.setupLadder();
            pairs = ladder.getPairs();
        } catch (Exception e) {
            throw e;
        }

        return pairs;
    }

    private void checkLogic(TimeSelection selector, List<Scorecard> scorecards) {
        int amountPlayingPairs = getAmountOfAllPairs(scorecards);

        int maxNumPairs = AMOUNT_TIME_SLOTS * MAX_NUM_PAIRS_PER_SLOT;
        boolean crowded = amountPlayingPairs > maxNumPairs;
        int expectedAmount = selector.getAmountPairsByTime(scorecards, Time.SLOT_1);

        if (crowded) {
            expectedAmount = amountPlayingPairs / AMOUNT_TIME_SLOTS;
        } else {
            if (expectedAmount > MAX_NUM_PAIRS_PER_SLOT) {
                expectedAmount = MAX_NUM_PAIRS_PER_SLOT;
            }
        }

        int amountPairs = selector.getAmountPairsByTime(scorecards, Time.SLOT_1);

        //Error on 1 pair is allowed as there are some groups with 4 pairs
        boolean approximateAmount =
                amountPairs == expectedAmount
                        || (amountPairs - 1) == expectedAmount
                        || (amountPairs + 1) == expectedAmount;

        Assert.assertEquals(approximateAmount, true);
    }

    private int getAmountOfAllPairs(List<Scorecard> scorecards) {
        int amount = 0;
        for (Scorecard scorecard : scorecards) {
            amount += scorecard.getReorderedPairs().size();
        }
        return amount;
    }

    @Test
    public void distributePairsCase_2() throws Exception {
        TimeSelection selector = new VrcTimeSelection();
        ScorecardGenerator generator = new VrcScorecardGenerator();

        List<Pair> pairs = setUpSmall();

        List<Scorecard> scorecards =
                generator.generateScorecards(pairs);
        selector.distributePairs(scorecards);

        //One of the time slots was overflowed
        //Distributes extra pairs to other time slots
        checkLogic(selector, scorecards);
    }

    private List<Pair> setUpSmall() throws Exception {
        List<Pair> pairs;

        try {
            pairs = createSmallAmountPairs();
        } catch (Exception e) {
            throw e;
        }

        for (Pair pair : pairs) {
            pair.setTimeSlot(Time.SLOT_1);
        }

        return pairs;
    }

    private List<Pair> createSmallAmountPairs() throws Exception {
        List<Pair> pairs;
        List<Pair> littlePairs = new ArrayList<>();

        try {
            Ladder ladder = CSVReader.setupLadder();
            pairs = ladder.getPairs();
        } catch (Exception e) {
            throw e;
        }

        int size = pairs.size();
        int startFromTwoThird = size * 2 / 3;

        for (int i = startFromTwoThird; i < size; i++) {
            Pair pair = pairs.get(i);
            littlePairs.add(pair);
        }

        return littlePairs;
    }

    @Test
    public void scorecardTimeScenario() {
        List<Pair> pairs = new ArrayList<>();
        final int NUM_OF_PLAYERS = 10;
        Player p1;
        Player p2;
        for (int i = 0; i < NUM_OF_PLAYERS; i++) {
            p1 = new Player(Integer.toString(i + 65), Integer.toString(i + 66));
            p2 = new Player(Integer.toString(i + 93), Integer.toString(i + 94));
            pairs.add(new Pair(p1, p2));
        }
        Ladder newLadder = new Ladder(pairs);
        SessionFactory sessionFactory = DBManager.getMySQLSession(true);
        GameSession gameSession = new GameSession(newLadder);
        DBManager dbManager = new DBManager(sessionFactory);
        dbManager.persistEntity(gameSession);

        for (int i = 0; i < pairs.size(); i++) {
            dbManager.setPairActive(gameSession, pairs.get(i).getID());
            if (i == 0 || i == 2 || i == 5 || i == 9) {
                pairs.get(i).setTimeSlot(Time.SLOT_2);
            } else {
                pairs.get(i).setTimeSlot(Time.SLOT_1);
            }
        }
        dbManager.performTimeDistribution();

        List<Scorecard> scorecards = gameSession.getScorecards();
        Assert.assertEquals(scorecards.get(0).getTimeSlot(), Time.SLOT_2);
        Assert.assertEquals(scorecards.get(1).getTimeSlot(), Time.SLOT_1);
        Assert.assertEquals(scorecards.get(2).getTimeSlot(), Time.SLOT_1);
    }
}