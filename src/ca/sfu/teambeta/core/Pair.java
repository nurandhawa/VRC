package ca.sfu.teambeta.core;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Gordon Shieh on 25/05/16.
 */
public class Pair {
    private ArrayList<Player> team = new ArrayList<>();
    private Date dateCreated;
    private int position;
    private int penalty;
<<<<<<< HEAD
    private boolean isPlaying;

    public Pair(Player firstPlayer, Player secondPlayer, boolean isPlaying){
        team.add(firstPlayer);
        team.add(secondPlayer);
        this.isPlaying = isPlaying;
>>>>>>> dc2f92b3850b156d2118c3378f0716ffd2d157f8
        dateCreated = new Date(); //sets to current Date
        position = 0;
        penalty = 0;
    }

    public Date whenCreated(){
        return dateCreated;
    }

    public boolean hasPlayer(Player searchPlayer){
        return (team.get(0).equals(searchPlayer) || team.get(1).equals(searchPlayer));
    }

    public boolean hasPlayer(Player firstPlayer, Player secondPlayer){
        return (team.get(0).equals(firstPlayer) || team.get(1).equals(firstPlayer))
                && (team.get(0).equals(secondPlayer) || team.get(1).equals(secondPlayer));
    }

    public void setPosition(int Position){
        this.position = Position;
    }

    public int getPosition(){
        return position;
    }

    //Penalty related methods

    public int positionAfterPenalty(){
        int newPosition = position + penalty;
        penalty = 0;
        return newPosition;
    }

    public void setPenalty(int penalty){
        this.penalty = penalty;
    }

<<<<<<< HEAD
    public boolean isPlaying() {
        return isPlaying;
    }

    public String toString() {
        return "Player 1: " + team.get(0).getName() + " Player 2:" + team.get(1).getName();
=======
    public String toString() {
        return "Player 1: " + team.get(0).getPlayerName() + " Player 2:" + team.get(1).getPlayerName();
>>>>>>> dc2f92b3850b156d2118c3378f0716ffd2d157f8
    }

}