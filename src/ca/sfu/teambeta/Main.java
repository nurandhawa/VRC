package ca.sfu.teambeta;

import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

public class Main {
    public static void main(String[] args) {

        LadderManager ladderManager;
        GameManager gameManager;

        AppController appController = new AppController(ladderManager,gameManager);

    }
}