package ca.sfu.teambeta.logic;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Penalty;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;

/**
 * Created by constantin on 30/06/16.
 */

public class LadderReordererTest {

    @Test
    public void testLogicFunctionality() {
        Pair p1 = new Pair(new Player("", "David"),
                new Player("", "Bob"), true);
        Pair p2 = new Pair(new Player("", "Richard"),
                new Player("", "Robin"), true);
        Pair p3 = new Pair(new Player("", "Kevin"),
                new Player("", "Jasmin"), true);
        List<Pair> pairs1 = Arrays.asList(p1, p2, p3);
        Scorecard sc1 = new Scorecard(pairs1, null);

        sc1.setGameResults(p1, p2);
        sc1.setGameResults(p3, p2);
        sc1.setGameResults(p3, p1);

        Pair p4 = new Pair(new Player("", "Tony"),
                new Player("", "Angelica"), true);

        Pair p5 = new Pair(new Player("", "Anastasia"),
                new Player("", "Victoria"), true);

        Pair p6 = new Pair(new Player("", "Ian"),
                new Player("", "Camden"), true);
        List<Pair> pairs2 = Arrays.asList(p4, p5, p6);
        Scorecard sc2 = new Scorecard(pairs2, null);

        sc2.setGameResults(p4, p5);
        sc2.setGameResults(p6, p5);
        sc2.setGameResults(p6, p4);

        List<Scorecard> scorecards = new ArrayList<>();
        scorecards.add(sc1);
        scorecards.add(sc2);

        Set<Pair> activePairs = new HashSet<>();
        activePairs.addAll(pairs1);
        activePairs.addAll(pairs2);


        Pair p7 = new Pair(new Player("", "Kate"),
                new Player("", "Nick"), false);
        Pair p8 = new Pair(new Player("", "Jim"),
                new Player("", "Ryan"), false);
        Pair p9 = new Pair(new Player("", "Amy"),
                new Player("", "Maria"), false);

        Map<Pair, Penalty> penalties = new HashMap<>();
        penalties.put(p1, Penalty.MISSING);
        penalties.put(p2, Penalty.LATE);

        LadderReorderer ladderReorderer = new VrcLadderReorderer();
        List<Pair> afterProcessing = ladderReorderer.reorder(fakeDB(), scorecards, activePairs, penalties);

        Assert.assertEquals(afterProcessing, processedFakeDB());
    }

    private List<Pair> fakeDB() {
        List<Pair> db = new ArrayList<>();

        Pair pair = new Pair(new Player("", "Kate"),
                new Player("", "Nick"), false);
        db.add(pair);

        pair = new Pair(new Player("", "Jim"),
                new Player("", "Ryan"), false);
        db.add(pair);

        pair = new Pair(new Player("", "David"),
                new Player("", "Bob"), true);
        db.add(pair);

        pair = new Pair(new Player("", "Richard"),
                new Player("", "Robin"), true);
        db.add(pair);

        pair = new Pair(new Player("", "Kevin"),
                new Player("", "Jasmin"), true);
        db.add(pair);

        pair = new Pair(new Player("", "Amy"),
                new Player("", "Maria"), false);
        db.add(pair);

        pair = new Pair(new Player("", "Tony"),
                new Player("", "Angelica"), true);
        db.add(pair);

        pair = new Pair(new Player("", "Anastasia"),
                new Player("", "Victoria"), true);
        db.add(pair);

        pair = new Pair(new Player("", "Ian"),
                new Player("", "Camden"), true);
        db.add(pair);

        return db;
    }

    private List<Pair> processedFakeDB() {
        List<Pair> db = new ArrayList<>();

        Pair pair = new Pair(new Player("", "Kevin"),
                new Player("", "Jasmin"), false);
        db.add(pair);

        pair = new Pair(new Player("", "Kate"),
                new Player("", "Nick"), false);
        db.add(pair);

        pair = new Pair(new Player("", "Jim"),
                new Player("", "Ryan"), false);
        db.add(pair);

        pair = new Pair(new Player("", "Ian"),
                new Player("", "Camden"), false);
        db.add(pair);

        pair = new Pair(new Player("", "Tony"),
                new Player("", "Angelica"), false);
        db.add(pair);

        pair = new Pair(new Player("", "Amy"),
                new Player("", "Maria"), false);
        db.add(pair);

        pair = new Pair(new Player("", "Anastasia"),
                new Player("", "Victoria"), false);
        db.add(pair);

        pair = new Pair(new Player("", "Richard"),
                new Player("", "Robin"), false);
        db.add(pair);

        pair = new Pair(new Player("", "David"),
                new Player("", "Bob"), false);
        db.add(pair);

        return db;
    }
}
