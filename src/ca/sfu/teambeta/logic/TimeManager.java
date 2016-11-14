package ca.sfu.teambeta.logic;

import java.util.Calendar;

/**
 * Created by constantin on 24/07/16.
 * --- This class uses Singleton Pattern ---
 * <p>
 * 1) It stores the time after which editing pairs (status and time) is disabled.
 * 2) Currently Blocking Time is set to -> 05:15 pm Thursday.
 * 3) When Admin reorders ladder time is updated. Which means that Users can edit
 * they information again up till next week.
 */
public class TimeManager {
    private static final int DAY = Calendar.THURSDAY;
    private static final int HOUR = 17;
    private static final int MINUTE = 15;
    private static final int SECOND = 0;
    private static final int WEEK = 7;
    private static Calendar Block_Time = Calendar.getInstance();
    private static TimeManager timeManager = null;

    //Constructor
    private TimeManager() {
        Block_Time.set(Calendar.DAY_OF_WEEK, DAY);

        int currentDayWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        boolean startupAfterDefaultDay = currentDayWeek > DAY;

        //If we run the program after Thursday we have to set the blocking time to next
        //week's Thursday not previous Thursday on this week
        if (startupAfterDefaultDay) {
            int currentDayMonth = Block_Time.get(Calendar.DAY_OF_MONTH);

            int nextWeeksBlockTime = currentDayMonth + WEEK;

            Block_Time.set(Calendar.DAY_OF_MONTH,  nextWeeksBlockTime);
        }

        Block_Time.set(Calendar.HOUR_OF_DAY, HOUR);
        Block_Time.set(Calendar.MINUTE, MINUTE);
        Block_Time.set(Calendar.SECOND, SECOND);
        System.out.println("BLOCK: " + Block_Time.getTime());
        System.out.println("NOW: " + Calendar.getInstance().getTime());
    }

    public static synchronized TimeManager getInstance() {
        if (timeManager == null) {
            timeManager = new TimeManager();
        }
        return timeManager;
    }

    public boolean isExpired() {
        Calendar currentTime = Calendar.getInstance();
        return currentTime.after(Block_Time);
    }

    public void updateTime() {
        if (isExpired()) {
            int day = Block_Time.get(Calendar.DAY_OF_MONTH);
            int dayNextWeek = day + WEEK;

            Block_Time.set(Calendar.DAY_OF_MONTH, dayNextWeek);
        }
    }
}
