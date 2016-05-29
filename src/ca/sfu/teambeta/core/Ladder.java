package ca.sfu.teambeta.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gordon Shieh on 25/05/16.
 */
public class Ladder {
    //Ladder is subdivided into two Lists: playing and not playing Pairs
    //Groups are created from playing(active) pairs in GameManager
    //Members are a total amount of all pairs = active + passive
    private List<Pair> passivePairs;
    private List<Pair> activePairs;
    private List<List<Pair>> groups;
    private int active;
    private int passive;

    public Ladder(){
        active = 0;
        passive = 0;
        activePairs = new ArrayList<>();
        //passivePairs from the DB
        groups = new ArrayList<List<Pair>>();
    }

    public List<Pair> getActivePair(){
        return passivePairs;
    }

    public List<Pair> getPassivePair(){
        return passivePairs;
    }

    public void putGroups(ArrayList<List<Pair>> groups){
        this.groups = groups;
    }

    public List<List<Pair>> getGroups(){
        return groups;
    }

    public void setIsPlaying(Pair somePair){
        if (!activePairs.contains(somePair)) {
            int index = somePair.getPosition();
            activePairs.add(index, somePair);
            passivePairs.remove(somePair);
            passive--;
            active++;
        }
    }

    public void setNotPlaying(Pair somePair){
        if (!passivePairs.contains(somePair)) {
            int index = somePair.getPosition();
            passivePairs.add(index, somePair);
            activePairs.remove(somePair);
            active--;
            passive++;
        }
    }

    public void addNewPair(Pair newPair){
        newPair.setPosition(passive);
        passivePairs.add(newPair);
        passive++;
    }

    public void removePair(Pair pair){
        if ( ! activePairs.remove(pair)){
            if (passivePairs.remove(pair)){
                passive--;
            }
        }else{
            active--;
        }
    }

    public void removePair(Player firstPlayer, Player secondPlayer){
        for (Pair current : passivePairs){
            if (current.hasPlayer(firstPlayer, secondPlayer)){
                int iPair = current.getPosition();
                passivePairs.remove(iPair);
                passive--;
                break;
            }
        }
        for (Pair current : activePairs){
            if (current.hasPlayer(firstPlayer, secondPlayer)){
                int iPair = current.getPosition();
                activePairs.remove(iPair);
                active--;
                break;
            }
        }
    }

    public void swapPair(int fromIndex, int toIndex){
        Pair firstPair = passivePairs.get(fromIndex);
        int firstPosition = firstPair.getPosition();
        Pair secondPair = passivePairs.get(toIndex);
        int secondPosition = secondPair.getPosition();

        firstPair.setPosition(secondPosition);
        firstPair.setPosition(firstPosition);

        passivePairs.add(fromIndex, secondPair);
        passivePairs.add(toIndex, firstPair);
    }

    public void assignNewLadder(List<Pair> newLadder){
        passivePairs = newLadder;
    }

    public void makeActiveEmpty(){
        activePairs.clear();
    }

    public int sizePlaying(){
        return active;
    }

    public int sizeNotPlaying(){
        return passive;
    }

    public int size(){
        return active+passive;
    }
}
