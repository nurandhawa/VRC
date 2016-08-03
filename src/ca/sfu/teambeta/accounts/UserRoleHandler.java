package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;

/**
 * This class handles:
 * - Setting a new role for a user
 * - Getting a user's role
 *
 */

public class UserRoleHandler {
    //private List<String> administrators;
    private AccountDatabaseHandler accountDbHandler;

    // MARK: Constructor
    public UserRoleHandler(AccountDatabaseHandler accountDbHandler) {
        this.accountDbHandler = accountDbHandler;

    }


    // MARK: The Core UserRole Handler Method(s)
    public UserRole getUserRole(String email) throws NoSuchUserException {
        User user = accountDbHandler.getUser(email);
        UserRole role = user.getUserRole();

        return role;
    }

    public boolean setUserRole(String userEmail, UserRole newRole) throws NoSuchUserException {
        User user = accountDbHandler.getUser(userEmail);

        if (user.getUserRole() == newRole) {
            return false;
        }

        user.setUserRole(newRole);
        accountDbHandler.updateExistingUser(user);

        return true;
    }


}
