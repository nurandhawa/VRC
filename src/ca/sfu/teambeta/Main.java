package ca.sfu.teambeta;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.logic.DBManager;
import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;
import ca.sfu.teambeta.logic.DBManager;
import ca.sfu.teambeta.ui.UserInterface;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        /*Ladder loadedLadder = DBManager.loadFromDB();*/

        //List<Pair> pairs = new ArrayList<>();

        /* -----FOR TESTING
        pairs.add(new Pair(new Player(3, "P3", "Test", ""), new Player(4, "P4", "Test", ""), true));
        pairs.add(new Pair(new Player(7, "P7", "Test", ""), new Player(8, "P8", "Test", ""), true));
        pairs.add(new Pair(new Player(11, "P11", "Test", ""), new Player(12, "P12", "Test", ""), true));*/

        /*Ladder loadedLadder = new Ladder(pairs);

        LadderManager ladderManager = new LadderManager(loadedLadder.getPairs());

        ladderManager.getLadder().forEach(ladderManager::setIsPlaying);

        GameManager gameManager = new GameManager(ladderManager.getActivePairs(), ladderManager);

        AppController appController = new AppController(ladderManager,gameManager);*/


    }
}