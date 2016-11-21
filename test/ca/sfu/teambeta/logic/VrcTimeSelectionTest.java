package ca.sfu.teambeta.logic;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.Time;

/**
 * Tests for the VrcTimeSelection class.
 */

public class VrcTimeSelectionTest {

    @Test
    public void test12GroupDistribution() throws Exception {
        List<Pair> pairs = new ArrayList<Pair>() {
            {
                add(new Pair(new Player("a", "b"), new Player("c", "d")));
                add(new Pair(new Player("e", "f"), new Player("g", "h")));
                add(new Pair(new Player("i", "j"), new Player("k", "l")));
            }
        };
        Map<Pair, Time> timeMap = new HashMap<>();
        for (Pair pair : pairs) {
            timeMap.put(pair, Time.SLOT_1);
        }

        List<Scorecard> scorecards = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            scorecards.add(new Scorecard(pairs, null));
        }

        TimeSelection selector = new VrcTimeSelection();
        selector.distributePairs(scorecards, timeMap);

        for (Scorecard scorecard : scorecards) {
            Assert.assertEquals(scorecard.getTimeSlot(), Time.SLOT_1);
        }
    }

    @Test
    public void test12GroupDistributionWithPreference() throws Exception {
        List<Pair> pairs = new ArrayList<Pair>() {
            {
                add(new Pair(new Player("a", "b"), new Player("c", "d")));
                add(new Pair(new Player("e", "f"), new Player("g", "h")));
                add(new Pair(new Player("i", "j"), new Player("k", "l")));
            }
        };

        List<Pair> slot1Pairs = new ArrayList<Pair>() {
            {
                add(new Pair(new Player("a", "b"), new Player("c", "d")));
                add(new Pair(new Player("e", "f"), new Player("g", "h")));
                add(new Pair(new Player("i", "j"), new Player("k", "l")));
            }
        };

        List<Pair> slot2Pairs = new ArrayList<Pair>() {
            {
                add(new Pair(new Player("a", "b"), new Player("c", "d")));
                add(new Pair(new Player("e", "f"), new Player("g", "h")));
                add(new Pair(new Player("i", "j"), new Player("k", "l")));
            }
        };

        Map<Pair, Time> timeMap = new HashMap<>();
        for (Pair pair : pairs) {
            timeMap.put(pair, Time.NO_SLOT);
        }
        for (Pair pair : slot1Pairs) {
            timeMap.put(pair, Time.SLOT_1);
        }
        for (Pair pair : slot2Pairs) {
            timeMap.put(pair, Time.SLOT_2);
        }

        Scorecard slot1Scorecard = new Scorecard(slot1Pairs, null);
        Scorecard slot2Scorecard = new Scorecard(slot2Pairs, null);

        List<Scorecard> scorecards = new ArrayList<>();
        scorecards.add(slot1Scorecard);
        scorecards.add(slot2Scorecard);

        for (int i = 0; i < 10; i++) {
            scorecards.add(new Scorecard(pairs, null));
        }

        TimeSelection selector = new VrcTimeSelection();
        selector.distributePairs(scorecards, timeMap);

        Assert.assertEquals(slot1Scorecard.getTimeSlot(), Time.SLOT_1);
        Assert.assertEquals(slot2Scorecard.getTimeSlot(), Time.SLOT_1);
    }

    @Test
    public void test13GroupDistribution() throws Exception {
        List<Pair> pairs = new ArrayList<Pair>() {
            {
                add(new Pair(new Player("a", "b"), new Player("c", "d")));
                add(new Pair(new Player("e", "f"), new Player("g", "h")));
                add(new Pair(new Player("i", "j"), new Player("k", "l")));
            }
        };
        Map<Pair, Time> timeMap = new HashMap<>();
        for (Pair pair : pairs) {
            timeMap.put(pair, Time.SLOT_1);
        }

        List<Scorecard> scorecards = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            scorecards.add(new Scorecard(pairs, null));
        }

        TimeSelection selector = new VrcTimeSelection();
        selector.distributePairs(scorecards, timeMap);

        int slot1Count = 0;
        int slot2Count = 0;
        for (Scorecard scorecard : scorecards) {
            Time time = scorecard.getTimeSlot();
            if (time == Time.SLOT_1) {
                slot1Count++;
            } else {
                slot2Count++;
            }
        }

        Assert.assertEquals(slot1Count, 7);
        Assert.assertEquals(slot2Count, 6);
    }

    @Test
    public void test13GroupDistributionWithPreference() throws Exception {
        List<Pair> pairs = new ArrayList<Pair>() {
            {
                add(new Pair(new Player("a", "b"), new Player("c", "d")));
                add(new Pair(new Player("e", "f"), new Player("g", "h")));
                add(new Pair(new Player("i", "j"), new Player("k", "l")));
            }
        };

        List<Pair> slot1Pairs = new ArrayList<Pair>() {
            {
                add(new Pair(new Player("a", "b"), new Player("c", "d")));
                add(new Pair(new Player("e", "f"), new Player("g", "h")));
                add(new Pair(new Player("i", "j"), new Player("k", "l")));
            }
        };

        List<Pair> slot2Pairs = new ArrayList<Pair>() {
            {
                add(new Pair(new Player("a", "b"), new Player("c", "d")));
                add(new Pair(new Player("e", "f"), new Player("g", "h")));
                add(new Pair(new Player("i", "j"), new Player("k", "l")));
            }
        };

        Map<Pair, Time> timeMap = new HashMap<>();
        for (Pair pair : pairs) {
            timeMap.put(pair, Time.NO_SLOT);
        }
        for (Pair pair : slot1Pairs) {
            timeMap.put(pair, Time.SLOT_1);
        }
        for (Pair pair : slot2Pairs) {
            timeMap.put(pair, Time.SLOT_2);
        }

        Scorecard slot1Scorecard = new Scorecard(slot1Pairs, null);
        Scorecard slot2Scorecard = new Scorecard(slot2Pairs, null);

        List<Scorecard> scorecards = new ArrayList<>();
        scorecards.add(slot1Scorecard);
        scorecards.add(slot2Scorecard);

        for (int i = 0; i < 11; i++) {
            scorecards.add(new Scorecard(pairs, null));
        }

        TimeSelection selector = new VrcTimeSelection();
        selector.distributePairs(scorecards, timeMap);

        Assert.assertEquals(slot1Scorecard.getTimeSlot(), Time.SLOT_1);
        Assert.assertEquals(slot2Scorecard.getTimeSlot(), Time.SLOT_2);
    }

    @Test
    public void testDynamicTimeslots() {

        List<Scorecard> scorecards = new ArrayList<>();
        Map<Pair, Time> timeMap = new HashMap<>();
        for (int i = 0; i < 15; i++) {
            List<Pair> pairs = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                String name = Integer.toString(i) + Integer.toString(j);
                Pair pair = new Pair(new Player(name, ""), new Player(name, ""));
                pairs.add(pair);
                timeMap.put(pair, Time.SLOT_1);
            }
            Scorecard scorecard = new Scorecard(pairs, null);
            scorecards.add(scorecard);
        }

        TimeSelection selector = new VrcTimeSelection();
        selector.distributePairs(scorecards, timeMap);

        int numOfFirstTimeslotScorecards = 0;
        int numOfSecondTimeslotScorecards = 0;
        for (Scorecard sc : scorecards) {
            if (sc.getTimeSlot().equals(Time.SLOT_1)) {
                numOfFirstTimeslotScorecards++;
            } else if (sc.getTimeSlot().equals(Time.SLOT_2)) {
                numOfSecondTimeslotScorecards++;
            }
        }

        Assert.assertEquals(numOfFirstTimeslotScorecards, 8);
        Assert.assertEquals(numOfSecondTimeslotScorecards, 5);
    }

}
