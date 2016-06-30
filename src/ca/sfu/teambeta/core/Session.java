package ca.sfu.teambeta.core;

/**
 * The Session class holds all the related information on a logged in user's session.
 *
 * TODO - Change expiryDate to use a Calendar object
 */
public class Session {
    private String token;
    private String expiryDate;

    // MARK: Constructor
    public Session(String token, String expiryDate) {
        this.token = token;
        this.expiryDate = expiryDate;
    }


    // MARK: Getters
    public String getExpiryDate() {
        return expiryDate;
    }

    public String getToken() {
        return token;
    }
}
