package ca.sfu.teambeta.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gordon Shieh on 25/05/16.
 */
public class Scorecard<Player> {
    public static final int WIN = 1;
    public static final int NO_SCORE = 0;
    public static final int LOSE = -1;
    private static final int NUM_GAMES = 3;
    private List<Player> steams = new ArrayList<>();

    private Map<Player, List<Integer>> scoreMap;

    public Scorecard(List<Player> teams) {
        int numTeams = teams.size();
        scoreMap = new HashMap<>(numTeams);
        for (Player t : teams) {
            List<Integer> emptyScores = new ArrayList<>(Collections.nCopies(NUM_GAMES, NO_SCORE));
            scoreMap.put(t, emptyScores);
        }
        this.steams = teams;
    }

    public boolean setWin(Player team, int matchNum) {
        List<Integer> scoreList = scoreMap.get(team);
        scoreList.set(matchNum, WIN);
        return true;
    }

    public boolean setLose(Player team, int matchNum) {
        List<Integer> scoreList = scoreMap.get(team);
        scoreList.set(matchNum, LOSE);
        return true;
    }

    public int getScore(Player team) {
        return scoreMap.get(team).stream()
                .mapToInt(Integer::intValue).sum();
    }

    // Java uses a stable sorting algorithm (TimSort) so that if there are ties,
    // the original order does not change
    public List<Player> getTeamRankings() {
        List<Player> teams = new ArrayList<>(scoreMap.keySet());
        teams.sort((Player a, Player b) -> getScore(b) - getScore(a));
        return teams;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Player, List<Integer>> entry : scoreMap.entrySet()) {
            builder.append(entry.getKey().toString());
            builder.append("\t");
            for (Integer i : entry.getValue()) {
                if (i == WIN) {
                    builder.append("Win\t");
                } else if (i == LOSE) {
                    builder.append("Lost\t");
                } else {
                    builder.append("Pending\t");
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        List<String> list = Arrays.asList("Canucks", "Flames", "Oilers", "Leafs");
        Scorecard<String> sc = new Scorecard<>(list);

        sc.setWin("Canucks", 0);
        sc.setWin("Oilers", 1);
        sc.setWin("Canucks", 2);
        System.out.println(sc.getScore("Canucks"));
        System.out.println(sc.getTeamRankings().toString());
    }

    public List<Player> getTeams() {
        return steams;
    }
}
