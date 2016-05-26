package ca.sfu.teambeta.logic;

/**
 * Created by Gordon Shieh on 25/05/16.
 */
public class GameManager {

    private MatchCard testCard;

    public GameManager() {

        // just testing it out. Can have an ArrayList here to store Match Cards.
        testCard = new MatchCard(3);
    }

    public void createMatchCards() {

        //Teams would be gathered from the Ladder.
        testCard.addTeamToMatchCard("Roger Federer");
        testCard.addTeamToMatchCard("Rafa Nadal");
        testCard.addTeamToMatchCard("Novak Djokovic");

        testCard.displayMatchCard();
    }

    public void inputMatchResults() {

        testCard.addMatchCardResults(1, "W", "L", "NA");
        testCard.addMatchCardResults(2, "W", "NA", "L");

        System.out.println();
        testCard.displayMatchCard();
    }

}
