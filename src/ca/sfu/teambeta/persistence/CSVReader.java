package ca.sfu.teambeta.persistence;

import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

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
        FileReader reader = new FileReader(DEFAULT_FILENAME);
        return setupLadder(reader);
    }

    private static Ladder setupLadder(InputStreamReader inputStreamReader) throws Exception {
        Map<Integer, Pair> pairs;
        pairs = getInformationAboutPairs(inputStreamReader);
        Ladder ladder = new Ladder();
        for (Map.Entry<Integer, Pair> entry : pairs.entrySet()) {
            Pair pair = entry.getValue();
            ladder.insertAtEnd(pair);
        }
        return ladder;
    }

    public static Ladder setupTestingLadder() throws Exception {
        FileReader reader = new FileReader(TESTING_FILENAME);
        return setupLadder(reader);
    }

    private static Map<Integer, Pair> getInformationAboutPairs(InputStreamReader inputStreamReader) throws Exception {
        Map<Integer, Pair> pairs = new LinkedHashMap<>();

        try (com.opencsv.CSVReader reader =
                     new com.opencsv.CSVReader(inputStreamReader)) {
            List<String[]> entries = reader.readAll();
            Iterator<String[]> iterator = entries.iterator();
            List<Player> distinctPlayers = new ArrayList<>();

            String[] pairInfo;
            while (iterator.hasNext()) {
                pairInfo = iterator.next();

                String lastNameFirst = pairInfo[0];
                String firstNameFirst = pairInfo[1];
                int idFirst = Integer.parseInt(pairInfo[2]);

                String lastNameSecond = pairInfo[3];
                String firstNameSecond = pairInfo[4];
                int idSecond = Integer.parseInt(pairInfo[5]);

                Player firstPlayer = null;
                Player secondPlayer = null;

                for (Player player : distinctPlayers) {
                    if (player.getExistingId() == idFirst) {
                        firstPlayer = player;
                    }
                    if (player.getExistingId() == idSecond) {
                        secondPlayer = player;
                    }
                }

                if (firstPlayer == null) {
                    firstPlayer = new Player(firstNameFirst, lastNameFirst);
                    // this is for the above part where we check if the ids are the same.
                    // cannot use getId() since that is not set at this point.
                    firstPlayer.setExistingId(idFirst);
                    distinctPlayers.add(firstPlayer);
                }
                if (secondPlayer == null) {
                    secondPlayer = new Player(firstNameSecond, lastNameSecond);
                    secondPlayer.setExistingId(idSecond);
                    distinctPlayers.add(secondPlayer);
                }

                int index = Integer.parseInt(pairInfo[6]);
                Pair pair = new Pair(firstPlayer, secondPlayer, false);

                pairs.put(index, pair);
            }
            reader.close();
        } catch (IOException e) {
            throw new Exception("Malformed CSV stream");
        }

        return pairs;
    }

    public static void exportCsv(OutputStream outputStream, List<Pair> pairs) throws IOException {
        OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
        CSVWriter writer = new CSVWriter(streamWriter);

        List<String[]> entries = new ArrayList<>();
        final int NUM_OF_COLUMNS_IN_CSV = 8;
        for (int i = 0; i < pairs.size(); i++) {
            Pair pair = pairs.get(i);
            Player p1 = pair.getPlayers().get(0);
            String[] entry = new String[NUM_OF_COLUMNS_IN_CSV];
            entry[0] = p1.getLastName();
            entry[1] = p1.getFirstName();
            entry[2] = String.valueOf(p1.getID());
            Player p2 = pair.getPlayers().get(1);
            entry[3] = p2.getLastName();
            entry[4] = p2.getFirstName();
            entry[5] = String.valueOf(p2.getID());
            entry[6] = String.valueOf(i + 1);
            entry[7] = String.valueOf(pair.getID());
            entries.add(entry);
        }
        writer.writeAll(entries, false);
        try {
            writer.flush();
            writer.close();
            streamWriter.flush();
            streamWriter.close();
        } catch (IOException e) {
            throw e;
        }
    }

    public static List<Integer> getPairIdsFromCsvStream(InputStreamReader inputStreamReader) throws Exception {
        List<Integer> pairIds = new ArrayList<>();
        try (com.opencsv.CSVReader reader =
                     new com.opencsv.CSVReader(inputStreamReader)) {
            List<String[]> entries = reader.readAll();
            Iterator<String[]> iterator = entries.iterator();

            String[] pairInfo;
            while (iterator.hasNext()) {
                pairInfo = iterator.next();

                int pairId = Integer.parseInt(pairInfo[7]);
                pairIds.add(pairId);
            }
            reader.close();
            return pairIds;
        } catch (IOException e) {
            throw new Exception("Malformed CSV stream");
        }
    }
}