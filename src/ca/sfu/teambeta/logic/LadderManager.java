package ca.sfu.teambeta.logic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Penalty;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;

/**
 * Created by constantin on 27/05/16.
 * <p>
 * <p>
 * USAGE: After all of the games took place
 * (1) pass groups to LadderManager
 * (2) call processLadder() for all the computations to be complete.
 */

public class LadderManager {
    private Ladder ladder;
    private List<Pair> activePairs;
    private List<Pair> passivePairs;
    private List<List<Pair>> groups;

    public LadderManager() {
        ladder = new Ladder(new ArrayList<Pair>());
        activePairs = new ArrayList<>();
        passivePairs = new ArrayList<>();
        groups = new ArrayList<>();
    }

    //init MUST be called to use LadderManager object
    /*NOTE: I am assuming that the LadderManager will somehow get a List<Pair> somehow; whether it is a new List<Pair>
      or retrieved from processing the DB. If this design is not suitable, please discuss it with me so we can change it.
      - Sam 5/30/2016 */
    public void init(List<Pair> dbLadder) {
        ladder = new Ladder(dbLadder);
        activePairs = findPairs(ladder.getLadder(), true);
        passivePairs = findPairs(ladder.getLadder(), false);
    }

    public List<Pair> getLadder() {
        return ladder.getLadder();
    }

    public List<Pair> getActivePairs() {
        return activePairs;
    }

    public List<Pair> getPassivePairs() {
        return passivePairs;
    }

    public void setGroups(ArrayList<List<Pair>> groups) {
        this.groups = groups;
    }

    public void addNewPair(Pair newPair) {
        newPair.setPosition(ladder.getLadderLength());
        ladder.insertAtEnd(newPair);
        ladder.incLadderLength();
    }

    public void setIsPlaying(Pair pair) {
        if (ladder.getLadder().contains(pair)) {
            pair.activate();
        }
    }

    public void setNotPlaying(Pair pair) {
        if (ladder.getLadder().contains(pair)) {
            pair.deActivate();
        }
    }

    public void removePenalty(Pair pair) {
        pair.setPenalty(Penalty.ZERO.getPenalty());
    }

    public void processLadder() {
        //The following functions have to be executed ONLY in such order
        applyAbsentPenalty(); //Absent pairs will Drop, except pairs with Accident
        //Passive pairs have changes
        swapBetweenGroups(); //Swap adjacent players between groups
        //Now active and passive pairs have changes
        mergeActivePassive(); //New positions for played pairs will be set
        applyLateMissedPenalty(); //Last penalty adjustments
    }

    //*******************************************
    //  Methods used ONLY INSIDE of this class
    // .: Public for the testing. TODO make the following functions private
    //*******************************************

    public void mergeActivePassive() {
        //Operations are done with separated ladder, then merged
        int allMembers = ladder.getLadderLength();
        int notPlaying = passivePairs.size();
        int arePlaying = allMembers - notPlaying;
        int[] positions = new int[notPlaying];
        int[] emptyPositions = new int[arePlaying];

        int i = 0;
        for (Pair current : passivePairs) {
            positions[i] = current.getPosition();
            i++;
        }

        //Create array of empty positions for participants
        i = 0;
        int j = 0;
        for (int position = 1; position <= allMembers; position++) {
            if (position == positions[j]) {
                //This position is taken
                j++;
            } else {
                //Position not used
                emptyPositions[i] = position;
                i++;
            }
        }

        //Assign participants to empty positions and sat them to not playing
        i = 0;
        for (Pair current : activePairs) {
            current.deActivate();
            current.setPosition(emptyPositions[i]);
            i++;
        }
        combine();
    }

    private int[] calAbsentPenalty() {
        int[] passivePairsPos = new int[passivePairs.size()];
        int i = 0;

        for (Pair current : passivePairs) {
            current.setPenalty(Penalty.ABSENT.getPenalty());
            passivePairsPos[i] = current.positionAfterPenalty();
            i++;
        }

        return passivePairsPos;
    }

    private int[] fixPosAbsentPenalty(int[] passivePairsPos) {
        int ladderLength = ladder.getLadderLength();

        for(int i = passivePairs.size() - 1; i > 0; i--) {
            if(passivePairsPos[i] > ladderLength) {
                passivePairsPos[i] = ladderLength;
                passivePairs.get(i).setPosition(ladderLength);
                ladderLength--;
            }
        }

        return passivePairsPos;
    }

    public void applyAbsentPenalty() {
        int notPlaying = passivePairs.size();
        int[] positions = calAbsentPenalty();
        positions = fixPosAbsentPenalty(positions);

        //Move players up on one position if adjacent pairs have the same position in the ladder
        for (int i = notPlaying - 1; i >= 1; i--) {
            if (positions[i - 1] >= positions[i]) {
                positions[i - 1] = positions[i] - 1;
            }
        }

        //Assign new positions after the penalty
        for (int i = 0; i < notPlaying; i++) {
            passivePairs.get(i).setPosition(positions[i]);
        }
        //Players who didn't play have new positions saved in passivePairs
    }

    public void swapBetweenGroups() {
        //
        // SWAPPING between groups and saving result in activePairs
        //      NOT IMPLEMENTED
        //
    }

    public void combine() {
        //Implemented by David Li and Kostiantyn Koval
        List<Pair> newLadder = new ArrayList<>();

        newLadder.addAll(passivePairs);
        newLadder.addAll(activePairs);

        Comparator<Pair> makeSorter = new Comparator<Pair>() {
            @Override
            public int compare(Pair p1, Pair p2) {
                return p1.getPosition() - p2.getPosition();
            }
        };

        java.util.Collections.sort(newLadder, makeSorter);
        passivePairs.clear();
        activePairs.clear();
        ladder = new Ladder(newLadder);
    }

    private List<Pair> findPairs(List<Pair> fullLadder, boolean isPlaying) {
        List<Pair> newPairs = fullLadder.stream().filter(p -> p.isPlaying() == isPlaying).collect(Collectors.toList());

        return newPairs;
    }

    private void applyLateMissedPenalty() {
        // activePairs and passivePairs now are empty, ladder has rearranged pairs.
        // NOTE some pairs have Late/Missed penalties
        //
        //          NOT IMPLEMENTED
        //
    }
}