package ca.sfu.teambeta.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Penalty;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.persistence.Persistable;

/**
 * Created by Gordon Shieh on 23/06/16.
 */
@Entity(name = "session")
public class GameSession extends Persistable {
    @OneToOne(cascade = CascadeType.ALL)
    private Ladder ladder;

    @OneToOne
    private Ladder reorderedLadder = null;

    @ManyToMany
    private Set<Pair> activePairs = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @OrderColumn
    private List<Scorecard> scorecards = new ArrayList<>();

    @ElementCollection
    private Map<Pair, Penalty> penalties = new HashMap<>();

    GameSession(Ladder ladder) {
        this.ladder = ladder;
    }

    public void createGroups() {
        List<Pair> activePairList = getActivePairs();
        int playingCount = activePairList.size();

        if (playingCount % 3 == 0) {
            //All 3 team groups.
            int noOfTripleGroups = playingCount / 3;
            makeTripleGroups(noOfTripleGroups, activePairList);
        } else if (playingCount % 3 == 1) {
            //One 4 team group.
            int noOftripleGroups = playingCount / 3 - 1;
            int currentIndex = makeTripleGroups(noOftripleGroups, activePairList);
            makeQuadGroup(currentIndex, activePairList);
        } else {
            //Two 4 team groups.
            int noOftripleGroups = playingCount / 3 - 2;
            int currentIndex = makeTripleGroups(noOftripleGroups, activePairList);
            makeQuadGroup(currentIndex, activePairList);
        }
    }

    public List<Pair> getAllPairs() {
        return new ArrayList<>(ladder.getPairs());
    }

    public List<Pair> getActivePairs() {
        return ladder.getPairs().stream()
                .filter(pair -> activePairs.contains(pair))
                .collect(Collectors.toList());
    }

    public List<Pair> getPassivePairs() {
        return ladder.getPairs().stream()
                .filter(pair -> !activePairs.contains(pair))
                .collect(Collectors.toList());
    }

    public List<Scorecard> getScorecards() {
        return new ArrayList<>(scorecards);
    }

    public List<Pair> getReorderedLadder() {
        if (reorderedLadder != null) {
            return new ArrayList<>(reorderedLadder.getPairs());
        } else {
            return new ArrayList<>();
        }
    }

    private void makeQuadGroup(int num, List<Pair> activePairList) {
        List<Pair> groupings = new ArrayList<>();
        for (int i = num; i < activePairList.size(); i++) {
            groupings.add(activePairList.get(i));

            if (groupings.size() == 4) {
                Scorecard sc = new Scorecard(groupings, null);
                scorecards.add(sc);
                groupings.clear();
            }
        }
    }

    private int makeTripleGroups(int num, List<Pair> activePairList) {
        int doneGroups = 0;
        int indexPosition = 0;
        List<Pair> groupings = new ArrayList<>();

        for (int i = 0; i < activePairList.size(); i++) {
            groupings.add(activePairList.get(i));

            if (groupings.size() == 3) {
                Scorecard sc = new Scorecard(groupings, null);
                scorecards.add(sc);
                System.out.println();
                groupings.clear();
                doneGroups++;
            }
            if (doneGroups == num) {
                indexPosition = i + 1;
                break;
            }
        }
        return indexPosition;
    }

    public void setPairActive(Pair pair) {
        activePairs.add(pair);
    }

    public void setPairInactive(Pair pair) {
        activePairs.remove(pair);
        if (scorecards != null) {
            setPenaltyToPair(pair, Penalty.ABSENT);
        }
    }

    public void setPenaltyToPair(Pair pair, Penalty penalty) {
        penalties.put(pair, penalty);
    }

    public void removePenaltyFromPair(Pair pair) {
        penalties.remove(pair);
    }

    public void reorderLadder() {
        int position = 1;
        for (Pair pair : ladder.getPairs()) {
            pair.setPosition(position);
            position++;
        }
        applyAbsentPenalty();
//        swapBetweenGroups(scorecards);
//        assignNewPositionsToActivePairs();
//        combineActivePassive();
//        applyLateMissPenalty();

    }

    private void applyAbsentPenalty() {
        int previousTakenPosition = ladder.getLadderLength();
        List<Pair> passivePairs = getPassivePairs();

        //Move pairs starting from the worse pair to the best
        for (int i = passivePairs.size() - 1; i >= 0; i--) {
            Pair pair = passivePairs.get(i);
            if (pair.getPenalty() != Penalty.ACCIDENT.getPenalty()) {
                int position = pair.getPosition();
                int possibleShift = previousTakenPosition - position;

                switch (possibleShift) {
                    case 0: //Do not move the pair
                        break;
                    case 1: //Move pair on 1 position
                        pair.setPosition(position + 1);
                        break;
                    default: //Move pair on 2 positions
                        pair.setPosition(position + Penalty.ABSENT.getPenalty());
                }
                pair.setPenalty(Penalty.ZERO.getPenalty());
                previousTakenPosition = pair.getPosition() - 1;

            } else {
                pair.setPenalty(Penalty.ZERO.getPenalty());
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 57 * ladder.hashCode() * reorderedLadder.hashCode() *
                activePairs.hashCode() * scorecards.hashCode() * penalties.hashCode();
    }
}
