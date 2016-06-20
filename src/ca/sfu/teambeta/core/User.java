package ca.sfu.teambeta.core;

/**
 * Created by j_jassal588 on 2016-06-19.
 */
public class User {
    private String username;
    private String passwordHash;


    // MARK: - Constructor
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
    }


    // MARK: - Getters
    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
