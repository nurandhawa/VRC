package ca.sfu.teambeta.core;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import ca.sfu.teambeta.persistence.Persistable;

@Entity(name = "Rank")
public class PairRanking extends Persistable {

    @ManyToOne
    private Pair pair;
    private int rank;

    public PairRanking() {
    }

    public PairRanking(Pair pair, int rank) {
        this.pair = pair;
        this.rank = rank;
    }

    boolean hasPair(Pair pair) {
        return this.pair.equals(pair);
    }

    int getRank() {
        return rank;
    }

    void setRank(int rank) {
        this.rank = rank;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final PairRanking otherPairRanking = (PairRanking) other;
        return pair.equals(otherPairRanking.pair);
    }

    @Override
    public int hashCode() {
        return 37 * pair.hashCode();
    }
}
