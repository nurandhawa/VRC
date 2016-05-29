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

        List<Pair> newLadder = new ArrayList<>();
        for (Pair current : ladder.getActivePair()){
            int first = current.getPosition();
            if (ladder.getPassivePair().isEmpty()){
                newLadder.addAll(ladder.getActivePair());
                ladder.makeActiveEmpty();
                break;
            }
            Pair obj = ladder.getPassivePair(0);
            int second = obj.getPosition();

            if (first < second){
                newLadder.add(current);
                ladder.removePair(current);
            }else{
                newLadder.add(obj);
                ladder.removePair(obj);
            }
        }

        if ( ! ladder.getPassivePair().isEmpty()){
            newLadder.addAll(ladder.getPassivePair());
            ladder.makePassiveEmpty();
        }
        ladder.assignNewLadder(newLadder);
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
