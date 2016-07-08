package ca.sfu.teambeta.logic;

import ca.sfu.teambeta.core.exceptions.InvalidInputException;
import org.apache.commons.lang3.StringUtils;

/**
 * This class holds methods to validate input that is passed in
 * from the front-end.
 *
 */

public class InputValidator {
    private static final int MAX_EMAIL_LENGTH = 40;
    private static final int MAX_PASSWORD_LENGTH = 128;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int PHONE_NUMBER_LENGTH = 10;


    // MARK: Core Input Validation Methods
    public static void checkEmailFormat(String email) throws InvalidInputException {
        checkNullOrEmptyString(email);

        // See citations.txt for source of Regex pattern
        String emailPattern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        boolean emailNotValid = !email.matches(emailPattern);

        boolean emailTooLong = email.length() > MAX_EMAIL_LENGTH;


        if (emailTooLong) {
            throw new InvalidInputException("The email address cannot exceed the allowed length of " + MAX_EMAIL_LENGTH +
                    " characters (includes special characters such as '@' and '.')");
        } else if (emailNotValid) {
            throw new InvalidInputException("The email address is not in a valid format");
        }
    }

    public static void checkPasswordFormat(String password) throws InvalidInputException {
        checkNullOrEmptyString(password);

        boolean passwordTooLong = password.length() > MAX_PASSWORD_LENGTH;
        boolean passwordTooShort = password.length() < MIN_PASSWORD_LENGTH;


        if (passwordTooLong) {
            throw new InvalidInputException("The password cannot exceed the allowed length of " + MAX_PASSWORD_LENGTH);
        } else if (passwordTooShort) {
            throw new InvalidInputException("The password cannot be less than " + MIN_PASSWORD_LENGTH + " characters");
        }

    }

    public static void checkPhoneNumberFormat(String phoneNumber) throws InvalidInputException {
        checkNullOrEmptyString(phoneNumber);

        boolean invalidPhoneNumberLength = phoneNumber.length() != PHONE_NUMBER_LENGTH;
        boolean phoneNumberNonNumeric = !StringUtils.isNumeric(phoneNumber);


        if (invalidPhoneNumberLength) {
            throw new InvalidInputException("The phone number field must be " + PHONE_NUMBER_LENGTH
                    + "characters. \nPlease ensure there are no dashes or spaces. IE: '6045551111'");
        } else if (phoneNumberNonNumeric) {
            throw new InvalidInputException("The phone number must only contain digits" +
                    "\nPlease ensure there are no dashes or spaces. IE: '6045551111'");
        }

    }

    public static void checkSessionIdFormat(String sessionId) throws InvalidInputException {
        checkNullOrEmptyString(sessionId);

    }


    // MARK: Helper Methods
    private static void checkNullOrEmptyString(String str) throws InvalidInputException {
        // This helper method will throw an exception if it encounters a
        //  null or empty string as opposed to returning a boolean so
        //  that the calling method doesn't crash down the road as it trys
        //  to call other methods on the String

        if (str == null || str.isEmpty()) {
            throw new InvalidInputException("The input cannot be empty");
        }
    }
}
