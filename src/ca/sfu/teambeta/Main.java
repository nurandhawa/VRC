package ca.sfu.teambeta;

import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.ui.UserInterface;

class Main {
    public static void main(String args[]) {

        GameManager newGame = new GameManager();

        UserInterface.start();

    }
}
