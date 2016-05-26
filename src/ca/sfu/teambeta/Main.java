package ca.sfu.teambeta;

import ca.sfu.teambeta.logic.GameManager;

class Main {
    public static void main(String args[]) {

        System.out.println("Hello World!");

        GameManager newGame = new GameManager();
        newGame.createMatchCards();
        newGame.inputMatchResults();
    }
}
