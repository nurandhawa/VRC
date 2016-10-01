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

    }

    public static Ladder setupLadder(DBManager db) throws Exception {
        FileReader reader = new FileReader(DEFAULT_FILENAME);
        return setupLadder(reader, db);
    }

    private static Ladder setupLadder(InputStreamReader inputStreamReader, DBManager db) throws Exception {
        Map<Integer, Pair> pairs;
        pairs = getInformationAboutPairs(inputStreamReader, db);
        Ladder ladder = new Ladder();
        for (Map.Entry<Integer, Pair> entry : pairs.entrySet()) {
            Pair pair = entry.getValue();
            ladder.insertAtEnd(pair);
        }
        return ladder;
    }

    public static Ladder setupTestingLadder(DBManager db) throws Exception {
        FileReader reader = new FileReader(TESTING_FILENAME);
        return setupLadder(reader, db);
    }

    private static Map<Integer, Pair> getInformationAboutPairs(InputStreamReader inputStreamReader,
                                                               DBManager db) throws Exception {
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
                    /*
                        Using existingIds here because the other ID is only generated after an entity is persisted.
                        In case the order of the csv changes (which it definitely would) and players are not
                        in order of the ID, then the ID that is given to an entity after persist would be different
                        from their ID on the csv. This is an issue not only because our csv would be out of sync with
                        the database but also result in invalid creation of the ladder. For e.g. if a player is in
                        multiple pairs and we compare by original ID then the database will have multiple entries
                        for the same player since their IDs on the csv and database don't match.
                     */
                    if (player.getExistingId() == idFirst) {
                        firstPlayer = player;
                    }
                    if (player.getExistingId() == idSecond) {
                        secondPlayer = player;
                    }
                }

                if (firstPlayer == null) {
                    firstPlayer = new Player(firstNameFirst, lastNameFirst);
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
                db.persistEntity(pair);

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

    public static Ladder importCsv(InputStreamReader inputStreamReader,
                                DBManager db) throws Exception {
        Ladder ladder = null;
        ladder = setupLadder(inputStreamReader, db);
        return ladder;
    }
}