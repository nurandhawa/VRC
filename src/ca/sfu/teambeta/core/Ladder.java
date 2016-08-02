package ca.sfu.teambeta.core;

import com.google.gson.annotations.Expose;

import org.hibernate.annotations.Type;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;

import ca.sfu.teambeta.persistence.Persistable;

/**
 * Ladder ranking object. Contains a List of pairs to indicate the ranking of each pair
 */
@Entity(name = "Ladder")
public class Ladder extends Persistable {
    //used for shiftPositions
    private static final int SHIFT_LEFT = 1;
    private static final int SHIFT_RIGHT = 2;

    @Column(name = "date_created")
    @Type(type = "timestamp")
    private Date modifiedDate = new Date();

    @ManyToMany(cascade = CascadeType.ALL)
    @OrderColumn
    @Expose
    private List<Pair> pairs;

    public Ladder() {
    }

    public Ladder(List<Pair> ladder) {
        this.pairs = ladder;
    }

    public boolean contains(Pair pair) {
        return pairs.contains(pair);
    }

    //returns false if pair was not found
    // Must create a new List and reassign to Pairs
    // Hibernate may sometimes use an ImmutableList,
    // so may cause exceptions when trying to remove
    public boolean removePair(Pair pair) {
        List<Pair> newList = new LinkedList<>(pairs);
        boolean success = newList.remove(pair);
        pairs = newList;
        modifiedDate = new Date();
        return success;
    }

    public boolean insertAtIndex(int index, Pair pair) {
        List<Pair> newList;
        if (pairs == null) {
            newList = new ArrayList<Pair>() {
                {
                    add(pair);
                }
            };
            pairs = newList;
        } else {
            newList = new LinkedList<>(pairs);

            if (0 <= index && index <= pairs.size()) {
                newList.add(index, pair);
                pairs = newList;
            } else {
                return false;
            }
        }
        modifiedDate = new Date();
        return true;
    }

    public void insertAtEnd(Pair pair) {
        List<Pair> newList;

        if (pairs == null) {
            newList = new ArrayList<>();
        } else {
            newList = new LinkedList<>(pairs);
        }

        newList.add(pair);
        pairs = newList;
        modifiedDate = new Date();
    }

    public List<Pair> getPairs() {
        return pairs;
    }

    public Pair getPairAtIndex(int index) {
        return pairs.get(index);
    }

    public int getLadderLength() {
        return pairs.size();
    }

    // DO NOT USE ME!! Unless you are specifically dealing with manual override
    public void setNewPairs(List<Pair> newPairList) {
        pairs = newPairList;
        modifiedDate = new Date();
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final Ladder otherLadder = (Ladder) other;
        return getPairs().equals(otherLadder.getPairs());
    }

    @Override
    public int hashCode() {
        return 31 * pairs.hashCode();
    }
}
