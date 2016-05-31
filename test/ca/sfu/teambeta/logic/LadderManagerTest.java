package ca.sfu.teambeta.core;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by David Li on 30/05/16.
 */
public class LadderManagerTest extends TestCase{

    List<Pair> pairList = new ArrayList<>(){{
        add(new Pair(new Player(1, "David1"), new Player(2, "Dave1"), true));
        add(new Pair(new Player(3, "David1"), new Player(4, "Dave1"), false));
        add(new Pair(new Player(5, "David1"), new Player(6, "Dave1"), false));
        add(new Pair(new Player(7, "David1"), new Player(8, "Dave1"), true));
    }};

    @Test
    public void testFindActivePairs() {

        LadderManager ladderManager;
        ladderManager = new LadderManager();
        ladderManager.init(pairList);

        List<Pair> activePairs = new ArrayList(){{
            add(new Pair(new Player(1, "David1"), new Player(2, "Dave1"), true));
            add(new Pair(new Player(7, "David1"), new Player(8, "Dave1"), true));
        }};

        Assert.assertEquals(ladderManager.getActivePlayers(), activePairs);
    }
    @Test
    public void testFindPassivePlayers() {

    }
}