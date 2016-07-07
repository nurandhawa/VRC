package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.PasswordHash;
import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.*;
import ca.sfu.teambeta.persistence.DBManager;
import org.apache.commons.lang3.StringUtils;

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
    private static final int MAX_EMAIL_LENGTH = 30;
    private static final int MAX_PASSWORD_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int PHONE_NUMBER_LENGTH = 10;

    private DBManager dbManager;

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
    public String login(String email, String password) throws InternalHashingException, NoSuchUserException, InvalidUserInputException, InvalidCredentialsException {
        validateEmailFormat(email);
        validatePasswordFormat(password);

        // Authenticate and if successful get the user from the database
        User user = authenticateUser(email, password);

        // Create a session for the user
        String sessionId = UserSessionManager.createNewSession(user);

        return sessionId;
    }

    public void logout(String sessionId) throws NoSuchSessionException {
        UserSessionManager.deleteSession(sessionId);
    }

    public void register(String email, String password) throws InternalHashingException,
            InvalidUserInputException, AccountRegistrationException {
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
    private User authenticateUser(String email, String password) throws InternalHashingException,
            NoSuchUserException, InvalidCredentialsException {
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
                throw new AccountRegistrationException("The email '" + newUser.getEmail() + "' is already in use");
            }
        }

        usersInMemory.add(newUser);
        */

        // Add the user to the database
        dbManager.addNewUser(newUser);

    }


    // MARK: - Miscellaneous Methods
    private void validatePhoneNumberFormat(String phoneNumber) throws InvalidUserInputException {
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

    private void validateEmailFormat(String email) throws InvalidUserInputException {
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

    private void validatePasswordFormat(String password) throws InvalidUserInputException {
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
        } catch (InvalidUserInputException e) {
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
        } catch (InvalidUserInputException e) {
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
        } catch (InvalidUserInputException e) {
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