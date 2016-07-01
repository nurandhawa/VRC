package ca.sfu.teambeta.core.exceptions;

/**
 * Primary use:
 *  This exception is thrown when a problem arises with user
 *  account registration. IE: User/Email already exists in database
 *
 */
public class AccountRegistrationException extends Exception {

    // MARK: - Constructors
    public AccountRegistrationException() {
        super();
    }

    public AccountRegistrationException(String message) {
        super(message);
    }

    public AccountRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountRegistrationException(Throwable cause) {
        super(cause);
    }

}
