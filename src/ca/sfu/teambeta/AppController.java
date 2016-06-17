package ca.sfu.teambeta;

import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;

import static spark.Spark.*;

/**
 * Created by NoorUllah on 2016-06-16.
 */
public class AppController {

    public AppController(LadderManager ladderManager, GameManager gameManager){
        port(8000);
        staticFiles.location(".");

        //EVERYTHING HAS TO BE CONVERTED INTO JSON
        //homepage: return ladder
        get("/index", (request, response) -> {
            return ladderManager.getLadder();
        });

        //updates a pair's playing status
        put("/index", (request, response) -> {
            //id to identify a pair
            request.queryParams("id");
            if(request.queryParams("playingStatus") == "playing"){
                //call setIsPlaying
            }
            if(request.queryParams("playingStatus") == "not playing"){
                //call setIsNotPlaying
            }

            return "Changing Playing Status";
        });

        //add pair to ladder
        post("/index/add", (request, response) -> {
            //call addNewPair
            return "Adding player";
        });

        //remove player from ladder
        delete("/index/remove", (request, response) -> {
            request.queryParams("id");
            return "Remove player from ladder";
        });

        //update a pair's position in the ladder
        put("/index/position", (request, response) -> {
            return "Update Position";
        });

        //add a penalty to a pair
        post("/index/penalty", (request, response) -> {
            request.queryParams("id");
            request.queryParams("penIndex");
            return "Add penalty";
        });

        //Show a list of matches
        get("/matches", (request, response) -> {
            return gameManager.getScorecards();
        });

        //Input match results
        post("/matches/input", (request, response) -> {
            //call input results passing array
            return "Input Results";
        });

        //Remove a pair from a match
        delete("/matches/remove", (request, response) -> {
            request.queryParams("matchIndex");
            request.queryParams("id");
            return "Hello, We are the matches";
        });

    }
}
