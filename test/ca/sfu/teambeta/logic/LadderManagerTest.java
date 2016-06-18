package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by David Li on 30/05/16.
 */
public class LadderManagerTest {

    @Test
    public void testFindActivePairs() {

        LadderManager ladderManager = new LadderManager(testData());

        List<Pair> expectedActivePairs = Arrays.asList(
                new Pair(new Player(3, "P3"), new Player(4, "P4"), true),
                new Pair(new Player(7, "P7"), new Player(8, "P8"), true),
                new Pair(new Player(11, "P11"), new Player(12, "P12"), true),
                new Pair(new Player(15, "P15"), new Player(16, "P16"), true),
                new Pair(new Player(19, "P19"), new Player(20, "P20"), true)
        );

        int position = 2;
        for (Pair p : expectedActivePairs) {
            p.setPosition(position);
            position += 2;
        }

        Assert.assertEquals(expectedActivePairs, ladderManager.getActivePairs());
    }

    @Test
    public void testFindPassivePairs() {

        LadderManager ladderManager = new LadderManager(testData());

        List<Pair> expectedPassivePairs = Arrays.asList(
                new Pair(new Player(1, "P1"), new Player(2, "P2"), false),
                new Pair(new Player(5, "P5"), new Player(6, "P6"), false),
                new Pair(new Player(9, "P9"), new Player(10, "P10"), false),
                new Pair(new Player(13, "P13"), new Player(14, "P14"), false),
                new Pair(new Player(17, "P17"), new Player(18, "P18"), false)
        );

        int position = 1;
        for (Pair p : expectedPassivePairs) {
            p.setPosition(position);
            position += 2;
        }

        Assert.assertEquals(expectedPassivePairs, ladderManager.getPassivePairs());
    }

    @Test
    public void testAddPair() {
        LadderManager ladderManager = new LadderManager(testData());
        ladderManager.addNewPair(new Pair(new Player(21, "P21"), new Player(22, "P22")));

        List<Pair> expectedLadder = Arrays.asList(
                new Pair(new Player(1, "P1"), new Player(2, "P2"), false),
                new Pair(new Player(3, "P3"), new Player(4, "P4"), true),
                new Pair(new Player(5, "P5"), new Player(6, "P6"), false),
                new Pair(new Player(7, "P7"), new Player(8, "P8"), true),
                new Pair(new Player(9, "P9"), new Player(10, "P10"), false),
                new Pair(new Player(11, "P11"), new Player(12, "P12"), true),
                new Pair(new Player(13, "P13"), new Player(14, "P14"), false),
                new Pair(new Player(15, "P15"), new Player(16, "P16"), true),
                new Pair(new Player(17, "P17"), new Player(18, "P18"), false),
                new Pair(new Player(19, "P19"), new Player(20, "P20"), true),
                new Pair(new Player(21, "P21"), new Player(22, "P22"), true)
        );

        int position = 0;
        for (Pair p : expectedLadder) {
            position++;
            p.setPosition(position);
        }

        Assert.assertEquals(expectedLadder, ladderManager.getLadder());
    }

    @Test
    public void testAddNewPairAtIndex() {
        LadderManager ladderManager = new LadderManager(testData());
        int newPairPosition = 2;

        ladderManager.addNewPairAtIndex(new Pair(new Player(21, "P21"), new Player(22, "P22"), true), 1);

        Assert.assertEquals(ladderManager.getLadder().get(1).getPosition(), newPairPosition);
    }

    private List<Pair> testData() {
        List<Pair> ladder = new ArrayList<>();
        String playerOne;
        String playerTwo;
        boolean isPlaying;

        for (int i = 1; i <= 10; i++) {
            playerOne = "P" + ((i * 2) - 1);
            playerTwo = "P" + (i * 2);

            if (i % 2 == 0) {
                isPlaying = true;
            } else {
                isPlaying = false;
            }

            Pair pair = new Pair(new Player((i * 2) - 1, playerOne), new Player((i * 2), playerTwo), isPlaying);
            pair.setPosition(i);
            ladder.add(pair);
        }

        return ladder;
    }

    @Test
    public void testLogicFunctionality() {
        LadderManager manager = new LadderManager(fakeDB());
        List<Pair> activePairs = manager.getActivePairs();
        Pair pair1 = activePairs.get(0);
        Pair pair2 = activePairs.get(1);
        Pair pair3 = activePairs.get(2);
        Pair pair4 = activePairs.get(3);
        Pair pair5 = activePairs.get(4);
        Pair pair6 = activePairs.get(5);

        List<Pair> group_1 = new ArrayList<>();
        List<Pair> group_2 = new ArrayList<>();
        group_1.add(pair1);
        group_1.add(pair2);
        group_1.add(pair3);
        group_2.add(pair4);
        group_2.add(pair5);
        group_2.add(pair6);

        Scorecard<Pair> scorecards1 = new Scorecard<>(group_1, null);
        scorecards1.setWin(pair3, 0);
        scorecards1.setWin(pair3, 1);

        scorecards1.setLose(pair2, 0);
        scorecards1.setLose(pair2, 2);

        scorecards1.setWin(pair1, 0);
        scorecards1.setWin(pair1, 1);
        scorecards1.setLose(pair1, 2);

        Scorecard<Pair> scorecards2 = new Scorecard<>(group_2, null);
        scorecards2.setWin(pair6, 0);
        scorecards2.setWin(pair6, 1);

        scorecards2.setLose(pair5, 0);
        scorecards2.setLose(pair5, 2);

        scorecards2.setWin(pair4, 0);
        scorecards2.setWin(pair4, 1);
        scorecards2.setLose(pair4, 2);

        List<Scorecard<Pair>> cards = new ArrayList<>();
        cards.add(scorecards1);
        cards.add(scorecards2);

        manager.processLadder(cards);
        List<Pair> afterProcessing = manager.getLadder();

        Assert.assertEquals(afterProcessing, processedFakeDB());
    }

    private List<Pair> fakeDB() {
        List<Pair> db = new ArrayList<>();

        Pair pair = new Pair(new Player(1, "Kate"), new Player(2, "Nick"), false);
        pair.setPosition(1);
        db.add(pair);

        pair = new Pair(new Player(3, "Jim"), new Player(4, "Ryan"), false);
        pair.setPosition(2);
        db.add(pair);

        pair = new Pair(new Player(5, "David"), new Player(6, "Bob"), true); //PLAY
        pair.setPosition(3);
        pair.setPenalty(Penalty.MISSING.getPenalty());
        db.add(pair);

        pair = new Pair(new Player(7, "Richard"), new Player(8, "Robin"), true); //PLAY
        pair.setPosition(4);
        pair.setPenalty(Penalty.LATE.getPenalty());
        db.add(pair);

        pair = new Pair(new Player(9, "Kevin"), new Player(10, "Jasmin"), true); //PLAY
        pair.setPosition(5);
        db.add(pair);

        pair = new Pair(new Player(11, "Amy"), new Player(12, "Maria"), false);
        pair.setPosition(6);
        db.add(pair);

        pair = new Pair(new Player(13, "Tony"), new Player(14, "Angelica"), true); //PLAY
        pair.setPosition(7);
        db.add(pair);

        pair = new Pair(new Player(15, "Anastasia"), new Player(16, "Victoria"), true); //PLAY
        pair.setPosition(8);
        db.add(pair);

        pair = new Pair(new Player(17, "Ian"), new Player(18, "Camden"), true); //PLAY
        pair.setPosition(9);
        db.add(pair);

        return db;
    }

    private List<Pair> processedFakeDB() {
        List<Pair> db = new ArrayList<>();

        Pair pair = new Pair(new Player(9, "Kevin"), new Player(10, "Jasmin"), false);
        pair.setPosition(1);
        db.add(pair);

        pair = new Pair(new Player(1, "Kate"), new Player(2, "Nick"), false);
        pair.setPosition(2);
        db.add(pair);

        pair = new Pair(new Player(3, "Jim"), new Player(4, "Ryan"), false);
        pair.setPosition(3);
        db.add(pair);

        pair = new Pair(new Player(17, "Ian"), new Player(18, "Camden"), false);
        pair.setPosition(4);
        db.add(pair);

        pair = new Pair(new Player(13, "Tony"), new Player(14, "Angelica"), false);
        pair.setPosition(5);
        db.add(pair);

        pair = new Pair(new Player(11, "Amy"), new Player(12, "Maria"), false);
        pair.setPosition(6);
        db.add(pair);

        pair = new Pair(new Player(15, "Anastasia"), new Player(16, "Victoria"), false);
        pair.setPosition(7);
        db.add(pair);

        pair = new Pair(new Player(7, "Richard"), new Player(8, "Robin"), false);
        pair.setPosition(8);
        db.add(pair);

        pair = new Pair(new Player(5, "David"), new Player(6, "Bob"), false);
        pair.setPosition(9);
        db.add(pair);

        return db;
    }
}