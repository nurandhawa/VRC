package ca.sfu.teambeta.logic;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;

/**
 * Utility class that reads and writes data to the database
 */
public class DBManager {
    private final String FILENAME = "ladder.csv";
    private final int PLAYERID_INDEX = 0;
    private final int PLAYERNAME_INDEX = 1;

    /**
     * Saves values to the database.
     *
     * Schema: PlayerID, Player Name
     *
     * @param ladder: A Ladder object
     */
    public void saveToDB(Ladder ladder) {
        List<String[]> values = new ArrayList<>();

        int pairNumber = 0;
        for (Pair pair : ladder.getLadder()) {
            List<Player> players = pair.getPlayers();
            for (Player player : players) {
                String[] playerData = {String.valueOf(player.getPlayerID()), player.getName()};
                values.add(playerData);
            }
            pairNumber++;
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(FILENAME))) {
            for (String[] nextLine : values) {
                writer.writeNext(nextLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads values from the database.
     *
     * @return Ladder
     */
    public Ladder loadFromDB() {
        try (CSVReader reader = new CSVReader(new FileReader(FILENAME))) {
            List<String[]> ladderEntries = reader.readAll();
            List<Pair> pairs = new ArrayList<>();
            Iterator<String[]> ladderIter = ladderEntries.iterator();
            while (ladderIter.hasNext()) {
                String[] player1Data = ladderIter.next();
                String[] player2Data = ladderIter.next();
                Player player1 = new Player(Integer.parseInt(player1Data[PLAYERID_INDEX]), player1Data[PLAYERNAME_INDEX]);
                Player player2 = new Player(Integer.parseInt(player2Data[PLAYERID_INDEX]), player2Data[PLAYERNAME_INDEX]);
                Pair pair = new Pair(player1, player2);
                pairs.add(pair);
            }
            return new Ladder(pairs);
        } catch (IOException e) {
            return new Ladder();
        }
    }
}
