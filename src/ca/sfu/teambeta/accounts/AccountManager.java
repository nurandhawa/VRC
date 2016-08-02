package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.accounts.Responses.SessionResponse;
import ca.sfu.teambeta.core.Player;
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

    // User's for testing purposes
    public static final String DEMO_EMAIL = "testuser@vrc.ca";
    public static final String DEMO_PASSWORD = "userPass";
    public static final String DEMO_ADMIN_EMAIL = "admin_billy@vrc.ca";
    public static final String DEMO_ADMIN_PASSWORD = "demoPass";


    // MARK: Constructor
    public AccountManager(AccountDatabaseHandler accountDbHandler) {
        this.accountDbHandler = accountDbHandler;

        userRoleHandler = new UserRoleHandler(accountDbHandler);
        tokenGenerator = new TokenGenerator();

        anonymousLoginCodes = new HashMap<>();

    }


    // MARK: Login/Logout Methods
    public SessionResponse login(String email, String password)
            throws InvalidInputException, NoSuchUserException,
            InvalidCredentialsException, GeneralUserAccountException {

        SessionResponse sessionResponse = login(email, password, false);
        return sessionResponse;
    }

    public SessionResponse login(String email, String password, boolean extendedSessionExpiry)
            throws InvalidInputException, NoSuchUserException,
            InvalidCredentialsException, GeneralUserAccountException {

        InputValidator.validateEmailFormat(email);
        InputValidator.validatePasswordFormat(password);

        // Authenticate the password
        // Get the user from the database
        User user = accountDbHandler.getUser(email);

        // Validate the entered password with the hash
        boolean isPasswordCorrect = CredentialsManager
                .checkHash(password, user.getPasswordHash(), "The user cannot be authenticated");

        if (!isPasswordCorrect) {
            throw new InvalidCredentialsException("Incorrect password");
        }


        // Create a session for the user
        UserRole role = userRoleHandler.getUserRole(email);

        return UserSessionManager.createNewSession(email, role, extendedSessionExpiry);
    }

    /**
     * NOT IN USE:
     * This method can be used to extend the functionality of anonymous
     * users in the future. Or simply provide a way to give limited access
     * to users.
     *
     */
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


    // MARK: Registration Methods
    public void registerUserWithPlayer(String email, String password, int playerId,
                                       String securityQuestion, String securityQuestionAnswer)
            throws InvalidInputException, NoSuchUserException,
            GeneralUserAccountException, AccountRegistrationException {

        InputValidator.validateEmailFormat(email);
        InputValidator.validatePasswordFormat(password);

        boolean questionFieldIsEmpty = (securityQuestion == null || securityQuestion == "");
        boolean answerFieldIsEmpty = (securityQuestionAnswer == null || securityQuestionAnswer == "");

        if (questionFieldIsEmpty || answerFieldIsEmpty ) {
            throw new InvalidInputException("Security question fields cannot be empty");
        }

        // Hash the user's password and security question answer
        String passwordHash = CredentialsManager.getHash(password, "Could not create the account");
        String securityQuestionAnswerHash = CredentialsManager.getHash(securityQuestionAnswer, "Could not create the account");

        // Create a new user and assign the fields
        User newUser = new User(email, passwordHash);

        newUser.setSecurityQuestion(securityQuestion);
        newUser.setSecurityAnswerHash(securityQuestionAnswerHash);

        // Get the associated player
        Player player = accountDbHandler.getPlayer(playerId);
        newUser.associatePlayer(player);

        // Finally save the user
        accountDbHandler.saveNewUser(newUser);
    }

    /**
     * @deprecated
     * Moving all registration to use the "registerUserWithPlayer" method
     * to associate players and users. In the future this method will move
     * to become a private method.
     */
    @Deprecated
    public void registerUser(String email, String password)
            throws InvalidInputException, AccountRegistrationException, GeneralUserAccountException {
        InputValidator.validateEmailFormat(email);
        InputValidator.validatePasswordFormat(password);

        // Hash the user's password
        String passwordHash = CredentialsManager.getHash(password, "Could not create the account");

        User newUser = new User(email, passwordHash);

        // Save the user to the database, no Exception marks success
        accountDbHandler.saveNewUser(newUser);
    }

    public void registerNewAdministratorAccount(String email, String password)
            throws InvalidInputException, GeneralUserAccountException, AccountRegistrationException {

        registerUser(email, password);

        try {
            userRoleHandler.setUserRole(email, UserRole.ADMINISTRATOR);
        } catch (NoSuchUserException e) {
            // Although this should not be reached as we are explicitly
            //  saving the user in the "register" method, something sometime
            //  could go wrong with the database and/or have concurrency
            //  issues.

            throw new GeneralUserAccountException("Could not make " + email + " an admin");
        }
    }

    /**
     * NOT IN USE:
     * This method can be used to extend the functionality of anonymous
     * users in the future. Or simply provide a way to give limited access
     * to users.
     *
     */
    public String registerNewAnonymousAccount() throws InvalidInputException, AccountRegistrationException, GeneralUserAccountException {
        String accountName = tokenGenerator.generateUniqueRandomToken();
        String password = tokenGenerator.generateUniqueRandomToken();
        String email = accountName + "@vrc.teambeta";

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

    // NOT IN USE
    public Map<String, String> deleteAllAnonymousUsers() {
        // HashMap is structured <Email, Reason Not Deleted>
        Map<String, String> nonDeletableUsers = new HashMap<>();

        nonDeletableUsers = accountDbHandler.deleteUsersOfRole(UserRole.ANONYMOUS);

        anonymousLoginCodes.clear();
        return nonDeletableUsers;
    }


    // !! TODO: DELETE THE MAIN FUNCTION - WILL BE REFACTORED INTO TESTS !!
    // MARK: - Main Function
/*
    public static void main(String[] args) {
        SessionFactory sessionFactory = DBManager.getTestingSession(true);
        DBManager dbManager = new DBManager(sessionFactory);
        AccountDatabaseHandler adbh = new AccountDatabaseHandler(dbManager);

        AccountManager accountManager = new AccountManager(adbh);

        // Register an admin
        try {
            accountManager.registerNewAdministratorAccount("admin_zong@vrc.ca", "adminPass");
        } catch (InvalidInputException | GeneralUserAccountException | AccountRegistrationException e) {
            System.out.println(e.getMessage());
            return;
        }

        // Register an admin
        try {
            accountManager.registerNewAdministratorAccount("admin_billy@vrc.ca", "adminPass2");
        } catch (InvalidInputException | GeneralUserAccountException | AccountRegistrationException e) {
            System.out.println(e.getMessage());
            return;
        }

        // Register an anonymous user
        try {
            accountManager.registerNewAnonymousAccount();
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
            SessionResponse sesRes = accountManager.login("admin_zong@vrc.ca", "adminPass");
            userSessionId = sesRes.getSessionToken();
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

        List<User> anonymousUsers = adbh.getAllAnonymousUsers();

        for (User user : anonymousUsers) {
            System.out.println(user.getEmail() + ", Role: " + user.getUserRole());
        }
    }
*/

}
