package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;

/**
 * This class handles:
 * - User roles
 * - Addition of an admin
 * - Removal of an admin
 *
 */

public class UserRoleHandler {
    //private List<String> administrators;
    private AccountDatabaseHandler accountDBHandler;

    // MARK: Constructor
    public UserRoleHandler(AccountDatabaseHandler accountDBHandler) {
        this.accountDBHandler = accountDBHandler;

    }


    // MARK: The Core UserRole Handler Method(s)
    public UserRole getUserRole(String email) throws NoSuchUserException {
        User user = accountDBHandler.getUser(email);
        UserRole role = user.getUserRole();

        return role;
    }

    public boolean setUserRole(String userEmail, UserRole newRole) throws NoSuchUserException {
        User user = accountDBHandler.getUser(userEmail);

        if (user.getUserRole() == newRole) {
            return false;
        }

        user.setUserRole(newRole);
        accountDBHandler.updateExistingUser(user);

        return true;
    }


}
