package ca.sfu.teambeta.core;

import org.junit.Assert;
import org.junit.Ignore;
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
        sc.setGameResults(victorJoyce,vickyDavid);
        sc.setGameResults(victorJoyce,rosStevie);
        sc.setGameResults(rosStevie,vickyDavid);

        List<Pair> reOrdered = sc.getReorderedPairs();
        List<Pair> expectedOrder = Arrays.asList(victorJoyce, rosStevie, vickyDavid);
        Assert.assertEquals(reOrdered, expectedOrder);
    }

    @Test
    public void testLastExample() {
        Arrays.asList(rosStevie, vickyDavid, victorJoyce);
        Scorecard sc = new Scorecard(threePairs, null);
        sc.setGameResults(rosStevie,vickyDavid);
        sc.setGameResults(rosStevie,victorJoyce);
        sc.setGameResults(victorJoyce,vickyDavid);

        List<Pair> reOrdered = sc.getReorderedPairs();
        List<Pair> expectedOrder = Arrays.asList(rosStevie, victorJoyce, vickyDavid);
        Assert.assertEquals(reOrdered, expectedOrder);
    }

    @Test
    public void testQuadGroup() {
        List<Pair> fourPairGroup = Arrays.asList(rosStevie, vickyDavid, victorJoyce, bobbyChan);
        Scorecard sc = new Scorecard(fourPairGroup, null);
        sc.setGameResults(rosStevie,bobbyChan);
        sc.setGameResults(victorJoyce,vickyDavid);
        sc.setGameResults(rosStevie,victorJoyce);
        sc.setGameResults(vickyDavid,bobbyChan);

        List<Pair> reOrdered = sc.getReorderedPairs();
        List<Pair> expectedOrder = Arrays.asList(rosStevie, victorJoyce, vickyDavid, bobbyChan);
        Assert.assertEquals(reOrdered, expectedOrder);
    }

    @Test(expected = RuntimeException.class)
    @Ignore
    public void testObserverThreeTeams() {
        Observer observer = () -> {
            throw new RuntimeException();
        };
        Scorecard sc = new Scorecard(threePairs, observer);
        sc.setGameResults(victorJoyce, vickyDavid);
        sc.setGameResults(rosStevie, vickyDavid);
        sc.setGameResults(rosStevie, victorJoyce);
    }

    @Test(expected = RuntimeException.class)
    @Ignore
    public void testObserverFourTeams() {
        Observer observer = () -> {
            throw new RuntimeException();
        };
        Scorecard sc = new Scorecard(threePairs, observer);
        sc.setGameResults(victorJoyce, vickyDavid);
        sc.setGameResults(victorJoyce, rosStevie);
        sc.setGameResults(bobbyChan, vickyDavid);
        sc.setGameResults(rosStevie, bobbyChan);
    }
}
