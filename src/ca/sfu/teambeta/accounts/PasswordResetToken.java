package ca.sfu.teambeta.accounts;

import java.util.Calendar;

/**
 * This class holds the token that authenticates whether a user
 * can change their password. This token is usually granted by
 * a method such as the one that validates the security question answer
 *
 */

public class PasswordResetToken {
    private static final int TIME_TO_LIVE = 15; // TTL is calculated in minutes
    private String token;
    private Calendar expiryDate;


    // MARK: Constructor
    PasswordResetToken(String token) {
        this.token = token;
        this.expiryDate = Calendar.getInstance();
        expiryDate.add(Calendar.MINUTE, TIME_TO_LIVE);
    }


    // MARK: Getter
    public String getToken() {
        return token;
    }


    // MARK: Helper Methods
    public boolean isExpired() {
        Calendar currentTime = Calendar.getInstance();
        int value = currentTime.compareTo(expiryDate);
        boolean expired = (value == 1);

        return expired;
    }
}
