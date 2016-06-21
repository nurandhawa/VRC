package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.PasswordHash;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.InternalHashingException;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;

import java.security.InvalidParameterException;

/**
 * AccountManager handles
 * - User Login
 * - User Registration
 *
 * Coming Soon to a VRC Server Near You
 * - Password Reset
 * -- Via Email
 * -- Via Text Message (Pass back last 2 digits of phone)
 * -- Via Security Questions (Hash answer)
 *
 * - Anonymous Users
 *
 */

public class AccountManager {
    public static final int MAX_EMAIL_LENGTH = 30;
    private static final int MAX_PASSWORD_LENGTH = 20;

    private static final String DEMO_EMAIL = "admin@vrc.com";
    private static final String DEMO_PASSWORD = "demoPass";


    // MARK: - The Core Login/Registration Methods
    public static boolean login(String email, String password) throws InternalHashingException, NoSuchUserException {
        validateUserInput(email, password);

        // Authenticate the user
        boolean successfullyAuthenticated = authenticateUser(email, password);

        // TODO: Create a session. For now return a boolean.
        return successfullyAuthenticated;
    }

    public static boolean register(String email, String password) throws InternalHashingException {
        validateUserInput(email, password);

        checkIfUserExists(email);

        // Hash the user's password
        String passwordHash;

        try {
            passwordHash = PasswordHash.createHash(password);
        } catch (Exception e) {
            // Rethrow a simpler Exception following from the abstract Exceptions thrown by ".createHash()"
            throw new InternalHashingException("Could not create password hash");
        }

        boolean success = saveNewUser(email, passwordHash);

        return success;
    }


    // MARK: - Helper Methods
    private static boolean authenticateUser(String email, String password) throws InternalHashingException, NoSuchUserException {
        // Get the user from the database
        User user = getUserFromDB(email);

        // Validate the entered password with the hash
        boolean isPasswordCorrect;

        try {
            isPasswordCorrect = PasswordHash.validatePassword(password, user.getPasswordHash());
        } catch (Exception e) {
            // Rethrow a simpler Exception following from the abstract Exceptions thrown by ".validatePassword()"
            throw new InternalHashingException("Password cannot be determined as correct or incorrect, " +
                    "please contact an administrator.");
        }

        return isPasswordCorrect;
    }


    // MARK: - Database Methods
    private static User getUserFromDB(String email) throws NoSuchUserException, InternalHashingException {
        // TODO: Check the real MySQL Database if the user exists
        // TODO: Login should only be allowed to read - fix DB permissions

        if (!email.equals(DEMO_EMAIL)) {
            // Note: In the case the username is not found in the database, throw an Exception,
            //  and let the AppController handle that. We can choose to handle this specifically or display a
            //  generic error message for security reasons.

            throw new NoSuchUserException();
        }

        String demoUsername = DEMO_EMAIL;
        String demoPassword = DEMO_PASSWORD;
        String demoPasswordHash;

        try {
            demoPasswordHash = PasswordHash.createHash(demoPassword);
        } catch (Exception e) {
            // TODO: Delete this exception from function header
            // Rethrow a simpler Exception following from the abstract Exceptions thrown by ".createHash()"
            throw new InternalHashingException("Could not create password hash");
        }

        User demoUser = new User(demoUsername, demoPasswordHash);

        return demoUser;
    }

    private static boolean saveNewUser(String email, String passwordHash) {
        // Check if the user exists, or better yet if the database does so then handle that.
        return true;
    }

    private static boolean checkIfUserExists(String email) {

        return false;
    }


    // MARK: - Miscellaneous Methods
    private static boolean isValidEmail(String email) {
        // See citations.txt for source

        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        return email.matches(emailPattern);
    }

    private static void validateUserInput(String email, String password) throws InvalidParameterException {
        // Check that the input is valid
        boolean emailTooLong = email.length() > MAX_EMAIL_LENGTH;
        boolean passwordTooLong = password.length() > MAX_PASSWORD_LENGTH;
        boolean emailNotValid = !isValidEmail(email);

        if (email.isEmpty() || password.isEmpty()) {
            throw new InvalidParameterException("The email or password field cannot be empty");
        } else if (emailTooLong || passwordTooLong) {
            throw new InvalidParameterException("The email or password exceed the allowed length");
        } else if (emailNotValid) {
            throw new InvalidParameterException("The email address is not in valid format");
        }
    }


    // MARK: - Main Function
    public static void main(String[] args) {
        boolean authenticated = false;

        try {
            authenticated = login("a", "d");
        } catch (InternalHashingException e) {
            e.printStackTrace();
            return;
        } catch (NoSuchUserException e) {
            e.printStackTrace();
            return;
        }
        
        System.out.println("User Authentication Status: " + authenticated);
    }
}
