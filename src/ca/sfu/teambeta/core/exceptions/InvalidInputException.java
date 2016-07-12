package ca.sfu.teambeta.core.exceptions;

/**
 * Primary use:
 *  This exception is thrown when invalid input is passed to the core methods
 *  within AccountManager.java. IE: email address missing the @ symbol, phone
 *  number being too long or not being all digits, etc.
 *
 * Note:
 *  While InvalidParameterException could have been used, that is a runtime exception
 *  thus, strict exception checking must be enforced in AccountManager.java
 *  (and within the consequent classes which will handle this) due to it's critical
 *  functionality.
 */
public class InvalidInputException extends Exception {

    // MARK: - Constructors
    public InvalidInputException() {
        super();
    }

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputException(Throwable cause) {
        super(cause);
    }

}
