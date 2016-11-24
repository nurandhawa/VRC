package ca.sfu.teambeta;

import org.hibernate.SessionFactory;

import ca.sfu.teambeta.accounts.AccountDatabaseHandler;
import ca.sfu.teambeta.accounts.CredentialsManager;
import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.logic.GameSession;
import ca.sfu.teambeta.notifications.EmailNotifier;
import ca.sfu.teambeta.notifications.NotificationManager;
import ca.sfu.teambeta.notifications.SimpleComposer;
import ca.sfu.teambeta.persistence.CSVReader;
import ca.sfu.teambeta.persistence.DBManager;

class Main {
    public static void main(String[] args) throws Exception {
        DBManager dbManager;
        if (args.length > 0 && args[0].equals("production")) {
            SessionFactory sessionFactory = DBManager.getProductionSession();
            dbManager = new DBManager(sessionFactory);
        } else {
            Ladder newLadder = null;
            SessionFactory sessionFactory = DBManager.getMySQLSession(true);
            dbManager = new DBManager(sessionFactory);
            try {
                newLadder = CSVReader.setupLadder(dbManager);
            } catch (Exception e) {
                System.out.println("INVALID CSV FILE");
                throw e;
            }
            GameSession gameSession = new GameSession(newLadder);
            dbManager.persistEntity(gameSession);
        }

        NotificationManager notificationManager = new NotificationManager(
                dbManager, NotificationManager.getDefaultEmailScheduledTime(),
                NotificationManager.PERIOD_ONE_WEEK);
        notificationManager.scheduleEmailNotifications(new EmailNotifier(new SimpleComposer()));

        AccountDatabaseHandler accountDatabaseHandler = new AccountDatabaseHandler(dbManager);

        CredentialsManager credentialsManager = new CredentialsManager(accountDatabaseHandler);

        AppController appController =
                new AppController(dbManager, credentialsManager, AppController.DEVELOP_SERVER_PORT,
                AppController.DEVELOP_STATIC_HTML_PATH);
    }
}
