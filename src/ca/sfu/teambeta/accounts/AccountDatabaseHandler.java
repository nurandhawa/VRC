package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.AccountRegistrationException;
import ca.sfu.teambeta.core.exceptions.IllegalDatabaseOperation;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;
import ca.sfu.teambeta.persistence.DBManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public List<User> getAllAnonymousUsers() {
        return dbManager.getAllUsersOfRole(UserRole.ANONYMOUS);
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

    public void deleteUser(String email) throws NoSuchUserException, IllegalDatabaseOperation {
        dbManager.deleteUser(email);
    }

    public Map<String, String> deleteUsersOfRole(UserRole role) {
        // HashMap is structured <Email, Reason Not Deleted>
        Map<String, String> nonDeletableUsers = new HashMap<>();

        List<User> usersToDelete = dbManager.getAllUsersOfRole(role);

        // We'll allow the method to delete as many users as it can
        //  instead of exiting when an exception is thrown; log the users
        //  that could not be deleted and the reason.
        for (int i = 0; i < usersToDelete.size(); i++) {
            String email = usersToDelete.get(i).getEmail();

            try {
                deleteUser(email);
            } catch (NoSuchUserException e) {
                nonDeletableUsers.put(email, "No such email was found");
            } catch (IllegalDatabaseOperation e) {
                nonDeletableUsers.put(email, e.getMessage());
            }
        }

        return nonDeletableUsers;
    }
}
