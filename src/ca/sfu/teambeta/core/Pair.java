package ca.sfu.teambeta.core;

//Pair should have information about pairs activity
//Ladder shoud return the size of itself

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Gordon Shieh on 25/05/16.
 */
public class Pair {
    private List<Player> team = new ArrayList<>();
    private Date dateCreated;
    private int position;
    private int penalty;
    private boolean isPlaying;
    private int prevPosition;

    public Pair(Player firstPlayer, Player secondPlayer) {
        team.add(firstPlayer);
        team.add(secondPlayer);
        dateCreated = new Date();
        position = 0;
        penalty = 0;
        this.isPlaying = true;
    }

    public Pair(Player firstPlayer, Player secondPlayer, boolean isPlaying) {
        team.add(firstPlayer);
        team.add(secondPlayer);
        dateCreated = new Date();
        position = 0;
        penalty = 0;
        this.isPlaying = isPlaying;
    }

    public List<Player> getPlayers() {
        return team;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        if(this.position == 0) {
            prevPosition = position;
        }
        this.position = position;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public void activate(){
        this.isPlaying = true;
    }

    public void deActivate(){
        this.isPlaying = false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public int positionAfterPenalty() {
        int newPosition = position + penalty;
        penalty = 0;
        return newPosition;
    }

    public int getPrevPosition() {
        return prevPosition;
    }

    public boolean hasPlayer(Player searchPlayer) {
        return (team.get(0).equals(searchPlayer) || team.get(1).equals(searchPlayer));
    }

    public boolean hasPlayer(Player firstPlayer, Player secondPlayer) {
        return (team.get(0).equals(firstPlayer) || team.get(1).equals(firstPlayer))
                && (team.get(0).equals(secondPlayer) || team.get(1).equals(secondPlayer));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pair pair = (Pair) o;

        return team.equals(pair.getPlayers())
                && position == pair.getPosition()
                && isPlaying == pair.isPlaying();
    }

    public String toString() {
        return "Team: " + team.get(0).getName()
                + " " + team.get(1).getName()
                + " Position: " + position;
    }
}
