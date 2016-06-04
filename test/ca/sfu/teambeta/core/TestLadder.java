package ca.sfu.teambeta.core;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Samuel Kim on 6/4/2016.
 */
public class TestLadder {
    @Test
    public void doTest(){

        System.out.println();
        System.out.println("Testing Insert:");
        List<Pair> someList = new ArrayList<Pair>();
            Ladder testLadder = new Ladder(someList);

        System.out.println("Adding pair at end Hughes/Mayes");
        testLadder.insertAtEnd(new Pair(new Player(0, "Hughes"), new Player( 1, "Mayes")));
        System.out.println("Adding pair at end Joan/Mario");
        testLadder.insertAtEnd(new Pair(new Player(2, "Joan"), new Player( 3, "Mario")));
        System.out.println("Adding pair at end Luke/Heather");
        testLadder.insertAtEnd(new Pair(new Player(4, "Luke"), new Player( 5, "Heather")));
        System.out.println("Adding pair at index 1 Jack/Ethan");
        testLadder.insertAtIndex(1, new Pair(new Player(6, "Jack"), new Player(7, "Ethan")));

        System.out.println();
        for(Pair thisPair:testLadder.getLadder()){
            System.out.println(thisPair.toString());
        }
        System.out.println();
        System.out.println("Testing Remove:");

        System.out.println("Removing pair at index 0 (position 1)");
        Pair pairToRemove = testLadder.getLadder().get(0);
        testLadder.removePair(pairToRemove);

        for(Pair thisPair:testLadder.getLadder()){
            System.out.println(thisPair.toString());
        }
        System.out.println();

        System.out.println("Removing pair at index 1 (position 2)");
        pairToRemove = testLadder.getLadder().get(1);
        testLadder.removePair(pairToRemove);

        for(Pair thisPair:testLadder.getLadder()){
            System.out.println(thisPair.toString());
        }
        System.out.println();

        System.out.println("Removing pair at index 0 (position 1)");
        pairToRemove = testLadder.getLadder().get(0);
        testLadder.removePair(pairToRemove);

        for(Pair thisPair:testLadder.getLadder()){
            System.out.println(thisPair.toString());
        }
        System.out.println();
    }



}
