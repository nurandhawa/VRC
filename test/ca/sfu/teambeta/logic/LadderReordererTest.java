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
    @Ignore("Test is broken since only two pairs are active")
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
        List<Pair> pairs2 = Arrays.asList(p1, p2, p3);
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
        penalties.put(p7, Penalty.ABSENT);
        penalties.put(p8, Penalty.ABSENT);
        penalties.put(p9, Penalty.ABSENT);


        LadderReorderer ladderReorderer = new VrcLadderReorderer();
        List<Pair> afterProcessing = ladderReorderer.reorder(fakeDB(), scorecards, activePairs, penalties);

        Assert.assertEquals(afterProcessing, processedFakeDB());
    }

    private List<Pair> fakeDB() {
        List<Pair> db = new ArrayList<>();

        Pair pair = new Pair(new Player("", "Kate"),
                new Player("", "Nick"), false);
        pair.setPosition(1);
        db.add(pair);

        pair = new Pair(new Player("", "Jim"),
                new Player("", "Ryan"), false);
        pair.setPosition(2);
        db.add(pair);

        pair = new Pair(new Player("", "David"),
                new Player("", "Bob"), true);
        pair.setPosition(3);
        pair.setPenalty(Penalty.MISSING.getPenalty());
        db.add(pair);

        pair = new Pair(new Player("", "Richard"),
                new Player("", "Robin"), true);
        pair.setPosition(4);
        pair.setPenalty(Penalty.LATE.getPenalty());
        db.add(pair);

        pair = new Pair(new Player("", "Kevin"),
                new Player("", "Jasmin"), true);
        pair.setPosition(5);
        db.add(pair);

        pair = new Pair(new Player("", "Amy"),
                new Player("", "Maria"), false);
        pair.setPosition(6);
        db.add(pair);

        pair = new Pair(new Player("", "Tony"),
                new Player("", "Angelica"), true);
        pair.setPosition(7);
        db.add(pair);

        pair = new Pair(new Player("", "Anastasia"),
                new Player("", "Victoria"), true);
        pair.setPosition(8);
        db.add(pair);

        pair = new Pair(new Player("", "Ian"),
                new Player("", "Camden"), true);
        pair.setPosition(9);
        db.add(pair);

        return db;
    }

    private List<Pair> processedFakeDB() {
        List<Pair> db = new ArrayList<>();

        Pair pair = new Pair(new Player("", "Kevin"),
                new Player("", "Jasmin"), false);
        pair.setPosition(1);
        db.add(pair);

        pair = new Pair(new Player("", "Kate"),
                new Player("", "Nick"), false);
        pair.setPosition(2);
        db.add(pair);

        pair = new Pair(new Player("", "Jim"),
                new Player("", "Ryan"), false);
        pair.setPosition(3);
        db.add(pair);

        pair = new Pair(new Player("", "Ian"),
                new Player("", "Camden"), false);
        pair.setPosition(4);
        db.add(pair);

        pair = new Pair(new Player("", "Tony"),
                new Player("", "Angelica"), false);
        pair.setPosition(5);
        db.add(pair);

        pair = new Pair(new Player("", "Amy"),
                new Player("", "Maria"), false);
        pair.setPosition(6);
        db.add(pair);

        pair = new Pair(new Player("", "Anastasia"),
                new Player("", "Victoria"), false);
        pair.setPosition(7);
        db.add(pair);

        pair = new Pair(new Player("", "Richard"),
                new Player("", "Robin"), false);
        pair.setPosition(8);
        db.add(pair);

        pair = new Pair(new Player("", "David"),
                new Player("", "Bob"), false);
        pair.setPosition(9);
        db.add(pair);

        return db;
    }
}
