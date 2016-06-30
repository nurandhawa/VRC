package ca.sfu.teambeta.core.exceptions;

/**
 * Primary use:
 *  This exception is thrown when no session exists for a
 *  specified email
 *
 */
public class NoSuchSessionException extends Exception {

    // MARK: - Constructors
    public NoSuchSessionException() {
        super();
    }

    public NoSuchSessionException(String message) {
        super(message);
    }

    public NoSuchSessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchSessionException(Throwable cause) {
        super(cause);
    }

}
