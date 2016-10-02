package ca.sfu.teambeta.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.persistence.DBManager;

/**
 * Schedule notifications that are sent to players before their matches.
 */
public class NotificationManager {
    public static final long PERIOD_ONE_WEEK = 604800000;

    private DBManager dbManager;
    private EmailNotifier emailNotifier = new EmailNotifier();
    private Date scheduledTime;
    private long timerPeriod;

    public NotificationManager(DBManager dbManager, Date scheduledTime, long timerPeriod) {
        this.dbManager = dbManager;
        this.scheduledTime = scheduledTime;
        this.timerPeriod = timerPeriod;
    }

    public void scheduleEmailNotifications() {
        // TODO: Log scheduled time and execution time
        Timer notificationTimer = new Timer();
        notificationTimer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        sendEmailNotification();
                    }
                },
                scheduledTime, timerPeriod);
    }

    private void sendEmailNotification() {
        List<User> users = dbManager.getAllUsers();
        List<User> activeUsers = new ArrayList<>();
        List<Pair> activePairs = dbManager.getGameSessionLatest().getActivePairs();
        for (User user : users) {
            Player player = user.getAssociatedPlayer();
            boolean belongsToActivePair = activePairs.stream().anyMatch(pair -> pair.hasPlayer(player));
            if (belongsToActivePair) {
                activeUsers.add(user);
            }
        }

        for (User user : activeUsers) {
            emailNotifier.notify(user);
        }
    }

    public static Date getDefaultEmailScheduledTime() {
        Calendar scheduledTime = Calendar.getInstance();
        scheduledTime.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        scheduledTime.set(Calendar.HOUR, 5);
        scheduledTime.set(Calendar.MINUTE, 0);
        scheduledTime.set(Calendar.SECOND, 0);
        scheduledTime.set(Calendar.AM_PM, Calendar.PM);
        return scheduledTime.getTime();
    }
}
