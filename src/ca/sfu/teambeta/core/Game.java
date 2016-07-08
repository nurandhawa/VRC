package ca.sfu.teambeta.core;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import ca.sfu.teambeta.persistence.Persistable;

@Entity(name = "Game")
public class Game extends Persistable {

    @OneToOne
    private Pair winner;

    @OneToOne
    private Pair loser;

    public Game() {
    }

    public Game(Pair winner, Pair loser) {
        this.winner = winner;
        this.loser = loser;
    }

    public boolean isWinner(Pair pair) {
        return winner.equals(pair);
    }

    public boolean isLoser(Pair pair) {
        return loser.equals(pair);
    }

    public Pair getWinner() {
        return winner;
    }

    public void setWinner(Pair winner) {
        this.winner = winner;
    }

    public Pair getLoser() {
        return loser;
    }

    public void setLoser(Pair loser) {
        this.loser = loser;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final Game otherGame = (Game) other;
        return getWinner().equals(otherGame.getWinner())
                && getLoser().equals(otherGame.getLoser());
    }

    @Override
    public int hashCode() {
        return 37 * getWinner().hashCode() * getLoser().hashCode();
    }
}
