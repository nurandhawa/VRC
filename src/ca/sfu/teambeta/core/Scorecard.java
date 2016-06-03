package ca.sfu.teambeta.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Gordon Shieh on 25/05/16.
 */
public class Scorecard<T> {
    public static final int WIN = 1;
    public static final int NO_SCORE = 0;
    public static final int LOSE = -1;
    private static final int NUM_GAMES = 3;

    private Map<T, List<Integer>> scoreMap;
    private Observer observer;

    public Scorecard(List<T> teams, Observer obs) {
        int numTeams = teams.size();
        scoreMap = new LinkedHashMap<>(numTeams);
        for (T t : teams) {
            List<Integer> emptyScores = new ArrayList<>(Collections.nCopies(NUM_GAMES, NO_SCORE));
            scoreMap.put(t, emptyScores);
        }
        observer = obs;
    }

    //Asked Gordon to finish check all input function.

    public boolean setWin(T team, int matchNum) {
        List<Integer> scoreList = scoreMap.get(team);
        scoreList.set(matchNum, WIN);
        if (observer != null) {
            observer.done();
        }
        return true;
    }

    public boolean setLose(T team, int matchNum) {
        List<Integer> scoreList = scoreMap.get(team);
        scoreList.set(matchNum, LOSE);
        if (observer != null) {
            observer.done();
        }
        return true;
    }

    public int getScore(T team) {
        return scoreMap.get(team).stream()
                .mapToInt(Integer::intValue).sum();
    }

    // Java uses a stable sorting algorithm (TimSort) so that if there are ties,
    // the original order does not change
    public List<T> getTeamRankings() {
        List<T> teams = new ArrayList<>(scoreMap.keySet());
        teams.sort((T a, T b) -> getScore(b) - getScore(a));
        return teams;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<T, List<Integer>> entry : scoreMap.entrySet()) {
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
        Scorecard<String> sc = new Scorecard<>(list, null);

        sc.setWin("Canucks", 0);
        sc.setWin("Oilers", 1);
        sc.setWin("Canucks", 2);
        System.out.println(sc.getScore("Canucks"));
        System.out.println(sc.getTeamRankings().toString());
    }
}