package ca.sfu.teambeta;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.logic.DBManager;
import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.staticFiles;

public class Main {
    public static void main(String[] args) {

        Ladder loadedLadder = DBManager.loadFromDB();
        LadderManager ladderManager = new LadderManager(loadedLadder.getLadder());

        ladderManager.getLadder().forEach(ladderManager::setIsPlaying);

        GameManager gameManager = new GameManager(ladderManager.getActivePairs(), ladderManager);

        AppController appController = new AppController(ladderManager,gameManager);

    }
}