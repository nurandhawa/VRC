package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.*;
import ca.sfu.teambeta.core.exceptions.*;
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
public class AccountManagerTest {
    private AccountManager manager;
    private DBManager dbManager;
    private Player playerFromDB;

    @Before
    public void setUp() throws Exception {
        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        Ladder newLadder = CSVReader.setupLadder();

        Pair pair = newLadder.getPairs().get(0);
        playerFromDB = pair.getPlayers().get(0);

        GameSession gameSession = new GameSession(newLadder);
        dbManager = new DBManager(sessionFactory);
        dbManager.persistEntity(gameSession);
        AccountDatabaseHandler handler = new AccountDatabaseHandler(dbManager);
        manager = new AccountManager(handler);
    }

    @Test
    public void registerNewAdministrator() throws InvalidInputException, AccountRegistrationException,
            GeneralUserAccountException, InvalidCredentialsException, NoSuchUserException {

        manager.registerNewAdministratorAccount("maria@gmail.com", "secret");
        SessionResponse response = manager.login("maria@gmail.com", "secret");

        UserRole actualRole = response.getUserRole();
        UserRole expectedRole = UserRole.ADMINISTRATOR;

        Assert.assertEquals(actualRole, expectedRole);
    }

    @Test
    public void registerAnonUser() throws InvalidInputException, AccountRegistrationException,
            GeneralUserAccountException, InvalidCredentialsException {

        String anonCode_1 = manager.registerNewAnonymousAccount();
        String anonCode_2 = manager.registerNewAnonymousAccount();

        SessionResponse session_1 = manager.loginViaAnonymousCode(anonCode_1);
        SessionResponse session_2 = manager.loginViaAnonymousCode(anonCode_2);

        Assert.assertEquals(UserRole.ANONYMOUS, session_1.getUserRole());
        Assert.assertEquals(UserRole.ANONYMOUS, session_2.getUserRole());
    }

    @Test(expected = NoSuchSessionException.class)
    public void logout() throws InvalidInputException, AccountRegistrationException,
            GeneralUserAccountException, InvalidCredentialsException, NoSuchUserException, NoSuchSessionException {

        String email = "maria@gmail.com";
        String password = "secret";

        manager.registerUser(email, password);
        SessionResponse response = manager.login(email, password);

        String sessionId = response.getSessionToken();
        manager.logout(response.getSessionToken());

        //Trial to access particular sessionId which doesn't exist
        //throws No Such Session Exception
        UserSessionManager.authenticateSession(sessionId);
    }

    @Test
    public void checkPassword() throws GeneralUserAccountException, AccountRegistrationException,
            NoSuchUserException, InvalidInputException {

        String email = "nick@gmail.com";
        String password = "111111";
        int playerId = playerFromDB.getID();
        String secQuestion = "What is the name of my dog?";
        String secAnswer = "Max";

        manager.registerUserWithPlayer(email, password, playerId, secQuestion, secAnswer);

        User user = new User(email, password);
        String passwordHash = user.getPasswordHash();

        user = new User(email, "another password");
        String anotherPassHash = user.getPasswordHash();

        boolean correctPass = CredentialsManager.checkHash(password, passwordHash, "Error message");
        boolean wrongPass = CredentialsManager.checkHash(password, anotherPassHash, "Error message");

        Assert.assertTrue(correctPass);
        Assert.assertTrue(wrongPass);
    }
}