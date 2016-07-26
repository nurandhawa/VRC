package ca.sfu.teambeta;

import org.hibernate.SessionFactory;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.accounts.AccountManager;
import ca.sfu.teambeta.logic.GameSession;
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
        AccountManager am = new AccountManager(dbManager);
        am.register("admin_billy@vrc.ca", "demoPass");

        AppController appController =
                new AppController(dbManager, AppController.DEVELOP_SERVER_PORT,
                AppController.DEVELOP_STATIC_HTML_PATH);
    }
}
