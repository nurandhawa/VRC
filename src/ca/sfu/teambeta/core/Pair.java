package ca.sfu.teambeta.core;

//Pair should have information about pairs activity
//Ladder should return the size of itself

import com.google.gson.annotations.Expose;

import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ManyToMany(cascade = CascadeType.ALL)
    @Expose
    private Set<Player> players = new HashSet<>();
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
    @Expose
    private int pairScore;

    public Pair() {
    }

    public Pair(Player firstPlayer, Player secondPlayer) {
        players.add(firstPlayer);
        players.add(secondPlayer);
        dateCreated = new Date();
        position = 0;
        penalty = 0;
        this.isPlaying = true;
    }

    public Pair(Player firstPlayer, Player secondPlayer, boolean isPlaying) {
        players.add(firstPlayer);
        players.add(secondPlayer);
        dateCreated = new Date();
        position = 0;
        penalty = 0;
        this.isPlaying = isPlaying;
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public Date getDateCreated() {
        return dateCreated;
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

        return players.equals(otherPair.players);
    }

    @Override
    public int hashCode() {
        return getPlayers().hashCode();
    }

    public String toString() {
        return position
                + ") " +  getPlayers().get(0).getFirstName()
                + " & " + getPlayers().get(1).getFirstName();
    }

    public int getPairScore() {
        return pairScore;
    }

    public void setPairScore(int pairScore) {
        this.pairScore = pairScore;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }
}
