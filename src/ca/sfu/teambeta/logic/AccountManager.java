package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.PasswordHash;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.InternalHashingException;
import ca.sfu.teambeta.core.exceptions.InvalidFieldEntryException;
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
    public static final int MAX_PASSWORD_LENGTH = 20;
    public static final int PHONE_NUMBER_LENGTH = 10;

    private static final String DEMO_EMAIL = "admin@vrc.com";
    private static final String DEMO_PASSWORD = "demoPass";


    // MARK: - The Core Login/Registration Methods
    public static boolean login(String email, String password) throws InternalHashingException, NoSuchUserException, InvalidFieldEntryException {
        validateEmailFormat(email);
        validatePasswordFormat(password);

        // Authenticate the user
        boolean successfullyAuthenticated = authenticateUser(email, password);

        // TODO: Create a session. For now return a boolean.
        return successfullyAuthenticated;
    }

    public static boolean register(String email, String password) throws InternalHashingException, InvalidFieldEntryException {
        validateEmailFormat(email);
        validatePasswordFormat(password);

        checkIfUserExists(email);

        // Hash the user's password
        String passwordHash;

        try {
            passwordHash = PasswordHash.createHash(password);
        } catch (Exception e) {
            // Rethrow a simpler Exception following from the abstract Exceptions thrown by ".createHash()"
            throw new InternalHashingException("Could not create password hash, " +
                    "please contact an administrator if the problem persists");
        }

        boolean successfullySavedUser = saveNewUser(email, passwordHash);

        return successfullySavedUser;
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
                    "please contact an administrator if the problem persists");
        }

        return isPasswordCorrect;
    }


    // MARK: - Database Methods
    private static User getUserFromDB(String email) throws NoSuchUserException, InternalHashingException {
        // TODO: Check the real MySQL Database if the user exists
        // TODO: Note: Login should use a read-only database user.

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
            // TODO: Delete this exception from function header when real DB is used
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

    private static void validatePhoneNumberFormat(String phoneNumber) throws InvalidFieldEntryException {
        // Let the phone number entered be empty (for no number specified - will help DB) or be the precise length of
        //  a phone number.

        boolean invalidPhoneNumberLength = phoneNumber.length() != 0 || phoneNumber.length() != PHONE_NUMBER_LENGTH;

        if (invalidPhoneNumberLength) {
            throw new InvalidFieldEntryException("The phone number field must be empty or of length " + PHONE_NUMBER_LENGTH
                    + "\nPlease ensure there are no dashs or spaces. IE: '6045551111' ");
        }

    }

    private static void validateEmailFormat(String email) throws InvalidFieldEntryException {
        // Check that the input is valid
        boolean emailTooLong = email.length() > MAX_EMAIL_LENGTH;
        boolean emailNotValid = !isValidEmail(email);

        if (email.isEmpty()) {
            throw new InvalidFieldEntryException("The email field cannot be empty");
        } else if (emailTooLong) {
            throw new InvalidFieldEntryException("The email address cannot exceed the allowed length of " + MAX_EMAIL_LENGTH +
                    " characters (includes special characters such as '@' and '.')");
        } else if (emailNotValid) {
            throw new InvalidFieldEntryException("The email address is not in a valid format");
        }
    }

    private static void validatePasswordFormat(String password) throws InvalidFieldEntryException {
        // Check that the input is valid
        boolean passwordTooLong = password.length() > MAX_PASSWORD_LENGTH;

        if (password.isEmpty()) {
            throw new InvalidFieldEntryException("The password field cannot be empty");
        } else if (passwordTooLong) {
            throw new InvalidFieldEntryException("The password cannot exceed the allowed length of " + MAX_PASSWORD_LENGTH);
        }
    }


    // MARK: - Main Function
    public static void main(String[] args) {
        boolean authenticated = false;

        try {
            authenticated = login("admin@vrc.com", "demoPass");
        } catch (InternalHashingException e) {
            System.out.println(e.getMessage());
            return;
        } catch (NoSuchUserException e) {
            System.out.println("No such user");
            return;
        } catch (InvalidFieldEntryException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("User Authentication Status: " + authenticated);
    }
}
