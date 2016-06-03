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
    private static final int EDIT_LADDER_ADD = 1;
    private static final int EDIT_LADDER_REMOVE = 2;
    private static final int EDIT_MATCHES_RESULTS = 1;
    private static final int EDIT_MATCHES_REMOVE = 2;
    private static final int FINISH = 5;
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
                        listLadder(ladderManager.getFullLadder());
                        break;
                    case LIST_MATCHES:
                        listMatches(gameManager.getScorecards());
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
        List<Scorecard<Pair>> scorecards = gameManager.getScorecards();
        listMatches(scorecards);
        System.out.println("Enter the match number you want to edit: ");
        String input;
        input = scanner.nextLine();

        int matchSelection = Integer.parseInt(input);
        Scorecard<Pair> match = scorecards.get(matchSelection);
        System.out.println(match.toString());

        System.out.println("Choose an action:");
        System.out.println(EDIT_MATCHES_RESULTS + ". Input results");
        System.out.println(EDIT_MATCHES_REMOVE + ". Remove a pair from the match");
        input = scanner.nextLine();

        int selection = Integer.parseInt(input);
        if (selection == EDIT_MATCHES_RESULTS) {
            inputMatchResults(match, gameManager);
        } else if (selection == EDIT_MATCHES_REMOVE) {
            System.out.println("Enter the pair number to remove: ");
            input = scanner.nextLine();
            int pairSelection = Integer.parseInt(input);
//            TODO: gameManager.removePlayingPair();
            // TODO: GameManager.editMatch(GameManager.REMOVE_PAIR, matchSelection or matches.get(matchselection), pairSelection);
        }
    }

    private static void inputMatchResults(Scorecard<Pair> match, GameManager gameManager) {
        int index = 1;
        for (Pair pair : match.getTeamRankings()) {
            System.out.println(index + ". " + pair.toString());
            index++;
        }

        String[][] results = new String[3][3];
        System.out.println("Enter team 1's record vs other teams (W for win, L for loss, - for bye, space delimited)");
        String input = scanner.nextLine();
        results[0] = input.split(" ");

        System.out.println("Enter team 2's record vs other teams (W for win, L for loss, - for bye, space delimited)");
        input = scanner.nextLine();
        results[1] = input.split(" ");

        System.out.println("Enter team 3's record vs other teams (W for win, L for loss, - for bye, space delimited)");
        input = scanner.nextLine();
        results[2] = input.split(" ");

        gameManager.inputMatchResults(match, results);

        System.out.println("Input more results? (y/n)");
        input = scanner.nextLine().toLowerCase();
        if (input.equals("y") | input.equals("yes")) {
            editMatches(gameManager);
        }
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

    private static void listMatches(List<Scorecard<Pair>> scorecards) {
        System.out.println("Matches: ");
        for (Scorecard<Pair> scorecard : scorecards) {
            System.out.println(scorecard.toString());
        }
    }

    private static void listLadder(List<Pair> ladder) {
        System.out.println("Ladder: ");
        int index = 1;
        for (Pair pair : ladder) {
            System.out.println(index + ". " + pair.toString());
            index++;
        }
    }

    private static void addPair(LadderManager ladderManager) {
        final int EXISTING_PLAYER = 1;
        final int NEW_PLAYER = 2;

        List<Pair> ladder = ladderManager.getFullLadder();
        List<Player> pair = new ArrayList<>(2);
        while (pair.size() < 2) {
            System.out.println("Enter " + EXISTING_PLAYER + " for existing player, " + NEW_PLAYER +
                    " for new player.");

            String input = scanner.nextLine();
            int selection = Integer.parseInt(input);
            if (selection == EXISTING_PLAYER) {
                List<Player> players = ladderManager.getAllPlayers();
                listPlayers(players);

                System.out.println("Enter number of player to use: ");
                input = scanner.nextLine();
                selection = Integer.parseInt(input);

                int index = selection - 1;
                pair.add(players.get(index));
            } else if (selection == NEW_PLAYER) {
                System.out.println("Enter name of new player: ");
                input = scanner.nextLine();

                int playerID = ladder.size() + 1;
                pair.add(new Player(playerID, input));
            }
        }
        ladderManager.addNewPair(new Pair(pair.get(0), pair.get(1)));
    }

    private static void listPlayers(List<Player> players) {
        int index = 1;
        for (Player player : players) {
            System.out.println(index + ". " + player.getName());
            index++;
        }
    }

    private static void removePair(LadderManager ladderManager) {
        System.out.println("Remove from ladder");
    }
}
