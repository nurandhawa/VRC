package test.ca.sfu.teambeta.logic;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.logic.DBManager;

/**
 * Created by AlexLand on 2016-05-31.
 */
public class DBManagerTest {
    @Test
    public void testSave() {
        Ladder ladder = makeTestLadder();
        DBManager.saveToDB(ladder, "test/ca/sfu/teambeta/logic/ladder.csv");

        File savedFile = new File("test/ca/sfu/teambeta/logic/ladder.csv");
        File referenceFile = new File("test/ca/sfu/teambeta/logic/referenceladder.csv");
        try {
            String savedLadder = fileToString(savedFile);
            String referenceLadder = fileToString(referenceFile);
            Assert.assertEquals(referenceLadder, savedLadder);
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void testLoad() {
        Ladder referenceLadder = makeTestLadder();
        Ladder savedLadder = DBManager.loadFromDB("test/ca/sfu/teambeta/logic/referenceladder.csv");

        List<Pair> referencePairs = referenceLadder.getLadder();
        List<Pair> savedPairs = savedLadder.getLadder();
        Assert.assertEquals(referencePairs.size(), savedPairs.size());

        for (int i = 0; i < referencePairs.size(); i++) {
            List<Player> referencePlayers = referencePairs.get(i).getPlayers();
            List<Player> savedPlayers = savedPairs.get(i).getPlayers();

            Assert.assertEquals(referencePlayers.get(0).getId(), savedPlayers.get(0).getId());
            Assert.assertEquals(referencePlayers.get(0).getName(), savedPlayers.get(0).getName());

            Assert.assertEquals(referencePlayers.get(1).getId(), savedPlayers.get(1).getId());
            Assert.assertEquals(referencePlayers.get(1).getName(), savedPlayers.get(1).getName());
        }

    }

    private Ladder makeTestLadder() {
        List<Pair> pairs = new ArrayList<>(4);
        pairs.add(new Pair(new Player(0, "Alex Land"), new Player(1, "David Li")));
        pairs.add(new Pair(new Player(2, "Jas Jassal"), new Player(3, "Noor Randhawa")));
        pairs.add(new Pair(new Player(4, "Constantin Koval"), new Player(5, "Sam Kim")));
        pairs.add(new Pair(new Player(6, "Raymond Huang"), new Player(7, "Gordon Shieh")));

        return new Ladder(pairs);
    }

    private String fileToString(File file) throws IOException {
        String thisLine;
        FileInputStream fileIn = new FileInputStream(file);
        BufferedReader input = new BufferedReader(new InputStreamReader(fileIn));
        StringBuilder sb = new StringBuilder();
        while ((thisLine = input.readLine()) != null) {
            sb.append(thisLine);
        }
        return sb.toString();
    }
}
