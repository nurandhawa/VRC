package ca.sfu.teambeta.core;


import com.google.gson.annotations.Expose;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Transient;

import ca.sfu.teambeta.persistence.Persistable;

/**
 * Created by Gordon Shieh on 25/05/16.
 */
@Entity(name = "Player")
public class Player extends Persistable {
    private UUID uuid = UUID.randomUUID();
    @Expose
    private String firstName;
    @Expose
    private String lastName;
    private String email;
    @Transient
    @Expose
    private int existingId;

    public Player() {
    }

    public Player(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = null;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        changeUUID();
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        changeUUID();
        this.lastName = lastName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }

        final Player otherPlayer = (Player) other;
        return getFirstName().equals(otherPlayer.getFirstName())
                && getLastName().equals(otherPlayer.getLastName())
                && uuid.equals(otherPlayer.uuid);
    }

    @Override
    public int hashCode() {
        return 23 * getFirstName().hashCode()
                * getLastName().hashCode() + uuid.hashCode();
    }

    public int getExistingId() {
        return existingId;
    }

    public void setExistingId(int existingId) {
        this.existingId = existingId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private void changeUUID() {
        this.uuid = UUID.randomUUID();
    }
}
