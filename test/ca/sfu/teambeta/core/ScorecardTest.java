package ca.sfu.teambeta.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Gordon Shieh on 26/05/16.
 */
public class ScorecardTest {
    private Pair victorJoyce = new Pair(new Player("Victor", ""), new Player("Joyce", ""));
    private Pair vickyDavid = new Pair(new Player("Vicky", ""), new Player("David", ""));
    private Pair rosStevie = new Pair(new Player("Ros", ""), new Player("Stevie", ""));
    private Pair bobbyChan = new Pair(new Player("Bobby", ""), new Player("Chan", ""));

    private List<Pair> threePairs = Arrays.asList(victorJoyce, vickyDavid, rosStevie);
    private List<Pair> fourPairs = Arrays.asList(victorJoyce, vickyDavid, rosStevie, bobbyChan);

    @Test
    public void testFirstExample() {
        Scorecard sc = new Scorecard(threePairs, null);
        sc.setGameResults(victorJoyce, 1);
        sc.setGameResults(rosStevie, 2);
        sc.setGameResults(vickyDavid, 3);

        List<Pair> reOrdered = sc.getReorderedPairs();
        List<Pair> expectedOrder = Arrays.asList(victorJoyce, rosStevie, vickyDavid);
        Assert.assertEquals(reOrdered, expectedOrder);
    }

    @Test
    public void testLastExample() {
        Arrays.asList(rosStevie, vickyDavid, victorJoyce);
        Scorecard sc = new Scorecard(threePairs, null);
        sc.setGameResults(rosStevie, 1);
        sc.setGameResults(victorJoyce, 2);
        sc.setGameResults(vickyDavid, 3);

        List<Pair> reOrdered = sc.getReorderedPairs();
        List<Pair> expectedOrder = Arrays.asList(rosStevie, victorJoyce, vickyDavid);
        Assert.assertEquals(reOrdered, expectedOrder);
    }

    @Test
    public void testQuadGroup() {
        List<Pair> fourPairGroup = Arrays.asList(rosStevie, vickyDavid, victorJoyce, bobbyChan);
        Scorecard sc = new Scorecard(fourPairGroup, null);
        sc.setGameResults(rosStevie, 1);
        sc.setGameResults(bobbyChan, 4);
        sc.setGameResults(victorJoyce, 2);
        sc.setGameResults(vickyDavid, 3);

        List<Pair> reOrdered = sc.getReorderedPairs();
        List<Pair> expectedOrder = Arrays.asList(rosStevie, victorJoyce, vickyDavid, bobbyChan);
        Assert.assertEquals(reOrdered, expectedOrder);
    }

}
