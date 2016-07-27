package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.Time;

import java.util.*;

/**
 * Created by constantin on 11/07/16.
 * <p>
 * VrcTimeSelection assigns Time Slots for all the scorecards depending on the
 * Time Slots of pairs inside of the group, without changing them.
 * <p>
 * 1) Logic works in such way that new time slots can be introduced and not break anything
 * 2) Pairs that don't care about they time slots are set to NO_SLOT
 * 3) NO_SLOT has the lowest priority. If all pairs in group don't care
 * sets the group to the DEFAULT_TIME_SLOT
 */
public class VrcTimeSelection implements TimeSelection {
    private static final Time DEFAULT_TIME_SLOT = Time.SLOT_1;
    private static final int MAX_NUM_PAIRS_PER_SLOT = 24;
    private static final int AMOUNT_TIME_SLOTS = Time.values().length - 1;

    public int getAmountPairsByTime(List<Scorecard> scorecards, Time time) {
        int amount = 0;

        for (Scorecard scorecard : scorecards) {
            if (scorecard.getTimeSlot() == time) {
                amount += scorecard.getReorderedPairs().size();
            }
        }

        return amount;
    }

    public void clearTimeSlots(Ladder ladder) {
        for (Pair pair : ladder.getPairs()) {
            pair.setTimeSlot(Time.NO_SLOT);
        }
    }

    public void distributePairs(List<Scorecard> allScorecards) {
        //Make schedule of groups by selecting most popular time slot
        for (Scorecard scorecard : allScorecards) {
            List<Time> timeSlots = getTimeSlotsOfGroup(scorecard);
            Time time = getDominantTime(timeSlots);
            scorecard.setTimeSlot(time);
        }

        //Create Limitations which determine how to arrange groups between time slots
        int amountPlayingPairs = getTotalPairCount(allScorecards);
        int maxNumPairs = AMOUNT_TIME_SLOTS * MAX_NUM_PAIRS_PER_SLOT;
        //Amount of active pairs exceed the amount the gym can contain at all time slots combined
        boolean crowded = amountPlayingPairs > maxNumPairs;

        if (crowded) {
            //Every time slot will have equal amount of pairs
            distributeEqually(amountPlayingPairs, allScorecards);
        } else {
            //Some time slots have to many groups, moves them to next time slot
            rearrangeGroupsBetweenTimeSlots(allScorecards);
        }
    }

    private int getTotalPairCount(List<Scorecard> allScorecards) {
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
        Map<Time, Integer> timeFrequency = new LinkedHashMap<>();

        //Initialize values
        for (Time time : Time.values()) {
            if (time == Time.NO_SLOT) {
                continue;
            }
            timeFrequency.put(time, 0);
        }

        //Save the amount each slot was selected
        for (Time time : timeSlots) {
            if (time == Time.NO_SLOT) {
                continue;
            }
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

        //NO_SLOT has the lowest priority which means
        //if there is at least one pair that selected different slot, than that's the result
        //if the biggest num was found is 0 that means all of pairs selected NO_SLOT
        if (maxFrequency == 0) {
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
        //Default time slot is first time slot
        //If the time passed is at the end of the Time.values()
        //Then next time is the element in the beginning.

        Time nextTimeSlot = DEFAULT_TIME_SLOT;
        Time[] times = Time.values();

        for (int index = 0; index < times.length; index++) {
            if (times[index] == time) {
                int indexNextSlot = index + 1;
                if (indexNextSlot < times.length) {
                    nextTimeSlot = times[index + 1];
                }
            }
        }
        return nextTimeSlot;
    }

    private void rearrangeGroupsBetweenTimeSlots(List<Scorecard> allScorecards) {
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