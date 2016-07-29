package ca.sfu.teambeta.persistence;

import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;

/**
 * Created by constantin on 29/06/16.
 */
public class CSVReader {
    private static final String DEFAULT_FILENAME = "ladder.csv";
    private static final String TESTING_FILENAME = "ladder_junit.csv";
    private static final String DEFAULT_PATH = System.getProperty("user.home") + "/Downloads/";

    public static void main(String[] args) throws Exception {
        try {
            setupLadder();
        } catch (Exception e) {
            throw e;
        }
    }

    public static Ladder setupLadder() throws Exception {
        return setupLadder(DEFAULT_FILENAME);
    }

    private static Ladder setupLadder(String fileName) throws Exception {
        Map<Integer, Pair> pairs;
        pairs = getInformationAboutPairs(fileName);

        Ladder ladder = new Ladder();
        for (Map.Entry<Integer, Pair> entry : pairs.entrySet()) {
            Pair pair = entry.getValue();
            ladder.insertAtEnd(pair);
        }

        return ladder;
    }

    public static Ladder setupTestingLadder() throws Exception {
        return setupLadder(TESTING_FILENAME);
    }

    private static Map<Integer, Pair> getInformationAboutPairs(String fileName) throws Exception {
        Map<Integer, Pair> pairs = new HashMap<>();

        try (com.opencsv.CSVReader reader =
                     new com.opencsv.CSVReader(new FileReader(fileName))) {
            List<String[]> entries = reader.readAll();
            Iterator<String[]> iterator = entries.iterator();

            String[] pairInfo;
            while (iterator.hasNext()) {
                pairInfo = iterator.next();

                String lastNameFirst = pairInfo[0];
                String firstNameFirst = pairInfo[1];
                int idFirst = Integer.parseInt(pairInfo[2]);


                String lastNameSecond = pairInfo[3];
                String firstNameSecond = pairInfo[4];
                int idSecond = Integer.parseInt(pairInfo[5]);

                Player firstPlayer = new Player(firstNameFirst, lastNameFirst);
                Player secondPlayer = new Player(firstNameSecond, lastNameSecond);

                int index = Integer.parseInt(pairInfo[6]);
                Pair pair = new Pair(firstPlayer, secondPlayer, false);
                pair.setLastWeekPosition(index);

                pairs.put(index, pair);
            }
            reader.close();
        } catch (IOException e) {
            throw new Exception("Error reading file " + fileName);
        }

        return pairs;
    }

    public static void exportCsv(OutputStream outputStream, List<Pair> pairs) {
        OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
        CSVWriter writer = new CSVWriter(streamWriter);
        List<String[]> entries = new ArrayList<>();
        final int NUM_OF_COLUMNS_IN_CSV = 7;
        for (int i = 0; i < pairs.size(); i++) {
            Pair pair = pairs.get(i);
            Player p1 = pair.getPlayers().get(0);
            Player p2 = pair.getPlayers().get(1);
            String[] entry = new String[NUM_OF_COLUMNS_IN_CSV];
            entry[0] = p1.getLastName();
            entry[1] = p1.getFirstName();
            entry[2] = String.valueOf(p1.getID());
            entry[3] = p2.getLastName();
            entry[4] = p2.getFirstName();
            entry[5] = String.valueOf(p2.getID());
            entry[6] = String.valueOf(i + 1);
            entries.add(entry);
        }
        writer.writeAll(entries, false);
        try {
            writer.flush();
            writer.close();
            streamWriter.flush();
            streamWriter.close();
        } catch (Exception e) {

        }
    }

    public static Ladder importCsv(String fileName) {
        Ladder newLadder = null;
        String filePath = DEFAULT_PATH + fileName;
        try {
            newLadder = setupLadder(filePath);
        } catch (Exception e) {

        }
        return newLadder;
    }
}