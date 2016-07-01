package ca.sfu.teambeta.core.exceptions;

/**
 * Primary use:
 *  This exception is used as an exception that is rethrown in AccountManager.java to
 *  abstract away the exceptions that are thrown by the password hashing methods, as
 *  they encounter internal errors.
 *
 */

public class InternalHashingException extends Exception {

    // MARK: - Constructors
    public InternalHashingException() {
        super();
    }

    public InternalHashingException(String message) {
        super(message);
    }

    public InternalHashingException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalHashingException(Throwable cause) {
        super(cause);
    }

}
