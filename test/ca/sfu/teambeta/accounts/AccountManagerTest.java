package ca.sfu.teambeta.accounts;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.sfu.teambeta.accounts.Responses.SessionResponse;
import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.AccountRegistrationException;
import ca.sfu.teambeta.core.exceptions.GeneralUserAccountException;
import ca.sfu.teambeta.core.exceptions.InvalidCredentialsException;
import ca.sfu.teambeta.core.exceptions.InvalidInputException;
import ca.sfu.teambeta.core.exceptions.NoSuchSessionException;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;
import ca.sfu.teambeta.logic.GameSession;
import ca.sfu.teambeta.persistence.CSVReader;
import ca.sfu.teambeta.persistence.DBManager;

/**
 * Created by constantin on 28/07/16.
 */
public class AccountManagerTest {
    private AccountManager accountManager;
    private AccountDatabaseHandler accountDbHandler;
    private DBManager dbManager;
    private Player playerFromDB;


    // MARK: Setup tests
    @Before
    public void setUp() throws Exception {
        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        dbManager = new DBManager(sessionFactory);
        Ladder newLadder = CSVReader.setupLadder(dbManager);

        Pair pair = newLadder.getPairs().get(0);
        playerFromDB = pair.getPlayers().get(0);

        GameSession gameSession = new GameSession(newLadder);
        dbManager.persistEntity(gameSession);

        accountDbHandler = new AccountDatabaseHandler(dbManager);
        accountManager = new AccountManager(accountDbHandler);
    }

    // MARK: Tests
    @Test
    public void registerNewAdministrator() throws InvalidInputException, AccountRegistrationException,
            GeneralUserAccountException, InvalidCredentialsException, NoSuchUserException {

        String email = "nick@gmail.com";
        String password = "password";
        String securityQuestion = "question";
        String securityAnswer = "answer";

        accountManager.registerNewAdministratorAccount(email, password, securityQuestion, securityAnswer);
        SessionResponse response = accountManager.login(email, password);

        UserRole actualRole = response.getUserRole();
        UserRole expectedRole = UserRole.ADMINISTRATOR;

        Assert.assertEquals(actualRole, expectedRole);
    }

    @Test
    public void registerAnonUser() throws InvalidInputException, AccountRegistrationException,
            GeneralUserAccountException, InvalidCredentialsException {

        String anonCode = accountManager.registerNewAnonymousAccount();

        SessionResponse session = accountManager.loginViaAnonymousCode(anonCode);

        Assert.assertEquals(UserRole.ANONYMOUS, session.getUserRole());
    }

    @Test
    public void registerUserWithPlayer() throws GeneralUserAccountException, AccountRegistrationException,
            NoSuchUserException, InvalidInputException, InvalidCredentialsException {

        // Fill all information needed to register a User (with a Player)
        String email = "nick@gmail.com";
        String password = "password";

        int playerId = playerFromDB.getID();

        String secQuestion = "What is the name of my dog?";
        String secAnswer = "Max";


        // Register the player
        accountManager.registerUserWithPlayer(email, password, playerId, secQuestion, secAnswer);

        // Get the registered User
        User retrievedUser = accountDbHandler.getUser(email);

        Assert.assertEquals(retrievedUser.getEmail(), email);

    }

    @Test(expected = AccountRegistrationException.class)
    public void registerTwice() throws GeneralUserAccountException, AccountRegistrationException, NoSuchUserException, InvalidInputException {
        // Fill all information needed to register a User (with a Player)
        String email = "nick@gmail.com";
        String password = "password";

        int playerId = playerFromDB.getID();

        String secQuestion = "What is the name of my dog?";
        String secAnswer = "Max";


        // Register the player
        accountManager.registerUserWithPlayer(email, password, playerId, secQuestion, secAnswer);

        // Register again
        accountManager.registerUserWithPlayer(email, password, playerId, secQuestion, secAnswer);

    }

    @Test
    public void login() throws InvalidInputException, AccountRegistrationException, GeneralUserAccountException, InvalidCredentialsException, NoSuchUserException, NoSuchSessionException {
        String email = "maria@gmail.com";
        String password = "secret";

        accountManager.registerUser(email, password);
        SessionResponse response = accountManager.login(email, password);

        boolean sessionExists = UserSessionManager.authenticateSession(response.getSessionToken());

        Assert.assertTrue(sessionExists);
    }

    @Test(expected = NoSuchUserException.class)
    public void loginWithNonExistentUser() throws GeneralUserAccountException, NoSuchUserException, InvalidInputException, InvalidCredentialsException {
        accountManager.login("noEmail@noDomain.com", "password");
    }

    @Test(expected = InvalidCredentialsException.class)
    public void loginWithWrongCredentials() throws GeneralUserAccountException, NoSuchUserException, InvalidInputException, InvalidCredentialsException, AccountRegistrationException {
        String email = "maria@gmail.com";
        String password = "secret";

        accountManager.registerUser(email, password);
        accountManager.login(email, "wrongPassword");
    }

    @Test(expected = InvalidInputException.class)
    public void loginWithInvalidEmailInput() throws GeneralUserAccountException, NoSuchUserException, InvalidInputException, InvalidCredentialsException {
        // User registration uses the same input validation, so don't need
        //  to add another test for that.

        accountManager.login("invalidEmail", "somePassword");
    }

    @Test(expected = NoSuchSessionException.class)
    public void logout() throws InvalidInputException, AccountRegistrationException,
            GeneralUserAccountException, InvalidCredentialsException, NoSuchUserException, NoSuchSessionException {

        String email = "maria@gmail.com";
        String password = "secret";

        accountManager.registerUser(email, password);
        SessionResponse response = accountManager.login(email, password);

        String sessionId = response.getSessionToken();
        accountManager.logout(response.getSessionToken());

        // Trial to access particular sessionId which doesn't exist
        //  throws No Such Session Exception
        UserSessionManager.authenticateSession(sessionId);

    }
}