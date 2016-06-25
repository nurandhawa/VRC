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

}
