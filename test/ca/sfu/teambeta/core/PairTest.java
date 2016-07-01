package ca.sfu.teambeta.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PairTest {
    @Test
    public void testEquals() {
        Player firstPlayer = new Player("Test", "A");
        Player secondPlayer = new Player("Test", "B");

        Pair firstPair = new Pair(firstPlayer, secondPlayer);
        Pair secondPair = new Pair(secondPlayer, firstPlayer);

        assertEquals(firstPair, secondPair);
    }
}
