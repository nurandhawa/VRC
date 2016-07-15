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
    private static final Time DEFAULT_TIME_SLOT = Time.TH_8_30;
    private static final int MAX_NUM_PAIRS_PER_SLOT = 24;
    private static final int AMOUNT_TIME_SLOTS = Time.values().length - 1;
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

        for (Pair pair : allPairs) {
            Time timeSlot = pair.getTimeSlot();
            if (timeSlot == time) {
                pairs.add(pair);
            }
        }

        return pairs;
    }

    @Override
    public void clearTimeSlots(Ladder ladder) {
        for (Pair pair : ladder.getPairs()) {
            pair.setTimeSlot(Time.NO_SLOT);
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

        //Create Limitations which determine how to arrange groups between time slots
        int amountPlayingPairs = allPairs.size();

        int maxNumPairs = AMOUNT_TIME_SLOTS * MAX_NUM_PAIRS_PER_SLOT;
        boolean crowded = amountPlayingPairs > maxNumPairs;

        if (crowded) {
            //Every time slot will have equall amount of pairs
            distributeEqually(amountPlayingPairs);
        } else {
            //Some time slots have to many groups, moves them to next time slot
            rearangeGroupsBetweenTimeSlots();
        }
    }

    private void saveValues(List<Scorecard> scorecards) {
        for (Scorecard group : scorecards) {
            List<Pair> pairs = group.getReorderedPairs();

            for (Pair pair : pairs) {
                Time time = pair.getTimeSlot();

                //If time was not selected set to default
                if (time == Time.NO_SLOT) {
                    pair.setTimeSlot(DEFAULT_TIME_SLOT);
                }
                allPairs.add(pair);
            }
        }
        this.allScorecards = scorecards;
    }

    private List<Time> getTimeSlotsOfGroup(Scorecard scorecard) {
        List<Pair> pairs = scorecard.getReorderedPairs();
        List<Time> timeSlots = new ArrayList<>();

        for (Pair pair : pairs) {
            Time time = pair.getTimeSlot();
            timeSlots.add(time);
        }

        return timeSlots;
    }

    private void assignTimeToGroup(Scorecard scorecard, Time time) {
        scorecard.setTimeSlot(time);

        //Update Pairs inside of the scorecard
        List<Pair> pairs = scorecard.getReorderedPairs();
        for (Pair pair : pairs) {
            pair.setTimeSlot(time);
        }
    }

    private Time getDominantTime(List<Time> timeSlots) {
        Map<Time, Integer> timeFrequency = new HashMap<>();

        //Initialize values
        for (Time time : Time.values()) {
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
        for (Map.Entry<Time, Integer> entry : timeFrequency.entrySet()) {
            int frequency = entry.getValue();

            if (frequency > maxFrequency) {
                maxFrequency = frequency;
                dominantTime = entry.getKey();
            }
        }

        return dominantTime;
    }

    private void distributeEqually(int amountPlayingPairs) {
        int avgPairsPerTimeSlot = amountPlayingPairs / AMOUNT_TIME_SLOTS;

        //Move extra groups to the next time slot, do that for all time slots
        for (Time time : Time.values()) {
            //Omit undefined time slot
            if(time == Time.NO_SLOT){
                continue;
            }
            int amount = getAmountPairsByTime(allPairs, time);

            if (amount > avgPairsPerTimeSlot) {
                int extraPairs = amount - avgPairsPerTimeSlot;
                moveOverflowedGroupsToNextTimeSlot(avgPairsPerTimeSlot, extraPairs, time);
            }
        }
    }

    private void moveOverflowedGroupsToNextTimeSlot(int limitPairs, int numExtraPairs, Time time) {
        List<Scorecard> scorecards = getScorecardsByTime(time);
        Time nextTimeSlot = getNextTimeSlot(time);

        int amountPairsByTime = getAmountPairsByTime(allPairs, time);

        //While we have more extra pairs and we are still having more pairs then allowed
        //Move the scorecards to next time slot
        while (numExtraPairs > 0 && amountPairsByTime > limitPairs) {

            //Groups with the lowest ratings will be moved to another time slot
            int lastIndex = scorecards.size() - 1;
            Scorecard group = scorecards.get(lastIndex);
            scorecards.remove(lastIndex);

            assignTimeToGroup(group, nextTimeSlot);

            int numPairsMoved = group.getReorderedPairs().size();
            numExtraPairs -= numPairsMoved;
        }
    }

    private List<Scorecard> getScorecardsByTime(Time time) {
        List<Scorecard> scorecards = new ArrayList<>();

        for (Scorecard group : allScorecards) {
            Time timeSlot = group.getTimeSlot();
            if (timeSlot == time) {
                scorecards.add(group);
            }
        }

        return scorecards;
    }

    private Time getNextTimeSlot(Time time) {
        boolean next = false;
        //Default time slot is first time slot
        //If the time passed is at the end of the Time.values()
        //Then next time is the element in the beginning.
        Time nextTimeSlot = DEFAULT_TIME_SLOT;
        for (Time timeSlot : Time.values()) {
            //Omit the no value slot
            if(timeSlot == Time.NO_SLOT) {
                continue;
            }

            if (timeSlot == time) {
                next = true;
            }

            if (next) {
                //Save next time slot
                nextTimeSlot = timeSlot;
            }
        }
        return nextTimeSlot;
    }

    private void rearangeGroupsBetweenTimeSlots() {
        for(Time time : Time.values()){
            int amount = getAmountPairsByTime(allPairs, time);
            int extra = amount - MAX_NUM_PAIRS_PER_SLOT;
            boolean crowded = extra > 0;

            if(crowded){
                //Move extra groups to the next time slot, do that for all time slots
                moveOverflowedGroupsToNextTimeSlot(MAX_NUM_PAIRS_PER_SLOT, extra, time);
            }

        }
    }
}