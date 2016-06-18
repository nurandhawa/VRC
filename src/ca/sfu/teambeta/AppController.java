package ca.sfu.teambeta;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;
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
            return "";
        });

        //add pair to ladder
        post("/api/index/add", (request, response) -> {
            //call addNewPair
            return "Adding player";
        });

        //remove player from ladder
        delete("/api/index/remove", (request, response) -> {
            request.queryParams("id");
            return "Remove player from ladder";
        });

        //update a pair's position in the ladder
        put("/api/index/position", (request, response) -> {
            return "Update Position";
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


/*
            response.body();               // get response content
            response.body("Hello");        // sets content to Hello
            response.header("FOO", "bar"); // sets header FOO with value bar
            response.raw();                // raw response handed in by Jetty
            response.redirect("/example"); // browser redirect to /example
            response.status();             // get the response status
            response.status(401);          // set status code to 401
            response.type();               // get the content type
            response.type("text/xml");     // set content type to text/xml

            request.attributes();             // the attributes list
            request.attribute("foo");         // value of foo attribute
            request.attribute("A", "V");      // sets value of attribute A to V
            request.body();                   // request body sent by the client
            request.bodyAsBytes();            // request body as bytes
            request.contentLength();          // length of request body
            request.contentType();            // content type of request.body
            request.contextPath();            // the context path, e.g. "/hello"
            request.cookies();                // request cookies sent by the client
            request.headers();                // the HTTP header list
            request.headers("BAR");           // value of BAR header
            request.host();                   // the host, e.g. "example.com"
            request.ip();                     // client IP address
            request.params("foo");            // value of foo path parameter
            request.params();                 // map with all parameters
            request.pathInfo();               // the path info
            request.port();                   // the server port
            request.protocol();               // the protocol, e.g. HTTP/1.1
            request.queryMap();               // the query map
            request.queryMap("foo");          // query map for a certain parameter
            request.queryParams();            // the query param list
            request.queryParams("FOO");       // value of FOO query param
            request.queryParamsValues("FOO")  // all values of FOO query param
            request.raw();                    // raw request handed in by Jetty
            request.requestMethod();          // The HTTP method (GET, ..etc)
            request.scheme();                 // "http"
            request.servletPath();            // the servlet path, e.g. /result.jsp
            request.session();                // session management
            request.splat();                  // splat (*) parameters
            request.uri();                    // the uri, e.g. "http://example.com/foo"
            request.url();                    // the url. e.g. "http://example.com/foo"
            request.userAgent();              // user agent




*/
