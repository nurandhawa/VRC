package ca.sfu.teambeta.logic;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;

/**
 * Created by constantin on 27/05/16.
 */
public class LadderManager {
    private final int DROP_PASSIVE = 2;
    private final int DROP_MISS = 10;
    private final int DROP_LATE = 4;

    private Ladder ladder;
    private List<Pair> activePairs;
    private List<Pair> passivePairs;
    private List<List<Pair>> groups;

    //FUNCTIONS TO BE REMOVED just for testing
    public List<Pair> getActivePairs(){
        split();
        return activePairs;
    }

    public List<Pair> getPassivePairs(){
        //split();
        return passivePairs;
    }
    //

    public LadderManager(){
        ladder = new Ladder(new ArrayList<Pair>());
        activePairs = new ArrayList<>();
        passivePairs = new ArrayList<>();
        groups = new ArrayList<>();
    }

    public void putGroups(ArrayList<List<Pair>> groups) {
        this.groups = groups;
    }

    public List<Pair> getLadder(){
        return ladder.getLadder();
    }
    //init MUST be called to use LadderManager object
    /*NOTE: I am assuming that the LadderManager will somehow get a List<Pair> somehow; whether it is a new List<Pair>
      or retrieved from processing the DB. If this design is not suitable, please discuss it with me so we can change it.
      - Sam 5/30/2016 */
    public void init(List<Pair> dbLadder) {
        ladder = new Ladder(dbLadder);
    }

    public void addNewPair(Pair newPair) {
        newPair.setPosition(ladder.getLadderLength());
        ladder.insertAtEnd(newPair);
        ladder.incLadderLength();
    }

    public void setIsPlaying(Pair pair){
        if (ladder.getLadder().contains(pair)){
            pair.activate();
        }
    }

    public void setNotPlaying(Pair pair){
        if (ladder.getLadder().contains(pair)) {
            pair.deActivate();
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

    public void processLadder(){
        applyAbsentPenalty(); //Absent pairs will Drop, except pairs with Accident
        //Passive pairs have changes
        swapBetweenGroups(); //Swap adjacent players between groups
        //Now active and passive pairs have changes
        mergeActivePassive(); //New positions for played pairs will be set
        applyLateMissedPenalty(); //Last penalty adjustments
    }

    //*******************************************
    //  Methods used ONLY INSIDE of this class
    //*******************************************

    public void mergeActivePassive(){
        //Operations are done with separated ladder, then merged
        int allMembers = ladder.getLadderLength();
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
                //Position not used
                emptyPositions[i] = position;
                i++;
            }
        }

        //Assign participants to empty positions and sat them to not playing
        i = 0;
        for (Pair current : activePairs){
            current.deActivate();
            current.setPosition(emptyPositions[i]);
            i++;
        }
        combine();
    }

    public void applyAbsentPenalty(){
        split();
        int allMembers = ladder.getLadderLength();
        int notPlaying = passivePairs.size();
        int[] positions = new int[notPlaying];

        int i = 0;
        for (Pair current : passivePairs){
            current.setPenalty(DROP_PASSIVE);
            positions[i] = current.positionAfterPenalty();
            i++;
        }

        if (positions[notPlaying - 1] > allMembers){ //Last player exceeded the ladder size
            positions[notPlaying - 1] = allMembers;
            passivePairs.get(notPlaying - 1).setPosition(allMembers);
        }
        if (positions[notPlaying - 2] >= allMembers){//Player before the last one exceeded the ladder size
            positions[notPlaying - 2] = allMembers;
            passivePairs.get(notPlaying - 2).setPosition(allMembers - 1);
        }


        for(int current : positions){
            System.out.print(current);
        }
        System.out.println();

        //Move players up on one position if adjacent pairs have the same position in the ladder
        for (i = notPlaying - 1; i >= 1; i--){
            if (positions[i-1] == positions[i]){
                positions[i-1]--;
            }
        }


        for(int current : positions){
            System.out.print(current);
        }
        System.out.println();

        //Assign new positions after the penalty
        for(i = 0; i < notPlaying; i++){
            passivePairs.get(i).setPosition(positions[i]);
        }

        //Array of absent pairs is sorted in ascending oder according to position
//        for (i = 0; i < passivePairs.size() - 1; i++){
//            Pair first = passivePairs.get(i);
//            Pair second = passivePairs.get(i+1);
//            int posFirst = first.getPosition();
//            int posSecond = second.getPosition();
//            if (posFirst > posSecond){
//                passivePairs.set(i, second);
//                passivePairs.set(i+1, first);
//            }
//        }
        //Players who didn't play have new positions saved in passivePairs
    }

    public void swapBetweenGroups(){
        //
        // SWAPPING between groups and saving result in activePairs
        //      NOT IMPLEMENTED
        //
    }

    private void split(){
        List<Pair> fullLadder = ladder.getLadder();

        activePairs = findPairs(fullLadder, true);
        passivePairs = findPairs(fullLadder, false);
    }

    private void combine(){
        int notPlaying = passivePairs.size();
        int arePlaying = activePairs.size();
        List<Pair> newLadder = new ArrayList<>();
        int i = 0;
        int j = 0;
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
        passivePairs.clear();
        activePairs.clear();
        ladder =  new Ladder(newLadder);
    }

    private List<Pair> findPairs(List<Pair> fullLadder, boolean isPlaying) {
        List<Pair> newPairs = new ArrayList<>();
        for (Pair p : fullLadder) {
            if (p.isPlaying() == isPlaying) {
                newPairs.add(p);
            }
        }

        return newPairs;
    }

    private void applyLateMissedPenalty(){
        // activePairs and passivePairs now are empty, ladder has rearranged pairs.
        // NOTE some pairs have Late/Missed penalties
        //
        //          NOT IMPLEMENTED
        //
    }
}
