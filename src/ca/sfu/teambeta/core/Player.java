package ca.sfu.teambeta.core;


import com.google.gson.annotations.SerializedName;

import javax.persistence.Entity;

/**
 * Created by Gordon Shieh on 25/05/16.
 */
@Entity(name = "Player")
public class Player extends Persistable {

    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    private String phoneNumber;

    private int playerID;
    private String name;

    public Player(int id, String name) {
        this.playerID = id;
        this.name = name;
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getName() {
        return name;
    }

    public Player(int id, String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        setID(id);
    }

    public Player(String firstName, String lastName, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
