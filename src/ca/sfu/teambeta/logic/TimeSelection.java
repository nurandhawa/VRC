package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.Time;

import java.util.List;

/**
 * Created by constantin on 10/07/16.
 */
public interface TimeSelection {
    //Used for testing purposes
    int getAmountPairsByTime(List<Scorecard> scorecards, Time time);

    //Distribute pairs every time scorecards are updated
    void distributePairs(List<Scorecard> scorecards);

    //When all the matches took place reset all pairs to NO_SLOT
    void clearTimeSlots(Ladder nextWeekLadder);
}