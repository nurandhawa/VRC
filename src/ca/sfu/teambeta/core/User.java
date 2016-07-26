package ca.sfu.teambeta.core;

import ca.sfu.teambeta.persistence.Persistable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * The User class holds the notion of a database-user.
 * <p>
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
    private String securityQuestion = "";
    private String securityAnswerHash = "";

    @OneToOne
    private Player associatedPlayer = null;


    // MARK: - Constructors
    // Default Constructor for Hibernate
    public User() {

    }

    public User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public User(String email, String passwordHash, String securityQuestion, String securityAnswerHash) {
        this(email, passwordHash);
        this.securityQuestion = securityQuestion;
        this.securityAnswerHash = securityAnswerHash;
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

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public String getSecurityAnswerHash() {
        return securityAnswerHash;
    }


    // MARK: Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public void setSecurityAnswerHash(String securityAnswerHash) {
        this.securityAnswerHash = securityAnswerHash;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    // MARK: Misc Methods
    public void associatePlayer(Player player) {
        this.associatedPlayer = player;
    }

    public void unassociatePlayer() {
        this.associatedPlayer = null;
    }


    // MARK: Helper Methods
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (!email.equals(user.email)) {
            return false;
        }
        if (!firstName.equals(user.firstName)) {
            return false;
        }
        if (!lastName.equals(user.lastName)) {
            return false;
        }
        if (phoneNumber != null) {
            return phoneNumber.equals(user.phoneNumber);
        } else {
            return true;
        }
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
