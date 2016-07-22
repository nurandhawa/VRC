package ca.sfu.teambeta.logic;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Dictionary;
import java.util.Hashtable;

import ca.sfu.teambeta.core.SessionInformation;
import ca.sfu.teambeta.core.SessionResponse;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.UserRole;
import ca.sfu.teambeta.core.exceptions.InvalidInputException;
import ca.sfu.teambeta.core.exceptions.NoSuchSessionException;

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


    // MARK: - The Core Session Methods
    public static SessionResponse createNewSession(User user, UserRole role) {
        String sessionId = generateRandomSessionID();
        String userEmail = user.getEmail();

        SessionInformation userSessionInformation = new SessionInformation(userEmail, role);

        sessions.put(sessionId, userSessionInformation);

        return new SessionResponse(sessionId, role);

    }

    public static void deleteSession(String sessionId) throws NoSuchSessionException {
        // Validate the input
        try {
            InputValidator.validateSessionIdFormat(sessionId);
        } catch (InvalidInputException e) {
            throw new NoSuchSessionException("Invalid SessionId");
        }

        // If session exists remove it, otherwise throw an exception
        if (sessions.get(sessionId) != null) {
            sessions.remove(sessionId);
        } else {
            throw new NoSuchSessionException("Invalid SessionId");
        }

    }

    public static boolean authenticateSession(String sessionId) throws NoSuchSessionException {
        // Validate the input
        try {
            InputValidator.validateSessionIdFormat(sessionId);
        } catch (InvalidInputException e) {
            throw new NoSuchSessionException("Invalid SessionId");
        }

        // Get the session metadata and check if it's expired
        SessionInformation sessionInformation = getSessionInformation(sessionId);

        if (sessionInformation.isSessionExpired() == false) {
            return true;
        } else {
            deleteSession(sessionId);
            return false;
        }
    }

    public static boolean isAdministratorSession(String sessionId)
            throws NoSuchSessionException {

        // Validate the input
        try {
            InputValidator.validateSessionIdFormat(sessionId);
        } catch (InvalidInputException e) {
            throw new NoSuchSessionException("Invalid SessionId");
        }

        SessionInformation sessionInformation = sessions.get(sessionId);

        return sessionInformation.isAdministratorSession();

    }

    public static String getEmailFromSessionId(String sessionId)
            throws NoSuchSessionException {

        // Validate the input
        try {
            InputValidator.validateSessionIdFormat(sessionId);
        } catch (InvalidInputException e) {
            throw new NoSuchSessionException("Invalid SessionId");
        }

        SessionInformation sessionInformation = sessions.get(sessionId);

        return sessionInformation.getEmail();
    }

    // MARK: Helper Methods
    private static SessionInformation getSessionInformation(String sessionId)
            throws NoSuchSessionException {

        SessionInformation sessionInformation = sessions.get(sessionId);

        if (sessionInformation == null) {
            throw new NoSuchSessionException("Invalid SessionId");
        } else {
            return sessionInformation;
        }
    }

    private static String generateRandomSessionID() {
        // See citations.txt for more information

        // DO NOT CHANGE THESE VALUES
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
