package ca.sfu.teambeta.core;


import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * Created by Gordon Shieh on 25/05/16.
 */

@Entity(name="Player")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id = null;

    public Player() {
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name")
    private String fname;
    private String lname;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    private String phoneNumber;

    public Player(int id, String fname, String lname) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
    }

    public Player(int id, String fname) {
        this.id = id;
        this.fname = fname;
    }

    public Player(String fname, String lname, String phoneNumber) {
        this.fname = fname;
        this.lname = lname;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return fname;
    }

    public void setName(String name) {
        fname = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    // Once we get a database setup, the id attribute will be guaranteed unique
    public int hashCode() {
        return fname.hashCode();
    }
}
