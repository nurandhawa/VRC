package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.AccountRegistrationException;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;
import ca.sfu.teambeta.persistence.DBManager;

/**
 * This class handles the api interactions between the account and user
 * related methods, and the database.
 *
 */

public class AccountDatabaseHandler {
    private DBManager dbManager;


    // Constructor
    public AccountDatabaseHandler(DBManager dbManager) {
        this.dbManager = dbManager;
    }


    // MARK: Database Retrieval Method
    public User getUser(String email) throws NoSuchUserException {
        // Get the user from the database
        User user = dbManager.getUser(email);

        if (user == null) {
            throw new NoSuchUserException("The user '" + email + "' does not exist");
        }

        return user;
    }


    // MARK: Database Save/Update Methods
    public void saveNewUser(User user) throws AccountRegistrationException {
        dbManager.addNewUser(user);
    }

    public void updateExistingUser(User user) {
        // TODO: Implement this update function
    }

}
