package ca.sfu.teambeta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import ca.sfu.teambeta.core.JsonExtractedData;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Penalty;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.exceptions.AccountRegistrationException;
import ca.sfu.teambeta.core.exceptions.InternalHashingException;
import ca.sfu.teambeta.core.exceptions.InvalidCredentialsException;
import ca.sfu.teambeta.core.exceptions.InvalidUserInputException;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;
import ca.sfu.teambeta.logic.AccountManager;
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

    public AppController(DBManager dbManager) {
        port(8000);
        staticFiles.location(".");
        AccountManager accountManager = new AccountManager(dbManager);
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        //homepage: return ladder
        get("/api/ladder", (request, response) -> {
            String json = dbManager.getJSONLadder();
            if (!json.isEmpty()) {
                return dbManager.getJSONLadder();
            } else {
                response.status(NOT_FOUND);
                return getErrResponse("No ladder was found");
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
                response.status(BAD_REQUEST);
                return getErrResponse(ID_NOT_INT + " or Position");
            }

            String status = request.queryParams(STATUS);

            boolean validNewPos = 0 < newPosition && newPosition <= dbManager.getLadderSize();
            boolean validStatus = status.equals(PLAYING) || status.equals(NOT_PLAYING);

            if (!dbManager.hasPairID(id)) {
                response.status(NOT_FOUND);
                return getErrResponse(PAIR_NOT_FOUND + id);
            }

            if (!validStatus && !validNewPos) {
                response.status(BAD_REQUEST);
                return getErrResponse("Specify what to update: position or status");
            } else if (validStatus && !validNewPos) {
                if (status.equals(PLAYING)) {
                    boolean statusChanged = dbManager.setPairActive(id);
                    if (statusChanged) {
                        return getOkResponse("");
                    } else {
                        Player activePlayer = dbManager.getAlreadyActivePlayer(id);
                        String firstName = activePlayer.getFirstName();
                        String lastName = activePlayer.getLastName();
                        response.status(NOT_FOUND);
                        return getErrResponse("Player " + firstName + " " + lastName + " is already playing");
                    }
                } else if (status.equals(NOT_PLAYING)) {
                    dbManager.setPairInactive(id);
                    return getOkResponse("");
                }

            } else if (!validStatus && validNewPos) {
                dbManager.movePair(id, newPosition);
                return getOkResponse("");

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
                    && extractedData.getPosition() <= dbManager.getLadderSize();

            List<Player> playerData = extractedData.getPlayers();

            if (playerData.size() != MAX_SIZE) {
                response.status(BAD_REQUEST);
                return getErrResponse("A Pair cannot have more than 2 players.");
            }

            List<Player> newPlayers = new ArrayList<>();

            for (int i = 0; i < MAX_SIZE; i++) {
                if (playerData.get(i).getExistingId() == null) {
                    newPlayers.add(new Player(playerData.get(i).getFirstName(), playerData.get(i).getLastName()));
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

            return getOkResponse("");
        });

        //remove player from ladder
        delete("/api/ladder/:id", (request, response) -> {
            int id;
            try {
                id = Integer.parseInt(request.queryParams(ID));
            } catch (Exception e) {
                response.status(BAD_REQUEST);
                return getErrResponse(ID_NOT_INT);
            }

            if (!dbManager.hasPairID(id)) {
                response.status(NOT_FOUND);
                return getErrResponse(PAIR_NOT_FOUND + id);
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
                response.status(BAD_REQUEST);
                return getErrResponse(ID_NOT_INT);
            }

            if (!dbManager.hasPairID(id)) {
                response.status(NOT_FOUND);
                return getErrResponse(PAIR_NOT_FOUND + id);
            }

            String penaltyType = request.queryParams(PENALTY);

            if (penaltyType.equals(LATE)) {
                dbManager.addPenaltyToPairToLatestGameSession(id, Penalty.LATE);
            } else if (penaltyType.equals(MISS)) {
                dbManager.addPenaltyToPairToLatestGameSession(id, Penalty.MISSING);
            } else if (penaltyType.equals(ACCIDENT)) {
                dbManager.addPenaltyToPairToLatestGameSession(id, Penalty.ACCIDENT);
            } else {
                response.status(BAD_REQUEST);
                return getErrResponse("Invalid Penalty Type");
            }
            return getOkResponse("");
        });

        //Show a list of matches
        get("/api/matches", (request, response) -> {
            String json = dbManager.getJSONScorecards();
            if (!json.isEmpty()) {
                response.status(OK);
                return json;
            } else {
                response.status(NOT_FOUND);
                return getErrResponse("No scorecards were found");
            }
        });

        //Input match results
        patch("/api/matches/:id", (request, response) -> {
            int id;
            try {
                id = Integer.parseInt(request.params(ID));
            } catch (Exception e) {
                response.status(BAD_REQUEST);
                return getErrResponse(ID_NOT_INT);
            }
            String body = request.body();
            JsonExtractedData extractedData = gson.fromJson(body, JsonExtractedData.class);

            try {
                dbManager.inputMatchResults(id, extractedData.results.clone());
                response.status(OK);
                return getOkResponse("");
            } catch (Exception e) {
                response.body("Invalid result format.");
                response.status(BAD_REQUEST);
                return getErrResponse("Invalid result format.");
            }
        });

        //Remove a pair from a match
        delete("/api/matches/:id", (request, response) -> {
            int id;
            try {
                id = Integer.parseInt(request.queryParams(ID));
            } catch (Exception e) {
                response.status(BAD_REQUEST);
                return getErrResponse(ID_NOT_INT);
            }

            if (!dbManager.hasPairID(id)) {
                response.status(NOT_FOUND);
                return getErrResponse(PAIR_NOT_FOUND);
            }

            if (!dbManager.isActivePair(id)) {
                response.status(BAD_REQUEST);
                return getErrResponse("The pair is not on the scorecard " + id);
            }

            dbManager.setPairInactive(id);

            response.status(OK);
            return getOkResponse("");
        });

        //logging in an existing users
        post("/api/login", (request, response) -> {
            String body = request.body();
            JsonExtractedData extractedData = gson.fromJson(body, JsonExtractedData.class);
            String email = extractedData.getEmail();
            String pwd = extractedData.getPassword();
            boolean isErrorResponse = false;
            String message = null;
            String sessionID = null;

            try {
                sessionID = accountManager.login(email, pwd);
                message = "sessionID: " + sessionID;
            } catch (InternalHashingException e) {
                message = e.getMessage();
                isErrorResponse = true;
            } catch (NoSuchUserException e) {
                message = e.getMessage();
                isErrorResponse = true;
            } catch (InvalidUserInputException e) {
                message = e.getMessage();
                isErrorResponse = true;
            } catch (InvalidCredentialsException e) {
                message = e.getMessage();
                isErrorResponse = true;
            }

            if (isErrorResponse) {
                return getErrResponse(message);
            }

            return getOkResponse(message);
        });

        //registers a new user
        post("/api/login/new", (request, response) -> {
            String body = request.body();
            JsonExtractedData extractedData = gson.fromJson(body, JsonExtractedData.class);
            String email = extractedData.getEmail();
            String pwd = extractedData.getPassword();
            boolean isErrorResponse = false;
            String message = null;

            try {
                accountManager.register(email, pwd);
            } catch (InternalHashingException e) {
                message = e.getMessage();
                isErrorResponse = true;
            } catch (AccountRegistrationException e) {
                message = e.getMessage();
                isErrorResponse = true;
            } catch (InvalidUserInputException e) {
                message = e.getMessage();
                isErrorResponse = true;
            }

            if (isErrorResponse) {
                return getErrResponse(message);
            }

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
