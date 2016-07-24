package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.InternalHashingException;
import ca.sfu.teambeta.core.exceptions.InvalidCredentialsException;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;
import ca.sfu.teambeta.persistence.DBManager;
import com.ja.security.PasswordHash;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles:
 * - Authenticating password reset requests
 * - Resetting the password via a security question
 *
 */

public class PasswordManager {
    private List<String> passwordResetTokens;
    private DBManager dbManager;


    // MARK: Constructor
    public PasswordManager(DBManager dbManager) {
        passwordResetTokens = new ArrayList<>();
        this.dbManager = dbManager;
    }


    // MARK: The Core Methods for Managing a Password
    public void changePassword(String email, String newPassword, String token) throws InternalHashingException {
        boolean tokenExists = passwordResetTokens.contains(token);
        boolean tokenExpired = false;

        if (tokenExists) {
            if (tokenExpired) {
                // TODO: Throw exception (Add new exception)
            }

            // Hash the user's password
            PasswordHash passwordHasher = new PasswordHash();
            String newPasswordHash;

            try {
                newPasswordHash = passwordHasher.createHash(newPassword);
            } catch (Exception e) {
                // Rethrow a simpler Exception following
                // from the abstract Exceptions thrown by ".createHash()"
                throw new InternalHashingException(
                        "Could not create password hash, "
                                + "please contact an administrator if the problem persists");
            }

            updateUserPasswordInDB(email, newPasswordHash);

            // TODO: Remove reset token from array
        } else {
            // Token does not exist
            // TODO: Throw exception (Add new exception)
        }
    }


    // MARK: Methods for Resetting a Password Via a Security Question
    public String getUserSecurityQuestion(String email) throws NoSuchUserException {
        User user = getUserFromDB(email);

        String securityQuestion = user.getSecurityQuestion();
        return securityQuestion;
    }

    public String validateSecurityQuestionAnswer(String email, String answer) throws InternalHashingException,
            InvalidCredentialsException, NoSuchUserException {
        User user = getUserFromDB(email);

        String securityAnswerHash = user.getSecurityAnswerHash();

        // TODO: Rename PasswordHash to better accommodate the things we can hash
        PasswordHash passwordHasher = new PasswordHash();
        boolean correctAnswer;

        try {
            correctAnswer = passwordHasher.validatePassword(answer, securityAnswerHash);
        } catch (Exception e) {
            // Rethrow a simpler Exception following from
            // the abstract Exceptions thrown by ".validatePassword()"
            throw new InternalHashingException(
                    "Password cannot be determined as correct or incorrect, "
                            + "please contact an administrator if this problem persists");
        }


        if (correctAnswer) {
            String token = generateRandomToken();

            passwordResetTokens.add(token);
            return token;
        } else {
            throw new InvalidCredentialsException("Incorrect security question answer");
        }

    }


    // MARK: Database Methods
    // TODO: Refactor this duplicate code into a account-related-database class
    private User getUserFromDB(String email) throws NoSuchUserException {
        // Get the user from the database
        User user = dbManager.getUser(email);

        if (user == null) {
            throw new NoSuchUserException("The user '" + email + "' does not exist");
        }

        return user;
    }

    private void updateUserPasswordInDB(String email, String newPasswordHash) {
        // TODO: Get a function to update database user
    }


    // MARK: Helper Methods
    // TODO: Refactor this duplicate code into a "TokenManager" class
    private static String generateRandomToken() {
        // See citations.txt for more information

        // DO NOT CHANGE THESE VALUES
        final int MAX_BIT_LENGTH = 130;
        final int ENCODING_BASE = 32;

        SecureRandom random = new SecureRandom();
        String sessionId = "";

        sessionId = new BigInteger(MAX_BIT_LENGTH, random).toString(ENCODING_BASE);

        /*
        // Make sure we don't have two of the same sessionId's
        while (sessions.get(sessionId) != null) {
            sessionId = new BigInteger(MAX_BIT_LENGTH, random).toString(ENCODING_BASE);
        }
        */

        return sessionId;
    }
}
