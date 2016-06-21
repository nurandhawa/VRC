package ca.sfu.teambeta.core;

/**
 * Created by constantin on 27/05/16. <p> <p> USAGE: After all of the games took place (1) pass
 * groups to LadderManager (2) call processLadder() for all the computations to be complete.
 */

public enum Penalty {
    ACCIDENT(-1), ZERO(0), ABSENT(2), LATE(4), MISSING(10);

    private int penalty;

    Penalty(int penalty) {
        this.penalty = penalty;
    }

    public int getPenalty() {
        return penalty;
    }

}
