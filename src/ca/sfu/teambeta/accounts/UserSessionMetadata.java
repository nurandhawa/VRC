package ca.sfu.teambeta.accounts;

import java.util.Calendar;

/**
 * The UserSessionMetadata class holds all the related information (metadata) on a logged in user's session.
 */

public class UserSessionMetadata {
    // TTL is calculated in days
    private static final int TIME_MEASUREMENT = Calendar.DAY_OF_MONTH;
    private static final int TIME_TO_LIVE_ADMIN = 3;
    private static final int TIME_TO_LIVE_ANON_USER = 2;
    private static final int TIME_TO_LIVE_REG_USER = 30;

    private String email;
    private Calendar expiryDate;
    private UserRole role;

    // MARK: Constructor
    public UserSessionMetadata(String email, UserRole role) {
        this.email = email;
        this.role = role;
        this.expiryDate = Calendar.getInstance();

        switch (role) {
            case REGULAR:
                expiryDate.add(TIME_MEASUREMENT, TIME_TO_LIVE_REG_USER);
                break;
            case ADMINISTRATOR:
                expiryDate.add(TIME_MEASUREMENT, TIME_TO_LIVE_ADMIN);
                break;
            case ANONYMOUS:
                expiryDate.add(TIME_MEASUREMENT, TIME_TO_LIVE_ANON_USER);
                break;
            default:
                // In case another role is added in the future this won't break
                expiryDate.add(TIME_MEASUREMENT, TIME_TO_LIVE_REG_USER);
                break;
        }
    }

    // MARK: Getters
    public Calendar getExpiryDate() {
        return expiryDate;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }


    // MARK: Helper Methods
    public boolean isSessionExpired() {
        Calendar currentTime = Calendar.getInstance();
        int value = currentTime.compareTo(expiryDate);
        boolean expired = (value == 1);

        return expired;
    }

}
