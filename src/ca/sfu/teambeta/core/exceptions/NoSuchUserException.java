package ca.sfu.teambeta.core.exceptions;

/**
 * Primary use:
 *  This exception is thrown when no user matching the inputted email exists in the database.
 */
public class NoSuchUserException extends Exception {

    // MARK: - Constructors
    public NoSuchUserException() {
        super();
    }

    public NoSuchUserException(String message) {
        super(message);
    }

    public NoSuchUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchUserException(Throwable cause) {
        super(cause);
    }

}
