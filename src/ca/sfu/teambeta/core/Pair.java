package ca.sfu.teambeta.core;

//Pair should have information about pairs activity
//Ladder should return the size of itself

import com.google.gson.annotations.Expose;
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

import ca.sfu.teambeta.persistence.Persistable;


/**
 * Created by Gordon Shieh on 25/05/16.
 */
@Entity(name = "Pair")
@Embeddable
public class Pair extends Persistable {
    private static final boolean DEFAULT_PLAYING_STATUS = true;

    @ManyToMany(cascade = CascadeType.ALL)
    @Expose
    private List<Player> team = new ArrayList<>();

    @Column(name = "date_created")
    @Type(type = "timestamp")
    private Date dateCreated;

    @Transient
    @Expose
    private int position;
    @Transient
    private int penalty;
    @Transient
    @Expose
    private boolean isPlaying;

    public Pair() {
    }

    public Pair(Player firstPlayer, Player secondPlayer) {
        team.add(firstPlayer);
        team.add(secondPlayer);
        dateCreated = new Date();
        position = 0;
        penalty = 0;
        this.isPlaying = DEFAULT_PLAYING_STATUS;
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

    public boolean hasPlayer(Player searchPlayer) {
        return (getPlayers().get(0).equals(searchPlayer)
                || getPlayers().get(1).equals(searchPlayer));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;

        if (other == null || getClass() != other.getClass()) return false;

        Pair otherPair = (Pair) other;

        return team.equals(otherPair.getPlayers());
    }

    @Override
    public int hashCode() {
        return getPlayers().hashCode();
    }

    public String toString() {
        return position
                + ") " +  getPlayers().get(0).getFirstName()
                + " & " + getPlayers().get(1).getFirstName()
                + " " + isPlaying;
    }
}