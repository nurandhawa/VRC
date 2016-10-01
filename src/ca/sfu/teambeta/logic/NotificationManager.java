package ca.sfu.teambeta.logic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.persistence.DBManager;

/**
 * Schedule notifications that are sent to players before their matches.
 */
public class NotificationManager {
    public static final long PERIOD_ONE_WEEK = 604800000;

    private DBManager dbManager;
    private EmailNotifier emailNotifier;
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
        List<Pair> activePairs = dbManager.getGameSessionLatest().getActivePairs();
        List<Player> activePlayers = new ArrayList<>();
        for (Pair pair : activePairs) {
            activePlayers.addAll(pair.getPlayers());
        }

        for (Player player : activePlayers) {
            emailNotifier.notify(player);
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
