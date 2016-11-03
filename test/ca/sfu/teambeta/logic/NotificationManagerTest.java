package ca.sfu.teambeta.logic;

import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Calendar;
import java.util.List;

import ca.sfu.teambeta.accounts.AccountDatabaseHandler;
import ca.sfu.teambeta.accounts.AccountManager;
import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.notifications.EmailNotifier;
import ca.sfu.teambeta.notifications.NotificationManager;
import ca.sfu.teambeta.notifications.Notifier;
import ca.sfu.teambeta.notifications.SimpleComposer;
import ca.sfu.teambeta.persistence.CSVReader;
import ca.sfu.teambeta.persistence.DBManager;

/**
 * Tests for the NotificationManager class
 */
public class NotificationManagerTest {
    private DBManager dbManager;

    @Before
    public void setUp() throws Exception {
        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        this.dbManager = new DBManager(sessionFactory);

        GameSession gameSession = setupGameSession();

        List<Pair> activePairs = gameSession.getAllPairs();
        Pair activePair1 = activePairs.get(0);
        Pair activePair2 = activePairs.get(1);
        Pair activePair3 = activePairs.get(2);
        Pair activePair4 = activePairs.get(3);
        dbManager.setPairActive(dbManager.getGameSessionLatest(), activePair1.getID());
        dbManager.setPairActive(dbManager.getGameSessionLatest(), activePair2.getID());
        dbManager.setPairActive(dbManager.getGameSessionLatest(), activePair3.getID());
        dbManager.setPairActive(dbManager.getGameSessionLatest(), activePair4.getID());

        dbManager.getGameSessionLatest().createGroups(new VrcScorecardGenerator(), new VrcTimeSelection());

        AccountDatabaseHandler accountDatabaseHandler = new AccountDatabaseHandler(dbManager);
        AccountManager am = new AccountManager(accountDatabaseHandler);

        am.registerUserWithPlayer(AccountManager.DEMO_EMAIL, AccountManager.DEMO_PASSWORD,
                activePair1.getPlayers().get(0).getID(),
                AccountManager.DEMO_SECURITY_QUESTION, AccountManager.DEMO_SECURITY_ANSWER);
    }

    private GameSession setupGameSession() throws Exception {
        Ladder newLadder;
        try {
            newLadder = CSVReader.setupTestingLadder(dbManager);
        } catch (Exception e) {
            System.out.println("INVALID CSV FILE");
            throw e;
        }
        GameSession gameSession = new GameSession(newLadder);
        dbManager.persistEntity(gameSession);
        return gameSession;
    }

    /**
     * Use this test to send a test email to your account. Before running this test, change the
     * email of the user registered in the setUp method from AccountManager.DEMO_EMAIL to your
     * email.
     */
    @Ignore
    @Test
    public void sendEmail() throws Exception {
        Calendar testTime = Calendar.getInstance();
        testTime.add(Calendar.MILLISECOND, 500);

        Notifier emailNotifier = new EmailNotifier(new SimpleComposer());

        NotificationManager notificationManager = new NotificationManager(dbManager,
                testTime.getTime(), NotificationManager.PERIOD_ONE_WEEK);
        notificationManager.scheduleEmailNotifications(emailNotifier);

        Thread.sleep(1000000);
    }

    @Test
    public void testRunsOnSchedule() throws Exception {
        Calendar testTime = Calendar.getInstance();
        testTime.add(Calendar.MILLISECOND, 500);

        Notifier mockEmailNotifier = Mockito.mock(EmailNotifier.class);

        NotificationManager notificationManager = new NotificationManager(dbManager,
                testTime.getTime(), NotificationManager.PERIOD_ONE_WEEK);
        notificationManager.scheduleEmailNotifications(mockEmailNotifier);

        Mockito.verify(mockEmailNotifier, Mockito.after(1000)).notify(Mockito.any(), Mockito.any());

    }

    @Test
    public void testRunsOnScheduleAtInterval() throws Exception {
        Calendar testTime = Calendar.getInstance();
        testTime.add(Calendar.MILLISECOND, 500);

        Notifier mockEmailNotifier = Mockito.mock(EmailNotifier.class);

        NotificationManager notificationManager = new NotificationManager(dbManager,
                testTime.getTime(), 1000);
        notificationManager.scheduleEmailNotifications(mockEmailNotifier);

        Mockito.verify(mockEmailNotifier, Mockito.after(3000).atLeast(3)).notify(Mockito.any(), Mockito.any());

    }
}
