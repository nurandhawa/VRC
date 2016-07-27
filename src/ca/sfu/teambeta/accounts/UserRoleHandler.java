package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles:
 * - User roles
 * - Addition of an admin
 * - Removal of an admin
 *
 */

public class UserRoleHandler {
    private List<String> administrators;
    private AccountDatabaseHandler accountDBHandler;

    // MARK: Constructor
    public UserRoleHandler(AccountDatabaseHandler accountDBHandler) {
        this.accountDBHandler = accountDBHandler;
        populateAdministratorList();
    }


    // MARK: The Core Role Handler Method(s)
    public UserRole getUserClearanceLevel(String email) {
        if (administrators.contains(email)) {
            return UserRole.ADMINISTRATOR;
        } else {
            return UserRole.REGULAR;
        }

    }


    // MARK: Addition/Removal of an Admin
    public void setAdminPrivilege(String email) throws NoSuchUserException {
        User user = accountDBHandler.getUser(email);
        user.setUserRole(UserRole.ADMINISTRATOR);
        accountDBHandler.updateExistingUser(user);

        administrators.add(email);
    }

    public void removeAdminPrivilege(String email) throws NoSuchUserException {
        User user = accountDBHandler.getUser(email);
        user.setUserRole(UserRole.REGULAR);
        accountDBHandler.updateExistingUser(user);

        administrators.remove(email);
    }

    public void setAsAnonymousUser(String email) throws NoSuchUserException {
        User user = accountDBHandler.getUser(email);
        user.setUserRole(UserRole.ANONYMOUS);
        accountDBHandler.updateExistingUser(user);
    }


    // MARK: Helper Method(s)
        private void populateAdministratorList() {
        // In the future this method can be changed to fetch
        //  a list of admin emails from the database, or a file, etc.

        List<String> admins = new ArrayList<>();

        final String DEMO_ADMIN_1 = "admin_billy@vrc.ca";
        final String DEMO_ADMIN_2 = "admin_zong@vrc.ca";

        admins.add(DEMO_ADMIN_1);
        admins.add(DEMO_ADMIN_2);

        administrators = admins;
    }

}
