package ca.sfu.teambeta.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Penalty;
import ca.sfu.teambeta.core.Scorecard;

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

    public LadderManager() {
        ladder = DBManager.loadFromDB();
        activePairs = findPairs(ladder.getLadder(), true);
        passivePairs = findPairs(ladder.getLadder(), false);
    }

    public LadderManager(String fileName) {
        ladder = DBManager.loadFromDB(fileName);
        activePairs = findPairs(ladder.getLadder(), true);
        passivePairs = findPairs(ladder.getLadder(), false);
    }

    public LadderManager(List<Pair> dbLadder) {
        ladder = new Ladder(dbLadder);
        activePairs = findPairs(ladder.getLadder(), true);
        passivePairs = findPairs(ladder.getLadder(), false);
    }

    public LadderManager(Ladder loadedLadder) {
        if (loadedLadder.getLadder().size() == 0) {
            ladder = new Ladder(new ArrayList<Pair>());
        } else {
            ladder = loadedLadder;
        }
        activePairs = new ArrayList<>();
        passivePairs = new ArrayList<>();
    }

    public List<Pair> getFullLadder() {
        return ladder.getLadder();
    }

    public List<Pair> getActivePairs() {
        split();
        return activePairs;
    }

    public List<Pair> getPassivePairs() {
        split();
        return passivePairs;
    }

    public void addNewPair(Pair newPair) {
        ladder.incLadderLength();
        newPair.setPosition(ladder.getLadderLength());
        ladder.insertAtEnd(newPair);
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

    public boolean removePairAtIndex(int index){
        Pair pairToRemove = ladder.getPairAtIndex(index);
        return ladder.removePair(pairToRemove);
    }

    public void removePenalty(Pair pair) {
        pair.setPenalty(Penalty.ZERO.getPenalty());
    }

    //Sets specific penalty to a pair on the ladder
    public void setPenaltyToPair(int pairIndex, String penaltyType) {
        Pair pair = ladder.getPairAtIndex(pairIndex);
        int retPenalty = getPenaltyFromString(penaltyType);
        int penalty = 0;
        //if penalty is already two and add missing should be 8 and late should only be 4
        if(pair.getPenalty() >= 2 && retPenalty != -1 && retPenalty != 0 ) {
            penalty = retPenalty - pair.getPenalty();
            pair.setPenalty(penalty);
        } else if (retPenalty == 0) {
            removePenalty(pair);
        } else {
            pair.setPenalty(retPenalty);
        }
    }

    public void processLadder(List<Scorecard> scorecards, String fileName) {
        /**
         * 1. swap the pairs around based on the result
         * 2. combine the list of both active and inactive pairs
         * 3. set the absent(-2) penalty to all passive pairs
         * 4. apply all penalties that have been previously set before the method call
         * 5. then save the ladder to a db
         **/
        swapBetweenGroups(scorecards);
        mergeActivePassive();
        setAbsentPenaltyToPairs();
        applyPenalties();
        saveLadderToDBFile(fileName);
    }

    //*******************************************
    //  Methods used ONLY INSIDE of this class
    // .: Public for the testing.
    //*******************************************

    public void mergeActivePassive() {
        List<Integer> emptyPositions = getAvailablePos();
        assignAvaiablePos(emptyPositions);
        combine();
    }

    private List<Integer> getAvailablePos() {
        int totalPos = ladder.getLadderLength();
        int notPlaying = passivePairs.size();
        List<Integer> takenPositions = new ArrayList<Integer>();
        List<Integer> emptyPositions = new ArrayList<Integer>();
        int i = 0;

        for (Pair current : passivePairs) {
            takenPositions.add(current.getPosition());
        }

        emptyPositions.addAll(findMissingPos(0, takenPositions.get(0)));

        while(i < (takenPositions.size() - 1)) {
            emptyPositions.addAll(findMissingPos(takenPositions.get(i), takenPositions.get(++i)));
        }

        if(takenPositions.get(notPlaying - 1) != totalPos) {
            emptyPositions.add(totalPos);
        }

        return emptyPositions;
    }

    private void assignAvaiablePos(List<Integer> emptyPositions) {
        int i = 0;
        for (Pair current : activePairs) {
            current.deActivate();
            current.setPosition(emptyPositions.get(i));
            i++;
        }
    }

    private List<Integer> findMissingPos(Integer startPos, Integer endPos) {
        List<Integer> emptyPositions = new ArrayList<Integer>();
        int difference = Math.subtractExact(endPos, startPos);
        if (difference == 2) {
            emptyPositions.add(++startPos);
        } else {
            for(int i = 1; i < difference; i++) {
                emptyPositions.add(startPos + i);
            }
        }

        return emptyPositions;
    }

    private void combine() {
        //Implemented by David Li and Kostiantyn Koval
        List<Pair> newLadder = new ArrayList<>();

        newLadder.addAll(passivePairs);
        newLadder.addAll(activePairs);

        Comparator<Pair> makeSorter = getPairPositionComparator();

        Collections.sort(newLadder, makeSorter);
        passivePairs.clear();
        activePairs.clear();
        ladder = new Ladder(newLadder);
    }

    private void split() {
        List<Pair> fullLadder = ladder.getLadder();

        activePairs = findPairs(fullLadder, true);
        passivePairs = findPairs(fullLadder, false);
    }

    private List<Pair> swapBetweenGroups(List<Scorecard> scorecards) {
        // SWAPPING between groups and saving result in activePairs

        // Setup a list to hold the decompiled Scorecard's and
        //  one to hold the first group
        List<Pair> completedPairs = new ArrayList<Pair>();
        List<Pair> firstGroup = scorecards.get(0).getTeamRankings();


        List<Pair> previousGroup = firstGroup;
        for (int i = 1; i < scorecards.size(); i++) {
            // Swap the player's in the first and last position of subsequent groups
            List<Pair> currentGroup = scorecards.get(i).getTeamRankings();
            swapPlayers(previousGroup, currentGroup);

            completedPairs.addAll(previousGroup);
            previousGroup = currentGroup;
        }

        // The for loop omits the last group, thus add it now:
        completedPairs.addAll(previousGroup);

        // Finally update the active list of players
        this.activePairs = completedPairs;

        return completedPairs;

    }

    private void swapPlayers(List<Pair> firstGroup, List<Pair> secondGroup) {
        // This method swaps the last member of 'firstGroup' with the first member of 'secondGroup'

        int lastIndexOfFirstGroup = firstGroup.size() - 1;

        Pair temp = firstGroup.get(lastIndexOfFirstGroup);

        firstGroup.set(lastIndexOfFirstGroup, secondGroup.get(0));
        secondGroup.set(0, temp);
    }

    private Comparator<Pair> getPairPositionComparator() {
        return new Comparator<Pair>() {
                @Override
                public int compare(Pair p1, Pair p2) {
                    int result;
                    if(p1.getPosition() == p2.getPosition()) {
                        result = p1.getPrevPosition() - p2.getPrevPosition();
                    } else {
                        result = p1.getPosition() - p2.getPosition();
                    }
                    return result;
                }
            };
    }

    private List<Pair> findPairs(List<Pair> fullLadder, boolean isPlaying) {
        List<Pair> newPairs = fullLadder.stream().filter(p -> p.isPlaying() == isPlaying).collect(Collectors.toList());

        return newPairs;
    }

    //Applies absent penalty to the pairs who didn't show up
    private void setAbsentPenaltyToPairs() {
        List<Pair> passivePairs = getPassivePairs();

        for (Pair currentPair : passivePairs) {
            currentPair.setPenalty(Penalty.ABSENT.getPenalty());
        }
    }

    private int getPenaltyFromString(String penaltyType) {
        int penalty;
        try {
            penalty = Penalty.fromString(penaltyType);
            return penalty;
        } catch (IllegalArgumentException ex) {
            System.out.println(ex);
            return -1;
        }
    }

    //Applies additional penalties beside the absent penalty
    public void applyPenalties() {
        List<Pair> currentLadder = getFullLadder();

        for (Pair currentPair : currentLadder) {
            currentPair.setPosition(currentPair.positionAfterPenalty());
        }

        Collections.sort(currentLadder, getPairPositionComparator());
        fixPairPositionOnLadder();
    }

    private void fixPairPositionOnLadder() {
        int position = 0;
        for (Pair currentPair : getFullLadder()) {
            position++;
            currentPair.setPosition(position);
        }
    }

    private void saveLadderToDBFile(String fileName) {
        DBManager.saveToDB(this.ladder, fileName);
    }

    public void printLadder() {
        for (Pair currentPair : getFullLadder()) {
            System.out.println(currentPair);
        }
        System.out.println();
    }
}
