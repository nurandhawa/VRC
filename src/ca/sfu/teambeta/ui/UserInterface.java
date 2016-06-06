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
    private static final int EDIT_LADDER_SET_PLAYING = 3;
    private static final int EDIT_LADDER_SET_NOT_PLAYING = 4;
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
                        listLadder(ladderManager.getLadder());
                        break;
                    case LIST_MATCHES:
                        listMatches(gameManager.getScorecards());
                        break;
                    case EDIT_LADDER:
                        editLadder(ladderManager);
                        gameManager.updateGroups(ladderManager.getActivePairs());
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
        int matchIndex = matchSelection - 1;
        Scorecard<Pair> match = scorecards.get(matchIndex);
        System.out.println(match.toString());

        System.out.println("Choose an action:");
        System.out.println(EDIT_MATCHES_RESULTS + ". Input results");
        System.out.println(EDIT_MATCHES_REMOVE + ". Remove a pair from the match");
        input = scanner.nextLine();

        int index = 1;
        for (Pair pair : match.getTeamRankings()) {
            System.out.println(index + ". " + pair.toString());
            index++;
        }

        int selection = Integer.parseInt(input);
        if (selection == EDIT_MATCHES_RESULTS) {
            inputMatchResults(match, gameManager);
        } else if (selection == EDIT_MATCHES_REMOVE) {
            System.out.println("Enter the pair number to remove: ");
            input = scanner.nextLine();
            int pairSelection = Integer.parseInt(input);
            int pairIndex = pairSelection - 1;
            gameManager.removePlayingPair(match.getTeamRankings().get(pairIndex));
        }
    }

    private static void inputMatchResults(Scorecard<Pair> match, GameManager gameManager) {
        String input;
        int numTeams = match.getTeamRankings().size();
        String[][] results = new String[numTeams][numTeams];

        System.out.println("Enter match results (W for win, L for loss, - for bye," +
                " space delimited, / for newline");
        input = scanner.nextLine();
        int i = 0;
        for (String round : input.split("/")) {
            results[i] = round.split(" ");
            i++;
        }

        gameManager.inputMatchResults(match, results);

        System.out.println("Input more results? (y/n)");
        input = scanner.nextLine().toLowerCase();
        if (input.equals("y") | input.equals("yes")) {
            editMatches(gameManager);
        }
    }

    private static void editLadder(LadderManager ladderManager) {
        System.out.println("Choose an action:");
        System.out.println(EDIT_LADDER_ADD + ". Add pair to ladder");
        System.out.println(EDIT_LADDER_REMOVE + ". Remove pair from ladder");
        System.out.println(EDIT_LADDER_SET_PLAYING + ". Set pair to play");
        System.out.println(EDIT_LADDER_SET_NOT_PLAYING + ". Set pair to not play");

        String input = scanner.nextLine();
        int selection = Integer.parseInt(input);

        if (selection == EDIT_LADDER_ADD) {
            addPair(ladderManager);
        } else if (selection == EDIT_LADDER_REMOVE) {
            removePair(ladderManager);
        } else if (selection == EDIT_LADDER_SET_PLAYING) {
            setPlaying(ladderManager);
        } else if (selection == EDIT_LADDER_SET_NOT_PLAYING) {
            setNotPlaying(ladderManager);
        }
    }

    private static void listMatches(List<Scorecard<Pair>> scorecards) {
        System.out.println("Matches: ");
        int index = 1;
        for (Scorecard<Pair> scorecard : scorecards) {
            System.out.println("****************** MATCH " + index + " ******************");
            System.out.println(scorecard.toString());
            index++;
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

        List<Pair> ladder = ladderManager.getLadder();
        List<Player> pair = new ArrayList<>(2);
        String input;
        while (pair.size() < 2) {
            System.out.println("Enter " + EXISTING_PLAYER + " for existing player, " + NEW_PLAYER
                    + " for new player.");

            input = scanner.nextLine();
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

        Pair newPair = new Pair(pair.get(0), pair.get(1));

        System.out.println("Add to end of ladder? (y/n): ");
        input = scanner.nextLine();
        if (input.toLowerCase().equals("y")) {
            ladderManager.addNewPair(newPair);
        } else {
            System.out.println("Enter position to insert at: ");
            input = scanner.nextLine();
            int position = Integer.parseInt(input);
            int index = position - 1;
            ladderManager.addNewPairAtIndex(newPair, index);
        }
    }

    private static void listPlayers(List<Player> players) {
        int index = 1;
        for (Player player : players) {
            System.out.println(index + ". " + player.getName());
            index++;
        }
    }

    private static void removePair(LadderManager ladderManager) {
        listLadder(ladderManager.getLadder());
        System.out.println("Enter number of pair to remove:");
        String input = scanner.nextLine();
        int selection = Integer.parseInt(input);
        int index = selection - 1;
        ladderManager.removePairAtIndex(index);
    }

    private static void setPlaying(LadderManager ladderManager) {
        listLadder(ladderManager.getLadder());
        System.out.println("Enter number of pair to set them to play: ");
        String input = scanner.nextLine();
        int selection = Integer.parseInt(input);
        int index = selection - 1;
        ladderManager.setIsPlaying(ladderManager.getLadder().get(index));
    }

    private static void setNotPlaying(LadderManager ladderManager) {
        listLadder(ladderManager.getLadder());
        System.out.println("Enter number of pair to set them to not play: ");
        String input = scanner.nextLine();
        int selection = Integer.parseInt(input);
        int index = selection - 1;
        ladderManager.setNotPlaying(ladderManager.getLadder().get(index));
    }
}
