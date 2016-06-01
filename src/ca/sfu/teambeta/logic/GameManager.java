package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Gordon Shieh on 25/05/16.
 */
public class GameManager {

    private ArrayList<Player> ladder;
    private ArrayList<Scorecard<Player>> groups;
    private ArrayList<MatchCard> matchCards;

    public GameManager() {
        ladder = new ArrayList<>();
        groups = new ArrayList<>();
        matchCards = new ArrayList<>();
        /* will not need this once ladder is up and filled already.
        * I am using a Player object and using "playerId" field to incorporate tags of will or will not play for the time being.*/
        fillUpLadder();
        splitLadderIntoGroups();

        System.out.println("Group Size: " + groups.size());

        /*for(Scorecard<Player> s : groups){
            System.out.println("Size: " + s.getTeams().size());
            List<Player> players = s.getTeams();

            for(Player p : players){
                System.out.printf("%3s",p.getName());
            }
            System.out.println();
        }*/
        //inputMatchResults();
    }

    private void splitLadderIntoGroups() {
        int playingCount = getWillingToPlayCount();
        System.out.println("Players willing to play: " + playingCount);

        ArrayList<Player> groupings = new ArrayList<>();

        if (playingCount % 3 == 0) {
            System.out.println("All 3 team groups.");
            int noOfTripleGroups = playingCount / 3;
            makeTripleGroups(noOfTripleGroups, groupings);

        } else if (playingCount % 3 == 1) {
            System.out.println("One 4 team group");
            int noOftripleGroups = playingCount / 3 - 1;
            int currentIndex = makeTripleGroups(noOftripleGroups, groupings);
            makeQuadGroup(currentIndex, groupings);
        } else {
            System.out.println("Two 4 team group");
            int noOftripleGroups = playingCount / 3 - 2;
            int currentIndex = makeTripleGroups(noOftripleGroups, groupings);
            makeQuadGroup(currentIndex, groupings);
        }
    }

    private void makeQuadGroup(int num, ArrayList<Player> groupings) {
        for (int i = num; i < ladder.size(); i++) {
            boolean check = false;
            if (ladder.get(i).getPlayerID() == 1) {
                groupings.add(ladder.get(i));
            }

            if (groupings.size() == 4) {
                Scorecard<Player> s = new Scorecard<>(groupings);
                groups.add(s);

                //MatchCard m = new MatchCard(groupings);
                //matchCards.add(m);

                for (Player p : groupings) {
                    System.out.printf("%5s", p.getName());
                }
                check = true;
                System.out.println();
            }
            if(check){
                groupings.clear();
            }
        }
    }

    private int makeTripleGroups(int num, ArrayList<Player> groupings) {
        int doneGroups = 0;
        int indexPosition = 0;

        for (int i = 0; i < ladder.size(); i++) {
            boolean check = false;

            if (ladder.get(i).getPlayerID() == 1) {
                groupings.add(ladder.get(i));
            }

            if (groupings.size() == 3) {
                check = true;
                Scorecard<Player> s = new Scorecard<>(groupings);
                groups.add(s);

                //MatchCard m = new MatchCard(groupings);
                //matchCards.add(m);

                for (Player p : groupings) {
                    System.out.printf("%5s", p.getName());
                }
                System.out.println();
                doneGroups++;
            }
            if(check){
                System.out.println("Group[0] size: " + groups.get(0).getTeams().size());
                groupings.clear();
                System.out.println("Group[00] size: " + groups.get(0).getTeams().size());
            }
            if (doneGroups == num) {
                indexPosition = i + 1;
                break;
            }
        }
        return indexPosition;
    }


    private int getWillingToPlayCount() {
        int count = 0;

        for (Player p : ladder) {
            if (p.getPlayerID() == 1) {
                count++;
            }
        }
        return count;
    }

    private void fillUpLadder() {
        Random rand = new Random();

        for (int i = 0; i < 25; i++) {
            int n = rand.nextInt(2);
            String name = String.valueOf(Character.toChars(65 + i));
            Player p = new Player(n, name);
            ladder.add(p);
        }
        displayLadder();
    }

    private void displayLadder() {
        System.out.printf("%10s %15s", "TEAMS", "   WILLING TO PLAY");
        System.out.println();

        for (int i = 0; i < ladder.size(); i++) {
            System.out.printf("%10s %10d", ladder.get(i).getName(), ladder.get(i).getPlayerID());
            System.out.println();
        }
    }

    public void inputMatchResults() {


        for(MatchCard m : matchCards){
            m.addMatchCardResults(1,"L","W","");
            m.addMatchCardResults(2,"W","","L");
            m.addMatchCardResults(3,"","W","L");
            //m.calculateRankings();
            m.displayMatchCard();
        }
    }

}
