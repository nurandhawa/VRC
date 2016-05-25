package ca.sfu.teambeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ScoreCard<T> {
    public static final int WIN = 1;
    public static final int NO_SCORE = 0;
    public static final int LOSE = -1;
    private static final int NUM_GAMES = 3;

    private T currentTeam;
    private Map<T, List<Integer>> scoreMap;

    public static <T> List<ScoreCard<T>> ScoreCardFactory(List<T> teams) {
        int numTeams = teams.size();
        List<ScoreCard<T>> scoreCards = new ArrayList<>(numTeams);

        for (T team : teams) {
            List<T> matchTeams = teams.stream()
                    .filter(t -> !t.equals(team))
                    .collect(Collectors.toList());

            scoreCards.add(new ScoreCard<>(team, matchTeams));
        }
        return scoreCards;
    }

    ScoreCard(T team, List<T> teams) {
        currentTeam = team;
        int numTeams = teams.size();
        scoreMap = new HashMap<>(numTeams);
        for (T t : teams) {
            List<Integer> emptyScores = new ArrayList<>(Collections.nCopies(NUM_GAMES, NO_SCORE));
            scoreMap.put(t, emptyScores);
        }
    }

    public T getCurrentTeam() {
        return currentTeam;
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

    //TODO: Calculate Score for each match

    public static void main(String[] args) {
        List<String> list = Arrays.asList("Canucks", "Flames", "Oilers", "Leafs");
        List<ScoreCard<String>> scoreCards = ScoreCardFactory(list);

        ScoreCard<String> sc = scoreCards.get(1);
        sc.setWin("Canucks", 0);
        sc.setWin("Canucks", 1);
        sc.setWin("Canucks", 2);

        for (ScoreCard s : scoreCards) {
            System.out.println(s.toString());
        }

    }
}
