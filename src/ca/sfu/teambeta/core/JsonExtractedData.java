package ca.sfu.teambeta.core;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by NoorUllah on 2016-06-22.
 */
public class JsonExtractedData {

    @Expose
    private List<Player> players;
    @Expose
    private int position;
    @Expose
    public String[][] results;
    @Expose
    private String email;
    @Expose
    private String password;
    @Expose
    private String time;


    public JsonExtractedData() {
        this.players = null;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
