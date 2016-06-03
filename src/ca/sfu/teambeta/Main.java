package ca.sfu.teambeta;

import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;
import ca.sfu.teambeta.ui.UserInterface;

class Main {
    public static void main(String args[]) {

        LadderManager ladderManager = new LadderManager();

        ladderManager.getLadder().forEach(ladderManager::setIsPlaying);

        GameManager gameManager = new GameManager(ladderManager.getActivePairs());
        UserInterface.start(gameManager, ladderManager);

    }
}
