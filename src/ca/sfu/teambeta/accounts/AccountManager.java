package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.Player;
import ca.sfu.teambeta.core.SessionResponse;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.*;
import ca.sfu.teambeta.logic.InputValidator;

import java.util.HashMap;
import java.util.Map;

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
    private AccountDatabaseHandler accountDbHandler;
    private UserRoleHandler userRoleHandler;
    private TokenGenerator tokenGenerator;
    private Map<String, String> anonymousLoginCodes;

/*
    // User for testing purposes
    private static final String DEMO_EMAIL = "testuser@vrc.com";
    private static final String DEMO_PASSWORD = "demoPass";
*/


    // MARK: Constructor
    public AccountManager(AccountDatabaseHandler accountDbHandler) {
        this.accountDbHandler = accountDbHandler;

        userRoleHandler = new UserRoleHandler(accountDbHandler);
        tokenGenerator = new TokenGenerator();

        anonymousLoginCodes = new HashMap<>();

    }


    // MARK: - The Core Login/Registration Methods
    public SessionResponse login(String email, String password) throws InvalidInputException, NoSuchUserException, InvalidCredentialsException, GeneralUserAccountException {
        InputValidator.validateEmailFormat(email);
        InputValidator.validatePasswordFormat(password);

        // Authenticate the password
        // Get the user from the database
        User user = accountDbHandler.getUser(email);

        // Validate the entered password with the hash
        boolean isPasswordCorrect = CredentialsManager.checkHash(password, user.getPasswordHash(), "The user cannot be authenticated");

        if (!isPasswordCorrect) {
            throw new InvalidCredentialsException("Incorrect password");
        }


        // Create a session for the user
        UserRole role = userRoleHandler.getUserRole(email);

        return UserSessionManager.createNewSession(email, role);
    }

    public SessionResponse loginViaAnonymousCode(String anonymousLoginCode) throws InvalidCredentialsException {
        boolean validAnonymousLoginCode = (anonymousLoginCodes.get(anonymousLoginCode) != null);

        if (!validAnonymousLoginCode) {
            throw new InvalidCredentialsException("Incorrect login code");
        }

        String email = anonymousLoginCodes.get(anonymousLoginCode);

        return UserSessionManager.createNewSession(email, UserRole.ANONYMOUS);
    }

    public void logout(String sessionId) throws NoSuchSessionException {
        UserSessionManager.deleteSession(sessionId);
    }

    public void registerUserWithPlayer(String email, String password, int playerId, String securityQuestion, String securityQuestionAnswer) throws InvalidInputException, NoSuchUserException, GeneralUserAccountException, AccountRegistrationException {
        InputValidator.validateEmailFormat(email);
        InputValidator.validatePasswordFormat(password);

        if ((securityQuestion == null || securityQuestion == "") || (securityQuestionAnswer == null || securityQuestionAnswer == "")) {
            throw new InvalidInputException("Security question fields cannot be empty");
        }


        // Get the associated player
        Player player = accountDbHandler.getPlayer(playerId);

        // Hash the user's password and security question answer
        String passwordHash = CredentialsManager.getHash(password, "Could not create the account");
        String securityQuestionAnswerHash = CredentialsManager.getHash(securityQuestionAnswer, "Could not create the account");


        // Create a new user and assign the fields
        User newUser = new User(email, passwordHash);

        newUser.setSecurityQuestion(securityQuestion);
        newUser.setSecurityAnswerHash(securityQuestionAnswerHash);

        newUser.associatePlayer(player);


        // Finally save the user
        accountDbHandler.saveNewUser(newUser);

    }

    public void registerUser(String email, String password) throws InvalidInputException, AccountRegistrationException, GeneralUserAccountException {
        InputValidator.validateEmailFormat(email);
        InputValidator.validatePasswordFormat(password);

        // Hash the user's password
        String passwordHash = CredentialsManager.getHash(password, "Could not create the account");

        User newUser = new User(email, passwordHash);

        // Save the user to the database, no Exception marks success
        accountDbHandler.saveNewUser(newUser);

    }

    public void registerNewAdministratorAccount(String email, String password) throws InvalidInputException, GeneralUserAccountException, AccountRegistrationException {
        registerUser(email, password);

        try {
            userRoleHandler.setUserRole(email, UserRole.ADMINISTRATOR);
        } catch (NoSuchUserException e) {
            // Although this should not be reached as we are explicity
            //  saving the user in the "register" method, something sometime
            //  could go wrong with the database and/or have concurrency
            //  issues.

            throw new GeneralUserAccountException("Could not make " + email + " an admin");
        }
    }

    public String registerNewAnonymousAccount() throws InvalidInputException, AccountRegistrationException, GeneralUserAccountException {
        String accountName = tokenGenerator.generateUniqueRandomToken();
        String password = tokenGenerator.generateUniqueRandomToken();

        String email = accountName + "@" + "vrc.teambeta";

        registerUser(email, password);

        String anonymousLoginCode = accountName.substring(0, 6);


        // Update the appropriate classes to let them know user is anonymous
        try {
            // The role is set so the front-end may further limit access to certain things
            userRoleHandler.setUserRole(email, UserRole.ANONYMOUS);
        } catch (NoSuchUserException e) {
            // Although this should not be reached as we are explicitly
            //  saving the user in the "register" method, something sometime
            //  could go wrong with the database and/or have concurrency
            //  issues.

            throw new GeneralUserAccountException("Could not make " + email + " an anonymous user");
        }

        // Save the random email we generated so we can delete the user at a later time
        anonymousLoginCodes.put(anonymousLoginCode, email);

        return anonymousLoginCode;
    }

    public void deleteAllAnonymousUsers() {

    }


    // !! TODO: DELETE THE MAIN FUNCTION - WILL BE REFACTORED INTO TESTS !!
    // MARK: - Main Function
/*
    public static void main(String[] args) {
        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        DBManager dbManager = new DBManager(sessionFactory);

        AccountManager accountManager = new AccountManager(new AccountDatabaseHandler(dbManager));
        // Register a user
        try {
            accountManager.registerNewAdministratorAccount("admin_zong@vrc.ca", "adminPass");
        } catch (InvalidInputException | GeneralUserAccountException | AccountRegistrationException e) {
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
            userSessionId = accountManager.login("admin_zong@vrc.ca", "adminPass");
        } catch (InvalidInputException | NoSuchUserException | InvalidCredentialsException | GeneralUserAccountException e) {
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
