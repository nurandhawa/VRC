package ca.sfu.teambeta.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.persistence.Persistable;

/**
 * Created by Gordon Shieh on 23/06/16.
 */
@Entity(name = "session")
public class GameSession extends Persistable {
    @OneToOne
    private Ladder ladder;

    @ManyToMany
    private Set<Pair> activePairs = new HashSet<>();

    @OneToMany
    @OrderColumn
    private List<Scorecard> scorecards = new ArrayList<>();

    @ElementCollection
    private Map<Pair, Integer> penalties = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
