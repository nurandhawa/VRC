package ca.sfu.teambeta.logic;

import java.util.ArrayList;

/**
 * Created by NoorUllah on 2016-05-25.
 */
public class MatchCard {

    private ArrayList<String> listOfTeams = new ArrayList<>();
    private String[][] matchCardInterface;
    private int numOfRows;
    private int numOfColumns;

    public MatchCard(int numOfTeams) {

        this.numOfRows = numOfTeams + 1;
        this.numOfColumns = 5;
        matchCardInterface = new String[numOfRows][numOfColumns];

        //headers
        matchCardInterface[0][0] = "NAME";
        matchCardInterface[0][1] = "ROUND 1";
        matchCardInterface[0][2] = "ROUND 2";
        matchCardInterface[0][3] = "ROUND 3";
        matchCardInterface[0][4] = "RANKING";
    }

    public void displayMatchCard() {

        for (int i = 0; i < numOfRows; i++) {

            for (int j = 0; j < numOfColumns; j++) {
                System.out.printf("%15s", matchCardInterface[i][j]);
            }

            System.out.println();
        }
    }

    public void addTeamToMatchCard(String teamName) {
        listOfTeams.add(teamName);

        matchCardInterface[listOfTeams.size()][0] = listOfTeams.get(listOfTeams.size() - 1);
    }

    public void addMatchCardResults(int round, String x, String y, String z) {

        matchCardInterface[numOfRows - 3][round] = x;
        matchCardInterface[numOfRows - 2][round] = y;
        matchCardInterface[numOfRows - 1][round] = z;
    }


}
