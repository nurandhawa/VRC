package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.SessionInformation;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.NoSuchSessionException;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * UserSessionManager handles:
 * - Creating a new session
 * - Authenticating an existing session token
 * - Deleting a session
 * <p>
 * <p>
 * Coming Soon:
 * - Backing session's up to database
 * <p>
 * <p>
 * TODO:
 * - Have a UDID as the key to our 'sessions' dictionary so that the user may login on multiple devices
 */

public class UserSessionManager {
    private static Dictionary<String, SessionInformation> sessions = new Hashtable<>();
    //private static final String DEMO_SESSION_ID = "T3STS355IONID";


    // MARK: - The Core SessionInformation Methods
    public static String createNewSession(User user) {
        String sessionID = generateRandomSessionID();
        String userEmail = user.getEmail();
        String expiryDate = "datePlaceholder";

        SessionInformation userSessionInformation = new SessionInformation(userEmail, expiryDate);

        sessions.put(sessionID, userSessionInformation);

        return sessionID;

    }

    public static void deleteSession(String sessionID) throws NoSuchSessionException {
        if (sessions.get(sessionID) != null) {
            sessions.remove(sessionID);
        } else {
            throw new NoSuchSessionException("Invalid SessionID");
        }

    }

    public static boolean authenticateSession(String sessionID) throws NoSuchSessionException {
        if (sessionID == null || sessionID == "") {
            return false;
        }

        SessionInformation sessionInformation = getSessionInformation(sessionID);

        if (sessionInformation.isSessionExpired() == false) {
            return true;
        } else {
            deleteSession(sessionID);
            return false;
        }
    }


    // MARK: Helper Methods
    private static SessionInformation getSessionInformation(String sessionID) throws NoSuchSessionException {
        SessionInformation sessionInformation = sessions.get(sessionID);

        if (sessionInformation == null) {
            throw new NoSuchSessionException("Invalid SessionID");
        } else {
            return sessionInformation;
        }
    }

    private static String generateRandomSessionID() {
        // See citation.txt for more information
        final int MAX_BIT_LENGTH = 130;
        final int ENCODING_BASE = 32;

        SecureRandom random = new SecureRandom();
        String sessionID = "";

        sessionID = new BigInteger(MAX_BIT_LENGTH, random).toString(ENCODING_BASE);

        // Make sure we don't have two of the same sessionID's
        while (sessions.get(sessionID) != null) {
            sessionID = new BigInteger(MAX_BIT_LENGTH, random).toString(ENCODING_BASE);
        }

        return sessionID;
    }


    // MARK: Misc Methods
    public static int numUsersLoggedIn() {
        return sessions.size();
    }


    // MARK: Main Function (Quick and dirty testing for now - will be refactored into tests)
    /*
    public static void main(String[] args) {
        // Creating a session
        User testUser1 = new User("admin@vrc.ca", "pwHash1");
        User testUser2 = new User("john@vrc.ca", "pwHash2");

        String user1SessionID = createNewSession(testUser1);
        String user2SessionID = createNewSession(testUser2);

        System.out.println(user1SessionID);
        System.out.println(user2SessionID);

        System.out.println("Number of sessions: " + UserSessionManager.numUsersLoggedIn()); // Will be 2


        // Authenticating SessionID's
        boolean sessionIDStatus;

        try {
            sessionIDStatus = authenticateSession(user1SessionID);
        } catch (NoSuchSessionException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println(sessionIDStatus);


        // Deleting a session
        try {
            UserSessionManager.deleteSession(user1SessionID);
        } catch (NoSuchSessionException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Number of sessions: " + UserSessionManager.numUsersLoggedIn()); // Will be 1 now

    }
    */

}
