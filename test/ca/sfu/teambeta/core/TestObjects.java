package ca.sfu.teambeta.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by j_jassal588 on 2016-05-31.
 */
public class TestObjects {

    private List<String> names;
    private List<Player> players;
    private List<Pair> pairs;

    public void TestObjects() {
        players = new ArrayList<>();

        setupNames();
        setupPlayers();
        setupPairs();
    }


    // MARK: - Getters
    public List<Player> getPlayers() {
        return players;
    }

    public List<Pair> getPairs() {
        return pairs;
    }


    // MARK: - Functions to setup our array's
    private void setupNames() {
        // Note: If adding a name to this array, make sure you add two names. Or else Pair creation will be thrown off
        //  IE: Keep it an even number of names

        names = new ArrayList<String>(Arrays.asList("Shikoba", "Lori", "Brant", "Mikki", "Kasandra", "Netta", "Dorita",
                "Lewis", "Nikolas", "Emmanuel", "Alvina", "Linette", "Dion", "Abegail"));
    }

    private void setupPlayers() {
        for (int i = 0; i < names.size(); i++) {
            Player player = new Player(i, names.get(i));

            players.add(player);
        }
    }

    private void setupPairs() {
        for (int i = 0; i < players.size(); i += 2) {
            Player player1 = players.get(i);
            Player player2 = players.get(i + 1);

            Pair pair = new Pair(player1, player2);

        }
    }
}
