package ca.sfu.teambeta;

import ca.sfu.teambeta.core.*;
import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;
import spark.Filter;
import spark.Response;
import spark.Request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ca.sfu.teambeta.core.JsonExtractedData;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Penalty;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;
import ca.sfu.teambeta.persistence.DBManager;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.patch;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

/**
 * Created by NoorUllah on 2016-06-16.
 */
public class AppController {
    private static final String ID = "id";
    private static final String STATUS = "newStatus";
    private static final String POSITION = "position";
    private static final String PLAYING = "playing";
    private static final String NOT_PLAYING = "not playing";

    private static final String PENALTY = "penalty";
    private static final String LATE = "late";
    private static final String MISS = "miss";
    private static final String ACCIDENT = "accident";

    private static final String PAIR_NOT_FOUND = "No pair was found with given id";
    private static final String ID_NOT_INT = "Id is not of integer type";


    private static final int NOT_FOUND = 404;
    private static final int BAD_REQUEST = 400;
    private static final int OK = 200;

    private static Gson gson;

    public AppController(LadderManager ladderManager, GameManager gameManager, DBManager dbManager) {
        port(8000);
        staticFiles.location(".");

        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        //homepage: return ladder
        get("/api/ladder", (request, response) -> {
            String json = dbManager.getJSONLadder();
            if (!json.isEmpty()) {
                response.status(OK);
                response.body(dbManager.getJSONLadder());
                return response;
            } else {
                response.body("No ladder was found");
                response.status(BAD_REQUEST);
                return response;
            }
        });

        //updates a pair's playing status or position
        patch("/api/ladder/:id", (request, response) -> {
            int id;
            int newPosition;
            try {
                id = Integer.parseInt(request.params(ID));
                newPosition = Integer.parseInt(request.queryParams(POSITION));
            } catch (Exception e) {
                response.body(ID_NOT_INT + " or Position");
                response.status(BAD_REQUEST);
                return response;
            }

            String status = request.queryParams(STATUS);

            boolean validNewPos = 0 < newPosition && newPosition <= dbManager.getLadderSize();
            boolean validStatus = status.equals(PLAYING) || status.equals(NOT_PLAYING);

            if (!dbManager.hasPairID(id)) {
                response.body(PAIR_NOT_FOUND);
                response.status(NOT_FOUND);
                return getErrResponse("Pair " + id + " not found");
            }

            if (!validStatus && !validNewPos) {
                response.status(BAD_REQUEST);
                return getErrResponse("Specify what to update: position or status");
            } else if (validStatus && !validNewPos) {
                if (status.equals(PLAYING)) {
                    boolean statusChanged = dbManager.setPairActive(id);
                    if (statusChanged) {
                        response.status(OK);
                    } else {
                        Player activePlayer = dbManager.getAlreadyActivePlayer(id);
                        String firstName = activePlayer.getFirstName();
                        String lastName = activePlayer.getLastName();
                        response.body("Player " + firstName + " " + lastName + " is already playing");
                        response.status(NOT_FOUND);
                    }
                } else if (status.equals(NOT_PLAYING)) {
                    dbManager.setPairInactive(id);
                    response.status(OK);
                }

            } else if (!validStatus && validNewPos) {
                dbManager.movePair(id, newPosition);
                response.status(OK);

            } else {
                response.body("Cannot change both: position and status");
                response.status(BAD_REQUEST);
            }

            return getOkResponse("");
        });

        //add pair to ladder
        //in case of adding a pair at the end of ladder, position is length of ladder
        post("/api/ladder", (request, response) -> {
            String body = request.body();
            JsonExtractedData extractedData = gson.fromJson(body, JsonExtractedData.class);
            final int MAX_SIZE = 2;

            boolean validPos = 0 < extractedData.getPosition()
                    && extractedData.getPosition() <= dbManager.getLadderSize();

            List<Player> playerData = extractedData.getPlayers();

            if (playerData.size() != MAX_SIZE) {
                response.status(BAD_REQUEST);
                return getErrResponse("A Pair cannot have more than 2 players.");
            }

            List<Player> newPlayers = new ArrayList<>();

            for (int i = 0; i < MAX_SIZE; i++) {
                if (playerData.get(i).getExistingId() == null) {
                    newPlayers.add(new Player(playerData.get(i).getFirstName(), playerData.get(i).getLastName(),
                            playerData.get(i).getPhoneNumber()));
                } else {
                    newPlayers.add(dbManager.getPlayerFromID(playerData.get(i).getExistingId()));
                }
            }

            Pair pair = new Pair(newPlayers.get(0), newPlayers.get(1));

            if (validPos) {
                dbManager.addPair(pair, extractedData.getPosition() - 1);
                response.status(OK);
            } else {
                dbManager.addPair(pair);
                response.status(OK);
            }

            return response;
        });

        //remove player from ladder
        delete("/api/ladder/:id", (request, response) -> {
            int id;
            try {
                id = Integer.parseInt(request.queryParams(ID));
            } catch (Exception e) {
                response.body(ID_NOT_INT);
                response.status(BAD_REQUEST);
                return response;
            }

            if (!dbManager.hasPairID(id)) {
                response.body(PAIR_NOT_FOUND);
                response.status(NOT_FOUND);
                return response;
            }

            dbManager.removePair(id);
            response.status(OK);

            return getOkResponse("");
        });

        //add a penalty to a pair
        post("/api/matches/:id", (request, response) -> {
            int id;
            try {
                id = Integer.parseInt(request.queryParams(ID));
            } catch (Exception e) {
                response.body(ID_NOT_INT);
                response.status(BAD_REQUEST);
                return response;
            }

            if (!dbManager.hasPairID(id)) {
                response.body(PAIR_NOT_FOUND);
                response.status(NOT_FOUND);
                return getErrResponse("Pair with the following id " + id + "wasn't found");
            }

            String penaltyType = request.queryParams(PENALTY);

            if (penaltyType == LATE) {
                dbManager.addPenaltyToPairToLatestGameSession(id, Penalty.LATE);
            } else if (penaltyType == MISS) {
                dbManager.addPenaltyToPairToLatestGameSession(id, Penalty.MISSING);
            } else if (penaltyType == ACCIDENT) {
                dbManager.addPenaltyToPairToLatestGameSession(id, Penalty.ACCIDENT);
            } else {
                response.status(BAD_REQUEST);
                return getErrResponse("Invalid Penalty Type");
            }
            return getOkResponse("");
        });

        //Show a list of matches
        get("/api/matches", (request, response) -> {
            if (gameManager.getScorecards() != null) {
                response.status(OK);
                response.body(dbManager.getJSONScorecards());
            } else {
                response.body("No scorecards were found");
                response.status(NOT_FOUND);
            }
            return response;
        });

        //Input match results
        patch("/api/matches/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(ID));
            Scorecard group = gameManager.getGroupByIndex(id);
            int numTeams = group.getReorderedPairs().size();
            String[][] input = new String[numTeams][numTeams];

            String body = request.body();
            JsonExtractedData extractedData = gson.fromJson(body, JsonExtractedData.class);

            int rows = extractedData.results.length;
            int cols = extractedData.results[0].length;
            boolean isValidResult = (rows == numTeams) && (cols == numTeams);

            if (!isValidResult) {
                response.status(BAD_REQUEST);
                return getErrResponse("Invalid result format.");
            }

            input = extractedData.results.clone();
            gameManager.inputMatchResults(group, input);
            response.status(OK);
            return getOkResponse("");
        });

        //Remove a pair from a match
        delete("/api/matches/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(ID));
            Pair pair = ladderManager.searchPairById(id);

            if (pair == null) {
                response.status(BAD_REQUEST);
                return getErrResponse("Pair " + id + " doesn't exist.");
            } else if (!pair.isPlaying()) {
                response.status(BAD_REQUEST);
                return getErrResponse("Pair " + id + " is not playing.");
            }
            gameManager.removePlayingPair(pair);
            response.status(OK);
            return getOkResponse("");
        });

    }

    private String getOkResponse(String message) {
        JsonObject okResponse = new JsonObject();
        okResponse.addProperty("status", "OK");
        okResponse.addProperty("message", message);
        return gson.toJson(okResponse);
    }

    private String getErrResponse(String message) {
        JsonObject errResponse = new JsonObject();
        errResponse.addProperty("status", "ERROR");
        errResponse.addProperty("message", message);
        return gson.toJson(errResponse);
    }
}
