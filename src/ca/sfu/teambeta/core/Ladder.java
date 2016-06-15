package ca.sfu.teambeta.core;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;

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
    @ManyToMany()
    @Column(name = "pair_id")
    @OrderBy("uuidColumn")
    private List<Pair> pairs;
    private int numPairs;

    public Ladder() {
        //passive
        //members
        //passivePairs from the DB
    }

    public Ladder(List<Pair> ladder) {
        this.ladder = ladder;
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
        ladder.add(pair);
        ladder.get(ladder.size() - 1).setPosition(ladder.size());
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
        return ladder.size();
    }
/* omitted but keeping in case we ever need it
    public void dumpLadder() {
        for(Pair pair : passivePairs) {
            System.out.println(pair);
        }
    } */
}
