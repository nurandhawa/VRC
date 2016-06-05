package ca.sfu.teambeta.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jasdeep on 2016-05-31.
 *
 * This class instantiates mock Player and Pair array's for use in testing. As it's initial setup it
 * will use 18 names: to create 18 Player objects: 9 Pair objects
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
        // NOTE: If adding a name to this array, make sure you add two names.
        // Or else Pair creation will be thrown off
        //  IE: Keep it an even number of names

        names = new ArrayList<>(Arrays.asList(
                "Shikoba", "Lori", "Brant",
                "Mikki", "Kasandra", "Netta",
                "Dorita", "Lewis", "Nikolas",
                "Emmanuel", "Alvina", "Linette",
                "Dion", "Sidney", "Ryan",
                "Ronald", "Sharron", "Reed"
        ));
    }

    private void setupPlayers() {
        for (int i = 0; i < names.size(); i++) {
            Player player = new Player(i + 1, names.get(i));

            players.add(player);
        }
    }

    private void setupPairs() {
        if (players.size() % 2 != 0) {
            // Generally we won't reach this condition because
            // the add function will prompt for two names
            //  However it is useful if someone edits the array of names internal to this class

            System.out.println("ERROR: Odd number of players");

            // Gracefully handle this error by adding a placeholder name indicating the error
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

    private void setupScorecards() {
        // TODO: Mock Scorecard's
        //       Once GameManager accept's a ladder object, construct that here and pass it a ladder
        //       and then take the List<Scorecard> 's it creates and add mock scores.
    }


    // MARK: - Additional/Misc Method(s)
    public boolean addAdditionalPair(String player1, String player2) {
        if (player1.isEmpty() || player2.isEmpty()) {
            System.out.println("ERROR: Player name cannot be an empty string");
            return false;
        }

        this.names.add(player1);
        this.names.add(player2);

        this.players.clear();
        this.pairs.clear();

        setupPlayers();
        setupPairs();

        return true;
    }


    // MARK: - Printing Methods
    public void printPlayers() {
        System.out.println("\nPlayers: (ID & Name)");

        for (Player player : this.players) {
            System.out.println("" + player.getPlayerID() + " : " + player.getName());
        }
    }

    public void printPairs() {
        System.out.println("\nPairs:");
        System.out.println("------");

        int num = 1;
        for (Pair pair : this.pairs) {
            System.out.println("Pair #: " + num);
            System.out.println(pair + "\n");

            num += 1;
        }
    }


    /*
    // MARK: - Main Function, Usage of Class explained below
    public static void main(String[] args) {
        TestObjects testObjs = new TestObjects();

        // Use these function's to get a List of Players or Pairs
        List<Player> mockListOfPlayers = testObjs.getPlayers();
        List<Pair> mockListOfPairs = testObjs.getPairs();

        // Use these two functions to print contents (for debugging)
        testObjs.printPlayers();
        testObjs.printPairs();

        // Use this to add an additional Pair
        testObjs.addAdditionalPair("Edit_Player1", "Edit_Player2");

    }
    */

}
