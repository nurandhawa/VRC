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

    int getAmountPairsByTime(List<Scorecard> scorecards, Time time);

    void distributePairs(List<Scorecard> scorecards);

    void clearTimeSlots(Ladder nextWeekLadder);
}