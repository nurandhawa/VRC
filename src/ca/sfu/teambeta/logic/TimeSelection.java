package ca.sfu.teambeta.logic;

import java.util.List;
import java.util.Map;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.Time;

/**
 * Created by constantin on 10/07/16.
 */
public interface TimeSelection {
    //Used for testing purposes
    int getAmountPairsByTime(List<Scorecard> scorecards, Time time);

    //Distribute pairs every time scorecards are updated
    void distributePairs(List<Scorecard> scorecards, Map<Pair, Time> timeSlotsMap);
}
