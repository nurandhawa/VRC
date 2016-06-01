//Pair should have information about pairs activity
//Ladder shoud return the size of itself

package ca.sfu.teambeta.core;

import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Gordon Shieh on 25/05/16.
 */
public class Pair {

    private ArrayList<Player> Players = new ArrayList<>();
    private Date DateCreated;
    private int Position;
    private int GroupNumber;
    private int GroupPosition;
    private boolean Activity;
    private int Penalty;

    Pair(Player firstPlayer, Player secondPlayer){
        Players.add(firstPlayer);
        Players.add(secondPlayer);
        DateCreated = new Date(); //sets to current Date
        Activity = true;
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
        this.Position = Position;
    }

    public int getPosition(){
        return Position;
    }

    public int getGroupPosition(){
        return GroupPosition;
    }

    public void setGroupPosition(int GroupPosition){
        this.GroupPosition = GroupPosition;
    }

    public int getGroupNum(){
        return GroupNumber;
    }

    public void setGroupNum(int GroupNumber){
        this.GroupNumber = GroupNumber;
    }

    public void setPassive(){
        Activity = false;
        Penalty = 0;
    }

    public void setActive(){
        Activity = true;
    }

    public boolean isActive(){
        return Activity;
    }

    public int positionAfterPenalty(){
        int newPosition = Position + Penalty;
        return newPosition;
    }

    public void miss(){
        Penalty = 10;
    }

    public void late(){
        Penalty = 4;
    }
}
