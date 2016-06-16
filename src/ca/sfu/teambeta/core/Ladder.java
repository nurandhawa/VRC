package ca.sfu.teambeta.core;

import org.hibernate.annotations.Type;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;

/**
 * Ladder ranking object. Contains a List of pairs to indicate the ranking of each pair
 */
@Entity(name = "Ladder")
public class Ladder {
    //used for shiftPositions
    private static final int SHIFT_LEFT = 1;
    private static final int SHIFT_RIGHT = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "date_created")
    @Type(type = "timestamp")
    private Date dateCreated;

    @ManyToMany
    @OrderColumn
    private List<Pair> pairs;

    public Ladder() {
    }

    public Ladder(List<Pair> ladder) {
        this.pairs = ladder;
    }

    //returns false if pair was not found
    public boolean removePair(Pair pair) {
        int index = pairs.indexOf(pair);
        if (index != -1) { //pair was found
            pairs.remove(index);
            shiftPositions(SHIFT_LEFT, index);
        } else {
            return false;
        }
        return true;
    }

    private void shiftPositions(int direction, int index) {
        if (direction == SHIFT_LEFT) {
            for (int i = index; i < pairs.size(); i++) {
                int position = pairs.get(i).getPosition();
                pairs.get(i).setPosition(position - 1);
            }
        } else if (direction == SHIFT_RIGHT) {
            for (int i = index; i < pairs.size(); i++) {
                int position = pairs.get(i).getPosition();
                pairs.get(i).setPosition(position + 1);
            }
        }
    }

    public void insertAtIndex(int index, Pair pair) {
        pairs.add(index, pair);
        pairs.get(index).setPosition(index + 1);
        shiftPositions(SHIFT_RIGHT, index + 1);
    }

    public void insertAtEnd(Pair pair) {
        pairs.add(pair);
        pairs.get(pairs.size() - 1).setPosition(pairs.size());
    }

    public List<Pair> getPairs() {
        return pairs;
    }

    public void assignNewLadder(List<Pair> newLadder) {
        pairs = newLadder;
    }

    public Pair getPairAtIndex(int index) {
        return pairs.get(index);
    }

    public int getLadderLength() {
        return pairs.size();
    }
}
