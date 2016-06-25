package ca.sfu.teambeta.logic;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import ca.sfu.teambeta.core.Game;
import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Penalty;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.Scorecard;
import ca.sfu.teambeta.persistence.PersistenceTest;

public class GameSessionTest extends PersistenceTest {
    private final Pair kateNick = new Pair(
            new Player("Kate", "Test"),
            new Player("Nick", "Test")
    );
    private final Pair jimRyan = new Pair(
            new Player("Jim", "Test"),
            new Player("Ryan", "Test")
    );
    private final Pair davidBob = new Pair(
            new Player("David", "Test"),
            new Player("Bob", "Test")
    );
    private final Pair richardRobin = new Pair(
            new Player("Richard", "Test"),
            new Player("Robin", "Test")
    );
    private final Pair kevinJasmin = new Pair(
            new Player("Kevin", "Test"),
            new Player("Jasmin", "Test")
    );
    private final Pair amyMaria = new Pair(
            new Player("Amy", "Test"),
            new Player("Maria", "Test")
    );
    private final Pair tonyAngelica = new Pair(
            new Player("Tony", "Test"),
            new Player("Angelica", "Test")
    );
    private final Pair anastasiaVictoria = new Pair(
            new Player("Anastasia", "Test"),
            new Player("Victoria", "Test")
    );
    private final Pair ianCamden = new Pair(
            new Player("Ian", "Test"),
            new Player("Camden", "Test")
    );

    private final List<Pair> pairList = Arrays.asList(kateNick, jimRyan, davidBob, richardRobin,
            kevinJasmin, amyMaria, tonyAngelica, anastasiaVictoria, ianCamden);

    private final List<Pair> reorderedList = Arrays.asList(kevinJasmin, kateNick, jimRyan,
            ianCamden, tonyAngelica, amyMaria, anastasiaVictoria, richardRobin, davidBob);

    GameSession gameSession;

    @Before
    public void setup() {
        Ladder ladder = new Ladder(pairList);
        gameSession = new GameSession(ladder);

        gameSession.setPenaltyToPair(davidBob, Penalty.MISSING);
        gameSession.setPenaltyToPair(richardRobin, Penalty.LATE);

        gameSession.setPairActive(davidBob);
        gameSession.setPairActive(richardRobin);
        gameSession.setPairActive(kevinJasmin);
        gameSession.setPairActive(tonyAngelica);
        gameSession.setPairActive(anastasiaVictoria);
        gameSession.setPairActive(ianCamden);

        gameSession.createGroups();
    }


    @Test
    public void testPersistSimpleSession() {
        gameSession.setPairActive(kateNick);
        Session session = getSession();
        int key = saveGameSession();

        GameSession newGameSession = session.get(GameSession.class, key);
        session.close();
        assert (newGameSession.getActivePairs().contains(kateNick));
    }

    @Test
    public void testScorecardSize() {
        List<Scorecard> scorecards = gameSession.getScorecards();
        assert (scorecards.size() == 2);
    }

    @Test
    public void testPersistActiveSession() {
        int key = saveGameSession();
        Session session = getSession();
        GameSession newGameSession = session.get(GameSession.class, key);
        session.close();
        assert (newGameSession.getScorecards().equals(gameSession.getScorecards()));
    }

    @Test
    public void testModifyScorecard() {
        saveGameSession();
        Session session = getSession();
        Scorecard firstCard = session.get(Scorecard.class, 1);
        Scorecard secondCard = session.get(Scorecard.class, 2);

        List<Pair> activePairs = gameSession.getActivePairs();
        Pair pair1 = activePairs.get(0);
        Pair pair2 = activePairs.get(1);
        Pair pair3 = activePairs.get(2);

        firstCard.setGameResults(pair3, pair2);
        firstCard.setGameResults(pair3, pair1);
        firstCard.setGameResults(pair1, pair2);
        Transaction tx = session.beginTransaction();
        session.update(firstCard);

        Pair pair4 = activePairs.get(3);
        Pair pair5 = activePairs.get(4);
        Pair pair6 = activePairs.get(5);

        secondCard.setGameResults(pair6, pair5);
        secondCard.setGameResults(pair4, pair5);
        secondCard.setGameResults(pair6, pair4);
        session.update(secondCard);
        tx.commit();

        Game testGame1 = session.get(Game.class, 2);
        session.close();
        assert (testGame1.getWinner().equals(pair3));
    }

    private int saveGameSession() {
        Session session = getSession();
        Transaction tx = null;
        int key = 0;
        try {
            tx = session.beginTransaction();
            key = (int) session.save(gameSession);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        }
        return key;
    }
}
