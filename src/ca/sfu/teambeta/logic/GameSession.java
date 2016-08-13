package ca.sfu.teambeta.logic;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Penalty;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.Time;
import ca.sfu.teambeta.persistence.Persistable;

@Entity(name = "session")
public class GameSession extends Persistable {
    @OneToOne(cascade = CascadeType.ALL)
    private Ladder ladder;

    @OneToOne(cascade = CascadeType.ALL)
    private Ladder reorderedLadder = null;

    @ManyToMany
    private Set<Pair> activePairs = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @OrderColumn
    private List<Scorecard> scorecards = new ArrayList<>();

    @ElementCollection
    private Map<Pair, Penalty> penalties = new HashMap<>();

    @ElementCollection
    private Map<Pair, Time> timeSlots = new HashMap<>();

    @OneToOne(fetch = FetchType.LAZY)
    private GameSession previousGameSession = null;

    private long timestamp;

    // Default constructor for Hibernate
    public GameSession() {
        setTimestamp();
    }

    public GameSession(Ladder ladder) {
        this.ladder = ladder;
        createGroups(new VrcScorecardGenerator(), new VrcTimeSelection());
        setTimestamp();
    }

    public GameSession(GameSession gameSession) {
        this.previousGameSession = gameSession;
        this.ladder = gameSession.getReorderedLadder();
        createGroups(new VrcScorecardGenerator(), new VrcTimeSelection());
        setTimestamp();
    }

    // Constructor for testing
    public GameSession(Ladder ladder, long timestamp) {
        this.ladder = ladder;
        createGroups(new VrcScorecardGenerator(), new VrcTimeSelection());
        this.timestamp = timestamp;
    }

    private void setTimestamp() {
        this.timestamp = Instant.now().getEpochSecond();
    }

    // Use me ONLY for importing a CSV file!!
    public void replaceLadder(Ladder ladder) {
        this.ladder = ladder;
        createGroups(new VrcScorecardGenerator(), new VrcTimeSelection());
        setTimestamp();
    }

    public List<Scorecard> createGroups(ScorecardGenerator generator, TimeSelection timeSelector) {
        //Generate groups
        scorecards = generator.generateScorecards(getActivePairs());
        //Set dominant time slots for each group
        timeSelector.distributePairs(scorecards, timeSlots);

        return Collections.unmodifiableList(scorecards);
    }

    public List<Pair> getAllPairs() {
        return new LinkedList<>(ladder.getPairs());
    }

    public List<Pair> getActivePairs() {
        return ladder.getPairs().stream()
                .filter(pair -> activePairs.contains(pair))
                .collect(Collectors.toList());
    }

    public Set<Pair> getActivePairSet() {
        return new HashSet<>(activePairs);
    }

    public List<Pair> getPassivePairs() {
        return ladder.getPairs().stream()
                .filter(pair -> !activePairs.contains(pair))
                .collect(Collectors.toList());
    }

    public List<Scorecard> getScorecards() {
        return Collections.unmodifiableList(scorecards);
    }

    public void setScorecards(List<Scorecard> scorecards) {
        this.scorecards = scorecards;
    }

    public Ladder getReorderedLadder() {
        return reorderedLadder;
    }

    public boolean setPairActive(Pair pair) {
        try {
            if (getAlreadyActivePlayer(pair) == null) {
                activePairs.add(pair);
                timeSlots.put(pair, Time.NO_SLOT);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public Player getAlreadyActivePlayer(Pair pair) throws Exception {
        if (ladder.getPairs().contains(pair)) {
            List<Player> team = pair.getPlayers();
            Player first = team.get(0);
            Player second = team.get(1);
            if (searchActivePlayer(first)) {
                return first;
            } else if (searchActivePlayer(second)) {
                return second;
            } else {
                return null;
            }
        } else {
            throw new Exception("Pair is not in the ladder");
        }
    }

    public void setPairInactive(Pair pair) {
        activePairs.remove(pair);
    }

    public void setPenaltyToPair(Pair pair, Penalty penalty) {
        penalties.put(pair, penalty);
    }

    public void removePenaltyFromPair(Pair pair) { //TODO should be automatic
        penalties.remove(pair);
    }

    public boolean isActivePair(Pair pair) {
        List<Pair> activeList = getActivePairs();
        boolean active = activeList.contains(pair);

        return active;
    }

    public void reorderLadder(LadderReorderer reorderer, TimeSelection timeSelector) {
        List<Pair> reorderedList =
                reorderer.reorder(getAllPairs(), scorecards, activePairs, penalties);
        if (reorderedLadder == null) {
            reorderedLadder = new Ladder(reorderedList);
        } else {
            reorderedLadder.setNewPairs(reorderedList);
        }
        for (Pair p : getAllPairs()) {
            p.setPairScore(0);
        }
    }

    public boolean addNewPairAtIndex(Pair newPair, int index) {
        boolean pairExists = ladder.getPairs().contains(newPair);
        if (!pairExists) {
            ladder.insertAtIndex(index, newPair);
        }
        return pairExists;
    }

    public boolean addNewPairAtEnd(Pair newPair) {
        boolean pairExists = ladder.getPairs().contains(newPair);
        if (!pairExists) {
            ladder.insertAtEnd(newPair);
        }
        return pairExists;
    }

    public Date getLadderModificationDate() {
        return ladder.getModifiedDate();
    }

    public boolean removePairFromLadder(Pair pair) {
        activePairs.remove(pair);
        penalties.remove(pair);
        timeSlots.remove(pair);
        return ladder.removePair(pair);
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 57 * ladder.hashCode() * reorderedLadder.hashCode()
                * activePairs.hashCode() * scorecards.hashCode()
                * penalties.hashCode();
    }

    private boolean searchActivePlayer(Player player) {
        for (Pair current : activePairs) {
            if (current.hasPlayer(player)) {
                return true;
            }
        }
        return false;
    }

    public Scorecard getScorecardByIndex(int index) {
        for (Scorecard s : scorecards) {
            if (s.getID() == index) {
                return s;
            }
        }
        return null;
    }

    public void setTimeSlot(Pair pair, Time time) {
        timeSlots.put(pair, time);
    }

    public Map<Pair, Time> getTimeSlots() {
        return new HashMap<>(timeSlots);
    }

    private int getPositionOfPair(Pair pair) {
        return ladder.getPairs().indexOf(pair);
    }

    public Map<Pair, Integer> getPositionChanges() {
        Map<Pair, Integer> positionsChanges = new HashMap<>();
        if (previousGameSession == null) {
            return positionsChanges;
        }
        List<Pair> pairs = ladder.getPairs();
        for (int position = 0; position < pairs.size(); position++) {
            Pair pair = pairs.get(position);

            int oldPosition = previousGameSession.getPositionOfPair(pair);

            if (oldPosition == -1) {
                positionsChanges.put(pair, 0);
            } else {
                positionsChanges.put(pair, oldPosition - position);
            }
        }
        return positionsChanges;
    }
}
