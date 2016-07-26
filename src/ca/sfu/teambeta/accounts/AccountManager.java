package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.*;
import ca.sfu.teambeta.logic.InputValidator;
import com.ja.security.PasswordHash;

/**
 * AccountManager handles:
 * - User Login
 * - User Registration
 * <p>
 * <p>
 * Coming Soon:
 * - Password Reset
 * -- Via Email
 * -- Via Text Message (Pass back last 2 digits of phone)
 * -- Via Security Questions (Hash answer)
 * <p>
 * - Anonymous Users
 * <p>
 * <p>
 */

public class AccountManager {
    private AccountDatabaseHandler accountDBHandler;
    private UserRoleHandler userRoleHandler;
    private PasswordHash passwordHasher = new PasswordHash();


/*
    // User for testing purposes
    private static final String DEMO_EMAIL = "testuser@vrc.com";
    private static final String DEMO_PASSWORD = "demoPass";
*/

    /*
    // When testing, uncomment and use this array to mimic database interaction
    private static List<User> usersInMemory = new ArrayList<>();
    */


    // MARK: Constructor
    public AccountManager(AccountDatabaseHandler accountDBHandler) {
        this.accountDBHandler = accountDBHandler;

        userRoleHandler = new UserRoleHandler(accountDBHandler);
    }


    // MARK: - The Core Login/Registration Methods

    public String login(String email, String password) throws InternalHashingException, NoSuchUserException, InvalidCredentialsException {

        // Authenticate and if successful get the user from the database
        User user = authenticateUser(email, password);

        // Create a session for the user
        UserRole role = userRoleHandler.getUserClearanceLevel(user.getEmail());

        return UserSessionManager.createNewSession(user, role);
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


    // MARK: - Helper Methods
    private User authenticateUser(String email, String password) throws InternalHashingException,
            NoSuchUserException, InvalidCredentialsException {
        // Get the user from the database
        User user = accountDBHandler.getUser(email);

        // Validate the entered password with the hash
        boolean isPasswordCorrect;

        try {
            isPasswordCorrect = passwordHasher.validatePassword(password, user.getPasswordHash());
        } catch (Exception e) {
            // Rethrow a simpler Exception following from
            // the abstract Exceptions thrown by ".validatePassword()"
            throw new InternalHashingException(
                    "Password cannot be determined as correct or incorrect, "
                            + "please contact an administrator if this problem persists");
        }

        if (!isPasswordCorrect) {
            throw new InvalidCredentialsException("Incorrect password");
        } else {
            return user;
        }

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
