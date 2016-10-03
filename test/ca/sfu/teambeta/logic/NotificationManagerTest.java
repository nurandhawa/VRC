package ca.sfu.teambeta.logic;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Calendar;

import ca.sfu.teambeta.accounts.AccountDatabaseHandler;
import ca.sfu.teambeta.accounts.AccountManager;
import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.persistence.CSVReader;
import ca.sfu.teambeta.persistence.DBManager;

/**
 * Created by AlexLand on 2016-10-02.
 */
public class NotificationManagerTest {
    private DBManager dbManager;

    @Before
    public void setUp() throws Exception {
        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        Session session = sessionFactory.openSession();
        this.dbManager = new DBManager(session);

        GameSession gameSession = setupGameSession();

        Pair activePair = gameSession.getAllPairs().get(0);
        dbManager.setPairActive(dbManager.getGameSessionLatest(), activePair.getID());

        AccountDatabaseHandler accountDatabaseHandler = new AccountDatabaseHandler(dbManager);
        AccountManager am = new AccountManager(accountDatabaseHandler);

        am.registerUserWithPlayer(AccountManager.DEMO_EMAIL, AccountManager.DEMO_PASSWORD,
                activePair.getPlayers().get(0).getID(),
                AccountManager.DEMO_SECURITY_QUESTION, AccountManager.DEMO_SECURITY_ANSWER);
    }

    private GameSession setupGameSession() throws Exception {
        Ladder newLadder;
        try {
            newLadder = CSVReader.setupLadder(dbManager);
        } catch (Exception e) {
            System.out.println("INVALID CSV FILE");
            throw e;
        }
        GameSession gameSession = new GameSession(newLadder);
        dbManager.persistEntity(gameSession);
        return gameSession;
    }

    @Test
    public void testRunsOnSchedule() throws Exception {
        Calendar testTime = Calendar.getInstance();
        testTime.add(Calendar.MILLISECOND, 500);

        Notifier mockEmailNotifier = Mockito.mock(EmailNotifier.class);

        NotificationManager notificationManager = new NotificationManager(dbManager,
                testTime.getTime(), NotificationManager.PERIOD_ONE_WEEK);
        notificationManager.scheduleEmailNotifications(mockEmailNotifier);

        Mockito.verify(mockEmailNotifier, Mockito.after(1000)).notify(Mockito.any());

    }

    @Test
    public void testRunsOnScheduleAtInterval() throws Exception {
        Calendar testTime = Calendar.getInstance();
        testTime.add(Calendar.MILLISECOND, 500);

        Notifier mockEmailNotifier = Mockito.mock(EmailNotifier.class);

        NotificationManager notificationManager = new NotificationManager(dbManager,
                testTime.getTime(), 1000);
        notificationManager.scheduleEmailNotifications(mockEmailNotifier);

        Mockito.verify(mockEmailNotifier, Mockito.after(3000).times(3)).notify(Mockito.any());

    }
}
