package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.Time;

import java.util.*;

/**
 * Created by constantin on 11/07/16.
 */
public class VrcTimeSelection implements TimeSelection {
    private static final Time DEFAULT_TIME_SLOT = null;
    private static final int MAX_NUM_PAIRS_PER_SLOT = 24;
    private List<Pair> allPairs;
    private List<Scorecard> allScorecards;

    public VrcTimeSelection() {
        allPairs = new ArrayList<>();
        allScorecards = new ArrayList<>();
    }

    @Override
    public int getAmountPairsByTime(List<Pair> allPairs, Time time) {
        List<Pair> pairs = getPairsByTime(allPairs, time);

        return pairs.size();
    }

    @Override
    public List<Pair> getPairsByTime(List<Pair> allPairs, Time time) {
        List<Pair> pairs = new ArrayList<>();

        for(Pair pair : allPairs) {
            Time timeSlot = pair.getTimeSlot();
            if(timeSlot == time) {
                pairs.add(pair);
            }
        }

        return pairs;
    }

    @Override
    public void clearTimeSlots(Ladder ladder) {
        for(Pair pair : ladder.getPairs()) {
            pair.setTimeSlot(DEFAULT_TIME_SLOT);
        }
    }

    @Override
    public void distributePairs(List<Scorecard> scorecards) {
        saveValues(scorecards);

        //Make schedule of groups by selecting most popular time slot
        for (Scorecard scorecard : allScorecards) {
            List<Time> timeSlots = getTimeSlotsOfGroup(scorecard);
            Time time = getDominantTime(timeSlots);
            assignTimeToGroup(scorecard, time);
        }

        //TODO distribution od groups
    }

    private void saveValues(List<Scorecard> scorecards) {
        for (Scorecard group : scorecards){
            List<Pair> pairs = group.getReorderedPairs();

            allPairs.addAll(pairs);
        }
        this.allScorecards = scorecards;
    }

    private List<Time> getTimeSlotsOfGroup(Scorecard scorecard) {
        List<Pair> pairs = scorecard.getReorderedPairs();
        List<Time> timeSlots = new ArrayList<>();

        for(Pair pair : pairs){
            Time time = pair.getTimeSlot();
            timeSlots.add(time);
        }

        return timeSlots;
    }

    private void assignTimeToGroup(Scorecard scorecard, Time time) {
        scorecard.setTimeSlot(time);

        //Update Pairs inside of the scorecard
        List<Pair> pairs = scorecard.getReorderedPairs();
        for(Pair pair : pairs) {
            pair.setTimeSlot(time);
        }
    }

    private Time getDominantTime(List<Time> timeSlots) {
        Map<Time, Integer> timeFrequency = new HashMap<>();

        //Initialize values
        for(Time time : Time.values()){
            timeFrequency.put(time, 0);
        }

        //Count how many pairs selected particular time slot
        for (Time time : timeSlots) {
            int amount = timeFrequency.get(time);
            amount++;

            timeFrequency.put(time, amount);
        }

        //Get the most popular time slot,
        //if all are the same, select the earliest time slot
        Time dominantTime = null;
        int maxFrequency = 0;
        for(Map.Entry<Time, Integer> entry : timeFrequency.entrySet()){
            int frequency = entry.getValue();

            if(frequency > maxFrequency){
                maxFrequency = frequency;
                dominantTime = entry.getKey();
            }
        }

        return dominantTime;
    }
}