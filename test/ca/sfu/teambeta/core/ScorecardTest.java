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
        ScorecardAdapter sc = new ScorecardAdapter(threePairs, null);
        sc.setWin(victorJoyce, 0);
        sc.setWin(victorJoyce, 1);

        sc.setLose(vickyDavid, 0);
        sc.setLose(vickyDavid, 2);

        sc.setWin(rosStevie, 1);
        sc.setLose(rosStevie, 2);

        List<Pair> reOrdered = sc.getTeamRankings();
        List<Pair> expectedOrder = Arrays.asList(victorJoyce, rosStevie, vickyDavid);
        Assert.assertEquals(reOrdered, expectedOrder);
    }

    @Test
    public void testLastExample() {
        Arrays.asList(rosStevie, vickyDavid, victorJoyce);
        ScorecardAdapter sc = new ScorecardAdapter(threePairs, null);
        sc.setWin(rosStevie, 0);
        sc.setWin(rosStevie, 1);

        sc.setLose(vickyDavid, 0);
        sc.setLose(vickyDavid, 2);

        sc.setWin(rosStevie, 0);
        sc.setWin(rosStevie, 1);
        sc.setLose(rosStevie, 2);

        List<Pair> reOrdered = sc.getTeamRankings();
        List<Pair> expectedOrder = Arrays.asList(rosStevie, victorJoyce, vickyDavid);
        Assert.assertEquals(reOrdered, expectedOrder);
    }

    @Test(expected = RuntimeException.class)
    public void testObserverThreeTeams() {
        Observer observer = () -> {
            throw new RuntimeException();
        };
        ScorecardAdapter sc = new ScorecardAdapter(threePairs, observer);
        sc.setWin(victorJoyce, 0);
        sc.setLose(victorJoyce, 1);

        sc.setLose(vickyDavid, 0);
        sc.setLose(vickyDavid, 2);

        sc.setWin(rosStevie, 2);
        sc.setWin(rosStevie, 1);
    }

    @Test(expected = RuntimeException.class)
    public void testObserverFourTeams() {
        Observer observer = () -> {
            throw new RuntimeException();
        };
        ScorecardAdapter sc = new ScorecardAdapter(threePairs, observer);
        sc.setWin(victorJoyce, 0);
        sc.setLose(vickyDavid, 0);

        sc.setWin(victorJoyce, 1);
        sc.setLose(rosStevie, 1);

        sc.setWin(bobbyChan, 2);
        sc.setLose(vickyDavid, 2);

        sc.setWin(rosStevie, 3);
        sc.setLose(bobbyChan, 3);
    }
}
