package ca.sfu.teambeta.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.logic.GameManager;
import ca.sfu.teambeta.logic.LadderManager;

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

    public static void start(GameManager gameManager, LadderManager ladderManager) {
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
                        listLadder(ladderManager.getPlayingPairs());
                        break;
                    case LIST_MATCHES:
                        listMatches();
                        break;
                    case EDIT_LADDER:
                        editLadder(ladderManager);
                        break;
                    case EDIT_MATCHES:
                        editMatches(gameManager);
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

    private static void editMatches(GameManager gameManager) {
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

    private static void editLadder(LadderManager ladderManager) {
        String input;
        int selection;
        System.out.println("Choose an action:");
        System.out.println(EDIT_LADDER_ADD + ". Add pair to ladder");
        System.out.println(EDIT_LADDER_REMOVE + ". Remove pair from ladder");

        input = scanner.nextLine();
        selection = Integer.parseInt(input);
        if (selection == EDIT_LADDER_ADD) {
            addPair(ladderManager);
        } else if (selection == EDIT_LADDER_REMOVE) {
            removePair(ladderManager);
        }
    }

    private static void listMatches() {
        System.out.println("Matches: ");
    }

    private static void listLadder(List<Pair> ladder) {
        System.out.println("Ladder: ");

        int index = 1;
        for (Pair pair : ladder) {
            System.out.println(index + ". " + pair.toString());
        }
    }

    private static void addPair(LadderManager ladderManager) {
        final int EXISTING_PLAYER = 1;
        final int NEW_PLAYER = 2;

        List<Player> pair = new ArrayList<>(2);
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
            } else if (selection == NEW_PLAYER) {
                System.out.println("Enter name of new player: ");
                input = scanner.nextLine();
                // TODO: get length of player list from LadderManager to pick new ID
                int playerID = 1;

                // TODO: Create player object and pair.add()
                pair.add(new Player(1, input));
            }
        }
        ladderManager.addNewPair(new Pair(pair.get(0), pair.get(1)));
    }

    private static void removePair(LadderManager ladderManager) {
        System.out.println("Remove from ladder");
    }
}
