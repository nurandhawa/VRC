package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.Session;
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
 *
 *
 * Coming Soon:
 * - Backing session's up to database
 *
 *
 * TODO:
 * - Have a UDID as the key to our 'sessions' dictionary so that the user may login on multiple devices
 *
 */

public class UserSessionManager {
    private static Dictionary<String, Session> sessions = new Hashtable<>();
    private static final String DEMO_SESSION_ID = "T3STS355IONID";


    // MARK: - The Core Session Methods
    public static String createNewSession(User user) {
        // Create a session, if a session with the email already exists, it will be overridden
        String sessionID = generateRandomToken();
        String expiryDate = "datePlaceholder";

        Session userSession = new Session(sessionID, expiryDate);

        String userEmail = user.getEmail();

        sessions.put(userEmail, userSession);

        return sessionID;

    }

    public static void deleteSession(String email, String token) throws NoSuchSessionException {
        Session session = getUserSession(email);

        boolean tokensMatch = session.getToken().equals(token);
        if (tokensMatch) {
            sessions.remove(email);
        } else {
            throw new NoSuchSessionException("Invalid token");
        }

    }

    public static boolean authenticateToken(String email, String token) throws NoSuchSessionException {
        Session session = getUserSession(email);

        // TODO: Check expiry date
        boolean tokensMatch = session.getToken().equals(token);
        if (tokensMatch) {
            return true;
        } else {
            return false;
        }

    }


    // MARK: Helper Methods
    private static Session getUserSession(String email) throws NoSuchSessionException {
        Session session = sessions.get(email);

        if (session == null) {
            throw new NoSuchSessionException("No session exists for user: " + email);
        } else {
            return session;
        }
    }

    private static String generateRandomToken() {
        // See citation.txt for more information
        int MAX_BIT_LENGTH = 130;
        int ENCODING_BASE = 32;

        SecureRandom random = new SecureRandom();
        return new BigInteger(MAX_BIT_LENGTH, random).toString(ENCODING_BASE);
    }


    // MARK: Main Function (Quick and dirty testing for now - will be refactored into tests)
    public static void main(String[] args) {
        // Creating a session
        User testUser1 = new User("admin@vrc.ca", "pwHash1");
        User testUser2 = new User("john@vrc.ca", "pwHash2");

        createNewSession(testUser1);
        createNewSession(testUser1); // Will update previous entry
        createNewSession(testUser2);

        System.out.println(sessions.get("admin@vrc.ca").getToken());
        System.out.println("Number of sessions: " + sessions.size()); // Will be 2


        // Authenticating a session
        boolean tokenStatus;
        try {
            tokenStatus = authenticateToken("admin@vrc.ca", DEMO_SESSION_ID);
        } catch (NoSuchSessionException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Session exists?: " + tokenStatus);


        // Deleting a session
        try {
            deleteSession("admin@vrc.ca", DEMO_SESSION_ID);
        } catch (NoSuchSessionException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Number of sessions: " + sessions.size()); // Will be 1

        System.out.println(generateRandomToken());


    }

}
