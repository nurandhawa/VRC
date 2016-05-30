package ca.sfu.teambeta.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ca.sfu.teambeta.core.Player;

/**
 * Simple text-based UI to perform basic display and modification functions.
 */
public class UserInterface {
    private static final int LIST_LADDER = 1;
    private static final int LIST_MATCHES = 2;
    private static final int EDIT_LADDER = 3;
    private static final int EDIT_MATCHES = 4;
    private static final int EDIT_LADDER_ADD = 5;
    private static final int EDIT_LADDER_REMOVE = 6;
    private static final int FINISH = 8;
    private static boolean isRunning = true;
    private static Scanner scanner = new Scanner(System.in);

    public static void start() {
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
                        listLadder();
                        break;
                    case LIST_MATCHES:
                        listMatches();
                        break;
                    case EDIT_LADDER:
                        editLadder();
                        break;
                    case EDIT_MATCHES:
                        editMatches();
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
        scanner.close();
    }

    private static void editMatches() {
        System.out.println("Current matches: ");
        listMatches();
        System.out.println("Enter the match number you want to edit: ");
        String input;
        input = scanner.nextLine();
        int matchSelection = Integer.parseInt(input);
        // TODO: List matches = GameManager.getMatches();
        // TODO: Print selected match
        System.out.println("Enter the pair number to remove: ");
        input = scanner.nextLine();
        int pairSelection = Integer.parseInt(input);
        // TODO: GameManager.editMatch(GameManager.REMOVE_PAIR, matchSelection or matches.get(matchselection), pairSelection);
    }

    private static void editLadder() {
        String input;
        int selection;
        System.out.println("Choose an action:");
        System.out.println(EDIT_LADDER_ADD + ". Add pair to ladder");
        System.out.println(EDIT_LADDER_REMOVE + ". Remove pair from ladder");

        input = scanner.nextLine();
        selection = Integer.parseInt(input);
        if (selection == EDIT_LADDER_ADD) {
            addPair();
        } else if (selection == EDIT_LADDER_REMOVE) {
            removePair();
        }
    }

    private static void listMatches() {
        System.out.println("Matches: ");
    }

    private static void listLadder() {
        System.out.println("Ladder: ");
    }

    private static void addPair() {
        final int EXISTING_PLAYER = 1;
        final int NEW_PLAYER = 2;

        List pair = new ArrayList<Player>(2);
        while (pair.size() < 2) {
            System.out.println("Enter " + EXISTING_PLAYER + " for existing player, " + NEW_PLAYER +
                    " for new player.");
            String input = scanner.nextLine();
            int selection = Integer.parseInt(input);
            if (selection == EXISTING_PLAYER) {
                // TODO: print ladder
                System.out.println("Enter number of player to use: ");
                input = scanner.nextLine();
                selection = Integer.parseInt(input);
                // TODO: pair.add(ladder[selection])
                pair.add(new Player());
            } else if (selection == NEW_PLAYER) {
                System.out.println("Enter name of new player: ");
                input = scanner.nextLine();
                // TODO: get length of player list from DB to pick new ID
                // or insert player name into DB and fetch back to get ID

                // TODO: Create player object and pair.add()
                pair.add(new Player());
            }
        }
    }

    private static void removePair() {
        System.out.println("Remove from ladder");
    }
}
