package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.exceptions.InvalidUserInputException;
import org.apache.commons.lang3.StringUtils;

/**
 * This class holds methods to validate input that is passed in
 * from the front-end.
 *
 */
public class InputValidator {
    private static final int MAX_EMAIL_LENGTH = 30;
    private static final int MAX_PASSWORD_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int PHONE_NUMBER_LENGTH = 10;


    public static void checkEmailFormat(String email) throws InvalidUserInputException {
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

    public static void checkPasswordFormat(String password) throws InvalidUserInputException {
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

    public static void checkPhoneNumberFormat(String phoneNumber) throws InvalidUserInputException {
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
}
