package ca.sfu.teambeta.accounts.Responses;

import com.google.gson.annotations.Expose;

/**
 * Created by AlexLand on 2016-07-28.
 */
public class PasswordResetResponse {
    @Expose
    String securityQuestion;

    public PasswordResetResponse(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }
}
