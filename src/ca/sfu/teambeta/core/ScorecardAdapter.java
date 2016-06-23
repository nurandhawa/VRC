package ca.sfu.teambeta.core;

import java.util.List;

// Adapter for the new Scorecard to work with the current method of inputting
// game results. The old and current method will input win/lose results per win/lose
// per call. The new method will only accept win and lose results in one call
// to be database friendly.
@Deprecated
public class ScorecardAdapter {
    private Scorecard scorecard;
    private Pair[] winningPairs;
    private Pair[] losingPairs;

    public ScorecardAdapter(List<Pair> pairs, Observer observer) {
        scorecard = new Scorecard(pairs, observer);
        winningPairs = new Pair[pairs.size()];
        losingPairs = new Pair[pairs.size()];
    }

    public List<Pair> getTeamRankings() {
        return scorecard.getReorderedPairs();
    }

    public void setWin(Pair winner, int round) {
        winningPairs[round] = winner;
        Pair losingPair = losingPairs[round];
        if (losingPair != null) {
            scorecard.setGameResults(winner, losingPair);
        }
    }

    public void setLose(Pair loser, int round) {
        losingPairs[round] = loser;
        Pair winningPair = winningPairs[round];
        if (winningPair != null) {
            scorecard.setGameResults(winningPair, loser);
        }
    }

    public Scorecard getScorecard() {
        return scorecard;
    }
}
