package ca.sfu.teambeta;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;
import com.google.gson.JsonElement;
import org.omg.CosNaming.NamingContextPackage.NotFound;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import java.util.List;
import java.util.Set;

import static spark.Spark.*;

/**
 * Created by NoorUllah on 2016-06-16.
 */
public class AppController {
    private static final String ID = "id";
    private static final String STATUS = "playingStatus";
    private static final String POSITION = "Position";
    private static final String NEW_POSITION = "newPosition";
    private static final String PLAYERS = "Players";
    private static final String IS_PLAYING = "IsPlaying";

    private static final int NOT_FOUND = 404;
    private static final int BAD_REQUEST = 400;
    private static final int OK = 200;

    public AppController(LadderManager ladderManager, GameManager gameManager){
        port(8000);
        staticFiles.location(".");

        //EVERYTHING HAS TO BE CONVERTED INTO JSON
        //homepage: return ladder
        get("/api/index", (request, response) -> {
            return toJSON(ladderManager.getLadder());
        });

        //updates a pair's playing status
        put("/api/index", (request, response) -> {
            String id = request.queryParams(ID);
            String status = request.queryParams(STATUS);
            Pair pair = ladderManager.searchPairById(id);
            if (pair == null){ //Wrong ID
                response.status(BAD_REQUEST);
                return response;
            }

            if(status == "playing"){
                ladderManager.setNotPlaying(pair);
                response.status(OK);
            } else if (status == "not playing") {
                boolean changed = ladderManager.setIsPlaying(pair);
                if(changed){
                    response.status(OK);
                } else {
                    //One of the players is already in the game
                    response.status(NOT_FOUND);
                }
            } else {
                response.status(BAD_REQUEST);
            }

            return response;
        });

        //add pair to ladder
        post("/api/index/add", (request, response) -> {
            //call addNewPair
            return "Adding player";
        });

        //remove player from ladder
        delete("/api/index/remove", (request, response) -> {
            String position = request.queryParams(POSITION);
            int index = Integer.parseInt(position) - 1;
            boolean removed = ladderManager.removePairAtIndex(index);

            if (removed){
                response.status(OK);
            } else {
                //Index out of bound
                response.status(BAD_REQUEST);
            }

            return response;
        });

        //update a pair's position in the ladder
        put("/api/index/position", (request, response) -> {
            String oldPositionStr = request.queryParams(POSITION);
            String newPositionStr = request.queryParams(NEW_POSITION);
            int oldPosition = Integer.parseInt(oldPositionStr);
            int newPosition = Integer.parseInt(newPositionStr);
            boolean validOldPos = 0 < oldPosition && oldPosition < ladderManager.ladderSize();
            boolean validNewPos = 0 < newPosition && oldPosition < ladderManager.ladderSize();

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
            request.queryParams("id");
            request.queryParams("penIndex");
            return "Add penalty";
        });

        //Show a list of matches
        get("/api/matches", (request, response) -> {
            return gameManager.getScorecards();
        });

        //Input match results
        post("/api/matches/input", (request, response) -> {
            //call input results passing array
            return "Input Results";
        });

        //Remove a pair from a match
        delete("/api/matches/remove", (request, response) -> {

            request.queryParams("matchIndex");
            request.queryParams("id");
            return "Hello, We are the matches";
        });

    }


    private JsonArray toJSON(List<Pair> ladder){
        JsonArrayBuilder builder = Json.createArrayBuilder();

        for(Pair current : ladder) {
            int position = current.getPosition();
            List<Player> team = current.getPlayers();
            String player_1 = team.get(0).getName();
            String player_2 = team.get(1).getName();
            Boolean isPlaying = current.isPlaying();

            JsonObject jsonOfPair = Json.createObjectBuilder()
                    .add(POSITION, position)
                    .add(PLAYERS, Json.createArrayBuilder()
                            .add(player_1)
                            .add(player_2)
                    )
                    .add(IS_PLAYING, isPlaying)
                    .build();
            builder.add(jsonOfPair);
        }
        JsonArray result = builder.build();

        return result;
    }
}
