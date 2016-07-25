package ca.sfu.teambeta.logic;
import java.util.Calendar;

/**
 * Created by constantin on 24/07/16.
 */
public class TimeManager {
    private static final int DAY = Calendar.THURSDAY;
    private static final int HOUR = 18;
    private static final int MINUTE = 0;
    private static final int SECOND = 0;
    private static Calendar Block_Time = Calendar.getInstance();
    private static TimeManager timeManager = null;

    private TimeManager() {
        Block_Time.set(Calendar.DAY_OF_WEEK, DAY);
        Block_Time.set(Calendar.HOUR_OF_DAY, HOUR);
        Block_Time.set(Calendar.MINUTE, MINUTE);
        Block_Time.set(Calendar.SECOND, SECOND);
    }

    public synchronized static TimeManager getInstance() {
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
            int dayNextWeek = day + 7;

            Block_Time.set(Calendar.DAY_OF_MONTH, dayNextWeek);
        }
    }
}
