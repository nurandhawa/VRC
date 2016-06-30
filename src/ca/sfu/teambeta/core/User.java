package ca.sfu.teambeta.core;

/**
 * The User class holds the notion of a database-user.
 *
 * A Player must hold a 1-to-1 relationship with a User; however the inverse is not true, as a
 * Administrator may not be a Player.
 */
public class User {
    private String email;
    private String passwordHash;
    private String firstName = "";
    private String lastName = "";
    private String phoneNumber = "";


    // MARK: - Constructors
    public User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public User(String email, String passwordHash, String phoneNumber) {
        this(email, passwordHash);
        this.phoneNumber = phoneNumber;
    }

    public User(String email, String passwordHash, String firstName, String lastName) {
        this(email, passwordHash);
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public User(String email, String passwordHash, String firstName, String lastName, String phoneNumber) {
        this(email, passwordHash, firstName, lastName);
        this.phoneNumber = phoneNumber;
    }


    // MARK: - Getters
    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}
