package ca.sfu.teambeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ScoreCard<T> {
    public static final int WIN = 1;
    public static final int NO_SCORE = 0;
    public static final int LOSE = -1;
    private static final int NUM_GAMES = 3;

    private Map<T, List<Integer>> scoreMap;

    ScoreCard(List<T> teams) {
        int numTeams = teams.size();
        scoreMap = new HashMap<>(numTeams);
        for (T t : teams) {
            List<Integer> emptyScores = new ArrayList<>(Collections.nCopies(NUM_GAMES, NO_SCORE));
            scoreMap.put(t, emptyScores);
        }
    }

    public boolean setWin(T againstTeam, int matchNum) {
        List<Integer> scoreList = scoreMap.get(againstTeam);
        scoreList.set(matchNum, WIN);
        return true;
    }

    public boolean setLose(T againstTeam, int matchNum) {
        List<Integer> scoreList = scoreMap.get(againstTeam);
        scoreList.set(matchNum, LOSE);
        return true;
    }

    public int getScore(T team) {
        return scoreMap.get(team).stream()
                .mapToInt(Integer::intValue).sum();
    }

    public static void main(String[] args) {
        List<String> list = Arrays.asList("Canucks", "Flames", "Oilers", "Leafs");
        ScoreCard<String> sc = new ScoreCard<>(list);

        sc.setWin("Canucks", 0);
        sc.setWin("Canucks", 1);
        sc.setWin("Canucks", 2);
        System.out.println(sc.getScore("Canucks"));

    }
}
