package ca.sfu.teambeta.core;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import ca.sfu.teambeta.persistence.Persistable;


@Entity(name = "Scorecard")
public class Scorecard extends Persistable {

    @OneToMany(cascade = CascadeType.ALL)
    Set<PairRanking> pairRankings = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @Expose
    private List<Pair> pairs;
    @Expose
    private boolean isDone;

    @Transient
    @Expose
    private Time timeSlot;

    public Scorecard() {

    }

    public Scorecard(List<Pair> pairs, Observer obs) {
        // Better make a copy of pairs, just in case it changes
        this.pairs = new ArrayList<>(pairs);
        this.isDone = false;
    }

    public void setGameResults(Pair winner, int rank) {
        pairRankings.add(new PairRanking(winner, rank));
        if (pairRankings.size() == pairs.size()) {
            isDone = true;
        }
    }

    public int getPairScore(Pair pair) {
        for (PairRanking ranking : pairRankings) {
            if (ranking.hasPair(pair)) {
                return ranking.getRank();
            }
        }
        return 0;
    }

    public List<Pair> getPairs() {
        return Collections.unmodifiableList(pairs);
    }

    public List<Pair> getReorderedPairs() {
        List<Pair> orderedPairs = new ArrayList<>(pairs);
        Collections.sort(orderedPairs, (pair1, pair2) -> getPairScore(pair1) - getPairScore(pair2));
        return orderedPairs;
    }

    public boolean isDone() {
        return isDone;
    }

    @Override
    public String toString() {
        List<Pair> teams = this.getReorderedPairs();
        String scString = "";
        for (Pair p : teams) {
            scString += p.toString();
            scString += "\n";
        }
        return scString;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final Scorecard otherScorecard = (Scorecard) other;
        return pairs.equals(otherScorecard.pairs)
                && pairRankings.equals(otherScorecard.pairRankings);
    }

    @Override
    public int hashCode() {
        return 47 * pairs.hashCode();
    }

    public Time getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(Time timeSlot) {
        this.timeSlot = timeSlot;
    }
}
