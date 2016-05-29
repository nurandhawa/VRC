package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by constantin on 27/05/16.
 */
public class LadderManager {
    private final int DROP_PASSIVE = 2;
    private final int DROP_MISS = 10;
    private final int DROP_LATE = 4;

    private Ladder ladder = new Ladder();
    private List<Pair> activePairs;
    private List<List<Pair>> groups;
    private int active;

    public void putGroups(ArrayList<List<Pair>> groups){
        this.groups = groups;
    }

    public List<Pair> getPlayingPairs(){
        return activePairs;
    }

    public void addNewPair(Pair newPair){
        newPair.setPosition(ladder.size());
        ladder.insert(ladder.size(), newPair);
        ladder.increaseSize();
    }

    public void setIsPlaying(Pair pair){
        if (ladder.getPassivePairs().contains(pair)){
            int position = pair.getPosition();
            insertPlaying(position, pair);
            ladder.removePair(pair);
            active++;
        }
    }

    public void setNotPlaying(Pair pair){
        if (activePairs.contains(pair)) {
            int position = pair.getPosition();
            ladder.insert(position, pair);
            activePairs.remove(pair);
            active--;
        }
    }

    public void accident(Pair pair){
        pair.setPenalty( - DROP_PASSIVE); //Results in penalty of 0
        setNotPlaying(pair);
    }

    public void miss(Pair pair){
        pair.setPenalty(DROP_MISS);
        //Remains active
    }

    public void late(Pair pair){
        pair.setPenalty(DROP_LATE);
        //Remains active
    }

    public void removePenalty(Pair pair){
        pair.setPenalty(0);
    }

    public int sizePlaying(){
        return active;
    }

    public void resetLadder(){
        //Combines active and passive pairs after all the matches were completed
        //To be optimized... as penalties for late and missed pairs are not applied
        List<Pair> passivePairs = ladder.getPassivePairs();
        int allMembers = ladder.size();
        int notPlaying = passivePairs.size();
        int arePlaying = allMembers - notPlaying;
        int[] positions = new int[notPlaying];
        int[] emptyPositions = new int[arePlaying];

        int i = 0;
        for (Pair current : passivePairs){
            positions[i] = current.getPosition();
            i++;
        }

        //Create array of empty positions for participants
        i = 0;
        int j = 0;
        for(int position = 1; position <= allMembers; position++){
            if (position == positions[j]){
                //This position is taken
                j++;
            }else{
                emptyPositions[i] = position; //Position not used
                i++;
            }
        }

        //Put participants in empty positions
        i = 0;
        for (Pair current : activePairs){
            current.setPosition(emptyPositions[i]);
            i++;
        }

        //Combine all pairs in new ladder
        List<Pair> newLadder = new ArrayList<>();
        i = 0;
        j = 0;
        while((i != notPlaying - 1) || (j != arePlaying - 1)){
            Pair pPair = passivePairs.get(i);
            Pair aPair = activePairs.get(j);
            if (pPair.getPosition() < aPair.getPosition()) {
                newLadder.add(pPair);
                i++;
            }else{
                newLadder.add(aPair);
                j++;
            }
        }
        activePairs.clear();
        ladder.assignNewLadder(newLadder);


        //
        // MOVE late and missed pairs in the ladder
        //      NOT IMPLEMENTED
        //
        
    }

    public void penaltyManager(){
        List<Pair> absentPairs = ladder.getPassivePairs();
        int allMembers = ladder.size();
        int notPlaying = absentPairs.size();
        int[] positions = new int[notPlaying];

        int i = 0;
        for (Pair current : absentPairs){
            current.setPenalty(DROP_PASSIVE);
            positions[i] = current.positionAfterPenalty();
            i++;
        }
        if (positions[notPlaying - 1] > allMembers){ //Last player exceeded the ladder size
            positions[notPlaying - 1] = allMembers;
            absentPairs.get(notPlaying - 1).setPosition(allMembers);
        }
        if (positions[notPlaying - 2] > allMembers){//Player before the last one exceeded the ladder size
            positions[notPlaying - 2] = allMembers;
        }
        //Move players up on one position if adjacent pairs have the same position in the ladder
        for (i = notPlaying - 1; i > 1; i--){
            if (positions[i-1] == positions[i]){
                absentPairs.get(i).setPosition(positions[i] - 1);
            }
        }
        //Array of absent pairs is sorted in ascending oder according to position
        for (i = 0; i < absentPairs.size() - 1; i++){
            Pair first = absentPairs.get(i);
            Pair second = absentPairs.get(i+1);
            int posFirst = first.getPosition();
            int posSecond = second.getPosition();
            if (posFirst > posSecond){
                absentPairs.set(i, second);
                absentPairs.set(i+1, first);
            }
        }
        //Players who didn't play have new positions
        ladder.assignNewLadder(absentPairs);
    }

    public void swapBetweenGroups(){
        //
        // SWAPPING between groups and saving result in activePairs
        //      NOT IMPLEMENTED
        //
    }

    private void insertPlaying(int position, Pair pair){
        int i = 0;
        boolean inserted = false;
        for (Pair current : activePairs){
            if (current.getPosition() > position){
                activePairs.add(i, pair);
                inserted = true;
                break;
            }
        }
        if (! inserted){
            activePairs.add(pair);
        }
        active++;
    }
}
