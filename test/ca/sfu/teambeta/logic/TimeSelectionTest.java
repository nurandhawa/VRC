package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by constantin on 11/07/16.
 */
public class TimeSelectionTest {
    @Test
    public void getPairsByTime() {
        Player firstPlayer = new Player("Kate", "Smith");
        Player secondPlayer = new Player("Nick", "Smith");
        Pair pair1 = new Pair(firstPlayer, secondPlayer, true);

        firstPlayer = new Player("Anna", "Fraser");
        secondPlayer = new Player("Camden", "Fraser");
        Pair pair2 = new Pair(firstPlayer, secondPlayer, true);

        TimeSelection selector = new VrcTimeSelection();
        pair1.setTimeSlot(Time.TH_8_30);
        pair2.setTimeSlot(Time.TH_9_00);
        List<Pair> activePairs = new ArrayList<Pair>() {
            {
                add(pair1);
                add(pair2);
            }
        };

        List<Pair> pairsFirstTimeSlot = selector.getPairsByTime(activePairs, Time.TH_8_30);
        List<Pair> pairsSecondTimeSlot = selector.getPairsByTime(activePairs, Time.TH_9_00);

        List<Pair> expectedFirstTimeSlot = new ArrayList<Pair>() {
            {
                add(pair1);
            }
        };


        List<Pair> expectedSecondTimeSlot = new ArrayList<Pair>() {
            {
                add(pair2);
            }
        };

        Assert.assertEquals(pairsFirstTimeSlot, expectedFirstTimeSlot);
        Assert.assertEquals(pairsSecondTimeSlot, expectedSecondTimeSlot);
    }

    @Test
    public void clearTimeSlots(){
        TimeSelection selector = new VrcTimeSelection();

        Player firstPlayer = new Player("Kate", "Smith");
        Player secondPlayer = new Player("Nick", "Smith");
        Pair pair1 = new Pair(firstPlayer, secondPlayer, true);
        pair1.setTimeSlot(Time.TH_8_30);

        firstPlayer = new Player("Anna", "Fraser");
        secondPlayer = new Player("Camden", "Fraser");
        Pair pair2 = new Pair(firstPlayer, secondPlayer, true);
        pair2.setTimeSlot(Time.TH_8_30);

        Ladder ladder = new Ladder();
        ladder.insertAtEnd(pair1);
        ladder.insertAtEnd(pair2);

        selector.clearTimeSlots(ladder);
        int size = selector.getAmountPairsByTime(ladder.getPairs(), Time.TH_8_30);
        Assert.assertEquals(size, 0);
    }

    @Test
    public void checkCommonTimeForGroup() {
        TimeSelection selector = new VrcTimeSelection();

        Player firstPlayer = new Player("Kate", "Smith");
        Player secondPlayer = new Player("Nick", "Smith");
        Pair pair1 = new Pair(firstPlayer, secondPlayer, true);
        pair1.setTimeSlot(Time.TH_8_30);

        firstPlayer = new Player("Anna", "Fraser");
        secondPlayer = new Player("Camden", "Fraser");
        Pair pair2 = new Pair(firstPlayer, secondPlayer, true);
        pair2.setTimeSlot(Time.TH_9_00);

        firstPlayer = new Player("Emma", "Johnson");
        secondPlayer = new Player("William", "Johnson");
        Pair pair3 = new Pair(firstPlayer, secondPlayer, true);
        pair3.setTimeSlot(Time.TH_8_30);

        List<Pair> pairs = new ArrayList<Pair>(){
            {
                add(pair1);
                add(pair2);
                add(pair3);
            }
        };
        Scorecard scorecard = new Scorecard(pairs, null);
        List<Scorecard> scorecards = new ArrayList<Scorecard>(){
            {
                add(scorecard);
            }
        };

        selector.distributePairs(scorecards);
        Time time = scorecard.getTimeSlot();
        Time expectedTime = Time.TH_8_30;

        Assert.assertEquals(expectedTime, time);
    }
}
