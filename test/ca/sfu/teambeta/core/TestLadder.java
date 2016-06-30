package ca.sfu.teambeta.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel Kim on 6/4/2016.
 */
public class TestLadder {
    @Test
    public void doTest() {

        System.out.println();
        System.out.println("Testing Insert:");
        List<Pair> someList = new ArrayList<>();
        Ladder testLadder = new Ladder(someList);

        System.out.println("Adding pair at end Hughes/Mayes");
        Pair thisPair = new Pair(new Player("Hugh", "Jass"), new Player("Mayes", "Weather"));
        testLadder.insertAtEnd(thisPair);
        Assert.assertEquals(testLadder.getPairAtIndex(0), thisPair);
        System.out.println("Adding pair at end Joan/Mario");
        thisPair = new Pair(new Player("Princess", "Peach"), new Player("Mario", "Mario"));
        testLadder.insertAtEnd(thisPair);
        Assert.assertEquals(testLadder.getPairAtIndex(1), thisPair);
        System.out.println("Adding pair at end Luke/Heather");
        thisPair = new Pair(new Player("Luke", "Skywalker"), new Player("Heather", "Seawalker"));
        testLadder.insertAtEnd(thisPair);
        Assert.assertEquals(testLadder.getPairAtIndex(2), thisPair);
        System.out.println("Adding pair at index 1 Jack/Ethan");
        thisPair = new Pair(new Player("Jack", "Sparrow"), new Player("Daniel", "Damnn"));
        testLadder.insertAtIndex(1, thisPair);
        Assert.assertEquals(testLadder.getPairAtIndex(1), thisPair);
        Assert.assertEquals(testLadder.getLadderLength(), 4);

        System.out.println();
        for (Pair iterPair : testLadder.getPairs()) {
            System.out.println(iterPair.toString());
        }
        System.out.println();
        System.out.println("Testing Remove:");

        System.out.println("Removing pair at index 0 (position 1)");
        Pair pairToRemove = testLadder.getPairs().get(0);
        thisPair = testLadder.getPairs().get(1);
        testLadder.removePair(pairToRemove);
        Assert.assertEquals(thisPair, testLadder.getPairs().get(0));
        Assert.assertEquals(testLadder.getLadderLength(), 3);

        for (Pair iterPair : testLadder.getPairs()) {
            System.out.println(iterPair.toString());
        }
        System.out.println();

        System.out.println("Removing pair at index 1 (position 2)");
        pairToRemove = testLadder.getPairs().get(1);
        thisPair = testLadder.getPairs().get(2);
        testLadder.removePair(pairToRemove);
        Assert.assertEquals(thisPair, testLadder.getPairs().get(1));
        Assert.assertEquals(testLadder.getLadderLength(), 2);

        for (Pair iterPair : testLadder.getPairs()) {
            System.out.println(iterPair.toString());
        }
        System.out.println();

        System.out.println("Removing pair at index 0 (position 1)");
        pairToRemove = testLadder.getPairs().get(0);
        thisPair = testLadder.getPairs().get(1);
        testLadder.removePair(pairToRemove);
        Assert.assertEquals(thisPair, testLadder.getPairs().get(0));
        Assert.assertEquals(testLadder.getLadderLength(), 1);

        for (Pair iterPair : testLadder.getPairs()) {
            System.out.println(iterPair.toString());
        }
        System.out.println();
    }


}
