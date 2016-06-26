package ca.sfu.teambeta.logic;

import java.util.List;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Scorecard;

/**
 * Created by Gordon Shieh on 25/06/16.
 */
public interface ScorecardGenerator {
    List<Scorecard> generateScorecards(List<Pair> activePairs);
}
