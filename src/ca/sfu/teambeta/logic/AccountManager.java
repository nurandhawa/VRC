package ca.sfu.teambeta.logic;

import com.ja.security.PasswordHash;

import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.AccountRegistrationException;
import ca.sfu.teambeta.core.exceptions.InternalHashingException;
import ca.sfu.teambeta.core.exceptions.InvalidCredentialsException;
import ca.sfu.teambeta.core.exceptions.InvalidInputException;
import ca.sfu.teambeta.core.exceptions.NoSuchSessionException;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;
import ca.sfu.teambeta.persistence.DBManager;

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
    private DBManager dbManager;
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
    public AccountManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }


    // MARK: - The Core Login/Registration Methods
    public String login(String email, String password) throws InternalHashingException, NoSuchUserException,
            InvalidCredentialsException {

        // Authenticate and if successful get the user from the database
        User user = authenticateUser(email, password);

        // Create a session for the user

        return UserSessionManager.createNewSession(user);
    }

    public void logout(String sessionId) throws NoSuchSessionException {
        UserSessionManager.deleteSession(sessionId);
    }

    public void register(String email, String password) throws InternalHashingException,
            InvalidInputException, AccountRegistrationException {
        InputValidator.validateEmailFormat(email);
        InputValidator.validatePasswordFormat(password);

        // Hash the user's password
        String passwordHash;

        try {
            passwordHash = passwordHasher.createHash(password);
        } catch (Exception e) {
            // Rethrow a simpler Exception following
            // from the abstract Exceptions thrown by ".createHash()"
            throw new InternalHashingException(
                    "Could not create password hash, "
                            + "please contact an administrator if the problem persists");
        }

        User newUser = new User(email, passwordHash);

        // Save the user to the database, no Exception marks success
        saveNewUser(newUser);

    }


    // MARK: - Helper Methods
    private User authenticateUser(String email, String password) throws InternalHashingException,
            NoSuchUserException, InvalidCredentialsException {
        // Get the user from the database
        User user = getUserFromDB(email);

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


    // MARK: - Database Methods
    private User getUserFromDB(String email) throws NoSuchUserException {
        /*
        // Uncomment to retrieve users from in-memory
        for (User user : usersInMemory) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }

        throw new NoSuchUserException("The user '" + email + "' does not exist");
        */

        // Get the user from the database
        User user = dbManager.getUser(email);

        if (user == null) {
            throw new NoSuchUserException("The user '" + email + "' does not exist");
        }

        return user;

    }

    private void saveNewUser(User newUser) throws AccountRegistrationException {
        /*
        // Uncomment to save users in-memory
        for (User user : usersInMemory) {
            if (user.getEmail().equals(newUser.getEmail())) {
                throw new AccountRegistrationException(
                "The email '" + newUser.getEmail() + "' is already in use");
            }
        }

        usersInMemory.add(newUser);
        */

        // Add the user to the database
        dbManager.addNewUser(newUser);

    }

    // MARK: - Main Function
    /*
    public static void main(String[] args) {
        SessionFactory sessionFactory = DBManager.getMySQLSession(true);
        DBManager dbManager = new DBManager(sessionFactory);

        AccountManager accountManager = new AccountManager(dbManager);
        // Register a user
        try {
            accountManager.register(DEMO_EMAIL, DEMO_PASSWORD);
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
        try {
            accountManager.register(DEMO_EMAIL, DEMO_PASSWORD);
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



        // Login the user registered above
        String userSessionId = "";

        try {
            userSessionId = accountManager.login(DEMO_EMAIL, DEMO_PASSWORD);
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

        System.out.println("User SessionInformation ID: " + userSessionId);

    }
    */

}
