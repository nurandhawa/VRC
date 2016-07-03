package ca.sfu.teambeta;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.logic.GameSession;
import ca.sfu.teambeta.persistence.CSVReader;
import ca.sfu.teambeta.persistence.DBManager;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Main {
    public static void main(String[] args) {

        /*Laddgiter loadedLadder = DBManager.loadFromDB();*/

        /* -----FOR TESTING*/
        /*List<Pair> ladderPairs = Arrays.asList(
                new Pair(new Player("Bobby", "Chan"), new Player("Wing", "Man"), true),
                new Pair(new Player("Ken", "Hazen"), new Player("Brian", "Fraser"), true),
                new Pair(new Player("Simon", "Fraser"), new Player("Dwight", "Howard"), true),
                new Pair(new Player("Bobby", "Chan"), new Player("Big", "Head"), true),
                new Pair(new Player("Alex", "Land"), new Player("Test", "Player"), false),
                new Pair(new Player("Manuel", "Neuer"), new Player("Gigi", "Buffon"), true),
                new Pair(new Player("Mesut", "Ozil"), new Player("Gareth", "Bale"), true)
        );
        Ladder newLadder = new Ladder(ladderPairs);*/

        Ladder newLadder = null;
        try{
            newLadder = CSVReader.setupLadder();
        } catch(Exception e) {
            System.out.println("INVALID CSV FILE");
        }

        SessionFactory sessionFactory = DBManager.getMySQLSession(true);
        DBManager dbManager = new DBManager(sessionFactory);
        GameSession gameSession = new GameSession(newLadder);
        dbManager.persistEntity(gameSession);

        AppController appController = new AppController(dbManager);
    }
}
