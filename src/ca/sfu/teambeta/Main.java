package ca.sfu.teambeta;

import com.esotericsoftware.minlog.Log;

import ca.sfu.teambeta.accounts.AccountDatabaseHandler;
import org.hibernate.SessionFactory;

import ca.sfu.teambeta.accounts.CredentialsManager;
import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.accounts.AccountManager;
import ca.sfu.teambeta.logic.GameSession;
import ca.sfu.teambeta.persistence.CSVReader;
import ca.sfu.teambeta.persistence.DBManager;

class Main {
    public static void main(String[] args) throws Exception {
        Log.set(Log.LEVEL_DEBUG);

        DBManager dbManager;
        if (args.length > 0 && args[0].equals("production")) {
            SessionFactory sessionFactory = DBManager.getProductionSession();
            dbManager = new DBManager(sessionFactory);
        } else {
            Ladder newLadder = null;
            try {
                newLadder = CSVReader.setupLadder();
            } catch (Exception e) {
                System.out.println("INVALID CSV FILE");
                throw e;
            }
            SessionFactory sessionFactory = DBManager.getMySQLSession(true);

            GameSession gameSession = new GameSession(newLadder);
            dbManager = new DBManager(sessionFactory);
            dbManager.persistEntity(gameSession);
        }
        AccountDatabaseHandler accountDatabaseHandler = new AccountDatabaseHandler(dbManager);
        AccountManager am = new AccountManager(accountDatabaseHandler);
        am.registerUser(AccountManager.DEMO_EMAIL, AccountManager.DEMO_PASSWORD);
        am.registerNewAdministratorAccount(AccountManager.DEMO_ADMIN_EMAIL, AccountManager.DEMO_ADMIN_PASSWORD);

        CredentialsManager credentialsManager = new CredentialsManager(accountDatabaseHandler);
        AppController appController =
                new AppController(dbManager, credentialsManager, AppController.DEVELOP_SERVER_PORT,
                AppController.DEVELOP_STATIC_HTML_PATH);
    }
}
