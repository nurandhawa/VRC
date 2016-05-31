package ca.sfu.teambeta;

import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;
import ca.sfu.teambeta.ui.UserInterface;

class Main {
    public static void main(String args[]) {

        GameManager gameManager = new GameManager();
        LadderManager ladderManager = new LadderManager();
        UserInterface.start(gameManager, ladderManager);

    }
}
