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

    private static final String PENALTY = "penalty";
    private static final String LATE = "late";
    private static final String MISS = "miss";
    private static final String ACCIDENT = "accident";

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
                return gson.toJson(ladderManager.getLadder());
            } else {
                response.body("No ladder was found");
                response.status(BAD_REQUEST);
                return response;
            }
        });

        //updates a pair's playing status or position
        patch("/api/ladder/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(ID));

            String status = request.queryParams(STATUS);
            if (status == null) {
                status = "";
            }

            int newPosition = -1;
            try {
                newPosition = Integer.parseInt(request.queryParams(POSITION));
            } catch (NumberFormatException ignored) {

            }

            boolean validNewPos = 0 < newPosition && newPosition <= ladderManager.ladderSize();
            boolean validStatus = status.equals("playing") || status.equals("not playing");

            Pair pair = ladderManager.searchPairById(id);

            if (pair == null) {
                response.status(NOT_FOUND);
                return getErrResponse("Pair " + id + " not found");
            }

            if (!validStatus && !validNewPos) {
                response.status(BAD_REQUEST);
                return getErrResponse("Specify what to update: position or status");
            } else if (validStatus && !validNewPos) {
                if (status.equals("playing")) {
                    ladderManager.setIsPlaying(pair);
                    response.status(OK);
                } else if (status.equals("not playing")) {
                    ladderManager.setNotPlaying(pair);
                    response.status(OK);
                } else {
                    response.status(BAD_REQUEST);
                    return getErrResponse("Invalid status");
                }
            } else if (!validStatus && validNewPos) {
                int currentPosition = pair.getPosition();
                ladderManager.movePair(currentPosition, newPosition);
                response.status(OK);

            } else {
                response.status(BAD_REQUEST);
                return getErrResponse("Cannot change both: position and status");
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
                    && extractedData.getPosition() <= ladderManager.ladderSize();
            List<Player> newPlayers = extractedData.getPlayers();

            if (newPlayers.size() != MAX_SIZE) {
                response.status(BAD_REQUEST);
                return getErrResponse("A Pair cannot have more than 2 players.");
            }

            for (int i = 0; i < MAX_SIZE; i++) {
                Integer existingId = newPlayers.get(i).getExistingId();
                if (existingId != null) {
                    newPlayers.remove(i);
                    newPlayers.add(i, ladderManager.searchPlayerById(existingId));
                }
            }

            Pair pair = new Pair(newPlayers.get(0), newPlayers.get(1));
            if (validPos) {
                ladderManager.addNewPairAtIndex(pair, extractedData.getPosition() - 1);
                response.status(OK);
            } else {
                ladderManager.addNewPair(pair);
                response.status(OK);
            }

            dbManager.addPairToLatestLadder(pair);

            return response;
        });

        //remove player from ladder
        delete("/api/ladder/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(ID));
            dbManager.removePairFromLatestLadder(id);
            Pair pair = ladderManager.searchPairById(id);
            int index = pair.getPosition() - 1;
            boolean removed = ladderManager.removePairAtIndex(index);

            if (removed) {
                response.body(getOkResponse(""));
                response.status(OK);
            } else {
                response.status(BAD_REQUEST);
                return getErrResponse("Index out of bound");
            }

            return getOkResponse("");
        });

        //add a penalty to a pair
        post("/api/matches/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(ID));
            Pair pair = ladderManager.searchPairById(id);
            if (pair == null) {
                response.status(BAD_REQUEST);
                return getErrResponse("Pair with the following id " + id + "wasn't found");
            }

            String penaltyType = request.queryParams(PENALTY);

            if (penaltyType.equals(LATE)) {
                pair.setPenalty(Penalty.LATE.getPenalty());
            } else if (penaltyType.equals(MISS)) {
                pair.setPenalty(Penalty.MISSING.getPenalty());
            } else if (penaltyType.equals(ACCIDENT)) {
                pair.setPenalty(Penalty.ACCIDENT.getPenalty());
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
            } else {
                response.status(BAD_REQUEST);
            }
            return gson.toJson(gameManager.getScorecards());
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
