package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.AccountRegistrationException;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;
import ca.sfu.teambeta.persistence.DBManager;

/**
 * This class handles the api interactions between the account/user
 * related classes, and the database. In the future a logger can be
 * implemented in this intermediary class to log all account related
 * interactions for debugging and security purposes. Furthermore, a
 * local in-memory version of this class can be passed into other
 * objects, if ever needed for testing purposes.
 *
 */

public class AccountDatabaseHandler {
    private DBManager dbManager;
    final String EMPTY_NAME_PLACEHOLDER = "*";


    // Constructor
    public AccountDatabaseHandler(DBManager dbManager) {
        this.dbManager = dbManager;
    }


    // MARK: Database Retrieval Methods
    public User getUser(String email) throws NoSuchUserException {
        // Get the user from the database
        User user = dbManager.getUser(email);

        if (user == null) {
            throw new NoSuchUserException("The user '" + email + "' does not exist");
        }

        return user;
    }

    public Player getPlayer(int playerId) throws NoSuchUserException {
        Player player = dbManager.getPlayerFromID(playerId);

        if (player == null) {
            throw new NoSuchUserException("The player with id " + playerId + " does not exist");
        }

        String firstName = player.getFirstName();
        String lastName = player.getLastName();

        if (firstName == null || firstName == "") {
            player.setFirstName(EMPTY_NAME_PLACEHOLDER);
        }

        if (lastName == null || lastName == "") {
            player.setLastName(EMPTY_NAME_PLACEHOLDER);
        }

        return player;
    }


    // MARK: Database Save/Update/Remove Methods
    public void saveNewUser(User user) throws AccountRegistrationException {
        dbManager.addNewUser(user);
    }

    public void updateExistingUser(User user) {
        dbManager.updateExistingUser(user);
    }

}
