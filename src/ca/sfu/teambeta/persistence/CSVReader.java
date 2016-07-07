package ca.sfu.teambeta.persistence;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;

/**
 * Created by constantin on 29/06/16.
 */
public class CSVReader {
    private static final String DEFAULT_FILENAME = "ladder.csv";

    public static void main(String args[]) throws  Exception{
        try {
            setupLadder();
        } catch(Exception e){
            throw e ;
        }
    }

    public static Ladder setupLadder() throws Exception {
        Map<Integer, Pair> pairs;
        try {
            pairs = getInformationAboutPairs();
        } catch (Exception exception){
            throw exception;
        }

        Ladder ladder = new Ladder();
        for(Map.Entry<Integer, Pair> entry : pairs.entrySet()){
            Pair pair = entry.getValue();
            ladder.insertAtEnd(pair);
        }

        return ladder;
    }

    private static Map<Integer, Pair> getInformationAboutPairs() throws Exception {
        Map<Integer, Pair> pairs = new HashMap<>();

        try (com.opencsv.CSVReader reader = new com.opencsv.CSVReader(new FileReader(DEFAULT_FILENAME))) {
            List<String[]> entries = reader.readAll();
            Iterator<String[]> iterator = entries.iterator();

            String[] pairInfo;
            while (iterator.hasNext()) {
                pairInfo = iterator.next();

                String lastName = pairInfo[0];
                String firstName = pairInfo[1];
                int id = Integer.parseInt(pairInfo[2]);

                Player firstPlayer = new Player(lastName, firstName);

                lastName = pairInfo[3];
                firstName = pairInfo[4];
                id = Integer.parseInt(pairInfo[5]);

                Player secondPlayer = new Player(lastName, firstName);

                int index = Integer.parseInt(pairInfo[6]);
                Pair pair = new Pair(firstPlayer, secondPlayer, false);

                pairs.put(index, pair);
            }
            reader.close();
        } catch (IOException e) {
            throw new Exception("Error reading file " + DEFAULT_FILENAME);
        }

        return pairs;
    }
}