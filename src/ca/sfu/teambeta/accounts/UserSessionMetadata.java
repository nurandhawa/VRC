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


    // MARK: Helper Methods
    public boolean isSessionExpired() {
        Calendar currentTime = Calendar.getInstance();
        int value = currentTime.compareTo(expiryDate);
        boolean expired = (value == 1);

        return expired;
    }

    public boolean isAdministratorSession() {
        return role == UserRole.ADMINISTRATOR;
    }

    /*
    public static void main(String[] args) {
        Calendar time = Calendar.getInstance();
        System.out.println("Current Time:     " + time.getTime());
        time.add(Calendar.DAY_OF_MONTH, 7);
        System.out.println("Time after 7 days:" + time.getTime());

        Calendar currentTime = Calendar.getInstance();
        Calendar past = Calendar.getInstance();
        past.add(Calendar.YEAR, -3);

        Calendar future = Calendar.getInstance();
        future.add(Calendar.YEAR, 3);

        System.out.println(currentTime.compareTo(past));
        System.out.println(currentTime.compareTo(future));

        UserSessionMetadata info = new UserSessionMetadata("email", UserRole.REGULAR);
        System.out.println(info.getExpiryDate().getTime());
        //For testing set TTL on 6 seconds
        System.out.println("Expired? (Expected: false) " + info.isSessionExpired());
        try {
            //61 sec
            Thread.sleep(5000);
        } catch (Exception e) {
            System.out.println("Interrupted");
        }
        System.out.println("Expired? (Expected: true) " + info.isSessionExpired());
    }
    */
}
