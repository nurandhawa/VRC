package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.SessionResponse;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.*;
import ca.sfu.teambeta.logic.InputValidator;

import java.util.ArrayList;
import java.util.List;

/**
 * AccountManager handles:
 * - User Login/Logout
 *
 * - User Registration
 *  - Registering a user as administrator
 *  - Registering a user as anonymous
 *   - The flow to register a user as anonymous is:
 *     first call registerAnonymousUser which will
 *     pass back a 6 digit code (aka anonymous code).
 *     Since registration is done by the admin, this
 *     code is given to the person to input on a
 *     separate login page. If successful the anon.
 *     user will notice no difference once logged in
 *     from a regular user.
 *
 */

public class AccountManager {
    private AccountDatabaseHandler accountDBHandler;
    private UserRoleHandler userRoleHandler;
    private TokenGenerator tokenGenerator;
    private List<String> anonymousCodes;


/*
    // User for testing purposes
    private static final String DEMO_EMAIL = "testuser@vrc.com";
    private static final String DEMO_PASSWORD = "demoPass";
*/


    // MARK: Constructor
    public AccountManager(AccountDatabaseHandler accountDBHandler) {
        this.accountDBHandler = accountDBHandler;

        userRoleHandler = new UserRoleHandler(accountDBHandler);
        tokenGenerator = new TokenGenerator();

        anonymousCodes = new ArrayList<>();

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

    public String loginAnonymousUser(String anonymousCode) throws InvalidCredentialsException {
        boolean validAnonymousCode = anonymousCodes.contains(anonymousCode);

        if (!validAnonymousCode) {
            throw new InvalidCredentialsException("Incorrect anonymous code");
        }

        String sessionId = UserSessionManager.createNewAnonymousSession(anonymousCode);

        return sessionId;
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

    public void registerNewAdministratorAccount(String email, String password) throws InvalidInputException, GeneralUserAccountException, AccountRegistrationException {
        register(email, password);

        try {
            userRoleHandler.setAdminPrivilege(email);
        } catch (NoSuchUserException e) {
            // Although this should not be reached as we are explicity
            //  saving the user in the "register" method, something sometime
            //  could go wrong with the database and/or have concurrency
            //  issues.

            throw new GeneralUserAccountException("Could not make " + email + " an admin");
        }
    }

    public String registerAnonymousUser() throws InvalidInputException, AccountRegistrationException, GeneralUserAccountException {
        String accountName = tokenGenerator.generateUniqueRandomToken();
        String password = tokenGenerator.generateUniqueRandomToken();

        String emailAddress = accountName + "@" + "vrc.teambeta";

        register(emailAddress, password);

        String anonymousCode = accountName.substring(0, 6);

        // Update the appropriate classes to let them know user is anonymous
        try {
            // The role is set so the front-end may further limit access to certain things
            userRoleHandler.setAsAnonymousUser(emailAddress);
        } catch (NoSuchUserException e) {
            // Although this should not be reached as we are explicity
            //  saving the user in the "register" method, something sometime
            //  could go wrong with the database and/or have concurrency
            //  issues.

            throw new GeneralUserAccountException("Could not make " + emailAddress + " an anonymous user");
        }

        anonymousCodes.add(anonymousCode);

        return anonymousCode;
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
