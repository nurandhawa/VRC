package ca.sfu.teambeta.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jasdeep on 2016-05-31.
 *
 * This class instantiates mock Player, Pair objects and array's for use in testing
 */
public class TestObjects {

    private List<String> names;
    private List<Player> players;
    private List<Pair> pairs;

    public TestObjects() {
        players = new ArrayList<>();
        pairs = new ArrayList<>();

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


    // MARK: - Methods to setup our array's
    private void setupNames() {
        // NOTE: If adding a name to this array, make sure you add two names. Or else Pair creation will be thrown off
        //  IE: Keep it an even number of names

        names = new ArrayList<String>(Arrays.asList("Shikoba", "Lori", "Brant", "Mikki", "Kasandra", "Netta", "Dorita",
                "Lewis", "Nikolas", "Emmanuel", "Alvina", "Linette", "Dion", "Sidney"));
    }

    private void setupPlayers() {
        for (int i = 0; i < names.size(); i++) {
            Player player = new Player(i + 1, names.get(i));

            players.add(player);
        }
    }

    private void setupPairs() {
        if (players.size() % 2 != 0) {
            System.out.println("ERROR: Odd number of players");

            // Gracefully handle this error by adding a placeholder name indicating the error
            //  Generally we won't reach this condition because the add function will prompt for two names
            String errorName = "ERR-ADD_NAME";
            this.names.add(errorName);
            this.players.add(new Player(this.names.size(), errorName));
        }

        for (int i = 0; i < players.size(); i += 2) {
            Player player1 = players.get(i);
            Player player2 = players.get(i + 1);

            Pair pair = new Pair(player1, player2);

            pairs.add(pair);
        }
    }


    // MARK: - Method to

    // MARK: - Printing Methods
    public void printPlayers() {
        System.out.println("\nPlayers: (ID & Name)");
        // Print all names
        for (Player player : this.players) {
            System.out.println("" + player.getPlayerID() + " : " + player.getName());
        }
    }

    public void printPairs() {
        System.out.println("\nPairs:");
        System.out.println("------");
        // Print all names

        int i = 1;
        for (Pair pair : this.pairs) {
            System.out.println("Pair #: " + i);
            System.out.println(pair + "\n");

            i += 1;
        }
    }


    public static void main(String[] args) {
        TestObjects testObjs = new TestObjects();

        testObjs.printPlayers();
        testObjs.printPairs();
    }
}
