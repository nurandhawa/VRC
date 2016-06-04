package ca.sfu.teambeta.logic;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Penalty;
import ca.sfu.teambeta.core.Player;

/**
 * Created by David Li on 30/05/16.
 */
public class LadderManagerTest {

    @Test
    public void testFindActivePairs() {

        LadderManager ladderManager = new LadderManager(testData());

        ArrayList<Pair> activePairs = new ArrayList<Pair>() {{
            add(new Pair(new Player(3, "Person 3"), new Player(4, "Person 4"), true));
            add(new Pair(new Player(7, "Person 7"), new Player(8, "Person 8"), true));
            add(new Pair(new Player(11, "Person 11"), new Player(12, "Person 12"), true));
            add(new Pair(new Player(15, "Person 15"), new Player(16, "Person 16"), true));
            add(new Pair(new Player(19, "Person 19"), new Player(20, "Person 20"), true));
        }};

        Assert.assertEquals(ladderManager.getActivePairs(), activePairs);
    }

    @Test
    public void testFindPassivePairs() {

        LadderManager ladderManager = new LadderManager(testData());

        ArrayList<Pair> passivePairs = new ArrayList<Pair>() {{
            add(new Pair(new Player(1, "Person 19"), new Player(2, "Person 20"), true));
            add(new Pair(new Player(5, "Person 5"), new Player(6, "Person 6"), true));
            add(new Pair(new Player(9, "Person 7"), new Player(10, "Person 9"), true));
            add(new Pair(new Player(13, "Person 11"), new Player(14, "Person 10"), true));
            add(new Pair(new Player(17, "Person 15"), new Player(18, "Person 16"), true));
        }};

        Assert.assertEquals(ladderManager.getPassivePairs(), passivePairs);
    }

//    @Test
//    public void testAddPair() {
//        LadderManager ladderManager = new LadderManager(testData());
//
//        Pair pair1 = new Pair(new Player(1, "Kate"), new Player(2, "Nick"), true);
//        Pair pair2 = new Pair(new Player(3, "Jim"), new Player(4, "Ryan"), true);
//        List<Pair> expected = new ArrayList<>();
//        expected.add(pair1);
//        expected.add(pair2);
//
//        manager.addNewPair(pair1);
//        manager.addNewPair(pair2);
//        List<Pair> ladder = manager.getFullLadder();
//
//        Assert.assertEquals(ladder, expected);
//    }

    @Test
    public void testApplyPenalties() {
        LadderManager ladderManager = new LadderManager(testData());
        ladderManager.setPenaltyToPair(1, "missing");
        ladderManager.setPenaltyToPair(2, "late");
        ladderManager.setPenaltyToPair(3, "missing");

        ladderManager.applyPenalties();
    }


    private List<Pair> testData() {
        List<Pair> ladder = new ArrayList<>();
        String playerOne;
        String playerTwo;
        boolean isPlaying;

        for (int i = 1; i <= 10; i++) {
            playerOne = "Person " + ((i * 2) - 1);
            playerTwo = "Person " + (i * 2);
            if(i % 2 == 0) {
                isPlaying = true;
            }
            else {
                isPlaying = false;
            }
            Pair pair = new Pair(new Player((i * 2) - 1, playerOne), new Player((i * 2), playerTwo), isPlaying);
            pair.setPosition(i);
            ladder.add(pair);
        }

        return ladder;
    }

    private List<Pair> fakeDB() {
        List<Pair> db = new ArrayList<>();

        Pair pair = new Pair(new Player(1, "Kate"), new Player(2, "Nick"), false);
        pair.setPosition(1);
        pair.setPenalty(Penalty.ABSENT.getPenalty());
        db.add(pair);

        pair = new Pair(new Player(3, "Jim"), new Player(4, "Ryan"), false);
        pair.setPosition(2);
        pair.setPenalty(Penalty.ABSENT.getPenalty());
        db.add(pair);

        pair = new Pair(new Player(5, "David"), new Player(6, "Bob"), true);
        pair.setPosition(3);
        //No penalty
        db.add(pair);

        pair = new Pair(new Player(7, "Richard"), new Player(8, "Robin"), true);
        pair.setPosition(4);
        //No penalty
        db.add(pair);

        pair = new Pair(new Player(9, "Kevin"), new Player(10, "Jasmin"), true);
        pair.setPosition(5);
        pair.setPenalty(Penalty.LATE.getPenalty());
        db.add(pair);

        pair = new Pair(new Player(11, "Amy"), new Player(12, "Maria"), false);
        pair.setPosition(6);
        pair.setPenalty(Penalty.ABSENT.getPenalty());
        db.add(pair);

        pair = new Pair(new Player(13, "Tony"), new Player(14, "Angelica"), false);
        pair.setPosition(7);
        pair.setPenalty(Penalty.ABSENT.getPenalty());
        db.add(pair);

        return db;
    }
}