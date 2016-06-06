package ca.sfu.teambeta.core;

/**
 * Created by constantin on 27/05/16. <p> <p> USAGE: After all of the games took place (1) pass
 * groups to LadderManager (2) call processLadder() for all the computations to be complete.
 */

public enum Penalty {
    ZERO(0), ABSENT(2), LATE(4), MISSING(10);

    private int penalty;

    Penalty(int penalty) {
        this.penalty = penalty;
    }

    public int getPenalty() {
        return penalty;
    }

    public static int fromString(String text) {
        if (text != null) {
            if (text.contains("zero")) {
                return ZERO.getPenalty();
            } else if (text.contains("absent")) {
                return ABSENT.getPenalty();
            } else if (text.contains("late")) {
                return LATE.getPenalty();
            } else if (text.contains("missing")) {
                return MISSING.getPenalty();
            }
        }

        throw new IllegalArgumentException("No penalty of type " + text + " is found");
    }
}
