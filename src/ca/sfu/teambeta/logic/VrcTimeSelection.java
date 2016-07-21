package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.Time;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by constantin on 11/07/16.
 */
public class VrcTimeSelection implements TimeSelection {
    private static final Time DEFAULT_TIME_SLOT = Time.SLOT_1;
    private static final int MAX_NUM_PAIRS_PER_SLOT = 24;
    private static final int AMOUNT_TIME_SLOTS = Time.values().length - 1;

    public VrcTimeSelection() {
    }

    @Override
    public int getAmountPairsByTime(List<Scorecard> scorecards, Time time) {
        List<Pair> pairs = new ArrayList<>();

        for (Scorecard scorecard : scorecards) {
            if (scorecard.getTimeSlot() == time) {
                pairs.addAll(scorecard.getReorderedPairs());
            }
        }

        return pairs.size();
    }

    @Override
    public void clearTimeSlots(Ladder ladder) {
        for (Pair pair : ladder.getPairs()) {
            pair.setTimeSlot(Time.NO_SLOT);
        }
    }

    @Override
    public void distributePairs(List<Scorecard> allScorecards) {
        //Make schedule of groups by selecting most popular time slot
        for (Scorecard scorecard : allScorecards) {
            List<Time> timeSlots = getTimeSlotsOfGroup(scorecard);
            Time time = getDominantTime(timeSlots);
            scorecard.setTimeSlot(time);
        }

        //Create Limitations which determine how to arrange groups between time slots
        int amountPlayingPairs = getAmountOfAllPairs(allScorecards);
        int maxNumPairs = AMOUNT_TIME_SLOTS * MAX_NUM_PAIRS_PER_SLOT;
        boolean crowded = amountPlayingPairs > maxNumPairs;

        if (crowded) {
            //Every time slot will have equall amount of pairs
            distributeEqually(amountPlayingPairs, allScorecards);
        } else {
            //Some time slots have to many groups, moves them to next time slot
            rearangeGroupsBetweenTimeSlots(allScorecards);
        }
    }

    private int getAmountOfAllPairs(List<Scorecard> allScorecards) {
        int amount = 0;
        for (Time time : Time.values()) {
            amount += getAmountPairsByTime(allScorecards, time);
        }
        return amount;
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


        //Count how many pairs selected particular time slot
    private Time getDominantTime(List<Time> timeSlots) {
        Map<Time, Integer> timeFrequency = new HashMap<>();

        //Initialize values
        for (Time time : Time.values()) {
            timeFrequency.put(time, 0);
        }
        for (Time time : timeSlots) {
            int amount = timeFrequency.get(time);
            amount++;
            timeFrequency.put(time, amount);
        }

        //Get the most popular time slot,
        //if all are the same, select the earliest time slot
        Time dominantTime = null;

        for (int check = 0; check < 2; check++) {
            int maxFrequency = 0;

            for (Map.Entry<Time, Integer> entry : timeFrequency.entrySet()) {
                //Loop twice (second time omit the NO_SLOT as it cannot be the result)
                //NO_SLOT has the lowest priority which means
                //if there is at least one pair that selected different slot, than that's the result
                if (check == 1 && entry.getKey() == Time.NO_SLOT) {
                    continue;
                }
                int frequency = entry.getValue();

                if (frequency > maxFrequency) {
                    maxFrequency = frequency;
                    dominantTime = entry.getKey();
                }
            }
        }

        //If all pairs decided not to select slots, switch to default
        if (dominantTime == Time.NO_SLOT) {
            dominantTime = DEFAULT_TIME_SLOT;
        }
        return dominantTime;
    }

    private void distributeEqually(int amountPlayingPairs, List<Scorecard> allScorecards) {
        int avgPairsPerTimeSlot = amountPlayingPairs / AMOUNT_TIME_SLOTS;

        //Move extra groups to the next time slot, do that for all time slots
        for (Time time : Time.values()) {
            //Omit undefined time slot
            if (time == Time.NO_SLOT) {
                continue;
            }
            int amount = getAmountPairsByTime(allScorecards, time);
            if (amount > avgPairsPerTimeSlot) {
                int extraPairs = amount - avgPairsPerTimeSlot;
                if (extraPairs != 1) {
                    //If the difference between time slots is on 1 pair
                    //do not move the whole group
                    //some groups have 4 pairs and some 3, time slots cannot be perfectly equal
                    moveOverflowedGroupsToNextTimeSlot(amount, avgPairsPerTimeSlot, extraPairs, time, allScorecards);
                }
            }
        }
    }

    private void moveOverflowedGroupsToNextTimeSlot(
            int amountPairsByTime, int limitPairs, int numExtraPairs,
            Time oldTime, List<Scorecard> allScorecards) {

        List<Scorecard> scorecards = getScorecardsByTime(allScorecards, oldTime);
        Time nextTimeSlot = getNextTimeSlot(oldTime);

        //While we have more extra pairs and we are still having more pairs then allowed
        //Move the scorecards to next time slot
        while (numExtraPairs > 0 && amountPairsByTime > limitPairs) {

            //Groups with the lowest ratings will be moved to another time slot
            Scorecard group = getLastScorecard(scorecards, oldTime);
            if (group == null) {
                break;
            }
            group.setTimeSlot(nextTimeSlot);

            int numPairsMoved = group.getReorderedPairs().size();
            numExtraPairs -= numPairsMoved;
            amountPairsByTime -= numPairsMoved;
        }
    }

    private Scorecard getLastScorecard(List<Scorecard> scorecards, Time oldTime) {
        for (int i = scorecards.size() - 1; i > 0; i--) {
            Scorecard group = scorecards.get(i);

            if (group.getTimeSlot() == oldTime) {
                return group;
            }
        }
        return null;
    }

    private List<Scorecard> getScorecardsByTime(List<Scorecard> allScorecards, Time time) {
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
        Time[] times = Time.values();

        for (int i = 0; i < times.length; i++) {
            if (times[i] == time) {
                if (i + 1 < times.length) {
                    nextTimeSlot = times[i + 1];
                }
            }
        }
        return nextTimeSlot;
    }

    private void rearangeGroupsBetweenTimeSlots(List<Scorecard> allScorecards) {
        for (Time time : Time.values()) {
            int amount = getAmountPairsByTime(allScorecards, time);
            int extra = amount - MAX_NUM_PAIRS_PER_SLOT;
            boolean crowded = extra > 0;
            if (crowded) {
                //Move extra groups to the next time slot, do that for all time slots
                moveOverflowedGroupsToNextTimeSlot(amount, MAX_NUM_PAIRS_PER_SLOT, extra, time, allScorecards);
            }

        }
    }
}