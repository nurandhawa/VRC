package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Penalty;
import ca.sfu.teambeta.core.Scorecard;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * Created by constantin on 30/06/16.
 */

public class LadderReordererTest {

    @Test
    public void testLogicFunctionality() {
        Pair p1 = new Pair(new Player("", "David", "PHONE"),
                new Player("", "Bob", "PHONE"), true);
        Pair p2 = new Pair(new Player("", "Richard", "PHONE"),
                new Player("", "Robin", "PHONE"), true);
        Pair p3 = new Pair(new Player("", "Kevin", "PHONE"),
                new Player("", "Jasmin", "PHONE"), true);
        List<Pair> pairs1 = Arrays.asList(p1, p2, p3);
        Scorecard sc1 = new Scorecard(pairs1, null);

        sc1.setGameResults(p1, p2);
        sc1.setGameResults(p3, p2);
        sc1.setGameResults(p3, p1);

        Pair p4 = new Pair(new Player("", "Tony", "PHONE"),
                new Player("", "Angelica", "PHONE"), true);

        Pair p5 = new Pair(new Player("", "Anastasia", "PHONE"),
                new Player("", "Victoria", "PHONE"), true);

        Pair p6 = new Pair(new Player("", "Ian", "PHONE"),
                new Player("", "Camden", "PHONE"), true);
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


        Pair p7 = new Pair(new Player("", "Kate", "PHONE"),
                new Player("", "Nick", "PHONE"), false);
        Pair p8 = new Pair(new Player("", "Jim", "PHONE"),
                new Player("", "Ryan", "PHONE"), false);
        Pair p9 = new Pair(new Player("", "Amy", "PHONE"),
                new Player("", "Maria", "PHONE"), false);

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

        Pair pair = new Pair(new Player("", "Kate", "PHONE"),
                new Player("", "Nick", "PHONE"), false);
        pair.setPosition(1);
        db.add(pair);

        pair = new Pair(new Player("", "Jim", "PHONE"),
                new Player("", "Ryan", "PHONE"), false);
        pair.setPosition(2);
        db.add(pair);

        pair = new Pair(new Player("", "David", "PHONE"),
                new Player("", "Bob", "PHONE"), true);
        pair.setPosition(3);
        pair.setPenalty(Penalty.MISSING.getPenalty());
        db.add(pair);

        pair = new Pair(new Player("", "Richard", "PHONE"),
                new Player("", "Robin", "PHONE"), true);
        pair.setPosition(4);
        pair.setPenalty(Penalty.LATE.getPenalty());
        db.add(pair);

        pair = new Pair(new Player("", "Kevin", "PHONE"),
                new Player("", "Jasmin", "PHONE"), true);
        pair.setPosition(5);
        db.add(pair);

        pair = new Pair(new Player("", "Amy", "PHONE"),
                new Player("", "Maria", "PHONE"), false);
        pair.setPosition(6);
        db.add(pair);

        pair = new Pair(new Player("", "Tony", "PHONE"),
                new Player("", "Angelica", "PHONE"), true);
        pair.setPosition(7);
        db.add(pair);

        pair = new Pair(new Player("", "Anastasia", "PHONE"),
                new Player("", "Victoria", "PHONE"), true);
        pair.setPosition(8);
        db.add(pair);

        pair = new Pair(new Player("", "Ian", "PHONE"),
                new Player("", "Camden", "PHONE"), true);
        pair.setPosition(9);
        db.add(pair);

        return db;
    }

    private List<Pair> processedFakeDB() {
        List<Pair> db = new ArrayList<>();

        Pair pair = new Pair(new Player("", "Kevin", "PHONE"),
                new Player("", "Jasmin", "PHONE"), false);
        pair.setPosition(1);
        db.add(pair);

        pair = new Pair(new Player("", "Kate", "PHONE"),
                new Player("", "Nick", "PHONE"), false);
        pair.setPosition(2);
        db.add(pair);

        pair = new Pair(new Player("", "Jim", "PHONE"),
                new Player("", "Ryan", "PHONE"), false);
        pair.setPosition(3);
        db.add(pair);

        pair = new Pair(new Player("", "Ian", "PHONE"),
                new Player("", "Camden", "PHONE"), false);
        pair.setPosition(4);
        db.add(pair);

        pair = new Pair(new Player("", "Tony", "PHONE"),
                new Player("", "Angelica", "PHONE"), false);
        pair.setPosition(5);
        db.add(pair);

        pair = new Pair(new Player("", "Amy", "PHONE"),
                new Player("", "Maria", "PHONE"), false);
        pair.setPosition(6);
        db.add(pair);

        pair = new Pair(new Player("", "Anastasia", "PHONE"),
                new Player("", "Victoria", "PHONE"), false);
        pair.setPosition(7);
        db.add(pair);

        pair = new Pair(new Player("", "Richard", "PHONE"),
                new Player("", "Robin", "PHONE"), false);
        pair.setPosition(8);
        db.add(pair);

        pair = new Pair(new Player("", "David", "PHONE"),
                new Player("", "Bob", "PHONE"), false);
        pair.setPosition(9);
        db.add(pair);

        return db;
    }
}
