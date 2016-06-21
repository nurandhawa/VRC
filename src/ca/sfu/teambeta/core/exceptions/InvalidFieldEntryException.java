package ca.sfu.teambeta.core.exceptions;

/**
 * Primary use:
 *  This exception is thrown when an invalid input is passed to the core methods
 *  within AccountManager.java.
 *
 * Note:
 *  While InvalidParameterException could have been used, it is a runtime exception
 *  thus, strict exception checking must be enforced in AccountManager.java
 *  (and consequent classes which will handle this) due to it's critical functionality.
 */
public class InvalidFieldEntryException extends Exception {

    // MARK: - Constructors
    public InvalidFieldEntryException() {
        super();
    }

    public InvalidFieldEntryException(String message) {
        super(message);
    }

    public InvalidFieldEntryException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidFieldEntryException(Throwable cause) {
        super(cause);
    }

}
