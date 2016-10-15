package ca.sfu.teambeta.logic;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.Time;

/**
 * If no. of scorecards are less than or equal to 12 then just assign the first time slot,
 * otherwise distribute scorecards as per the preference of pairs and then distribute them
 * equally into all time slots.
 */
public class VrcTimeSelection implements TimeSelection {
    private static final Time DEFAULT_TIME_SLOT = Time.SLOT_1;
    private static final int AMOUNT_TIME_SLOTS = Time.values().length - 1;
    private static final int MIN_SCORECARDS_FOR_DISTRIBUTION = 12;
    private boolean[] noPreferredTime;

    public int getAmountPairsByTime(List<Scorecard> scorecards, Time time) {
        int amount = 0;
        for (Scorecard scorecard : scorecards) {
            if (scorecard.getTimeSlot() == time) {
                amount += scorecard.getReorderedPairs().size();
            }
        }
        return amount;
    }

    public void distributePairs(List<Scorecard> allScorecards, Map<Pair, Time> timeSlotsMap) {
        /* If no. of scorecards are less than or equal to 12 then just assign the first time slot,
        *  otherwise distribute scorecards as per the preference of pairs and then distribute them
        *  equally into all time slots.*/

        noPreferredTime = new boolean[allScorecards.size()];

        if (allScorecards.size() <= MIN_SCORECARDS_FOR_DISTRIBUTION) {
            for (Scorecard scorecard : allScorecards) {
                scorecard.setTimeSlot(Time.SLOT_1);
            }
        } else {
            for (Scorecard scorecard : allScorecards) {
                List<Time> timeSlots = getTimeSlotsOfGroup(scorecard, timeSlotsMap);
                Time time = getDominantTime(timeSlots);
                if (time.equals(Time.NO_SLOT)) {
                    noPreferredTime[allScorecards.indexOf(scorecard)] = true;
                    scorecard.setTimeSlot(DEFAULT_TIME_SLOT);
                } else {
                    scorecard.setTimeSlot(time);
                }
            }
            distributeScorecardsEqually(allScorecards);
        }
    }

    private List<Time> getTimeSlotsOfGroup(Scorecard scorecard, Map<Pair, Time> timeSlotsMap) {
        List<Pair> pairs = scorecard.getReorderedPairs();
        List<Time> timeSlots = new ArrayList<>();

        for (Pair pair : pairs) {
            Time time = timeSlotsMap.get(pair);
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
            dominantTime = Time.NO_SLOT;
        }
        return dominantTime;
    }

    private void moveOverflowedGroupsToNextTimeSlot(
            int amountOfScorecardsByTime, int limitScorecards, int extraScorecards,
            Time oldTime, List<Scorecard> allScorecards) {

        List<Scorecard> scorecards = getScorecardsByTime(allScorecards, oldTime);
        Time nextTimeSlot = getNextTimeSlot(oldTime);

        //While we have more extra pairs and we are still having more pairs then allowed
        //Move the scorecards to next time slot
        while (extraScorecards > 0 && amountOfScorecardsByTime > limitScorecards) {

            //Groups with the lowest ratings will be moved to another time slot
            Scorecard group = getLastScorecardWithNoPreferredTime(allScorecards, oldTime);
            if (group == null) {
                group = getLastScorecard(scorecards, oldTime);
            }
            if (group == null) {
                break;
            }
            group.setTimeSlot(nextTimeSlot);
            extraScorecards--;
            amountOfScorecardsByTime--;
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

    private Scorecard getLastScorecardWithNoPreferredTime(List<Scorecard> scorecards, Time oldTime) {
        for (int i = scorecards.size() - 1; i > 0; i--) {
            Scorecard group = scorecards.get(i);
            if (group.getTimeSlot() == oldTime) {
                if (noPreferredTime[i]) {
                    return group;
                }
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

    private void distributeScorecardsEqually(List<Scorecard> allScorecards) {
        int numOfScorecards = allScorecards.size();
        int scorecardsOnEachTime = numOfScorecards / AMOUNT_TIME_SLOTS;
        for (Time time : Time.values()) {
            List<Scorecard> scorecardsByTime = getScorecardsByTime(allScorecards, time);
            int amount = scorecardsByTime.size();
            int extra = amount - scorecardsOnEachTime;

            if (extra > 0) {
                //Move extra groups to the next time slot, do that for all time slots
                moveOverflowedGroupsToNextTimeSlot(
                        amount, scorecardsOnEachTime, extra, time, allScorecards);
            }

        }
    }
}
