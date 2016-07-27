package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.SessionResponse;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.*;
import ca.sfu.teambeta.logic.InputValidator;

/**
 * AccountManager handles:
 * - User Login/Logout
 * - User Registration
 * <p>
 * <p>
 * - Anonymous Users
 * <p>
 * <p>
 */

public class AccountManager {
    private AccountDatabaseHandler accountDBHandler;
    private UserRoleHandler userRoleHandler;

/*
    // User for testing purposes
    private static final String DEMO_EMAIL = "testuser@vrc.com";
    private static final String DEMO_PASSWORD = "demoPass";
*/


    // MARK: Constructor
    public AccountManager(AccountDatabaseHandler accountDBHandler) {
        this.accountDBHandler = accountDBHandler;

        userRoleHandler = new UserRoleHandler(accountDBHandler);
    }


    // MARK: - The Core Login/Registration Methods
    public SessionResponse login(String email, String password) throws InvalidInputException, NoSuchUserException, InvalidCredentialsException, GeneralUserAccountException {
        InputValidator.validateEmailFormat(email);
        InputValidator.validatePasswordFormat(password);

        // Authenticate the password
        // Get the user from the database
        User user = accountDBHandler.getUser(email);

        // Validate the entered password with the hash
        boolean isPasswordCorrect = CredentialsManager.checkHash(password, user.getPasswordHash(), "The user cannot be authenticated");

        if (!isPasswordCorrect) {
            throw new InvalidCredentialsException("Incorrect password");
        }


        // Create a session for the user
        UserRole role = userRoleHandler.getUserClearanceLevel(email);

        return UserSessionManager.createNewSession(email, role);
    }

    public void logout(String sessionId) throws NoSuchSessionException {
        UserSessionManager.deleteSession(sessionId);
    }

    public void register(String email, String password) throws InvalidInputException, AccountRegistrationException, GeneralUserAccountException {
        InputValidator.validateEmailFormat(email);
        InputValidator.validatePasswordFormat(password);

        // Hash the user's password
        String passwordHash = CredentialsManager.getHash(password, "Could not create the account");

        User newUser = new User(email, passwordHash);

        // Save the user to the database, no Exception marks success
        accountDBHandler.saveNewUser(newUser);

    }

    public void registerNewAdministratorAccount(String email, String password) {

    }


    // MARK: - Main Function
/*
    public static void main(String[] args) {
        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        DBManager dbManager = new DBManager(sessionFactory);

        AccountManager accountManager = new AccountManager(dbManager);
        // Register a user
        try {
            accountManager.register("admin_zong@vrc.ca", DEMO_PASSWORD);
        } catch (InternalHashingException e) {
            System.out.println(e.getMessage());
            return;
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
            return;
        } catch (AccountRegistrationException e) {
            System.out.println(e.getMessage());
            return;
        }


        // Register the same user again (Should fail with duplicate user email)
//        try {
//            accountManager.register("admin_zong@vrc.ca", DEMO_PASSWORD);
//        } catch (InternalHashingException e) {
//            System.out.println(e.getMessage());
//            return;
//        } catch (InvalidInputException e) {
//            System.out.println(e.getMessage());
//            return;
//        } catch (AccountRegistrationException e) {
//            System.out.println(e.getMessage());
//            return;
//        }



        // Login the user registered above
        String userSessionId = "";

        try {
            userSessionId = accountManager.login("admin_zong@vrc.ca", DEMO_PASSWORD);
        } catch (InternalHashingException e) {
            System.out.println(e.getMessage());
            return;
        } catch (NoSuchUserException e) {
            System.out.println(e.getMessage());
            return;
        } catch (InvalidInputException e) {
            System.out.println(e.getMessage());
            return;
        } catch (InvalidCredentialsException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("User UserSessionMetadata ID: " + userSessionId);

        boolean admin;

        try {
            admin = UserSessionManager.isAdministratorSession(userSessionId);
        } catch (NoSuchSessionException e) {
            admin = false;
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Is Admin: " + admin);
    }
*/

}
