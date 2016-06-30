package ca.sfu.teambeta.core.exceptions;

/**
 * Primary use:
 *  This exception is thrown when the user login credentials passed to the system are
 *  invalid. IE: Wrong password
 *
 */

public class InvalidCredentialsException extends Exception {

    // MARK: - Constructors
    public InvalidCredentialsException() {
        super();
    }

    public InvalidCredentialsException(String message) {
        super(message);
    }

    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidCredentialsException(Throwable cause) {
        super(cause);
    }

}
