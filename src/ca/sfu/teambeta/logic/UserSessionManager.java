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
 */

public class UserSessionManager {
    private static Dictionary<String, SessionInformation> sessions = new Hashtable<>();


    // MARK: - The Core SessionInformation Methods
    public static String createNewSession(User user) {
        String sessionId = generateRandomSessionID();
        String userEmail = user.getEmail();
        String expiryDate = "datePlaceholder";

        SessionInformation userSessionInformation = new SessionInformation(userEmail, expiryDate);

        sessions.put(sessionId, userSessionInformation);

        return sessionId;

    }

    public static void deleteSession(String sessionId) throws NoSuchSessionException {
        if (sessions.get(sessionId) != null) {
            sessions.remove(sessionId);
        } else {
            throw new NoSuchSessionException("Invalid SessionID");
        }

    }

    public static boolean authenticateSession(String sessionId) throws NoSuchSessionException {
        if (sessionId == null || sessionId == "") {
            return false;
        }

        SessionInformation sessionInformation = getSessionInformation(sessionId);

        if (sessionInformation.isSessionExpired() == false) {
            return true;
        } else {
            deleteSession(sessionId);
            return false;
        }
    }


    // MARK: Helper Methods
    private static SessionInformation getSessionInformation(String sessionId)
            throws NoSuchSessionException {

        SessionInformation sessionInformation = sessions.get(sessionId);

        if (sessionInformation == null) {
            throw new NoSuchSessionException("Invalid SessionID");
        } else {
            return sessionInformation;
        }
    }

    private static String generateRandomSessionID() {
        // See citations.txt for more information
        final int MAX_BIT_LENGTH = 130;
        final int ENCODING_BASE = 32;

        SecureRandom random = new SecureRandom();
        String sessionId = "";

        sessionId = new BigInteger(MAX_BIT_LENGTH, random).toString(ENCODING_BASE);

        // Make sure we don't have two of the same sessionId's
        while (sessions.get(sessionId) != null) {
            sessionId = new BigInteger(MAX_BIT_LENGTH, random).toString(ENCODING_BASE);
        }

        return sessionId;
    }


    // MARK: Misc Methods
    public static int numUsersLoggedIn() {
        return sessions.size();
    }

}
