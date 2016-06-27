package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.PasswordHash;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * AccountManager handles:
 * - User Login
 * - User Registration
 *
 *
 * Coming Soon:
 * - Password Reset
 * -- Via Email
 * -- Via Text Message (Pass back last 2 digits of phone)
 * -- Via Security Questions (Hash answer)
 *
 * - Logout (If needed: Right now front-end presumed to handle deletion of cookie, thus act as a logout)
 * - Anonymous Users
 *
 *
 *  TODO:
 *  - Reset password via security question. Passback list of questions.
 *  - Caching user data in memory for increased security and faster authentication (when possible)
 *
 */

public class AccountManager {
    public static final int MAX_EMAIL_LENGTH = 30;
    public static final int MAX_PASSWORD_LENGTH = 20;
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int PHONE_NUMBER_LENGTH = 10;

    private static final String DEMO_EMAIL = "admin@vrc.com";
    private static final String DEMO_PASSWORD = "demoPass";
    private static ArrayList<User> dummyUsers = new ArrayList<User>();


    // MARK: - The Core Login/Registration Methods
    public static String login(String email, String password) throws InternalHashingException, NoSuchUserException, InvalidUserInputException, InvalidCredentialsException {
        validateEmailFormat(email);
        validatePasswordFormat(password);

        // Authenticate and if successful get the user from the database
        User user = authenticateUser(email, password);

        // Create a session for the user
        String sessionID = UserSessionManager.createNewSession(user);

        return sessionID;
    }

    public static void logout(String email) {

    }

    public static void register(String email, String password) throws InternalHashingException, InvalidUserInputException, AccountRegistrationException {
        validateEmailFormat(email);
        validatePasswordFormat(password);

        // Hash the user's password
        String passwordHash;

        try {
            passwordHash = PasswordHash.createHash(password);
        } catch (Exception e) {
            // Rethrow a simpler Exception following from the abstract Exceptions thrown by ".createHash()"
            throw new InternalHashingException("Could not create password hash, " +
                    "please contact an administrator if the problem persists");
        }

        User newUser = new User(email, passwordHash);

        // Save the user to the database, no Exception marks success
        saveNewUser(newUser);

    }


    // MARK: - Helper Methods
    private static User authenticateUser(String email, String password) throws InternalHashingException, NoSuchUserException, InvalidCredentialsException {
        // Get the user from the database
        User user = getUserFromDB(email);

        // Validate the entered password with the hash
        boolean isPasswordCorrect;

        try {
            isPasswordCorrect = PasswordHash.validatePassword(password, user.getPasswordHash());
        } catch (Exception e) {
            // Rethrow a simpler Exception following from the abstract Exceptions thrown by ".validatePassword()"
            throw new InternalHashingException("Password cannot be determined as correct or incorrect, " +
                    "please contact an administrator if this problem persists");
        }

        if (!isPasswordCorrect) {
            throw new InvalidCredentialsException("Incorrect password");
        } else {
            return user;
        }

    }


    // MARK: - Database Methods
    private static User getUserFromDB(String email) throws NoSuchUserException {
        // Note: Login should use a read-only database user.

        for (User user : dummyUsers) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }

        throw new NoSuchUserException("The user '" + email + "' does not exist");

    }

    private static void saveNewUser(User newUser) throws AccountRegistrationException {
        for (User user : dummyUsers) {
            if (user.getEmail().equals(newUser.getEmail())) {
                throw new AccountRegistrationException("The email '" + newUser.getEmail() + "' is already in use");
            }
        }

        dummyUsers.add(newUser);

    }


    // MARK: - Miscellaneous Methods
    private static void validatePhoneNumberFormat(String phoneNumber) throws InvalidUserInputException {
        boolean invalidPhoneNumberLength = phoneNumber.length() != PHONE_NUMBER_LENGTH;
        boolean phoneNumberNonNumeric = !StringUtils.isNumeric(phoneNumber);

        if (invalidPhoneNumberLength) {
            throw new InvalidUserInputException("The phone number field must be empty or of length " + PHONE_NUMBER_LENGTH
                    + "\nPlease ensure there are no dashs or spaces. IE: '6045551111'");
        } else if (phoneNumberNonNumeric) {
            throw new InvalidUserInputException("The phone number must only contain digits" +
                    "\nPlease ensure there are no dashs or spaces. IE: '6045551111'");
        }

    }

    private static void validateEmailFormat(String email) throws InvalidUserInputException {
        // Check that the input is valid
        boolean emailTooLong = email.length() > MAX_EMAIL_LENGTH;

        // See citations.txt for source for Regex pattern
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        boolean emailNotValid = !email.matches(emailPattern);

        if (email.isEmpty()) {
            throw new InvalidUserInputException("The email field cannot be empty");
        } else if (emailTooLong) {
            throw new InvalidUserInputException("The email address cannot exceed the allowed length of " + MAX_EMAIL_LENGTH +
                    " characters (includes special characters such as '@' and '.')");
        } else if (emailNotValid) {
            throw new InvalidUserInputException("The email address is not in a valid format");
        }

    }

    private static void validatePasswordFormat(String password) throws InvalidUserInputException {
        // Check that the input is valid
        boolean passwordTooLong = password.length() > MAX_PASSWORD_LENGTH;
        boolean passwordTooShort = password.length() < MIN_PASSWORD_LENGTH;

        if (password.isEmpty()) {
            throw new InvalidUserInputException("The password field cannot be empty");
        } else if (passwordTooLong) {
            throw new InvalidUserInputException("The password cannot exceed the allowed length of " + MAX_PASSWORD_LENGTH);
        } else if (passwordTooShort) {
            throw new InvalidUserInputException("The password cannot be less than " + MIN_PASSWORD_LENGTH + " characters");
        }

    }


    // MARK: - Main Function (Quick and dirty testing for now - will be refactored into tests)
    public static void main(String[] args) {
        // Register a user
        try {
            register(DEMO_EMAIL, DEMO_PASSWORD);
        } catch (InternalHashingException e) {
            System.out.println(e.getMessage());
            return;
        } catch (InvalidUserInputException e) {
            System.out.println(e.getMessage());
            return;
        } catch (AccountRegistrationException e) {
            System.out.println(e.getMessage());
            return;
        }


        // Login a user
        String userSessionId = "";

        try {
            userSessionId = login("admin@vrc.com", "demoPass");
        } catch (InternalHashingException e) {
            System.out.println(e.getMessage());
            return;
        } catch (NoSuchUserException e) {
            System.out.println("No such user");
            return;
        } catch (InvalidUserInputException e) {
            System.out.println(e.getMessage());
            return;
        } catch (InvalidCredentialsException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("User Session ID: " + userSessionId);
    }
}
