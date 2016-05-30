package ca.sfu.teambeta.core;

import java.util.List;

/**
 * Created by Gordon Shieh on 25/05/16.
 */
public class Ladder {
    private List<Pair> passivePairs;
    private int members;

    public Ladder(){
        //passive
        //members
        //passivePairs from the DB
    }

    public Ladder(List<Pair> passivePairs, int members) {
        this.passivePairs = passivePairs;
        this.members = members;
    }

    public void removePair(Pair pair) {
        int index = passivePairs.indexOf(pair);
        if (index != -1) { //pair was found
            passivePairs.remove(index);
            for(int i = index; i < passivePairs.size(); i++){ //Process following pairs by moving them up
                int position = passivePairs.get(i).getPosition();
                passivePairs.get(i).setPosition(position - 1);
            }
            members--;
        }
    }

    public void removePair(Player firstPlayer, Player secondPlayer){
        boolean removed = false;
        for (Pair current : passivePairs){
            if (current.hasPlayer(firstPlayer, secondPlayer)){
                int iPair = current.getPosition();
                passivePairs.remove(iPair);
                members--;
                removed = true;
            }
            if (removed){ //Process following pairs by moving them up
                int position = current.getPosition();
                current.setPosition(position - 1);
            }
        }
    }

    public void insert(int position, Pair pair){
        int i = 0;
        boolean inserted = false;
        for (Pair current : passivePairs){
            if (current.getPosition() > position){
                passivePairs.add(i, pair);
                inserted = true;
                break;
            }
        }
        if (! inserted){
            passivePairs.add(pair);
        }
        members++;
    }

    public List<Pair> getPassivePairs(){
        return passivePairs;
    }

    public void assignNewLadder(List<Pair> newLadder){
        passivePairs = newLadder;
    }

    public void increaseSize(){
        members++;
    }

    public int size(){
        return members;
    }

    public void dumpLadder() {
        for(Pair pair : passivePairs) {
            System.out.println(pair);
        }
    }
}
