package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.logic.GameSession;
import ca.sfu.teambeta.persistence.CSVReader;
import ca.sfu.teambeta.persistence.DBManager;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by constantin on 28/07/16.
 */
public class AccountDatabaseHandlerTest {
    DBManager dbManager;
    private AccountDatabaseHandler handler;

    @Before
    public void setUp() throws Exception {
        SessionFactory sessionFactory = DBManager.getMySQLSession(true);
        Ladder newLadder = CSVReader.setupLadder();
        GameSession gameSession = new GameSession(newLadder);
        dbManager = new DBManager(sessionFactory);
        dbManager.persistEntity(gameSession);
        handler = new AccountDatabaseHandler(dbManager);
    }

    @Test
    public void addNewUser() throws Exception {
        String email = "maria@gmail.com";
        User expectedUser = new User(email, "password");
        handler.saveNewUser(expectedUser);
        User actualUser = handler.getUser(email);

        Assert.assertEquals(expectedUser, actualUser);
    }

    @Test
    public void getPlayer() throws Exception {
        Player expectedPlayer = new Player("Jordan", "Richard");
        dbManager.addNewPlayer(expectedPlayer);
        Player actualPlayer = handler.getPlayer(expectedPlayer.getID());

        Assert.assertEquals(expectedPlayer, actualPlayer);
    }

    @Test
    public void updateUser() throws Exception {
        String email = "maria@gmail.com";
        String password = "password";
        User user = new User(email, password);
        handler.saveNewUser(user);

        String expectedPassword = "password changed";
        user.setPasswordHash(expectedPassword);
        handler.updateExistingUser(user);

        user = handler.getUser(email);
        String actualPassword = user.getPasswordHash();
        Assert.assertEquals(expectedPassword, actualPassword);
    }
}
