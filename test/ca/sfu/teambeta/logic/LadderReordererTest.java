package ca.sfu.teambeta.logic;

import org.junit.Assert;
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
    private Pair kateNick = new Pair(new Player("", "Kate"),
            new Player("", "Nick"), false);

    private Pair jimRyan = new Pair(new Player("", "Jim"),
            new Player("", "Ryan"), false);

    private Pair davidBob = new Pair(new Player("", "David"),
            new Player("", "Bob"), true);

    private Pair richardRobin = new Pair(new Player("", "Richard"),
            new Player("", "Robin"), true);

    private Pair kevinJasmin = new Pair(new Player("", "Kevin"),
            new Player("", "Jasmin"), true);

    private Pair amyMaria = new Pair(new Player("", "Amy"),
            new Player("", "Maria"), false);

    private Pair tonyAngelica = new Pair(new Player("", "Tony"),
            new Player("", "Angelica"), true);

    private Pair anastasiaVictoria = new Pair(new Player("", "Anastasia"),
            new Player("", "Victoria"), true);

    private Pair ianCamden = new Pair(new Player("", "Ian"),
            new Player("", "Camden"), true);

    private List<Pair> originalList = new ArrayList<>(Arrays.asList(kateNick, jimRyan, davidBob, richardRobin,
            kevinJasmin, amyMaria, tonyAngelica, anastasiaVictoria, ianCamden));

    private List<Pair> reorderedLadder = new ArrayList<>(Arrays.asList(kevinJasmin, kateNick, jimRyan, ianCamden,
            tonyAngelica, amyMaria, anastasiaVictoria, richardRobin, davidBob));

    @Test
    public void testLogicFunctionality() {
        List<Pair> pairs1 = Arrays.asList(davidBob, richardRobin, kevinJasmin);
        Scorecard sc1 = new Scorecard(pairs1, null);

        sc1.setGameResults(davidBob, 2);
        sc1.setGameResults(kevinJasmin, 1);
        sc1.setGameResults(richardRobin, 3);

        List<Pair> pairs2 = Arrays.asList(tonyAngelica, anastasiaVictoria, ianCamden);
        Scorecard sc2 = new Scorecard(pairs2, null);

        sc2.setGameResults(tonyAngelica, 2);
        sc2.setGameResults(ianCamden, 1);
        sc2.setGameResults(anastasiaVictoria, 3);

        List<Scorecard> scorecards = new ArrayList<>();
        scorecards.add(sc1);
        scorecards.add(sc2);

        Set<Pair> activePairs = new HashSet<>();
        activePairs.addAll(pairs1);
        activePairs.addAll(pairs2);

        Map<Pair, Penalty> penalties = new HashMap<>();
        penalties.put(davidBob, Penalty.MISSING);
        penalties.put(richardRobin, Penalty.LATE);

        LadderReorderer ladderReorderer = new VrcLadderReorderer();
        List<Pair> afterProcessing = ladderReorderer.reorder(
                originalList, scorecards, activePairs, penalties);

        Assert.assertEquals(afterProcessing, reorderedLadder);
    }

}
