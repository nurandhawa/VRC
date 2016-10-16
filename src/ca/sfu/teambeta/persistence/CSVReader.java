package ca.sfu.teambeta.persistence;

import ca.sfu.teambeta.core.User;
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
            entries.remove(0);
            Iterator<String[]> iterator = entries.iterator();
            List<Player> distinctPlayers = new ArrayList<>();

            String[] pairInfo;
            int index = 1;
            while (iterator.hasNext()) {
                pairInfo = iterator.next();

                String lastNameFirst = pairInfo[0];
                String firstNameFirst = pairInfo[1];
                int idFirst = Integer.parseInt(pairInfo[2]);
                String emailFirst = pairInfo[3];
                String lastNameSecond = pairInfo[4];
                String firstNameSecond = pairInfo[5];
                int idSecond = Integer.parseInt(pairInfo[6]);
                String emailSecond = pairInfo[7];

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

                Pair pair = new Pair(firstPlayer, secondPlayer, false);
                db.persistEntity(pair);

                associateUsers(db, emailFirst, emailSecond, firstPlayer, secondPlayer);

                pairs.put(index, pair);
                index++;
            }
            reader.close();
        } catch (IOException e) {
            throw new Exception("Malformed CSV stream");
        }

        return pairs;
    }

    private static void associateUsers(DBManager db, String emailFirst, String emailSecond, Player firstPlayer, Player secondPlayer) {
        User user = db.getUser(emailFirst);
        if (user != null) {
            user.associatePlayer(firstPlayer);
        }
        user = db.getUser(emailSecond);
        if (user != null) {
            user.associatePlayer(secondPlayer);
        }
    }

    public static void exportCsv(OutputStream outputStream, List<Pair> pairs) throws IOException {
        OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
        CSVWriter writer = new CSVWriter(streamWriter);

        List<String[]> entries = new ArrayList<>();
        final int NUM_OF_COLUMNS_IN_CSV = 9;
        String[] headers = {"Last Name", "First Name", "Player ID", "Email",
                "Last Name", "First Name", "Player ID", "Email",
                "Pair ID"};
        entries.add(headers);
        for (int i = 0; i < pairs.size(); i++) {
            Pair pair = pairs.get(i);
            Player p1 = pair.getPlayers().get(0);
            String[] entry = new String[NUM_OF_COLUMNS_IN_CSV];
            entry[0] = p1.getLastName();
            entry[1] = p1.getFirstName();
            entry[2] = String.valueOf(p1.getID());
            entry[3] = p1.getEmail();
            Player p2 = pair.getPlayers().get(1);
            entry[4] = p2.getLastName();
            entry[5] = p2.getFirstName();
            entry[6] = String.valueOf(p2.getID());
            entry[7] = p2.getEmail();
            entry[8] = String.valueOf(pair.getID());
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