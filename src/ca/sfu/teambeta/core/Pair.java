package ca.sfu.teambeta.core;

import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Gordon Shieh on 25/05/16.
 */
public class Pair {
    private ArrayList<Player> Players = new ArrayList<>();
    private Date DateCreated;
    private int position;
    private int penalty;

    public Pair(Player firstPlayer, Player secondPlayer){
        Players.add(firstPlayer);
        Players.add(secondPlayer);
        DateCreated = new Date(); //sets to current Date
        position = 0;
        penalty = 0;
    }

    public Date whenCreated(){
        return DateCreated;
    }

    public boolean hasPlayer(Player searchPlayer){
        return (Players.get(1).equals(searchPlayer) || Players.get(2).equals(searchPlayer));
    }

    public boolean hasPlayer(Player firstPlayer, Player secondPlayer){
        return (Players.get(1).equals(firstPlayer) || Players.get(2).equals(firstPlayer))
                && (Players.get(1).equals(secondPlayer) || Players.get(2).equals(secondPlayer));
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

    public void miss(){
        penalty = 10;
    }

    public void late(){
        penalty = 4;
    }

    public void absent(){
        penalty = 2;
    }
}