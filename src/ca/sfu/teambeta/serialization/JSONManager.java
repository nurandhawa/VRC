package ca.sfu.teambeta.serialization;

import ca.sfu.teambeta.core.Pair;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.logic.GameSession;
import ca.sfu.teambeta.persistence.DBManager;

/**
 * Creates JSON for various data structures used in the project.
 */
public class JSONManager {
    private DBManager dbManager;

    public JSONManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    public synchronized String getJSONLadder(GameSession gameSession) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(GameSession.class, new LadderJSONSerializer())
                .create();
        return gson.toJson(gameSession);
    }

    public synchronized String getJSONDanglingPlayers() {
        Gson gson = new GsonBuilder().create();
        List<Player> players = getDanglingPlayers(dbManager);
        return gson.toJson(players);
    }

    public synchronized String getJSONPlayersWithAccount() {
        Gson gson = new GsonBuilder().create();
        List<Player> players = getPlayersWithAccount(dbManager);
        return gson.toJson(players);
    }

    public synchronized List<Player> getPlayers(DBManager dbManager) {
        List<Player> players = new ArrayList<>();
        for (Pair pair : dbManager.getLatestLadder().getPairs()) {
            players.add(pair.getPlayers().get(0));
            players.add(pair.getPlayers().get(1));
        }
        return players;
    }

    public synchronized String getJSONAllPlayers() {
        Gson gson = new GsonBuilder().create();
        List<Player> players = getPlayers(dbManager);
        return gson.toJson(players);
    }

    public synchronized String getJSONScorecards(GameSession gameSession) {
        List<Scorecard> scorecards = gameSession.getScorecards();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Scorecard.class, new ScorecardSerializer())
                .create();

        return gson.toJson(scorecards);
    }

    public synchronized String getJSONSession(String sessionToken) {
        //ex: {"email":"test@gmail.com","admin":true}
        JSONSerializer serializer = new SessionJSONSerializer(sessionToken);
        return serializer.toJson();
    }

    private synchronized List<Player> getDanglingPlayers(DBManager dbManager) {
        List<Player> players = getPlayers(dbManager);
        List<Player> playersWithAccounts = getPlayersWithAccount(dbManager);

        for (Player player : playersWithAccounts) {
            players.remove(player);
        }
        return players;
    }

    private synchronized List<Player> getPlayersWithAccount(DBManager dbManager) {
        List<Player> playersWithAccount = new ArrayList<>();
        List<User> users = dbManager.getAllUsers();
        for (User user : users) {
            if (user.getAssociatedPlayer() != null) {
                playersWithAccount.add(user.getAssociatedPlayer());
            }
        }
        return playersWithAccount;
    }
}
