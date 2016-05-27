package ca.sfu.teambeta.core;

/**
 * Created by Gordon Shieh on 25/05/16.
 */
public class Player {
    private int playerID;
    private String name;

    Player(int id, String name) {
        this.playerID = id;
        this.name = name;
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return playerID == player.playerID;

    }

    @Override
    public int hashCode() {
        return playerID;
    }
}
