package ca.sfu.teambeta.persistence;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;

import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;

public class PairDBTest extends PersistenceTest {

    //Test shouldn't raise an foreign key violation error
    @Test
    public void testSimilarPairCreation() {
        Player firstPlayer = new Player("Test", "A");
        Player secondPlayer = new Player("Test", "B");
        Player thirdPlayer = new Player("Test", "C");

        Pair firstPair = new Pair(firstPlayer, secondPlayer);
        Pair secondPair = new Pair(secondPlayer, thirdPlayer);

        Transaction tx;
        Session session = getSession();
        tx = session.beginTransaction();
        session.save(firstPair);
        session.save(secondPair);
        tx.commit();
    }
}
