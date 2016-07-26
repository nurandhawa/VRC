package ca.sfu.teambeta.accounts;

import java.util.Calendar;

/**
 * The UserSessionMetadata class holds all the related information on a logged in user's session.
 */

public class UserSessionMetadata {
    private static final int TIME_TO_LIVE = 7; // TTL is calculated in days
    private String email;
    private Calendar expiryDate;
    private UserRole role;


    // MARK: Constructor
    public UserSessionMetadata(String email, UserRole role) {
        this.email = email;
        this.role = role;
        this.expiryDate = Calendar.getInstance();
        expiryDate.add(Calendar.DAY_OF_MONTH, TIME_TO_LIVE);
    }


    // MARK: Getters
    public Calendar getExpiryDate() {
        return expiryDate;
    }

    public String getEmail() {
        return email;
    }

    /*
    // Method in here if in future explicit role is needed
    public UserRole getRole() { return role; }
    */


    // MARK: Helper Methods
    public boolean isSessionExpired() {
        Calendar currentTime = Calendar.getInstance();
        int value = currentTime.compareTo(expiryDate);
        boolean expired = (value == 1);

        return expired;
    }

    public boolean isAdministratorSession() {
        if (role == UserRole.ADMINISTRATOR) {
            return true;
        } else {
            return false;
        }
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
