package ca.sfu.teambeta.notifications;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.persistence.DBManager;

/**
 * Schedule notifications that are sent to players before their matches.
 */
public class NotificationManager {
    public static final long PERIOD_ONE_WEEK = 604800000;

    private DBManager dbManager;
    private Date scheduledTime;
    private long timerPeriod;

    public NotificationManager(DBManager dbManager, Date scheduledTime, long timerPeriod) {
        this.dbManager = dbManager;
        this.scheduledTime = scheduledTime;
        this.timerPeriod = timerPeriod;
    }

    public void scheduleEmailNotifications(Notifier emailNotifier) {
        // TODO: Log scheduled time and execution time
        Timer notificationTimer = new Timer();
        notificationTimer.scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        sendEmailNotification(emailNotifier);
                    }
                },
                scheduledTime, timerPeriod);
    }

    private void sendEmailNotification(Notifier emailNotifier) {
        List<User> users = dbManager.getAllUsers();
        List<Scorecard> scorecards = dbManager.getGameSessionLatest().getScorecards();
        Map<Player, Scorecard> activePlayers = new HashMap<>();

        // Create a map of players to the scorecards they are in
        for (Scorecard scorecard : scorecards) {
            for (Pair pair : scorecard.getPairs()) {
                pair.getPlayers().forEach(player -> activePlayers.put(player, scorecard));
            }
        }

        // If a User's Player is active, pass the User and the Scorecard that
        // contains the Player (as mapped above) to the notifier
        for (User user : users) {
            Player player = user.getAssociatedPlayer();
            boolean isActive = activePlayers.containsKey(player);
            if (isActive) {
                Scorecard playerScorecard = activePlayers.get(player);
                emailNotifier.notify(user, playerScorecard);
            }
        }
    }

    public static Date getDefaultEmailScheduledTime() {
        Calendar scheduledTime = Calendar.getInstance();
        scheduledTime.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        scheduledTime.set(Calendar.HOUR, 5);
        scheduledTime.set(Calendar.MINUTE, 0);
        scheduledTime.set(Calendar.SECOND, 0);
        scheduledTime.set(Calendar.AM_PM, Calendar.PM);

        // scheduledTime could be set to the previous week's Thursday.
        Calendar now = Calendar.getInstance();
        if (scheduledTime.before(now)) {
            scheduledTime.add(Calendar.WEEK_OF_YEAR, 1);
        }

        return scheduledTime.getTime();
    }
}
