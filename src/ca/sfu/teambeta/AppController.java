package ca.sfu.teambeta;

import ca.sfu.teambeta.core.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import ca.sfu.teambeta.core.exceptions.AccountRegistrationException;
import ca.sfu.teambeta.core.exceptions.InternalHashingException;
import ca.sfu.teambeta.core.exceptions.InvalidCredentialsException;
import ca.sfu.teambeta.core.exceptions.InvalidInputException;
import ca.sfu.teambeta.core.exceptions.NoSuchSessionException;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;

import ca.sfu.teambeta.logic.AccountManager;
import ca.sfu.teambeta.logic.GameSession;
import ca.sfu.teambeta.logic.InputValidator;
import ca.sfu.teambeta.logic.UserSessionManager;
import ca.sfu.teambeta.persistence.DBManager;

import java.util.List;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.exception;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.patch;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.secure;
import static spark.Spark.staticFiles;

/**
 * Created by NoorUllah on 2016-06-16.
 */
public class AppController {
    public static final String DEVELOP_STATIC_HTML_PATH = ".";
    public static final String JAR_STATIC_HTML_PATH = "/web";
    public static final int DEVELOP_SERVER_PORT = 8000;
    public static final int JAR_SERVER_PORT = 443;
    private static final String ID = "id";
    private static final String STATUS = "newStatus";
    private static final String POSITION = "position";

    private static final String TIME_SLOT = "time";
    public static final String PLAYING_STATUS = "playing";
    public static final String NOT_PLAYING_STATUS = "not playing";

    private static final String GAMESESSION = "gameSession";
    private static final String GAMESESSION_PREVIOUS = "previous";
    private static final String GAMESESSION_LATEST = "latest";

    private static final String PENALTY = "penalty";
    private static final String LATE = "late";
    private static final String MISS = "miss";
    private static final String ACCIDENT = "accident";
    private static final String PAIR_NOT_FOUND = "No pair was found with given id";
    private static final String ID_NOT_INT = "Id is not of integer type";
    private static final int NOT_FOUND = 404;
    private static final int BAD_REQUEST = 400;
    private static final int NOT_AUTHENTICATED = 401;
    private static final int SERVER_ERROR = 500;
    private static final int OK = 200;
    private static final String KEYSTORE_LOCATION = "testkeystore.jks";
    private static final String KEYSTORE_PASSWORD = "password";
    private static final String SESSION_TOKEN_KEY = "sessionToken";
    private static Gson gson;

    public AppController(DBManager dbManager, int port, String staticFilePath) {
        final AccountManager accountManager = new AccountManager(dbManager);
        port(port);
        staticFiles.location(staticFilePath);

        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String keystorePath = this.getClass().getClassLoader()
                .getResource(KEYSTORE_LOCATION).toString();
        secure(keystorePath, KEYSTORE_PASSWORD, null, null);

        before("/api/*", (request, response) -> {
            // Allow access to the login endpoint, so they can sign up/log in
            String endpoint = request.splat()[0];
            if (!endpoint.contains("login")) {

                String sessionToken = request.cookie(SESSION_TOKEN_KEY);
                try {
                    boolean authenticated =
                            UserSessionManager.authenticateSession(sessionToken);
                    if (!authenticated) {
                        halt(NOT_AUTHENTICATED, getNotAuthenticatedResponse(
                                "You must be logged in view this page."));
                    }
                } catch (NoSuchSessionException exception) {
                    halt(NOT_AUTHENTICATED, getNotAuthenticatedResponse(
                            "You must be logged in view this page."));
                }

            }
        });

        before("/api/matches", (request, response) -> {
            String requestedGameSession = request.queryParams(GAMESESSION);
            if (requestedGameSession == null ||
                    getRequestedGameSession(dbManager, requestedGameSession) == null) {
                halt(BAD_REQUEST, getErrResponse("Must specify gameSession: latest or previous"));
            }
        });

        //homepage: return ladder
        get("/api/ladder", (request, response) -> {
            String json = dbManager.getJSONLadder(dbManager.getGameSessionLatest());
            if (!json.isEmpty()) {
                return json;
            } else {
                response.status(NOT_FOUND);
                return getErrResponse("No ladder was found");
            }
        });

        //logout
        post("/api/logout", (request, response) -> {
            String sessionToken = request.cookie(SESSION_TOKEN_KEY);
            accountManager.logout(sessionToken);
            return getOkResponse("Logged out.");
        });

        //updates a pair's playing status or position
        patch("/api/ladder/:id", (request, response) -> {
            int id;
            try {
                id = Integer.parseInt(request.params(ID));
            } catch (Exception e) {
                response.status(BAD_REQUEST);
                return getErrResponse(ID_NOT_INT);
            }

            int newPosition = -1;

            try {
                newPosition = Integer.parseInt(request.queryParams(POSITION)) - 1;
            } catch (Exception e) {
                throw e;
            }

            String status = request.queryParams(STATUS);
            if (status == null) {
                status = "";
            }

            GameSession gameSession = dbManager.getGameSessionLatest();

            boolean validNewPos = InputValidator.checkLadderPosition(newPosition,
                    dbManager.getLadderSize(gameSession));

            boolean validStatus = InputValidator.checkPlayingStatus(status);

            if (!InputValidator.checkPairExists(dbManager, id)) {
                response.status(NOT_FOUND);
                return getErrResponse(PAIR_NOT_FOUND + id);
            }

            if (!validStatus && !validNewPos) {
                response.status(BAD_REQUEST);
                return getErrResponse("Specify what to update: position or status");
            } else if (validStatus && !validNewPos) {
                if (status.equals(PLAYING_STATUS)) {
                    boolean statusChanged = dbManager.setPairActive(gameSession, id);
                    if (statusChanged) {
                        return getOkResponse("");
                    } else {
                        Player activePlayer = dbManager.getAlreadyActivePlayer(gameSession, id);
                        String firstName = activePlayer.getFirstName();
                        String lastName = activePlayer.getLastName();
                        response.status(NOT_FOUND);
                        return getErrResponse(
                                "Player " + firstName + " "
                                        + lastName + " is already playing");
                    }
                } else if (status.equals(NOT_PLAYING_STATUS)) {
                    dbManager.setPairInactive(gameSession, id);
                    return getOkResponse("");
                }

            } else if (!validStatus && validNewPos) {
                dbManager.movePair(gameSession, id, newPosition);
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

            GameSession gameSession = dbManager.getGameSessionLatest();

            boolean validPos = InputValidator.checkLadderPosition(
                    extractedData.getPosition(), dbManager.getLadderSize(gameSession));

            List<Player> newPlayers = extractedData.getPlayers();

            try {
                InputValidator.validateNewPlayers(newPlayers, MAX_SIZE);
            } catch (InvalidInputException exception) {
                response.status(BAD_REQUEST);
                return getErrResponse(exception.getMessage());
            }

            for (int i = 0; i < MAX_SIZE; i++) {
                Integer existingId = newPlayers.get(i).getExistingId();
                if (existingId != null && existingId >= 0) {
                    newPlayers.remove(i);
                    newPlayers.add(i, dbManager.getPlayerFromID(existingId));
                }
            }

            Pair pair = new Pair(newPlayers.get(0), newPlayers.get(1));
            Time time = convertStrTime(request.queryParams(TIME_SLOT));

            if (validPos) {
                dbManager.addPair(gameSession, pair, extractedData.getPosition() - 1, time);
                response.status(OK);
            } else {
                dbManager.addPair(gameSession, pair, time);
                response.status(OK);
            }

            return getOkResponse("");
        });

        //remove player from ladder
        delete("/api/ladder/:id", (request, response) -> {
            int id;
            try {
                id = Integer.parseInt(request.params(ID));
            } catch (Exception e) {
                response.status(BAD_REQUEST);
                return getErrResponse(ID_NOT_INT);
            }

            if (!InputValidator.checkPairExists(dbManager, id)) {
                response.status(NOT_FOUND);
                return getErrResponse(PAIR_NOT_FOUND + id);
            }

            dbManager.removePair(id);
            response.status(OK);

            return getOkResponse("");
        });

        post("/api/matches", ((request, response) -> {
            GameSession gameSession = getRequestedGameSession(dbManager,
                    request.queryParams(GAMESESSION));

            dbManager.reorderLadder(gameSession);

            return getOkResponse("");
        }));

        //add a penalty to a pair
        post("/api/matches/:id", (request, response) -> {
            int id;
            try {
                id = Integer.parseInt(request.params(ID));
            } catch (Exception e) {
                response.status(BAD_REQUEST);
                return getErrResponse(ID_NOT_INT);
            }

            if (!InputValidator.checkPairExists(dbManager, id)) {
                response.status(NOT_FOUND);
                return getErrResponse(PAIR_NOT_FOUND + id);
            }

            String penaltyType = request.queryParams(PENALTY);

            GameSession gameSession = getRequestedGameSession(dbManager,
                    request.queryParams(GAMESESSION));

            if (penaltyType.equals(LATE)) {
                dbManager.addPenaltyToPair(gameSession, id, Penalty.LATE);
            } else if (penaltyType.equals(MISS)) {
                dbManager.addPenaltyToPair(gameSession, id, Penalty.MISSING);
            } else if (penaltyType.equals(ACCIDENT)) {
                dbManager.addPenaltyToPair(gameSession, id, Penalty.ACCIDENT);
            } else {
                response.status(BAD_REQUEST);
                return getErrResponse("Invalid Penalty Type");
            }
            return getOkResponse("");
        });

        //Show a list of matches
        get("/api/matches", (request, response) -> {
            GameSession gameSession = getRequestedGameSession(dbManager,
                    request.queryParams(GAMESESSION));

            String json = dbManager.getJSONScorecards(gameSession);
            final String EMPTY_JSON_ARRAY = "[]";
            if (!json.equals(EMPTY_JSON_ARRAY)) {
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

            GameSession gameSession = getRequestedGameSession(dbManager,
                    request.queryParams(GAMESESSION));

            Scorecard scorecard = dbManager.getScorecardFromGame(gameSession, id);

            try {
                InputValidator.validateResults(scorecard, extractedData.results);
                dbManager.inputMatchResults(gameSession, scorecard, extractedData.results.clone());
            } catch (InvalidInputException exception) {
                response.status(BAD_REQUEST);
                return getErrResponse(exception.getMessage());
            }

            response.status(OK);
            return getOkResponse("");
        });

        //Remove a pair from a match
        delete("/api/matches/:id", (request, response) -> {
            int id;
            try {
                id = Integer.parseInt(request.params(ID));
            } catch (Exception e) {
                response.status(BAD_REQUEST);
                return getErrResponse(ID_NOT_INT);
            }

            if (!InputValidator.checkPairExists(dbManager, id)) {
                response.status(NOT_FOUND);
                return getErrResponse(PAIR_NOT_FOUND);
            }

            GameSession gameSession = getRequestedGameSession(dbManager,
                    request.queryParams(GAMESESSION));

            if (!InputValidator.checkPairActive(dbManager, gameSession, id)) {
                response.status(BAD_REQUEST);
                return getErrResponse("The pair is not on the scorecard " + id);
            }

            dbManager.setPairInactive(gameSession, id);

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
            String sessionToken = "";
            try {
                sessionToken = accountManager.login(email, pwd);
                successResponse.addProperty(SESSION_TOKEN_KEY, sessionToken);
                return gson.toJson(successResponse);
            } catch (InternalHashingException | NoSuchUserException | InvalidCredentialsException e) {
                response.status(NOT_AUTHENTICATED);
                return "";
            }
        });

        //registers a new user
        post("/api/login/new", (request, response) -> {
            String body = request.body();
            JsonExtractedData extractedData = gson.fromJson(body, JsonExtractedData.class);
            String email = extractedData.getEmail();
            String pwd = extractedData.getPassword();

            String message = "";
            try {
                accountManager.register(email, pwd);
                return getOkResponse("Account registered");
            } catch (InternalHashingException e) {
                message = e.getMessage();
            } catch (AccountRegistrationException e) {
                message = e.getMessage();
            } catch (InvalidInputException e) {
                message = e.getMessage();
            }

            response.status(400);
            return getErrResponse(message);
        });

        exception(Exception.class, (exception, request, response) -> {
            exception.printStackTrace();
            response.status(SERVER_ERROR);
            response.body(getErrResponse(exception.getMessage()));
        });
    }

    private Time convertStrTime(String timeStr) {
        Time time = Time.NO_SLOT;

        //Convert string to enum type
        for (Time timeSlot : Time.values()) {
            if (timeSlot.getTime() == timeStr) {
                time = timeSlot;
                break;
            }
        }
        return time;
    }

    private GameSession getRequestedGameSession(DBManager dbManager, String requestedGameSession) {
        if (requestedGameSession.equals(GAMESESSION_LATEST)) {
            return dbManager.getGameSessionLatest();
        } else if (requestedGameSession.equals(GAMESESSION_PREVIOUS)) {
            return dbManager.getGameSessionPrevious();
        } else {
            return null;
        }
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
