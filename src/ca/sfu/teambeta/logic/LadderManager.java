package ca.sfu.teambeta.logic;

import java.util.ArrayList;
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
        ladder = new Ladder(new ArrayList<Pair>());
        activePairs = new ArrayList<>();
        passivePairs = new ArrayList<>();
    }

    public LadderManager(List<Pair> dbLadder) {
        ladder = new Ladder(dbLadder);
        activePairs = findPairs(ladder.getLadder(), true);
        passivePairs = findPairs(ladder.getLadder(), false);
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
                return true;
            }
        }
        return false;
    }

    public void setNotPlaying(Pair pair) {
        if (ladder.getLadder().contains(pair)) {
            pair.deActivate();
        }
    }

    public void removePenalty(Pair pair) {
        pair.setPenalty(Penalty.ZERO.getPenalty());
    }

    public void processLadder(List<Scorecard<Pair>> scorecards) {
        //The following functions have to be executed ONLY in such order
        applyAbsentPenalty(); //Absent pairs will Drop, except pairs with Accident
        //Passive pairs have changes
        swapBetweenGroups(scorecards); //Swap adjacent players between groups
        //Now active and passive pairs have changes
        mergeActivePassive(); //New positions for played pairs will be set
        applyLateMissedPenalty(); //Last penalty adjustments
        savePositions();
    }

    //*******************************************
    //  Methods used ONLY INSIDE of this class
    // .: Public for the testing.
    //*******************************************

    private void applyAbsentPenalty() {
        split();
        int notPlaying = passivePairs.size();
        int[] positions = callAbsentPenalty();
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

    private void mergeActivePassive() {
        //Operations are done with separated ladder, then merged
        int allMembers = ladder.getLadderLength();
        int notPlaying = passivePairs.size();
        int arePlaying = allMembers - notPlaying;
        int[] positions = new int[notPlaying];
        int[] emptyPositions = new int[arePlaying];

        int positionIndex = 0;
        for (Pair current : passivePairs) {
            positions[positionIndex] = current.getPosition();
            positionIndex++;
        }

        //Create array of empty positions for participants
        int emptyPostitionIndex = 0;
        int currentPositionIndex = 0;
        for (int position = 1; position <= allMembers; position++) {
            if (position < positions.length && position == positions[currentPositionIndex]) {
                //This position is taken
                currentPositionIndex++;
            } else {
                //Position not used
                emptyPositions[emptyPostitionIndex] = position;
                emptyPostitionIndex++;
            }
        }

        //Assign participants to empty positions and sat them to not playing
        int index = 0;
        for (Pair current : activePairs) {
            current.deActivate();
            current.setPosition(emptyPositions[index]);
            index++;
        }
        combine();
    }

    private void applyLateMissedPenalty() {
        List<Pair> pairList = ladder.getLadder();
        int size = ladder.getLadderLength();

        for (Pair current : pairList) {
            int penalty = current.getPenalty();
            if (penalty != 0) {
                current.setPenalty(Penalty.ZERO.getPenalty());
                int actualPosition = current.getPosition();
                int newPosition = current.getOldPosition() + penalty;
                if (newPosition >= size) {
                    newPosition = size - 1;
                }
                for (int i = actualPosition; i <= newPosition; i++) {
                    swapPair(i - 1, i);
                }
            }
        }
    }

    private void savePositions() {
        List<Pair> listPairs = ladder.getLadder();
        for (Pair current : listPairs) {
            current.establishPosition();
        }
    }


    private void split() {
        List<Pair> fullLadder = ladder.getLadder();

        activePairs = findPairs(fullLadder, true);
        passivePairs = findPairs(fullLadder, false);
    }

    private void combine() {
        List<Pair> newLadder = new ArrayList<>();

        newLadder.addAll(passivePairs);
        newLadder.addAll(activePairs);

        passivePairs.clear();
        activePairs.clear();
        ladder = new Ladder(newLadder);
        sortByPosition();
    }


    private int[] callAbsentPenalty() {
        int[] passivePairsPos = new int[passivePairs.size()];
        int absentPairIndex = 0;

        for (Pair current : passivePairs) {
            current.setPenalty(Penalty.ABSENT.getPenalty());
            passivePairsPos[absentPairIndex] = current.positionAfterPenalty();
            absentPairIndex++;
        }

        return passivePairsPos;
    }

    private int[] fixPosAbsentPenalty(int[] passivePairsPos) {
        int ladderLength = ladder.getLadderLength();

        for (int i = passivePairs.size() - 1; i > 0; i--) {
            if (passivePairsPos[i] > ladderLength) {
                passivePairsPos[i] = ladderLength;
                passivePairs.get(i).setPosition(ladderLength);
                ladderLength--;
            }
        }

        return passivePairsPos;
    }

    private void swapPlayers(List<Pair> firstGroup, List<Pair> secondGroup) {
        // This method swaps the last member of 'firstGroup' with the first member of 'secondGroup'

        int lastIndexOfFirstGroup = firstGroup.size() - 1;

        Pair temp = firstGroup.get(lastIndexOfFirstGroup);

        firstGroup.set(lastIndexOfFirstGroup, secondGroup.get(0));
        secondGroup.set(0, temp);

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

    private void sortByPosition() {
        Comparator<Pair> makeSorter = new Comparator<Pair>() {
            @Override
            public int compare(Pair p1, Pair p2) {
                return p1.getPosition() - p2.getPosition();
            }
        };

        java.util.Collections.sort(ladder.getLadder(), makeSorter);
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

}
