package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.AccountRegistrationException;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;
import ca.sfu.teambeta.persistence.DBManager;

/**
 * This class handles the api interactions between the account/user
 * related methods, and the database. In the future a logger can be
 * implemented in this intermediary class to log all account related
 * interactions for debugging and security purposes. Furthermore, a
 * local in-memory version of this class can be passed into other
 * objects, if ever needed.
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


    // MARK: Database Save/Update/Remove Methods
    public void saveNewUser(User user) throws AccountRegistrationException {
        dbManager.addNewUser(user);
    }

    public void updateExistingUser(User user) {
        dbManager.updateExistingUser(user);
    }

    // Invoking the admin methods without going through the
    //  UserRoleHandler class will result in the changes not
    //  taking effect until the server is restarted.
    public void addEmailToAdminList(String email) {
        // TODO: Implement this
    }

    public void removeEmailFromAdminList(String email) {
        // TODO: Implement this
    }


}
