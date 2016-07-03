package ca.sfu.teambeta.persistence;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;

/**
 * Created by constantin on 29/06/16.
 */
public class CSVReader {
    private static final String DEFAULT_FILENAME = "ladder.csv";

    public static Ladder setupLadder() throws Exception {
        List<Player> players;
        try {
            players = getInformationAboutPlayers();
        } catch (Exception exception) {
            throw exception;
        }

        List<Pair> pairs = new ArrayList<>();
        int index = 0;
        Player temp = null;
        for (Player player : players) {
            index++;
            if (index == 2) {
                Pair newPair = new Pair(player, temp);
                pairs.add(newPair);
                index = 0;
            }
            temp = player;
        }
        return new Ladder(pairs);
    }

    public static List<Player> getInformationAboutPlayers() throws Exception {
        List<Player> players = new ArrayList<>();
        try (com.opencsv.CSVReader reader = new com.opencsv.CSVReader(new FileReader(DEFAULT_FILENAME))) {
            List<String[]> entries = reader.readAll();
            Iterator<String[]> iterator = entries.iterator();

            String[] pairInfo;
            while (iterator.hasNext()) {
                pairInfo = iterator.next();

                String lastName = pairInfo[0];
                String firstName = pairInfo[1];
                //String id = pairInfo[2];

                Player player = new Player(firstName, lastName);
                players.add(player);

            }
            reader.close();
        } catch (IOException e) {
            throw new Exception("Error reading file " + DEFAULT_FILENAME);
        }

        return players;
    }
}
