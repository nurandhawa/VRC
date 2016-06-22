package ca.sfu.teambeta;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static spark.Spark.*;

/**
 * Created by NoorUllah on 2016-06-16.
 */
public class AppController {
    private static final String ID = "id";
    private static final String STATUS = "newStatus";
    private static final String POSITION = "position";
    private static final String NEW_POSITION = "newPosition";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    private static final int NOT_FOUND = 404;
    private static final int BAD_REQUEST = 400;
    private static final int OK = 200;

    private static Gson gson;

    public AppController(LadderManager ladderManager, GameManager gameManager) {
        port(8000);
        staticFiles.location(".");
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

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

        //updates a pair's playing status
        patch("/api/ladder/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(ID));
            String status = request.queryParams(STATUS);
            System.out.println("Status: " + status);
            int newPosition = Integer.parseInt(request.queryParams(POSITION));
            System.out.println("Position: " + newPosition);

            boolean validNewPos = 0 < newPosition && newPosition <= ladderManager.ladderSize();
            boolean validStatus = status.equals("playing") || status.equals("not playing");
            System.out.println(validStatus + "  +  "+ validNewPos);

            Pair pair = ladderManager.searchPairById(id);
            System.out.println(pair);

            if (pair == null) { //Wrong ID
                response.status(NOT_FOUND);
                return response;
            }

            if(!validStatus && !validNewPos) {
                response.body("Specify what to update: position or status");
                response.status(BAD_REQUEST);
            } else if (validStatus && !validNewPos) {
                System.out.println("hello");
                if (status.equals("playing")) {
                    ladderManager.setIsPlaying(pair);
                    response.status(OK);
                } else if (status.equals("not playing")) {
                    ladderManager.setNotPlaying(pair);
                    response.status(OK);
                } else {
                    response.status(BAD_REQUEST);
                }
            } else if (!validStatus && validNewPos) {
                int currentPosition = pair.getPosition();
                System.out.println("current: " + currentPosition);
                ladderManager.movePair(currentPosition,newPosition);
                System.out.println(pair.getPosition());
                response.status(OK);

            } else {
                response.body("Cannot change both: position and status");
                response.status(BAD_REQUEST);
            }

            return response;
        });

        //add pair to ladder
        //in case of adding a pair at the end of ladder, position is length of ladder
        post("/api/ladder", (request, response) -> {
            //int id = Integer.parseInt(request.queryParams(ID));
            response.status(OK);
            //ArrayList players = gson.fromJson(request.body(), ArrayList.class);
            //System.out.println(players);
            /*
            String firstName = request.params(FIRST_NAME);
            String lastName = request.params(LAST_NAME);
            String email = request.params(EMAIL);
            String pwd = request.params(PASSWORD);
            String position = request.params(POSITION);

            if(position == null) {

            } else {
                int index = Integer.parseInt(position) - 1;
            }

            int id = Integer.parseInt(request.queryParams(ID));
            Pair pair = ladderManager.searchPairById(id);

            if (pair == null) { //Wrong ID
                response.status(BAD_REQUEST);
                return response;
            }
            ladderManager.addNewPairAtIndex(pair, index);
            response.status(OK);*/
            return response;
        });

        //remove player from ladder
        delete("/api/ladder/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(ID));
            Pair pair = ladderManager.searchPairById(id);
            System.out.println(pair);
            int index = pair.getPosition() - 1;
            boolean removed = ladderManager.removePairAtIndex(index);

            if (removed) {
                response.status(OK);
            } else {
                //Index out of bound
                response.status(BAD_REQUEST);
            }

            return response;
        });

        //update a pair's position in the ladder
        patch("/api/:id/:position/:newPosition", (request, response) -> {
            String oldPositionStr = request.params(":" + POSITION);
            String newPositionStr = request.params(":" + NEW_POSITION);
            int id = Integer.parseInt(request.params(":id"));
            int oldPosition = Integer.parseInt(oldPositionStr);
            int newPosition = Integer.parseInt(newPositionStr);
            boolean validOldPos = 0 < oldPosition && oldPosition < ladderManager.ladderSize();
            boolean validNewPos = 0 < newPosition && oldPosition < ladderManager.ladderSize();

            if (ladderManager.searchPairById(id).getPosition() != oldPosition) {
                response.status(BAD_REQUEST);
                return response;
            }

            if (validOldPos && validNewPos) {
                ladderManager.movePair(oldPosition, newPosition);
                response.status(OK);
            } else {
                response.status(BAD_REQUEST);
            }

            return response;
        });

        //add a penalty to a pair
        post("/api/index/penalty", (request, response) -> {
            int id = Integer.parseInt(request.queryParams(ID));
            Pair pair = ladderManager.searchPairById(id);
            int pairIndex = pair.getPosition() - 1;
            String penaltyType = request.queryParams("penType");
            response.status(OK);

            if (penaltyType == "late") {
                //call late penalty function in ladderManager
            } else if (penaltyType == "miss") {
                //call miss penalty function in ladderManager
            } else if (penaltyType == "absent") {
                //call absent penalty function in ladderManager
            } else {
                response.status(BAD_REQUEST);
            }
            return response;
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
        post("/api/matches/input", (request, response) -> {
            //need to figure out how results would be passed
            return "Input Results";
        });

        //Remove a pair from a match
        delete("/api/matches/remove", (request, response) -> {

            request.queryParams("matchIndex");
            int id = Integer.parseInt(request.queryParams(ID));
            Pair pair = ladderManager.searchPairById(id);

            if (pair == null || !pair.isPlaying()) { //Wrong ID
                response.status(BAD_REQUEST);
                return response;
            }
            gameManager.removePlayingPair(pair);
            response.status(OK);
            return response;
        });

    }
}
