package ca.sfu.teambeta.accounts;

import ca.sfu.teambeta.core.User;
import ca.sfu.teambeta.core.exceptions.GeneralUserAccountException;
import ca.sfu.teambeta.core.exceptions.InvalidCredentialsException;
import ca.sfu.teambeta.core.exceptions.NoSuchSessionException;
import ca.sfu.teambeta.core.exceptions.NoSuchUserException;
import com.ja.security.PasswordHash;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class handles:
 * - Authenticating a password reset request via a security question
 * - Resetting the password
 * <p>
 * How to reset a password:
 * When a user correctly answers the security question they are granted a
 * password reset voucher (in our case a unique secure string) which is to be
 * sent along with the users email and new password to the changePassword
 * method.
 */

public class CredentialsManager {
    private static Map<String, PasswordResetVoucherMetadata> passwordResetVouchers;
    private AccountDatabaseHandler accountDbHandler;
    private TokenGenerator tokenGenerator;


    // MARK: Constructor
    public CredentialsManager(AccountDatabaseHandler accountDbHandler) {
        this.accountDbHandler = accountDbHandler;

        passwordResetVouchers = new HashMap<>();
        tokenGenerator = new TokenGenerator();

    }


    // MARK: The Core Methods for Managing a Password
    public void changePassword(String email, String newPassword, String voucherCode)
            throws NoSuchUserException, InvalidCredentialsException, GeneralUserAccountException {
        PasswordResetVoucherMetadata voucherMetadata = passwordResetVouchers.get(voucherCode);

        // Since it's a dictionary, invalid key returns null value
        boolean voucherExists = (voucherMetadata != null);

        if (voucherExists) {
            boolean voucherExpired = voucherMetadata.isExpired();

            if (voucherExpired) {
                passwordResetVouchers.remove(voucherCode);
                throw new InvalidCredentialsException("The password reset session has expired");
            }

            // Hash the user's new password
            String newPasswordHash = getHash(newPassword, "The password could not be changed");

            // Update the user
            User user = accountDbHandler.getUser(email);
            user.setPasswordHash(newPasswordHash);
            accountDbHandler.updateExistingUser(user);

            passwordResetVouchers.remove(voucherCode);
        } else {
            // Voucher does not exist
            throw new InvalidCredentialsException("No password reset session exists");
        }

    }


    public void changePasswordByAdmin(String userEmail, String newUserPassword, String adminSessionId)
            throws NoSuchSessionException, InvalidCredentialsException,
            GeneralUserAccountException, NoSuchUserException {

        boolean isAdmin = UserSessionManager.isAdministratorSession(adminSessionId);

        if (!isAdmin) {
            throw new InvalidCredentialsException("An administrator may only change the password");
        }

        // Hash the new password
        String newPasswordHash = getHash(newUserPassword, "The password cannot be changed");

        User user = accountDbHandler.getUser(userEmail);
        user.setPasswordHash(newPasswordHash);
        accountDbHandler.updateExistingUser(user);

    }


    // MARK: Methods for Resetting a Password (Via a Security Question)
    public String getUserSecurityQuestion(String email) throws NoSuchUserException, GeneralUserAccountException {
        User user = accountDbHandler.getUser(email);

        String securityQuestion = user.getSecurityQuestion();

        if (securityQuestion == null || securityQuestion == "") {
            throw new GeneralUserAccountException("No security question set up");
        }

        return securityQuestion;
    }


    public void setSecurityQuestion(String userEmail, String question, String answer, String sessionId)
            throws NoSuchUserException, GeneralUserAccountException,
            InvalidCredentialsException, NoSuchSessionException {

        // Although a user may be logged in with a valid sessionId, we must check they are
        //  setting their own security question.
        String sessionEmail = UserSessionManager.getEmailFromSessionId(sessionId);
        boolean emailsMatch = userEmail.equalsIgnoreCase(sessionEmail);

        if (!emailsMatch) {
            throw new InvalidCredentialsException("Invalid email address");
        }

        // Hash the user's security question answer
        String answerHash = getHash(answer, "The security question could not be set");

        User user = accountDbHandler.getUser(userEmail);

        user.setSecurityQuestion(question);
        user.setSecurityAnswerHash(answerHash);

        accountDbHandler.updateExistingUser(user);
    }

    public String validateSecurityQuestionAnswer(String email, String securityQuestionAnswer)
            throws InvalidCredentialsException, NoSuchUserException, GeneralUserAccountException {
        User user = accountDbHandler.getUser(email);

        String securityQuestionAnswerHash = user.getSecurityAnswerHash();

        if (securityQuestionAnswerHash == null || securityQuestionAnswerHash == "") {
            throw new GeneralUserAccountException("No security question set");
        }

        boolean correctAnswer = checkHash(
                securityQuestionAnswer, securityQuestionAnswerHash, "The answer could not be validated");

        if (correctAnswer) {
            String voucherCode = tokenGenerator.generateUniqueRandomToken();
            PasswordResetVoucherMetadata voucherMetadata = new PasswordResetVoucherMetadata();

            passwordResetVouchers.put(voucherCode, voucherMetadata);

            return voucherCode;
        } else {
            throw new InvalidCredentialsException("Incorrect security question answer");
        }

    }


    // MARK: General Helper Methods
    // This method encapsulates the hashing of a string while also handling any internal hashing exceptions
    public static String getHash(String stringToHash, String friendlyErrorMessage) throws GeneralUserAccountException {
        if (friendlyErrorMessage == null || friendlyErrorMessage == "") {
            friendlyErrorMessage = "Something went wrong. Please contact an administrator.";
        } else {
            friendlyErrorMessage = friendlyErrorMessage + ". Please contact an administrator.";
        }

        PasswordHash passwordHasher = new PasswordHash();
        String hash;

        try {
            hash = passwordHasher.createHash(stringToHash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // Rethrow a simpler Exception
            throw new GeneralUserAccountException(friendlyErrorMessage);
        }

        return hash;
    }

    // This method encapsulates checking a string against it's hash, while also handling any internal hashing exceptions
    public static boolean checkHash(String originalString, String hashedString, String friendlyErrorMessage)
            throws GeneralUserAccountException {
        if (friendlyErrorMessage == null || friendlyErrorMessage == "") {
            friendlyErrorMessage = "Validation cannot be done at this done. Please contact the administrator.";
        }

        PasswordHash passwordHasher = new PasswordHash();
        boolean matches;

        try {
            matches = passwordHasher.validatePassword(originalString, hashedString);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // Rethrow a simpler Exception
            throw new GeneralUserAccountException(friendlyErrorMessage);
        }

        return matches;
    }

}
