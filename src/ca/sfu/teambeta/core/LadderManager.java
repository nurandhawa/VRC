package ca.sfu.teambeta.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by constantin on 27/05/16.
 */
public class LadderManager {
    private Ladder ladder = new Ladder();

    public void resetLadder(){
        //To be optimized... (reset penalties)

        List<Pair> newLadder = new ArrayList<>();
        for (Pair current : ladder.getActivePair()){
            int first = current.getPosition();
            if (ladder.getPassivePair().isEmpty()){
                newLadder.addAll(ladder.getActivePair());
                ladder.makePassiveEmpty();
                break;
            }
            Pair obj = ladder.getPassivePair().get(0);
            int second = obj.getPosition();

            if (first > second){
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

    public void penaltyManager(){
        //not implemented
    }

    public void swapBetweenGroups(){
        //not implemented
    }
}
