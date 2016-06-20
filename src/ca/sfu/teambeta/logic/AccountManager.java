package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.PasswordHash;
import ca.sfu.teambeta.core.User;

import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * AccountManager handles
 * - User Login
 * - User Registration
 *
 * Coming Soon to a VRC Server Near You
 * - Password Reset
 *
 */

public class AccountManager {
    private static final int MAX_USERNAME_LENGTH = 25;

    private static final String DEMO_USERNAME = "admin";
    private static final String DEMO_PASSWORD = "demoPass";


    // MARK: - Database Methods
    private static User getUserFromDB(String username) throws Exception {
        // TODO: Check the real MySQL Database if the user exists
        if (!username.equals(DEMO_USERNAME)) {
            // Note: In the case the username is not found in the database, throw an Exception,
            //  and let the front-end handle that. We can choose to handle this specifically or display a
            //  generic error message for security reasons.

            throw new Exception("User does not exist");
        }

        String demoUsername = DEMO_USERNAME;
        String demoPassword = DEMO_PASSWORD;
        String demoPasswordHash;

        try {
            demoPasswordHash = PasswordHash.createHash(demoPassword);
        } catch (Exception e) {
            // Take the abstract Exceptions thrown by ".createHash()" and throw a new simpler-general Exception
            //e.printStackTrace();
            throw new Exception("Error with hashing password");
        }

        User demoUser = new User(demoUsername, demoPasswordHash);

        return demoUser;
    }


    // MARK: - The Core User Methods
    public static boolean authenticateUser(String username, String password) throws Exception {
        // Check that the input is valid
        boolean usernameTooLong = username.length() > MAX_USERNAME_LENGTH;
        boolean usernameNotAlphaNumeric = !isAlphaNumeric(username);

        if (username.isEmpty() || password.isEmpty() || usernameTooLong || usernameNotAlphaNumeric) {
            throw new InvalidParameterException();
        }


        // Grab the user from the database
        User user = getUserFromDB(username);


        // Validate the entered password with the hash
        boolean isPasswordCorrect;

        try {
            isPasswordCorrect = PasswordHash.validatePassword(password, user.getPasswordHash());
        } catch (Exception e) {
            // Take the abstract Exceptions thrown by ".validatePassword()" and throw a new simpler-general Exception
            e.printStackTrace();
            throw new Exception("Error in hashing method. Password cannot be determined as correct or incorrect.");
        }

        return isPasswordCorrect;
    }


    // MARK: - Miscellaneous Methods
    private static boolean isAlphaNumeric(String str) {
        // See citations.txt for source

        String pattern= "^[a-zA-Z0-9]*$";
        return str.matches(pattern);
    }


    // MARK: - Main Function
    public static void main(String[] args) {
        boolean authenticated = false;
        
        try {
            authenticated = authenticateUser("admin", "demoPass");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("AUTH STATUS: " + authenticated);
    }
}
