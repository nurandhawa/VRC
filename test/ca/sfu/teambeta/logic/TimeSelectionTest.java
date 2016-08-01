package ca.sfu.teambeta.logic;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.Time;
import ca.sfu.teambeta.persistence.CSVReader;

/**
 * Created by constantin on 11/07/16.
 */

public class TimeSelectionTest {
    private static final int AMOUNT_TIME_SLOTS = Time.values().length - 1;
    private static final int MAX_NUM_PAIRS_PER_SLOT = 24;

    @Ignore
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
        List<Scorecard> scorecards = Arrays.asList(sc1, sc2);

        int pairsFirstTimeSlot = selector.getAmountPairsByTime(scorecards, Time.SLOT_1);
        int pairsSecondTimeSlot = selector.getAmountPairsByTime(scorecards, Time.SLOT_2);

        Assert.assertEquals(3, pairsFirstTimeSlot);
        Assert.assertEquals(3, pairsSecondTimeSlot);
    }

    @Ignore
    @Test
    public void checkCommonTimeForGroup() {
        Map<Pair, Time> timeSlots = new HashMap<>();

        Player firstPlayer = new Player("Kate", "Smith");
        Player secondPlayer = new Player("Nick", "Smith");
        Pair pair1 = new Pair(firstPlayer, secondPlayer, true);
        timeSlots.put(pair1, Time.SLOT_1);

        firstPlayer = new Player("Anna", "Fraser");
        secondPlayer = new Player("Camden", "Fraser");
        Pair pair2 = new Pair(firstPlayer, secondPlayer, true);
        timeSlots.put(pair2, Time.SLOT_1);

        firstPlayer = new Player("Emma", "Johnson");
        secondPlayer = new Player("William", "Johnson");
        Pair pair3 = new Pair(firstPlayer, secondPlayer, true);
        timeSlots.put(pair3, Time.SLOT_1);

        List<Pair> pairs = Arrays.asList(pair1, pair2, pair3);
        Scorecard scorecard = new Scorecard(pairs, null);
        List<Scorecard> scorecards = Collections.singletonList(scorecard);

        TimeSelection selector = new VrcTimeSelection();
        selector.distributePairs(scorecards, timeSlots);
        Time time = scorecard.getTimeSlot();
        Time expectedTime = Time.SLOT_1;

        Assert.assertEquals(expectedTime, time);
    }

    @Ignore
    @Test
    public void fourPairScorecardTimeTest() {
        Pair pair1 = new Pair(new Player("First",""), new Player("Player",""), true);
        Pair pair2 = new Pair(new Player("Second",""), new Player("Player",""), true);
        Pair pair3 = new Pair(new Player("Third",""), new Player("Player",""), true);
        Pair pair4 = new Pair(new Player("Fourth",""), new Player("Player",""), true);

        Map<Pair, Time> timeSlots = new HashMap<>();
        timeSlots.put(pair1, Time.SLOT_1);
        timeSlots.put(pair2, Time.SLOT_2);
        timeSlots.put(pair3, Time.SLOT_2);
        timeSlots.put(pair4, Time.SLOT_1);

        List<Pair> pairs = Arrays.asList(pair1, pair2, pair3, pair4);
        Scorecard scorecard = new Scorecard(pairs, null);
        List<Scorecard> scorecards = Collections.singletonList(scorecard);

        TimeSelection selector = new VrcTimeSelection();
        selector.distributePairs(scorecards, timeSlots);
        Time time = scorecard.getTimeSlot();
        Time expectedTime = Time.SLOT_1;
        Assert.assertEquals(expectedTime, time);

        timeSlots.put(pair4, Time.SLOT_2);
        selector.distributePairs(scorecards, timeSlots);
        time = scorecard.getTimeSlot();
        expectedTime = Time.SLOT_2;
        Assert.assertEquals(expectedTime, time);
    }

    @Test
    public void distributePairsCase_1() throws Exception {
        TimeSelection selector = new VrcTimeSelection();
        ScorecardGenerator generator = new VrcScorecardGenerator();

        Map<Pair, Time> timeSlots = new HashMap<>();

        List<Pair> pairs = setUpBig(timeSlots);
        List<Scorecard> scorecards =
                generator.generateScorecards(pairs);

        selector.distributePairs(scorecards, timeSlots);

        //Check logic when too many pairs are playing
        //It should distribute pairs equally between time slots
        checkLogic(selector, scorecards);
    }

    private List<Pair> setUpBig(Map<Pair, Time> timeSlots) throws Exception {
        List<Pair> pairs;

        try {
            pairs = createBigAmountPairs();
        } catch (Exception e) {
            throw e;
        }

        int count = 0;
        for (Pair pair : pairs) {
            if (count % 5 == 0) {
                timeSlots.put(pair, Time.SLOT_2);
            } else {
                timeSlots.put(pair, Time.NO_SLOT);
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

    @Ignore
    @Test
    public void distributePairsCase_2() throws Exception {
        TimeSelection selector = new VrcTimeSelection();
        ScorecardGenerator generator = new VrcScorecardGenerator();

        Map<Pair, Time> timeSlots = new HashMap<>();

        List<Pair> pairs = setUpSmall(timeSlots);

        List<Scorecard> scorecards =
                generator.generateScorecards(pairs);
        selector.distributePairs(scorecards, timeSlots);

        //One of the time slots was overflowed
        //Distributes extra pairs to other time slots
        checkLogic(selector, scorecards);
    }

    private List<Pair> setUpSmall(Map<Pair, Time> timeSlots) throws Exception {
        List<Pair> pairs;

        try {
            pairs = createSmallAmountPairs();
        } catch (Exception e) {
            throw e;
        }

        for (Pair pair : pairs) {
            timeSlots.put(pair, Time.SLOT_1);
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
}
