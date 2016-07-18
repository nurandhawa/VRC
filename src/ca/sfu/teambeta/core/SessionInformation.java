package ca.sfu.teambeta.core;

/**
 * The SessionInformation class holds all the related information on a logged in user's session.
 *
 * TODO - Change expiryDate to use a Calendar object
 */
public class SessionInformation {
    private String email;
    private String expiryDate;
    private UserRole role;

    // MARK: Constructor
    public SessionInformation(String email, UserRole role) {
        this.email = email;
        this.role = role;
        this.expiryDate = "datePlaceholder";
    }


    // MARK: Getters
    public String getExpiryDate() {
        return expiryDate;
    }

    public String getEmail() { return email; }

    /*
    // Method in here if in future explicit role is needed
    public UserRole getRole() { return role; }
    */

    // MARK: Helper Methods
    public boolean isSessionExpired() {
        return false;
    }

    public boolean isAdministratorSession() {
        if (role == UserRole.ADMINISTRATOR) {
            return true;
        } else {
            return false;
        }

    }
}
