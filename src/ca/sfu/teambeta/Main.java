package ca.sfu.teambeta;

import java.util.Scanner;

import ca.sfu.teambeta.logic.GameManager;

class Main {
    private static final int LIST_LADDER = 1;
    private static final int LIST_MATCHES = 2;
    private static final int EDIT_LADDER = 3;
    private static final int EDIT_MATCHES = 4;
    private static final int EDIT_LADDER_ADD = 5;
    private static final int EDIT_LADDER_REMOVE = 6;
    private static final int EDIT_MATCHES_REMOVE = 7;
    private static final int FINISH = 8;
    private static boolean isRunning = true;

    public static void main(String args[]) {

        GameManager newGame = new GameManager();
        newGame.createMatchCards();
        newGame.inputMatchResults();

        Scanner scanner = new Scanner(System.in);


        while (isRunning) {
            System.out.println("Welcome. Enter an option:");
            System.out.println(LIST_LADDER + ". List Ladder");
            System.out.println(LIST_MATCHES + ". List Matches");
            System.out.println(EDIT_LADDER + ". Edit Ladder");
            System.out.println(EDIT_MATCHES + ". Edit Matches");
            String input = scanner.nextLine();

            try {
                int selection = Integer.parseInt(input);
                switch (selection) {
                    case LIST_LADDER:
                        System.out.println("Ladder: ");
                        break;
                    case LIST_MATCHES:
                        System.out.println("Matches: ");
                        break;
                    case EDIT_LADDER:
                        System.out.println("Choose an action:");
                        System.out.println(EDIT_LADDER_ADD + ". Add pair to ladder");
                        System.out.println(EDIT_LADDER_REMOVE + ". Remove pair from ladder");

                        input = scanner.nextLine();
                        selection = Integer.parseInt(input);
                        if (selection == EDIT_LADDER_ADD) {
                            System.out.println("Add to ladder");
                        } else if (selection == EDIT_LADDER_REMOVE) {
                            System.out.println("Remove from ladder");
                        }
                        break;
                    case EDIT_MATCHES:
                        input = scanner.nextLine();
                        selection = Integer.parseInt(input);
                        if (selection == EDIT_MATCHES_REMOVE) {
                            System.out.println("Remove from match");
                        }
                        break;
                    case FINISH:
                        isRunning = false;
                        break;
                    default:
                        break;
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
    }
}
