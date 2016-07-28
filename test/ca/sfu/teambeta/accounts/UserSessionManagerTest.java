package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.SessionResponse;
import ca.sfu.teambeta.core.exceptions.NoSuchSessionException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by constantin on 27/07/16.
 */

public class UserSessionManagerTest {
    @After
    public void clearSessions(){
        UserSessionManager.clearSessions();
    }

    @Test
    public void createIdenticalSessions(){
        UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR);
        UserSessionManager.createNewSession("steve@gmail.com", UserRole.REGULAR);
        UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR);

        int numUsers = UserSessionManager.numUsersLoggedIn();
        Assert.assertEquals(3, numUsers);
    }

    @Test
    public void deleteSession() {
        SessionResponse session =  UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR);
        String token = session.getSessionToken();
        try {
            UserSessionManager.deleteSession(token);
        } catch (NoSuchSessionException e) {
            assert false;
        }

        int numUsers = UserSessionManager.numUsersLoggedIn();
        Assert.assertEquals(0, numUsers);
    }

    @Test
    public void authenticateSession() {
        SessionResponse session_1 = UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR);
        SessionResponse session_2 = UserSessionManager.createNewSession("nick@gmail.com", UserRole.ADMINISTRATOR);

        String token_1 = session_1.getSessionToken();
        String token_2 = session_2.getSessionToken();

        boolean authenticated_1 = false;
        boolean authenticated_2 = false;

        try {
            authenticated_1 = UserSessionManager.authenticateSession(token_1);
            authenticated_2 = UserSessionManager.authenticateSession(token_2);
        } catch (NoSuchSessionException e) {
            assert false;
        }

        Assert.assertTrue(authenticated_1);
        Assert.assertTrue(authenticated_2);
    }

    @Test (expected = NoSuchSessionException.class)
    public void invalidSessionId() throws NoSuchSessionException {
        UserSessionManager.authenticateSession("");
    }

    @Test
    public void isAdminSession(){
        SessionResponse session_1 = UserSessionManager.createNewSession("maria@gmail.com", UserRole.REGULAR);
        SessionResponse session_2 = UserSessionManager.createNewSession("nick@gmail.com", UserRole.ADMINISTRATOR);

        String token_1 = session_1.getSessionToken();
        String token_2 = session_2.getSessionToken();

        boolean admin_1 = false;
        boolean admin_2 = false;

        try {
            admin_1 = UserSessionManager.isAdministratorSession(token_1);
            admin_2 = UserSessionManager.isAdministratorSession(token_2);
        } catch (NoSuchSessionException e) {
            assert false;
        }

        Assert.assertFalse(admin_1);
        Assert.assertTrue(admin_2);
    }

    @Test
    public void checkEmail(){
        String expected_email = "maria@gmail.com";
        SessionResponse session = UserSessionManager.createNewSession(expected_email, UserRole.REGULAR);
        String token = session.getSessionToken();

        String actual_email = "";
        try {
             actual_email = UserSessionManager.getEmailFromSessionId(token);
        } catch (NoSuchSessionException e) {
            assert false;
        }

        Assert.assertEquals(expected_email, actual_email);
    }
}
