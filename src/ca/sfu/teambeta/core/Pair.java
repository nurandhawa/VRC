package ca.sfu.teambeta.core;

//Pair should have information about pairs activity
//Ladder shoud return the size of itself

import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;


/**
 * Created by Gordon Shieh on 25/05/16.
 */
@Entity(name = "Pair")
@Embeddable
public class Pair extends Persistable {

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Player> team = new ArrayList<>();
    private String id;
    @Column(name = "date_created")
    @Type(type = "timestamp")
    private Date dateCreated;

    @Transient
    private int position;
    @Transient
    private int oldPosition;
    @Transient
    private int penalty;
    @Transient
    private boolean isPlaying;

    public Pair() {
    }

    public Pair(Player firstPlayer, Player secondPlayer) {
        id = "" + firstPlayer.getPlayerID() + secondPlayer.getPlayerID();
        team.add(firstPlayer);
        team.add(secondPlayer);
        dateCreated = new Date();
        position = 0;
        oldPosition = 0;
        penalty = 0;
        this.isPlaying = true;
    }

    public Pair(Player firstPlayer, Player secondPlayer, boolean isPlaying) {
        id = "" + firstPlayer.getPlayerID() + secondPlayer.getPlayerID();
        team.add(firstPlayer);
        team.add(secondPlayer);
        dateCreated = new Date();
        position = 0;
        oldPosition = 0;
        penalty = 0;
        this.isPlaying = isPlaying;
    }

    public String getId(){
        return id;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(team);
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        if (oldPosition == 0) {
            this.oldPosition = position;
        }
    }

    public int getOldPosition() {
        return oldPosition;
    }

    public void establishPosition() {
        this.oldPosition = position;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public void activate() {
        this.isPlaying = true;
    }

    public void deActivate() {
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

    public boolean hasPlayer(Player searchPlayer) {
        return (getPlayers().get(0).equals(searchPlayer) || getPlayers().get(1).equals(searchPlayer));
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
        return getPlayers().get(0).getFirstName()
                + " & " + getPlayers().get(1).getFirstName()
                + "  Playing? " + isPlaying;
    }
}
