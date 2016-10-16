package ca.sfu.teambeta.core;

import ca.sfu.teambeta.accounts.UserRole;
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
    private String securityQuestion = "";
    private String securityAnswerHash = "";
    private UserRole role = UserRole.REGULAR;

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

    public UserRole getUserRole() {
        return role;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public String getSecurityAnswerHash() {
        return securityAnswerHash;
    }

    public Player getAssociatedPlayer() {
        return associatedPlayer;
    }


    // MARK: Setters
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public void setSecurityAnswerHash(String securityAnswerHash) {
        this.securityAnswerHash = securityAnswerHash;
    }

    public void setUserRole(UserRole role) {
        this.role = role;
    }


    // MARK: Misc Methods
    public void associatePlayer(Player player) {
        this.associatedPlayer = player;
        player.setEmail(getEmail());
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
        } else {
            return true;
        }
    }

    @Override
    public int hashCode() {
        int result = email.hashCode();
        return result;
    }
}
