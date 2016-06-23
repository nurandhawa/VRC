package ca.sfu.teambeta.core;

import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import ca.sfu.teambeta.persistence.DBManager;
import ca.sfu.teambeta.persistence.Persistable;


@Entity(name = "Scorecard")
public class Scorecard extends Persistable {
    private static final int WIN = 1;
    private static final int NO_SCORE = 0;
    private static final int LOSE = -1;
    private static final int NUM_GAMES = 4;

    @OneToMany(cascade = CascadeType.ALL)
    Set<Game> games = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    List<Pair> pairs;

    @Transient
    Observer observer = null;
    int finishedGameCount = 0;

    public Scorecard() {

    }

    public Scorecard(List<Pair> pairs, Observer obs) {
        // Better make a copy of pairs, just in case it changes
        this.pairs = new ArrayList<>(pairs);
        this.observer = obs;
    }

    public static void main(String[] args) {
        Pair p1 = new Pair(new Player("A", "A", ""), new Player("B", "B", ""));
        Pair p2 = new Pair(new Player("C", "C", ""), new Player("D", "D", ""));
        Pair p3 = new Pair(new Player("E", "E", ""), new Player("F", "F", ""));
        List<Pair> pairs = Arrays.asList(p1, p2, p3);
        Scorecard sc = new Scorecard(pairs, null);

        sc.setGameResults(p2, p1);
        sc.setGameResults(p3, p2);
        sc.setGameResults(p3, p1);

        for (Pair p : sc.getReorderedPairs()) {
            System.out.println(p.toString());
        }

        DBManager dbManager = new DBManager(DBManager.getTestingSession(true));
        dbManager.persistEntity(sc);
    }

    public void setGameResults(Pair winner, Pair loser) {
        games.add(new Game(winner, loser));
        finishedGameCount++;
        if (observer != null && finishedGameCount == pairs.size()) {
            observer.done();
        }
    }

    private int getPairScore(Pair pair) {
        int winCount = (int) games.stream()
                .filter(game -> game.isWinner(pair))
                .count();

        int loseCount = (int) games.stream()
                .filter(game -> game.isLoser(pair))
                .count();
        return winCount - loseCount;
    }

    public List<Pair> getReorderedPairs() {
        List<Pair> orderedPairs = new ArrayList<>(pairs);
        Collections.sort(orderedPairs, (pair1, pair2) -> getPairScore(pair2) - getPairScore(pair1));
        return orderedPairs;
    }

    @Override
    public String toString() {
        List<Pair> teams = this.getReorderedPairs();
        String s = "";
        for (Pair p : teams) {
            s += p.toString();
            s += "\n";
        }
        return s;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        final Scorecard otherScorecard = (Scorecard) other;
        return pairs.equals(otherScorecard.pairs) && games.equals(otherScorecard.games);
    }

    @Override
    public int hashCode() {
        return 47 * pairs.hashCode();
    }
}
