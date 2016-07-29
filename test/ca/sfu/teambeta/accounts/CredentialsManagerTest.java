package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.Ladder;
import ca.sfu.teambeta.core.Pair;
import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.*;
import ca.sfu.teambeta.logic.GameSession;
import ca.sfu.teambeta.persistence.CSVReader;
import ca.sfu.teambeta.persistence.DBManager;
import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by constantin on 28/07/16.
 */
public class CredentialsManagerTest {
    private CredentialsManager credManager;
    private String email;
    private String password;
    private String secQuestion;
    private String secAnswer;

    @Before
    public void setUp() throws Exception {
        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        Ladder newLadder = CSVReader.setupLadder();
        GameSession gameSession = new GameSession(newLadder);
        DBManager dbManager = new DBManager(sessionFactory);
        dbManager.persistEntity(gameSession);
        AccountDatabaseHandler handler = new AccountDatabaseHandler(dbManager);
        AccountManager manager = new AccountManager(handler);
        credManager= new CredentialsManager(handler);

        email = "nick@gmail.com";
        password = "111111";
        secQuestion = "What is the name of my dog?";
        secAnswer = "Max";

        Pair pair = newLadder.getPairs().get(0);
        Player playerFromDB = pair.getPlayers().get(0);
        int playerId = playerFromDB.getID();

        manager.registerUserWithPlayer(email, password, playerId, secQuestion, secAnswer);
    }

    @Test
    public void checkQuestion() throws GeneralUserAccountException,
            AccountRegistrationException, NoSuchUserException, InvalidInputException {

        String actualQuestion = credManager.getUserSecurityQuestion(email);
        Assert.assertEquals(secQuestion, actualQuestion);
    }

    @Test
    public void changePassword() throws GeneralUserAccountException,
            AccountRegistrationException, NoSuchUserException, InvalidInputException, InvalidCredentialsException {

        String newPassword = "chicken";
        String voucherCode = credManager.validateSecurityQuestionAnswer(email, secAnswer);
        credManager.changePassword(email, newPassword, voucherCode);

        //Try to use new password
        User user = new User(email, newPassword);
        String passwordHashNew = user.getPasswordHash();

        boolean correctPass = CredentialsManager.checkHash(newPassword, passwordHashNew, "Error message");
        Assert.assertTrue(correctPass);

        //Try to use old password
        user = new User(email, password);
        String passwordHashOld = user.getPasswordHash();

        boolean wrongPass = CredentialsManager.checkHash(password, passwordHashOld, "Error message");
        Assert.assertFalse(wrongPass);
    }
}
