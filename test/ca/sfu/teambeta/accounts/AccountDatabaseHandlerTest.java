package ca.sfu.teambeta.accounts;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.logic.GameSession;
import ca.sfu.teambeta.persistence.CSVReader;
import ca.sfu.teambeta.persistence.DBManager;

/**
 * Tests for the intermediary class AccountDatabaseHandler
 */

public class AccountDatabaseHandlerTest {
    private DBManager dbManager;
    private AccountDatabaseHandler accountDbHandler;


    // MARK: Setup tests
    @Before
    public void setUp() throws Exception {
        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        dbManager = new DBManager(sessionFactory);
        Ladder newLadder = CSVReader.setupLadder(dbManager);
        GameSession gameSession = new GameSession(newLadder);
        dbManager.persistEntity(gameSession);

        accountDbHandler = new AccountDatabaseHandler(dbManager);
    }

    // MARK: Tests
    @Test
    public void addAndRetrieveUser() throws Exception {
        // We would have to add a new user before we could
        //  retrieve them, thus this test inadvertently
        //  tests both methods.

        String email = "maria@gmail.com";
        String password = "password";

        User savedUser = new User(email, password);

        accountDbHandler.saveNewUser(savedUser);

        User retrievedUser = accountDbHandler.getUser(email);

        Assert.assertEquals(savedUser, retrievedUser);
    }

    @Test
    public void getPlayer() throws Exception {
        Player expectedPlayer = new Player("Jordan", "Richard");
        dbManager.addNewPlayer(expectedPlayer);
        Player actualPlayer = accountDbHandler.getPlayer(expectedPlayer.getID());

        Assert.assertEquals(expectedPlayer, actualPlayer);
    }

    @Test
    public void wupdateUser() throws Exception {
        String email = "maria@gmail.com";
        String password = "password";

        User user = new User(email, password);
        accountDbHandler.saveNewUser(user);

        String expectedPassword = "newPassword";
        user.setPasswordHash(expectedPassword);
        accountDbHandler.updateExistingUser(user);

        user = accountDbHandler.getUser(email);
        String actualPassword = user.getPasswordHash();
        Assert.assertEquals(expectedPassword, actualPassword);
    }
}
