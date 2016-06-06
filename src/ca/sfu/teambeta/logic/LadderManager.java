package ca.sfu.teambeta.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Penalty;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;

/**
 * Created by constantin on 27/05/16. <p> <p> USAGE: After all of the games took place
 * setIsPlaying(Pair) returns false if any of players are already playing (1) pass groups to
 * LadderManager (2) call processLadder() for all the computations to be complete.
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
        int index = 1;
        for (Pair current : dbLadder) {
            current.setPosition(index);
            index++;
        }
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

    public List<Pair> getLadder() {
        return ladder.getLadder();
    }

    public List<Pair> getActivePairs() {
        split();
        return activePairs;
    }

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>();
        for (Pair pair : ladder.getLadder()) {
            players.addAll(pair.getPlayers());
        }
        return players;
    }

    public List<Pair> getPassivePairs() {
        split();
        return passivePairs;
    }

    public boolean addNewPair(Pair newPair) { //Reports if it was successful
        boolean pairExists = ladder.getLadder().contains(newPair);
        if (!pairExists) {
            newPair.setPosition(ladder.getLadderLength());
            setIsPlaying(newPair);
            ladder.insertAtEnd(newPair);
        }
        return !pairExists;
    }

    public boolean addNewPairAtIndex(Pair newPair, int index) {
        boolean pairExists = ladder.getLadder().contains(newPair);
        if (!pairExists) {
            newPair.setPosition(ladder.getLadderLength());
            setIsPlaying(newPair);
            ladder.insertAtIndex(index, newPair);
        }
        return !pairExists;
    }

    public boolean removePairAtIndex(int index) {
        Pair pairToRemove = ladder.getPairAtIndex(index);
        return ladder.removePair(pairToRemove);
    }

    public boolean setIsPlaying(Pair pair) {
        //Set pair to playing if players are unique(returns true)
        if (ladder.getLadder().contains(pair)) {
            List<Player> team = pair.getPlayers();
            Player first = team.get(0);
            Player second = team.get(1);
            if (!searchActivePlayer(first) && !searchActivePlayer(second)) {
                pair.activate();
                getActivePairs();
                return true;
            }
        }
        return false;
    }

    public void setNotPlaying(Pair pair) {
        if (ladder.getLadder().contains(pair)) {
            pair.deActivate();
            getActivePairs();
        }
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
        if (pair.getPenalty() >= 2 && retPenalty != -1 && retPenalty != 0) {
            penalty = retPenalty - pair.getPenalty();
            pair.setPenalty(penalty);
        } else if (retPenalty == 0) {
            removePenalty(pair);
        } else {
            pair.setPenalty(retPenalty);
        }
    }

    public void processLadder(List<Scorecard<Pair>> scorecards) {
        /**
         * 1. swap the pairs around based on the result
         * 2. combine the list of both active and inactive pairs
         * 3. set the absent(-2) penalty to all passive pairs
         * 4. apply all penalties that have been previously set before the method call
         **/
        swapBetweenGroups(scorecards);
        mergeActivePassive();
        setAbsentPenaltyToPairs();
        applyPenalties();
        resetPenalties();
    }

    //*******************************************
    //  Methods used ONLY INSIDE of this class
    // .: Public for the testing.
    //*******************************************

    private void mergeActivePassive() {
        List<Integer> emptyPositions = getAvailablePos();
        assignAvailablePos(emptyPositions);
        combine();
    }

    private List<Integer> getAvailablePos() {
        int totalPos = ladder.getLadderLength();
        int notPlaying = passivePairs.size();
        List<Integer> takenPositions = new ArrayList<Integer>();
        List<Integer> emptyPositions = new ArrayList<Integer>();
        int index = 0;

        if (notPlaying == 0) {
            return emptyPositions;
        }

        for (Pair current : passivePairs) {
            takenPositions.add(current.getPosition());
        }

        emptyPositions.addAll(findMissingPos(0, takenPositions.get(0)));

        while (index < (takenPositions.size() - 1)) {
            emptyPositions.addAll(findMissingPos(takenPositions.get(index), takenPositions.get(++index)));
        }

        if (takenPositions.get(notPlaying - 1) != totalPos) {
            emptyPositions.add(totalPos);
        }

        return emptyPositions;
    }

    private void assignAvailablePos(List<Integer> emptyPositions) {
        int index = 0;
        if (emptyPositions.isEmpty()) {
            return;
        }
        for (Pair current : activePairs) {
            current.deActivate();
            current.setPosition(emptyPositions.get(index));
            index++;
        }
    }

    private List<Integer> findMissingPos(Integer startPos, Integer endPos) {
        List<Integer> emptyPositions = new ArrayList<Integer>();
        int difference = Math.subtractExact(endPos, startPos);
        if (difference == 2) {
            emptyPositions.add(++startPos);
        } else {
            for (int i = 1; i < difference; i++) {
                emptyPositions.add(startPos + i);
            }
        }

        return emptyPositions;
    }

    private void split() {
        List<Pair> fullLadder = ladder.getLadder();

        activePairs = findPairs(fullLadder, true);
        passivePairs = findPairs(fullLadder, false);
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

    private List<Pair> swapBetweenGroups(List<Scorecard<Pair>> scorecards) {
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
                if (p1.getPosition() == p2.getPosition()) {
                    result = p1.getOldPosition() - p2.getOldPosition();
                } else {
                    result = p1.getPosition() - p2.getPosition();
                }
                return result;
            }
        };
    }

    private List<Pair> findPairs(List<Pair> fullLadder, boolean isPlaying) {
        return fullLadder.stream()
                .filter(p -> p.isPlaying() == isPlaying)
                .collect(Collectors.toList());
    }

    private boolean searchActivePlayer(Player player) {
        split();
        for (Pair current : activePairs) {
            if (current.hasPlayer(player)) {
                return true;
            }
        }
        return false;
    }

    public void swapPair(int firstIndex, int secondIndex) {
        List<Pair> listPairs = ladder.getLadder();

        Pair first = listPairs.get(firstIndex);
        Pair second = listPairs.get(secondIndex);

        first.setPosition(secondIndex + 1);
        second.setPosition(firstIndex + 1);

        listPairs.set(firstIndex, second);
        listPairs.set(secondIndex, first);

        ladder.assignNewLadder(listPairs);
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
        List<Pair> currentLadder = getLadder();

        for (Pair currentPair : currentLadder) {
            currentPair.setPosition(currentPair.positionAfterPenalty());
        }

        Collections.sort(currentLadder, getPairPositionComparator());
        fixPairPositionOnLadder();
    }

    private void fixPairPositionOnLadder() {
        int position = 0;
        for (Pair currentPair : getLadder()) {
            position++;
            currentPair.setPosition(position);
        }
    }

    private void resetPenalties() {
        getLadder().forEach(this::removePenalty);
    }

    private void saveLadderToDBFile(String fileName) {
        DBManager.saveToDB(this.ladder, fileName);
    }

    public void printLadder() {
        for (Pair currentPair : getLadder()) {
            System.out.println(currentPair);
        }
        System.out.println();
    }

}
