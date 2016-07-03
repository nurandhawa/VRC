package ca.sfu.teambeta.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

import ca.sfu.teambeta.persistence.Persistable;

/**
 * The User class holds the notion of a database-user.
 *
 * A Player must hold a 1-to-1 relationship with a User; however the inverse is not true, as a
 * Administrator may not be a Player.
 */

@Entity
public class User extends Persistable {
    @Column(name = "email", unique = true)
    private String email;
    private String passwordHash;
    private String firstName = "";
    private String lastName = "";
    private String phoneNumber = "";

    @OneToOne
    private Player associatedPlayer = null;

    // MARK: - Constructors
    // Dafault constructor for Hibernate
    public User() {

    }

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

    public void associatePlayer(Player player) {
        this.associatedPlayer = player;
    }

    public void unassociatePlayer() {
        this.associatedPlayer = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (!email.equals(user.email)) return false;
        if (!firstName.equals(user.firstName)) return false;
        if (!lastName.equals(user.lastName)) return false;
        return phoneNumber != null ? phoneNumber.equals(user.phoneNumber) : user.phoneNumber == null;

    }

    @Override
    public int hashCode() {
        int result = email.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        return result;
    }
}
