package ca.sfu.teambeta;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;
import ca.sfu.teambeta.ui.UserInterface;

import java.util.ArrayList;
import java.util.List;

class Main {
    public static void main(String[] args) {

        /*Laddgiter loadedLadder = DBManager.loadFromDB();*/

        /* -----FOR TESTING*/
        List<Pair> pairs = new ArrayList<>();

        pairs.add(new Pair(new Player("P1", "Test", ""), new Player("P2", "Test", ""), true));
        pairs.add(new Pair(new Player("P3", "Test", ""), new Player("P4", "Test", ""), true));
        pairs.add(new Pair(new Player("P5", "Test", ""), new Player("P6", "Test", ""), true));
        pairs.add(new Pair(new Player("P7", "Test", ""), new Player("P8", "Test", ""), true));
        pairs.add(new Pair(new Player("P9", "Test", ""), new Player("P10", "Test", ""), true));
        pairs.add(new Pair(new Player("P11", "Test", ""), new Player("P12", "Test", ""), true));

        Ladder loadedLadder = new Ladder(pairs);

        LadderManager ladderManager = new LadderManager(loadedLadder.getPairs());

        ladderManager.getLadder().forEach(ladderManager::setIsPlaying);

        GameManager gameManager = new GameManager(ladderManager.getActivePairs(), ladderManager);
        //UserInterface.start(gameManager,ladderManager);
        AppController appController = new AppController(ladderManager,gameManager);


    }
}
