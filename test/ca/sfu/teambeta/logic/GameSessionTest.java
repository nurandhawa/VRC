package ca.sfu.teambeta.logic;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.persistence.DBManager;

/**
 * Created by Gordon Shieh on 24/06/16.
 */
public class GameSessionTest {
    Pair p1 = new Pair(new Player("P3", "Test", ""), new Player("P4", "Test", ""), true);
    Pair p2 = new Pair(new Player("P7", "Test", ""), new Player("P8", "Test", ""), true);
    Pair p3 = new Pair(new Player("P11", "Test", ""), new Player("P12", "Test", ""), true);
    Pair p4 = new Pair(new Player("P15", "Test", ""), new Player("P16", "Test", ""), true);
    Pair p5 = new Pair(new Player("P19", "Test", ""), new Player("P20", "Test", ""), true);
    List<Pair> pairs = Arrays.asList(p1, p2, p3, p4, p5);

    GameSession gameSession;
    SessionFactory factory;

    @Before
    public void setup() {
        Ladder ladder = new Ladder(pairs);
        gameSession = new GameSession(ladder);
        factory = DBManager.getTestingSession(true);
    }

    @Test
    public void persistenceTest() {
        gameSession.setPairActive(p5);
        Session session = factory.openSession();
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

        GameSession newGameSession = session.get(GameSession.class, key);

        assert (newGameSession.getActivePairs().contains(p5));
    }
}
