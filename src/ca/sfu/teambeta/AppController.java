package ca.sfu.teambeta;

import ca.sfu.teambeta.core.JsonExtractedData;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

        //updates a pair's playing status or position
        patch("/api/ladder/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(ID));
            String status = request.queryParams(STATUS);
            int newPosition = Integer.parseInt(request.queryParams(POSITION));

            boolean validNewPos = 0 < newPosition && newPosition <= ladderManager.ladderSize();
            boolean validStatus = status.equals("playing") || status.equals("not playing");

            Pair pair = ladderManager.searchPairById(id);

            if (pair == null) {
                response.status(NOT_FOUND);
                return response;
            }

            if(!validStatus && !validNewPos) {
                response.body("Specify what to update: position or status");
                response.status(BAD_REQUEST);
            } else if (validStatus && !validNewPos) {
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
                ladderManager.movePair(currentPosition,newPosition);
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
            String body = request.body();
            JsonExtractedData extractedData = gson.fromJson(body, JsonExtractedData.class);

            boolean validPos = 0 < extractedData.getPosition()
                                && extractedData.getPosition() <= ladderManager.ladderSize();
            List<Player> playerData = extractedData.getPlayers();

            if(playerData.size() != 2) {
                response.status(BAD_REQUEST);
                response.body("A Pair cannot have more than 2 players.");
                return response;
            }
            Player p1 = new Player(playerData.get(0).getFirstName(),playerData.get(0).getLastName(),"");
            Player p2 = new Player(playerData.get(1).getFirstName(),playerData.get(1).getLastName(),"");
            Pair pair = new Pair(p1, p2);

            if (validPos) {
                ladderManager.addNewPairAtIndex(pair,extractedData.getPosition()-1);
                response.status(OK);
            } else {
                ladderManager.addNewPair(pair);
                response.status(OK);
            }

            return response;
        });

        //remove player from ladder
        delete("/api/ladder/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(ID));
            Pair pair = ladderManager.searchPairById(id);
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

        //add a penalty to a pair
        post("/api/matches/:id", (request, response) -> {
            int id = Integer.parseInt(request.queryParams(ID));
            Pair pair = ladderManager.searchPairById(id);

            String penaltyType = request.queryParams(PENALTY);

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
        patch("/api/matches/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(ID));
            Scorecard<Pair> group = gameManager.getGroupByIndex(id);
            int numTeams = group.getTeamRankings().size();
            String[][] input = new String[numTeams][numTeams];

            String body = request.body();
            JsonExtractedData extractedData = gson.fromJson(body, JsonExtractedData.class);

            int rows = extractedData.results.length;
            int cols = extractedData.results[0].length;
            boolean isValidResult = (rows == numTeams) && (cols == numTeams);

            if (!isValidResult) {
                response.status(BAD_REQUEST);
                response.body("Invalid result format.");
                return response;
            }

            input = extractedData.results.clone();
            gameManager.inputMatchResults(group,input);
            response.status(OK);
            return response;
        });

        //Remove a pair from a match
        delete("/api/matches/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(ID));
            Pair pair = ladderManager.searchPairById(id);

            if (pair == null || !pair.isPlaying()) {
                response.status(BAD_REQUEST);
                return response;
            }
            gameManager.removePlayingPair(pair);
            response.status(OK);
            return response;
        });

    }
}
