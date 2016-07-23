package ca.sfu.teambeta.persistence;

import ca.sfu.teambeta.core.SessionInformation;
import ca.sfu.teambeta.logic.UserSessionManager;
import com.google.gson.JsonObject;

/**
 * Created by constantin on 21/07/16.
 */
public class SessionJSONSerializer implements JSONSerializer {
    private static final String EMAIL = "email";
    private static final String ADMIN = "admin";
    private String token;

    public SessionJSONSerializer(String sessionToken) {
        this.token = sessionToken;
    }

    @Override
    public String toJson() {
        JsonObject jsonObject = new JsonObject();
        String email;
        try {
            email = UserSessionManager.getEmailFromSessionId(token);
            jsonObject.addProperty(EMAIL, email);

            boolean isAdmin = UserSessionManager.isAdministratorSession(token);
            jsonObject.addProperty(ADMIN, isAdmin);

        } catch (Exception e) {
            return "";
        }

        return jsonObject.toString();
    }
}
