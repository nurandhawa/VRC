package ca.sfu.teambeta.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Gordon Shieh on 26/05/16.
 */
public class ScorecardTest {
    @Test
    public void testFirstExample() {
        List<String> teams = Arrays.asList("Victor Joyce", "Vicky David", "Ros Stevie");
        Scorecard<String> sc = new Scorecard<>(teams, null);
        sc.setWin("Victor Joyce", 0);
        sc.setWin("Victor Joyce", 1);

        sc.setLose("Vicky David", 0);
        sc.setLose("Vicky David", 2);

        sc.setWin("Ros Stevie", 1);
        sc.setLose("Ros Stevie", 2);

        List<String> reOrdered = sc.getTeamRankings();
        List<String> expectedOrder = Arrays.asList("Victor Joyce", "Ros Stevie", "Vicky David");
        Assert.assertEquals(reOrdered, expectedOrder);
    }

    @Test
    public void testLastExample() {
        List<String> teams = Arrays.asList("Jerome Karen", "Peter Aby", "Ben Katrina");
        Scorecard<String> sc = new Scorecard<>(teams, null);
        sc.setWin("Ben Katrina", 0);
        sc.setWin("Ben Katrina", 1);

        sc.setLose("Peter Aby", 0);
        sc.setLose("Peter Aby", 2);

        sc.setWin("Jerome Karen", 0);
        sc.setWin("Jerome Karen", 1);
        sc.setLose("Jerome Karen", 2);

        List<String> reOrdered = sc.getTeamRankings();
        List<String> expectedOrder = Arrays.asList("Ben Katrina", "Jerome Karen", "Peter Aby");
        Assert.assertEquals(reOrdered, expectedOrder);
    }

    @Test(expected = RuntimeException.class)
    public void testObserverThreeTeams() {
        List<String> teams = Arrays.asList("Jerome Karen", "Peter Aby", "Ben Katrina");
        Observer observer = () -> {
            throw new RuntimeException();
        };
        Scorecard<String> sc = new Scorecard<>(teams, observer);
        sc.setWin("Ben Katrina", 0);
        sc.setWin("Ben Katrina", 1);

        sc.setLose("Peter Aby", 0);
        sc.setLose("Peter Aby", 2);

        sc.setWin("Jerome Karen", 0);
        sc.setWin("Jerome Karen", 1);
    }

    @Test(expected = RuntimeException.class)
    public void testObserverFourTeams() {
        List<String> teams = Arrays.asList(
                "Jerome Karen", "Peter Aby", "Ben Katrina", "Bobby Chan");
        Observer observer = () -> {
            throw new RuntimeException();
        };
        Scorecard<String> sc = new Scorecard<>(teams, observer);
        sc.setWin("Ben Katrina", 0);
        sc.setLose("Peter Aby", 0);

        sc.setWin("Ben Katrina", 1);
        sc.setLose("Jerome Karen", 1);

        sc.setWin("Bobby Chan", 2);
        sc.setLose("Peter Aby", 2);

        sc.setWin("Jerome Karen", 3);
        sc.setLose("Bobby Chan", 3);
    }
}
