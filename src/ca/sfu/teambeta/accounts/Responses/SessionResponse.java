package ca.sfu.teambeta.accounts.Responses;

import ca.sfu.teambeta.accounts.UserRole;
import com.google.gson.annotations.Expose;

/**
 * Data structure to pass session information to the front end
 */
public class SessionResponse {
    @Expose
    private String sessionToken;
    @Expose
    private UserRole userRole;
    @Expose
    private int playerId;

    public SessionResponse(String sessionId, UserRole userRole, int playerId) {
        this.sessionToken = sessionId;
        this.userRole = userRole;
        this.playerId = playerId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public int getPlayerId() {
        return playerId;
    }
}
