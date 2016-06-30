package ca.sfu.teambeta.core;

/**
 * The SessionInformation class holds all the related information on a
 * logged in user's session.
 *
 * TODO
 * - Change expiryDate to use a Calendar object
 *
 */
public class SessionInformation {
    private String email;
    private String expiryDate;

    // MARK: Constructor
    public SessionInformation(String email, String expiryDate) {
        this.email = email;
        this.expiryDate = expiryDate;
    }


    // MARK: Getters
    public String getExpiryDate() {
        return expiryDate;
    }

    public String getEmail() {
        return email;
    }


    // MARK: Helper Methods
    public boolean isSessionExpired() {
        return false;
    }
}
