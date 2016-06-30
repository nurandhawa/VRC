package ca.sfu.teambeta.persistence;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.sfu.teambeta.core.Player;

/**
 * Created by constantin on 29/06/16.
 */
public class CSVReader {
    private static final String DEFAULT_FILENAME = "ladder.csv";

    public static List<Player> getInformationAboutPlayers() throws Exception {
        List<Player> players;
        try (com.opencsv.CSVReader reader = new com.opencsv.CSVReader(new FileReader(DEFAULT_FILENAME))) {
            List<String[]> entries = reader.readAll();
            Iterator<String[]> iterator = entries.iterator();
            players = new ArrayList<>();

            String[] pairInfo;
            while (iterator.hasNext()) {
                pairInfo = iterator.next();

                String lastName = pairInfo[0];
                String firstName = pairInfo[1];
                String id = pairInfo[2];

                if (lastName.equals("*") && firstName.equals("*") && id.equals("*")) {
                    Player player = new Player(lastName, firstName);
                    players.add(player);
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new Exception("Error reading file " + DEFAULT_FILENAME);
        }

        return players;
    }
}
