package ca.sfu.teambeta;

import com.google.gson.Gson;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {
    private static Gson gson = new Gson();
    public static void main(String[] args) {
        port(8000);
        get("/", (request, response) -> {
            response.type("application/json");
            return gson.toJson(new Pair(new Player(1, "Bob"), new Player(2, "Bobby")));
        });
    }
}