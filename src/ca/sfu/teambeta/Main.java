package ca.sfu.teambeta;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

public class Main {
    public static void main(String[] args) {
        port(8000);
        staticFiles.location(".");

        get("/ladder", (request, response) -> {
            return "Hello, I am Ladder";
        });

        get("/matches", (request, response) -> {
            return "Hello, We are the matches";
        });
        
    }
}