package ca.sfu.teambeta.logic;

import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Penalty;
import ca.sfu.teambeta.core.Scorecard;

/**
 * Created by Gordon Shieh on 25/06/16.
 */
public interface LadderReorderer {
    List<Pair> reorder(List<Pair> originalPairs, List<Scorecard> scorecards,
                       Set<Pair> activePairs, Map<Pair, Penalty> penalties);
}
