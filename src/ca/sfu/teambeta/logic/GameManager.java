package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Gordon Shieh on 25/05/16.
 */
public class GameManager {

    private List<Pair> ladder;
    private List<Scorecard<Pair>> groups;

    public GameManager(List<Pair> activeLadder) {
        ladder = activeLadder;
        groups = new ArrayList<>();

        splitLadderIntoGroups();
    }

    public List<Scorecard<Pair>> getScorecards() {
        return groups;
    }

    private void splitLadderIntoGroups() {
        int playingCount = ladder.size();

        ArrayList<Pair> groupings = new ArrayList<>();

        if (playingCount % 3 == 0) {
            //All 3 team groups.
            int noOfTripleGroups = playingCount / 3;
            makeTripleGroups(noOfTripleGroups, groupings);

        } else if (playingCount % 3 == 1) {
            //One 4 team group.
            int noOftripleGroups = playingCount / 3 - 1;
            int currentIndex = makeTripleGroups(noOftripleGroups, groupings);
            makeQuadGroup(currentIndex, groupings);
        } else {
            //Two 4 team groups.
            int noOftripleGroups = playingCount / 3 - 2;
            int currentIndex = makeTripleGroups(noOftripleGroups, groupings);
            makeQuadGroup(currentIndex, groupings);
        }
    }

    private void makeQuadGroup(int num, ArrayList<Pair> groupings) {
        for (int i = num; i < ladder.size(); i++) {
            groupings.add(ladder.get(i));

            if (groupings.size() == 4) {
                Scorecard<Pair> s = new Scorecard<>(groupings);
                groups.add(s);
                System.out.println();
                groupings.clear();
            }
        }
    }

    private int makeTripleGroups(int num, ArrayList<Pair> groupings) {
        int doneGroups = 0;
        int indexPosition = 0;

        for (int i = 0; i < ladder.size(); i++) {
            groupings.add(ladder.get(i));

            if (groupings.size() == 3) {
                Scorecard<Pair> s = new Scorecard<>(groupings);
                groups.add(s);
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

    public void inputMatchResults(Scorecard<Pair> s, String results[][]) {
        List<Pair> teams = s.getTeamRankings();
        int rows = results.length;
        int cols = teams.size();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (results[i][j].equals("W")) {
                    s.setWin(teams.get(j), i);
                } else if (results[i][j].equals("L")) {
                    s.setLose(teams.get(j), i);
                } else {
                }
            }
        }
    }
}
