package ca.sfu.teambeta.persistence;

import ca.sfu.teambeta.accounts.UserRole;
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
                int pairId;
                Pair pair = null;

                if (pairInfo[0].equals("")) {
                    associateUser(db, pairInfo, null, null);
                    continue;
                } else {
                    pairId = Integer.parseInt(pairInfo[0]);
                }

                if (db.getGameSessionLatest() != null && db.hasPairID(pairId)) {
                    pair = db.getPairFromID(pairId);
                    associateUser(db, pairInfo, pair.getPlayers().get(0), pair.getPlayers().get(1));
                } else {
                    String lastNameFirst = pairInfo[1];
                    String firstNameFirst = pairInfo[2];
                    int idFirst = Integer.parseInt(pairInfo[3]);

                    String lastNameSecond = pairInfo[9];
                    String firstNameSecond = pairInfo[10];
                    int idSecond = Integer.parseInt(pairInfo[11]);

                    Player firstPlayer = null;
                    Player secondPlayer = null;

                    if (db.getGameSessionLatest() != null && db.hasPlayerID(idFirst)) {
                        firstPlayer = db.getPlayerFromID(idFirst);
                    }
                    if (db.getGameSessionLatest() != null && db.hasPlayerID(idSecond)) {
                        secondPlayer = db.getPlayerFromID(idSecond);
                    }

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
                        if (firstPlayer == null) {
                            if (player.getExistingId() == idFirst) {
                                firstPlayer = player;
                            }
                        }
                        if (secondPlayer == null) {
                            if (player.getExistingId() == idSecond) {
                                secondPlayer = player;
                            }
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

                    pair = new Pair(firstPlayer, secondPlayer, false);
                    db.persistEntity(pair);
                    associateUser(db, pairInfo, firstPlayer, secondPlayer);
                }

                pairs.put(index, pair);
                index++;
            }
            reader.close();
        } catch (IOException e) {
            throw new Exception("Malformed CSV stream");
        }

        return pairs;
    }

    /*  If an account is already created, the player is again associated with that
    *   account since the id has changed. If account has not been created,
    *   then create a new one.  */
    private static void associateUser(DBManager db, String[] pairInfo,
                                      Player firstPlayer, Player secondPlayer) {
        String emailFirst = pairInfo[4];
        String emailSecond = pairInfo[12];
        User user = db.getUser(emailFirst);
        if (user != null) {
            if (firstPlayer != null) {
                user.associatePlayer(firstPlayer);
            }
        } else {
            if (!emailFirst.equals("")) {
                int startIndex = 5;
                createUser(emailFirst, pairInfo, startIndex, firstPlayer, db);
            }
        }
        user = db.getUser(emailSecond);
        if (user != null) {
            if (secondPlayer != null) {
                user.associatePlayer(secondPlayer);
            }
        } else {
            if (!emailSecond.equals("")) {
                int startIndex = 13;
                createUser(emailSecond, pairInfo, startIndex, secondPlayer, db);
            }
        }
    }

    private static void createUser(String email, String[] pairInfo, int startIndex, Player player,
                                   DBManager db) {
        User newUser;
        String passHash = pairInfo[startIndex];
        String question = pairInfo[startIndex + 1];
        String answer = pairInfo[startIndex + 2];
        UserRole userRole = null;
        for (UserRole role : UserRole.values()) {
            if (role.name().equals(pairInfo[startIndex + 3])) {
                userRole = role;
            }
        }
        newUser = new User(email, passHash, question, answer);
        newUser.setUserRole(userRole);
        if (!userRole.equals(UserRole.ADMINISTRATOR)) {
            newUser.associatePlayer(player);
        }
        try {
            db.addNewUser(newUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Ladder importCsv(InputStreamReader inputStreamReader,
                                   DBManager db) throws Exception {
        Ladder ladder = null;
        ladder = setupLadder(inputStreamReader, db);
        return ladder;
    }

    public static void exportCsv(OutputStream outputStream, List<Pair> pairs,
                                 DBManager db) throws IOException {
        OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
        CSVWriter writer = new CSVWriter(streamWriter);

        List<String[]> entries = new ArrayList<>();
        final int NUM_OF_COLUMNS_IN_CSV = 17;
        String[] headers = {"Pair ID", "Last Name", "First Name", "Player ID", "Email",
                            "Password Hash", "Security Question", "Answer Hash", "Role",
                            "Last Name", "First Name", "Player ID", "Email",
                            "Password Hash", "Security Question", "Answer Hash", "Role"};
        entries.add(headers);
        for (int i = 0; i < pairs.size(); i++) {
            Pair pair = pairs.get(i);
            Player p1 = pair.getPlayers().get(0);
            Player p2 = pair.getPlayers().get(1);
            User user1 = db.getUser(p1.getEmail());
            User user2 = db.getUser(p2.getEmail());
            String[] entry = new String[NUM_OF_COLUMNS_IN_CSV];
            entry[0] = String.valueOf(pair.getID());
            entry[1] = p1.getLastName();
            entry[2] = p1.getFirstName();
            entry[3] = String.valueOf(p1.getID());
            entry[4] = p1.getEmail();
            if (user1 != null) {
                entry[5] = user1.getPasswordHash();
                entry[6] = user1.getSecurityQuestion();
                entry[7] = user1.getSecurityAnswerHash();
                entry[8] = String.valueOf(user1.getUserRole());
            } else {
                entry[5] = null;
                entry[6] = null;
                entry[7] = null;
                entry[8] = null;
            }
            entry[9] = p2.getLastName();
            entry[10] = p2.getFirstName();
            entry[11] = String.valueOf(p2.getID());
            entry[12] = p2.getEmail();
            if (user2 != null) {
                entry[13] = user2.getPasswordHash();
                entry[14] = user2.getSecurityQuestion();
                entry[15] = user2.getSecurityAnswerHash();
                entry[16] = String.valueOf(user2.getUserRole());
            } else {
                entry[13] = null;
                entry[14] = null;
                entry[15] = null;
                entry[16] = null;
            }

            entries.add(entry);
        }

        addAdminAccountsToCsv(entries, db, NUM_OF_COLUMNS_IN_CSV);
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

    private static void addAdminAccountsToCsv(List<String[]> entries, DBManager db, int cols) {
        List<User> users = db.getAllUsersOfRole(UserRole.ADMINISTRATOR);
        for (User user : users) {
            String[] entry = new String[cols];
            entry[0] = null;
            entry[1] = null;
            entry[2] = null;
            entry[3] = null;
            entry[4] = user.getEmail();
            entry[5] = user.getPasswordHash();
            entry[6] = user.getSecurityQuestion();
            entry[7] = user.getSecurityAnswerHash();
            entry[8] = String.valueOf(user.getUserRole());

            entry[9] = null;
            entry[10] = null;
            entry[11] = null;
            entry[12] = null;
            entry[13] = null;
            entry[14] = null;
            entry[15] = null;
            entry[16] = null;
            entries.add(entry);
        }
    }
}