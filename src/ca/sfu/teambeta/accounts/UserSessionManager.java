package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.accounts.Responses.SessionResponse;
import ca.sfu.teambeta.core.exceptions.InvalidInputException;
import ca.sfu.teambeta.core.exceptions.NoSuchSessionException;
import ca.sfu.teambeta.logic.InputValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserSessionManager handles:
 * - Creating a new session
 * - Authenticating an existing sessionId
 * - Deleting a session
 * - Checking if a session belongs to an administrator
 * - Getting the number of users logged in
 */

public class UserSessionManager {
    private static Map<String, UserSessionMetadata> sessions = new HashMap<>();
    private static TokenGenerator tokenGenerator = new TokenGenerator();


    // MARK: Methods for creating/deleting a session
    public static SessionResponse createNewSession(String email, UserRole role, int playerId) {
        return createNewSession(email, role, playerId, false);
    }

    public static SessionResponse createNewSession(String email, UserRole role, int playerId, boolean extendSessionExpiry) {
        String sessionId = tokenGenerator.generateUniqueRandomToken();

        UserSessionMetadata metadata = new UserSessionMetadata(email, role, extendSessionExpiry);

        sessions.put(sessionId, metadata);

        return new SessionResponse(sessionId, role, playerId);
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
        UserSessionMetadata metadata = getSessionMetadata(sessionId);

        if (metadata.isSessionExpired()) {
            deleteSession(sessionId);
            return false;
        }

        return true;
    }

    public static boolean isAdministratorSession(String sessionId) throws NoSuchSessionException {
        // Validate the input
        try {
            InputValidator.validateSessionIdFormat(sessionId);
        } catch (InvalidInputException e) {
            throw new NoSuchSessionException("Invalid SessionId");
        }

        UserSessionMetadata metadata = sessions.get(sessionId);

        return (metadata.getRole() == UserRole.ADMINISTRATOR);

    }

    public static boolean isAnonymousSession(String sessionId) throws NoSuchSessionException {
        // Validate the input
        try {
            InputValidator.validateSessionIdFormat(sessionId);
        } catch (InvalidInputException e) {
            throw new NoSuchSessionException("Invalid SessionId");
        }

        UserSessionMetadata metadata = sessions.get(sessionId);

        return (metadata.getRole() == UserRole.ANONYMOUS);

    }

    public static String getEmailFromSessionId(String sessionId) throws NoSuchSessionException {
        // Validate the input
        try {
            InputValidator.validateSessionIdFormat(sessionId);
        } catch (InvalidInputException e) {
            throw new NoSuchSessionException("Invalid SessionId");
        }

        UserSessionMetadata metadata = sessions.get(sessionId);

        return metadata.getEmail();
    }


    // MARK: Helper Methods
    private static UserSessionMetadata getSessionMetadata(String sessionId) throws NoSuchSessionException {
        UserSessionMetadata metadata = sessions.get(sessionId);

        if (metadata == null) {
            throw new NoSuchSessionException("Invalid SessionId");
        } else {
            return metadata;
        }

    }


    // MARK: Misc Methods
    public static void clearExpiredSessions() {
        List<String> sessionsToRemove = new ArrayList<>();

        // Get a list of expired sessionId's
        for (Map.Entry<String, UserSessionMetadata> sessionEntry : sessions.entrySet()) {
            String sessionId = sessionEntry.getKey();
            UserSessionMetadata metadata = sessionEntry.getValue();

            if (metadata.isSessionExpired()) {
                sessionsToRemove.add(sessionId);
            }
        }

        // Remove the sessions
        for (String sessionId : sessionsToRemove) {
            sessions.remove(sessionId);
        }
    }


    // MARK: Misc Methods
    public static int numUsersLoggedIn() {
        return sessions.size();
    }

    public static void clearSessions() {
        // This will cause everyone to re-authenticate
        sessions.clear();
    }

}
