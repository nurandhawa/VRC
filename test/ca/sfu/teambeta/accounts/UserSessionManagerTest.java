package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.SessionResponse;
import ca.sfu.teambeta.core.exceptions.*;
import ca.sfu.teambeta.persistence.DBManager;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;


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
        UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR);
        UserSessionManager.createNewSession("steve@gmail.com", UserRole.REGULAR);
        UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR);

        int numUsers = UserSessionManager.numUsersLoggedIn();
        Assert.assertEquals(3, numUsers);
    }

    @Test
    public void deleteSession() throws NoSuchSessionException {
        SessionResponse session = UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR);
        String token = session.getSessionToken();

        UserSessionManager.deleteSession(token);

        int numUsers = UserSessionManager.numUsersLoggedIn();
        Assert.assertEquals(0, numUsers);
    }

    @Test
    public void authenticateSession() throws NoSuchSessionException {
        SessionResponse session_1 = UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR);
        SessionResponse session_2 = UserSessionManager.createNewSession("nick@gmail.com", UserRole.ADMINISTRATOR);

        String token_1 = session_1.getSessionToken();
        String token_2 = session_2.getSessionToken();

        boolean authenticated_1 = UserSessionManager.authenticateSession(token_1);
        boolean authenticated_2 = UserSessionManager.authenticateSession(token_2);

        Assert.assertTrue(authenticated_1);
        Assert.assertTrue(authenticated_2);
    }

    @Test(expected = NoSuchSessionException.class)
    public void invalidSessionId() throws NoSuchSessionException {
        UserSessionManager.authenticateSession("");
    }

    @Test
    public void isAdminSession() throws NoSuchSessionException {
        SessionResponse session_1 = UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR);
        SessionResponse session_2 = UserSessionManager.createNewSession("nick@gmail.com", UserRole.ADMINISTRATOR);

        String token_1 = session_1.getSessionToken();
        String token_2 = session_2.getSessionToken();

        boolean admin_1 = UserSessionManager.isAdministratorSession(token_1);
        boolean admin_2 = UserSessionManager.isAdministratorSession(token_2);


        Assert.assertFalse(admin_1);
        Assert.assertTrue(admin_2);
    }

    @Test
    public void checkEmail() throws NoSuchSessionException {
        String expected_email = "maria@gmail.com";
        SessionResponse session = UserSessionManager.createNewSession(expected_email, UserRole.REGULAR);
        String token = session.getSessionToken();

        String actual_email = UserSessionManager.getEmailFromSessionId(token);

        Assert.assertEquals(expected_email, actual_email);
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
        manager.registerNewAdministratorAccount(email, password);
        handler.setUserRole(email, UserRole.REGULAR);
        SessionResponse sessionResponse = manager.login(email, password);
        UserRole actualRole = sessionResponse.getUserRole();

        Assert.assertEquals(UserRole.REGULAR, actualRole);
    }
}