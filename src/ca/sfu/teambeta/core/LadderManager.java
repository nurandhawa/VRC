package ca.sfu.teambeta.core;

import java.lang.reflect.Array;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by constantin on 27/05/16.
 */
public class LadderManager {
    private Ladder ladder = new Ladder();
    private final int DROP_PASSIVE = 2;
    private final int DROP_MISS = 10;
    private final int DROP_LATE = 4;

    public void resetLadder(){
        //Combines active and passive pairs after all the matches were completed
        //To be optimized... as penalties for late and missed pairs are not applied
        List<Pair> passivePairs = ladder.getPassivePair();
        List<Pair> activePairs = ladder.getActivePair();
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
        ladder.makeActiveEmpty();
        ladder.assignNewLadder(newLadder);


        //
        // MOVE late and missed pairs in the ladder
        //      NOT IMPLEMENTED
        //
        
    }

    public void accident(Pair pair){
        pair.setPenalty( - DROP_PASSIVE); //Results in penalty of 0
        ladder.setNotPlaying(pair);
    }

    public void miss(Pair pair){
        pair.setPenalty(DROP_MISS);
        //Remains active
    }

    public void late(Pair pair){
        pair.setPenalty(DROP_LATE);
        //Remains active
    }

    public void penaltyManager(){
        List<Pair> absentPairs = ladder.getPassivePair();
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
        }
        if (positions[notPlaying - 2] > allMembers){//Player before the last one exceeded the ladder size
            positions[notPlaying - 2] = allMembers;
        }

        //Move players up on one position if adjacent pairs have the same position in the ladder
        for (int j = notPlaying - 1; j > 1; j--){
            if (positions[j-1] == positions[j]){
                positions[j-1] -= 1;
            }
        }

        i = 0;
        for (Pair current : absentPairs){
            current.setPosition(positions[i]);
            i++;
        }
        //Players who didn't play have new positions
        ladder.assignNewLadder(absentPairs);
    }

    public void swapBetweenGroups(){
        //not implemented
    }
}
