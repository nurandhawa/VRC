package ca.sfu.teambeta.logic;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Scorecard;

/**
 * Created by Gordon Shieh on 25/06/16.
 */
public class VrcScorecardGenerator implements ScorecardGenerator {
    @Override
    public List<Scorecard> generateScorecards(List<Pair> activePairs) {
        int playingCount = activePairs.size();

        List<Scorecard> scorecards = new ArrayList<>();

        if (playingCount % 3 == 0) {
            //All 3 team groups.
            int noOfTripleGroups = playingCount / 3;
            makeTripleGroups(scorecards, noOfTripleGroups, activePairs);
        } else if (playingCount % 3 == 1) {
            //One 4 team group.
            int noOftripleGroups = playingCount / 3 - 1;
            int currentIndex = makeTripleGroups(scorecards, noOftripleGroups, activePairs);
            makeQuadGroup(scorecards, currentIndex, activePairs);
        } else {
            //Two 4 team groups.
            int noOftripleGroups = playingCount / 3 - 2;
            int currentIndex = makeTripleGroups(scorecards, noOftripleGroups, activePairs);
            makeQuadGroup(scorecards, currentIndex, activePairs);
        }
        return scorecards;
    }

    private void makeQuadGroup(List<Scorecard> scorecards, int num, List<Pair> activePairs) {
        List<Pair> groupings = new ArrayList<>();
        for (int i = num; i < activePairs.size(); i++) {
            groupings.add(activePairs.get(i));

            if (groupings.size() == 4) {
                Scorecard sc = new Scorecard(groupings, null);
                scorecards.add(sc);
                groupings.clear();
            }
        }
    }

    private int makeTripleGroups(List<Scorecard> scorecards, int num, List<Pair> activePairs) {
        int doneGroups = 0;
        int indexPosition = 0;
        List<Pair> groupings = new ArrayList<>();

        if(num == indexPosition) {
            return indexPosition;
        }
        for (int i = 0; i < activePairs.size(); i++) {
            groupings.add(activePairs.get(i));

            if (groupings.size() == 3) {
                Scorecard sc = new Scorecard(groupings, null);
                scorecards.add(sc);
                System.out.println();
                groupings.clear();
                doneGroups++;
            }
            if (doneGroups == num) {
                indexPosition = i + 1;
                break;
            }
        }
        return indexPosition;
    }
}
