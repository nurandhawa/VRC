package ca.sfu.teambeta;

import ca.sfu.teambeta.core.*;
import ca.sfu.teambeta.core.exceptions.*;
import ca.sfu.teambeta.logic.AccountManager;
import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;
import ca.sfu.teambeta.logic.UserSessionManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.List;

import static spark.Spark.*;

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

    private static final String KEYSTORE_LOCATION = "testkeystore.jks";
    private static final String KEYSTORE_PASSWORD = "password";

    private static Gson gson;
    private final String SESSION_TOKEN_KEY = "sessionToken";

    public AppController(LadderManager ladderManager, GameManager gameManager) {
        port(8000);
        staticFiles.location(".");

        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        secure(KEYSTORE_LOCATION, KEYSTORE_PASSWORD, null, null);

        before("/api/*", (request, response) -> {
            // Allow access to the login endpoint, so they can sign up/log in
            String endpoint = request.splat()[0];
            if (!endpoint.contains("login")) {

                String sessionToken = request.cookie(SESSION_TOKEN_KEY);
                boolean authenticated = UserSessionManager.authenticateSession(sessionToken);
                if (!authenticated) {
                    halt(getNotAuthenticatedResponse("You must be logged in view this page."));
                }

            }
        });

        //homepage: return ladder
        get("/api/ladder", (request, response) -> {
            if (ladderManager.getLadder() != null) {
                response.status(OK);
            } else {
                response.status(BAD_REQUEST);
                return response;
            }
            return gson.toJson(ladderManager.getLadder());
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

            return getOkResponse("");
        });

        //remove player from ladder
        delete("/api/ladder/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(ID));
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

        //logging in an existing users
        post("/api/login", (request, response) -> {
            String body = request.body();
            JsonExtractedData extractedData = gson.fromJson(body, JsonExtractedData.class);
            String email = extractedData.getEmail();
            String pwd = extractedData.getPassword();

            JsonObject successResponse = new JsonObject();
            String errMessage = "";
            String sessionToken = "";
            try {
                sessionToken = AccountManager.login(email, pwd);
                successResponse.addProperty(SESSION_TOKEN_KEY, sessionToken);
                return gson.toJson(successResponse);
            } catch (InternalHashingException e) {
                errMessage = e.getMessage();
            } catch (NoSuchUserException e) {
                errMessage = e.getMessage();
            } catch (InvalidUserInputException e) {
                errMessage = e.getMessage();
            } catch (InvalidCredentialsException e) {
                errMessage = e.getMessage();
            }

            return getErrResponse(errMessage);
        });

        //registers a new user
        post("/api/login/new", (request, response) -> {
            String body = request.body();
            JsonExtractedData extractedData = gson.fromJson(body, JsonExtractedData.class);
            String email = extractedData.getEmail();
            String pwd = extractedData.getPassword();

            String message = "";
            try {
                AccountManager.register(email, pwd);
                return getOkResponse("Account registered");
            } catch (InternalHashingException e) {
                message = e.getMessage();
            } catch (AccountRegistrationException e) {
                message = e.getMessage();
            } catch (InvalidUserInputException e) {
                message = e.getMessage();
            }

            return getErrResponse(message);
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

    private String getNotAuthenticatedResponse(String message) {
        JsonObject authResponse = new JsonObject();
        authResponse.addProperty("status", "AUTH_ERROR");
        authResponse.addProperty("message", message);
        return gson.toJson(authResponse);
    }
}
