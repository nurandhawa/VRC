package ca.sfu.teambeta.core;

import java.util.ArrayList;

/**
 * Created by Gordon Shieh on 25/05/16.
 */
public class Ladder {
    private ArrayList<Pair> listOfPairs = new ArrayList<>();
    private ArrayList<Pair> activePairs = new ArrayList<>();
    private int Members;

    public void activatePair(Pair somePair){
        if (!activePairs.contains(somePair)) {
            activePairs.add(somePair);
            listOfPairs.remove(somePair);
        }
    }



    public void makeGroups(){
        //assign group numbers to every pair
    }

    public void rearrangeGroups(){
        //regroup when all info about w/l is available
    }

    public void swapBetweenGroups(){
        if (Members >= 2){
            int lastIndex = Members - (Members % 2) - 2;
            for(int i = 0; i < lastIndex; i += 2){
                int j = i + 1;
                if (activePairs.get(i).getGroupNum() != activePairs.get(j).getGroupNum()){ //different groups
                    swapPair(i, j);
                }
            }
        }
    }

    public void addNewPair(Pair newPair){
        newPair.setPosition(Members);
        listOfPairs.add(newPair);
        Members++;
    }

    public void removePair(Player firstPlayer, Player secondPlayer){
        for (Pair current : listOfPairs){
            if (current.hasPlayer(firstPlayer, secondPlayer)){
                int iPair = current.getPosition();
                listOfPairs.remove(iPair);
                break;
            }
        }
        Members--;
    }

    public void swapPair(int fromIndex, int toIndex){
        Pair firstPair = listOfPairs.get(fromIndex);
        int firstPosition = firstPair.getPosition();
        Pair secondPair = listOfPairs.get(toIndex);
        int secondPosition = secondPair.getPosition();

        firstPair.setPosition(secondPosition);
        firstPair.setPosition(firstPosition);

        listOfPairs.add(fromIndex, secondPair);
        listOfPairs.add(toIndex, firstPair);
    }
}
