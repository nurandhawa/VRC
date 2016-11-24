package ca.sfu.teambeta.accounts;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import ca.sfu.teambeta.accounts.Responses.SessionResponse;
import ca.sfu.teambeta.core.exceptions.AccountRegistrationException;
import ca.sfu.teambeta.core.exceptions.GeneralUserAccountException;
import ca.sfu.teambeta.core.exceptions.InvalidCredentialsException;
import ca.sfu.teambeta.core.exceptions.InvalidInputException;
import ca.sfu.teambeta.core.exceptions.NoSuchSessionException;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;
import ca.sfu.teambeta.persistence.DBManager;


/**
 * Created by constantin on 27/07/16.
 */

public class UserSessionManagerTest {
    @After
    public void clearSessions() {
        UserSessionManager.clearSessions();
    }

    @Test
    public void createIdenticalSessions() {
        UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR, -1);
        UserSessionManager.createNewSession("steve@gmail.com", UserRole.REGULAR, -1);
        UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR, -1);

        int numUsers = UserSessionManager.numUsersLoggedIn();
        Assert.assertEquals(3, numUsers);
    }

    @Test
    public void deleteSession() throws NoSuchSessionException {
        SessionResponse session = UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR, -1);
        String token = session.getSessionToken();

        UserSessionManager.deleteSession(token);

        int numUsers = UserSessionManager.numUsersLoggedIn();
        Assert.assertEquals(0, numUsers);
    }

    @Test
    public void authenticateSession() throws NoSuchSessionException {
        SessionResponse session1 = UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR, -1);
        SessionResponse session2 = UserSessionManager.createNewSession("nick@gmail.com", UserRole.ADMINISTRATOR, -1);

        String token1 = session1.getSessionToken();
        String token2 = session2.getSessionToken();

        boolean authenticated1 = UserSessionManager.authenticateSession(token1);
        boolean authenticated2 = UserSessionManager.authenticateSession(token2);

        Assert.assertTrue(authenticated1);
        Assert.assertTrue(authenticated2);
    }

    @Test(expected = NoSuchSessionException.class)
    public void invalidSessionId() throws NoSuchSessionException {
        UserSessionManager.authenticateSession("");
    }

    @Test
    public void isAdminSession() throws NoSuchSessionException {
        SessionResponse session1 = UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR, -1);
        SessionResponse session2 = UserSessionManager.createNewSession("nick@gmail.com", UserRole.ADMINISTRATOR, -1);

        String token1 = session1.getSessionToken();
        String token2 = session2.getSessionToken();

        boolean admin1 = UserSessionManager.isAdministratorSession(token1);
        boolean admin2 = UserSessionManager.isAdministratorSession(token2);


        Assert.assertFalse(admin1);
        Assert.assertTrue(admin2);
    }

    @Test
    public void checkEmail() throws NoSuchSessionException {
        String expectedEmail = "maria@gmail.com";
        SessionResponse session = UserSessionManager.createNewSession(expectedEmail, UserRole.REGULAR, -1);
        String token = session.getSessionToken();

        String actualEmail = UserSessionManager.getEmailFromSessionId(token);

        Assert.assertEquals(expectedEmail, actualEmail);
    }

    @Test
    public void changeUserRole() throws NoSuchSessionException, NoSuchUserException,
            InvalidInputException, AccountRegistrationException,
            GeneralUserAccountException, InvalidCredentialsException {

        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        DBManager dbManager = new DBManager(sessionFactory);
        AccountDatabaseHandler dbHandler = new AccountDatabaseHandler(dbManager);
        UserRoleHandler handler = new UserRoleHandler(dbHandler);
        AccountManager manager = new AccountManager(dbHandler);

        String email = "nick@gmail.com";
        String password = "secret";
        String securityQuestion = "question";
        String securityAnswer = "answer";

        manager.registerNewAdministratorAccount(email, password, securityQuestion, securityAnswer);
        handler.setUserRole(email, UserRole.REGULAR);
        SessionResponse sessionResponse = manager.login(email, password);
        UserRole actualRole = sessionResponse.getUserRole();

        Assert.assertEquals(UserRole.REGULAR, actualRole);
    }
}