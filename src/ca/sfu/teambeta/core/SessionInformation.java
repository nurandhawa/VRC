package ca.sfu.teambeta.core;

import java.util.Calendar;

/**
 * The SessionInformation class holds all the related information on a logged in user's session.
 */
public class SessionInformation {
    // TTL is calculated in minutes
    private static final int TIME_TO_LIVE = 60;
    private String email;
    private Calendar expiryDate;
    private UserRole role;

    // MARK: Constructor
    public SessionInformation(String email, UserRole role) {
        this.email = email;
        this.role = role;
        this.expiryDate = Calendar.getInstance();
        expiryDate.add(Calendar.MINUTE, TIME_TO_LIVE);
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

    public static void main(String[] args) {
        Calendar time = Calendar.getInstance();
        System.out.println("Current Time:       " + time.getTime());
        time.add(Calendar.HOUR, 1);
        System.out.println("Time after one hour:" + time.getTime());

        Calendar currentTime = Calendar.getInstance();
        Calendar past = Calendar.getInstance();
        past.add(Calendar.YEAR, -3);

        Calendar future = Calendar.getInstance();
        future.add(Calendar.YEAR, 3);

        System.out.println(currentTime.compareTo(past));
        System.out.println(currentTime.compareTo(future));

        SessionInformation info = new SessionInformation("email", UserRole.REGULAR);
        System.out.println(info.getExpiryDate().getTime());
        //For testing set TTL to 1 minute !
        System.out.println("Expired? (Expected: false) " + info.isSessionExpired());
        try {
            //61 sec
            Thread.sleep(61000);
        } catch (Exception e) {
            System.out.println("Interrupted");
        }
        System.out.println("Expired? (Expected: true) " + info.isSessionExpired());
    }
}
