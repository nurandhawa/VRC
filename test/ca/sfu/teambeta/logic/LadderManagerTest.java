package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.Scorecard;
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
public class LadderManagerTest{

    List<Pair> pairList = new ArrayList<Pair>(){{
        add(new Pair(new Player(1, "David1"), new Player(2, "Dave1"), true));
        add(new Pair(new Player(3, "David1"), new Player(4, "Dave1"), false));
        add(new Pair(new Player(5, "David1"), new Player(6, "Dave1"), false));
        add(new Pair(new Player(7, "David1"), new Player(8, "Dave1"), true));
    }};

    @Test
    public void testFindActivePairs() {

        LadderManager ladderManager;
        ladderManager = new LadderManager(pairList);

        ArrayList<Pair> activePairs = new ArrayList<Pair>(){{
            add(new Pair(new Player(1, "David1"), new Player(2, "Dave1"), true));
            add(new Pair(new Player(7, "David1"), new Player(8, "Dave1"), true));
        }};

        Assert.assertEquals(ladderManager.getActivePairs(), activePairs);
    }

    @Test
    public void testAddPair(){
        LadderManager manager = new LadderManager();
        Pair pair1 = new Pair(new Player(1, "Kate"), new Player(2, "Nick"), true);
        Pair pair2 = new Pair(new Player(3, "Jim"), new Player(4, "Ryan"), true);
        Pair duplicatePair = new Pair(new Player(3, "Jim"), new Player(4, "Ryan"), true);
        List<Pair> expected = new ArrayList<>();
        expected.add(pair1);
        expected.add(pair2);

        Assert.assertEquals(manager.addNewPair(pair1), true);
        Assert.assertEquals(manager.addNewPair(pair2), true);
        Assert.assertEquals(manager.addNewPair(duplicatePair), false); //Such pair was already added
        List<Pair> ladder = manager.getLadder();

        Assert.assertEquals(ladder,expected);
    }

    @Test
    public void testSetIsPlaying(){
        LadderManager manager = new LadderManager(fakeDB());
        Pair repeatedPlayer = new Pair(new Player(15, "Jessica"), new Player(7, "Richard"), false); //Richard already is in game
        Pair uniquePair = new Pair(new Player(16, "Hannah"), new Player(17, "Kate"), false); // None of players are playing

        manager.addNewPair(repeatedPlayer);
        Assert.assertEquals(manager.setIsPlaying(repeatedPlayer), false); //This pair cannot play

        manager.addNewPair(uniquePair);
        Assert.assertEquals(manager.setIsPlaying(uniquePair), true);
    }

    /*
    //      NOTE:
    //Functions are unavailable as they are not public anymore
    //
    @Test
    public void testAbsentPenalties(){
        LadderManager manager = new LadderManager(fakeDB());

        manager.applyAbsentPenalty();

        List<Pair> passivePairs = manager.getPassivePairs();

        int[] actualPositions = new int[passivePairs.size()];
        for(int i = 0; i < passivePairs.size(); i++){
            actualPositions[i] = passivePairs.get(i).getPosition();
        }

        int[] expectedPositions = new int[]{3,4,6,7};

        Assert.assertArrayEquals(actualPositions, expectedPositions);
    }

    @Test
    public void testSplitAndCombine(){
        LadderManager manager = new LadderManager(fakeDB());

        manager.combine();

        List<Pair> ladder = manager.getLadder();
        for (Pair current : ladder){
            System.out.println(current);
        }
    }


    @Test
    public void  testMergePairs(){
        LadderManager manager = new LadderManager(fakeDB());

        manager.processLadder();
        List<Pair> ladder = manager.getLadder();

        for (Pair current : ladder){
            System.out.println(current);
        }
    }
    */

    private List<Pair> fakeDB(){
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