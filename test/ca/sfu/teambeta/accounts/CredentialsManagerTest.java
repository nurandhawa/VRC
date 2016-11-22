package ca.sfu.teambeta.accounts;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.sfu.teambeta.accounts.Responses.SessionResponse;
import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
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
public class CredentialsManagerTest {
    private CredentialsManager credentialsManager;
    private AccountManager accountManager;
    private DBManager dbManager;
    private String email;
    private String password;
    private String secQuestion;
    private String secAnswer;

    @Before
    public void setUp() throws Exception {
        // Setup the database
        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        dbManager = new DBManager(sessionFactory);
        Ladder newLadder = CSVReader.setupLadder(dbManager);

        GameSession gameSession = new GameSession(newLadder);
        dbManager.persistEntity(gameSession);

        AccountDatabaseHandler accountDbHandler = new AccountDatabaseHandler(dbManager);
        accountManager = new AccountManager(accountDbHandler);

        credentialsManager = new CredentialsManager(accountDbHandler);

        // Setup the test fields
        email = "nick@gmail.com";
        password = "111111";
        secQuestion = "What is the name of my dog?";
        secAnswer = "Max";

        Pair pair = newLadder.getPairs().get(0);
        Player playerFromDB = pair.getPlayers().get(0);
        int playerId = playerFromDB.getID();

        accountManager.registerUserWithPlayer(email, password, playerId, secQuestion, secAnswer);
    }

    @Test
    public void checkQuestion() throws GeneralUserAccountException,
            AccountRegistrationException, NoSuchUserException, InvalidInputException {

        String actualQuestion = credentialsManager.getUserSecurityQuestion(email);
        Assert.assertEquals(secQuestion, actualQuestion);
    }

    @Test
    public void changePassword() throws GeneralUserAccountException,
            AccountRegistrationException, NoSuchUserException, InvalidInputException, InvalidCredentialsException, NoSuchSessionException {

        // Obtain a voucher and change the password
        String newPassword = "chicken";
        String voucherCode = credentialsManager.validateSecurityQuestionAnswer(email, secAnswer);
        credentialsManager.changePassword(email, newPassword, voucherCode);

        // Try to use new password
        SessionResponse response = accountManager.login(email, newPassword);

        // Check the user session token
        boolean sessionExists = UserSessionManager.authenticateSession(response.getSessionToken());

        Assert.assertTrue(sessionExists);
        

    }
}
