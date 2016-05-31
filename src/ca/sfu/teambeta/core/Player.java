package ca.sfu.teambeta.core;

/**
 * Created by Gordon Shieh on 25/05/16.
 */
public class Player {
<<<<<<< HEAD
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Player player = (Player) o;
        return playerID == player.playerID;
    }

    @Override
    // Once we get a database setup, the playerID attribute will be guaranteed unique
    public int hashCode() {
        return playerID;
>>>>>>> dc2f92b3850b156d2118c3378f0716ffd2d157f8
    }
}
