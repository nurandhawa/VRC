package ca.sfu.teambeta;

import com.esotericsoftware.minlog.Log;

import org.hibernate.SessionFactory;

import ca.sfu.teambeta.accounts.AccountDatabaseHandler;
import ca.sfu.teambeta.accounts.CredentialsManager;
import ca.sfu.teambeta.persistence.DBManager;

// Entry Point for Program when executing via a jar file
// Do not call me via the IDE!
public class JarEntry {
    public static void main(String[] args) {
        Log.set(Log.LEVEL_DEBUG);

        SessionFactory sessionFactory = DBManager.getProductionSession();
        DBManager dbManager = new DBManager(sessionFactory);

        AccountDatabaseHandler accountDatabaseHandler = new AccountDatabaseHandler(dbManager);
        CredentialsManager credentialsManager = new CredentialsManager(accountDatabaseHandler);

        AppController appController = new AppController(dbManager, credentialsManager, AppController.JAR_SERVER_PORT,
                AppController.JAR_STATIC_HTML_PATH);
    }
}
