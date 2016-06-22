package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.User;

import java.util.ArrayList;
import java.util.List;

/**
 * UserSessionManager handles
 * - Creating a new session
 *
 * Coming Soon
 * - Backing session's up to database
 */
public class UserSessionManager {
    private static List<String> sessions = new ArrayList<>();


    // MARK: - The Core Create/Delete Session Methods
    public static String createNewSession(User user) {
        String sessionID = "DI4J59JEN2XC39XFJJ30ASD3";
        sessions.add(sessionID);

        return sessionID;

    }


}
