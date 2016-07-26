package ca.sfu.teambeta.core.exceptions;

/**
 * Primary use:
 *  This exception covers any general user account related error.
 *  IE: User not having a security question set up.
 *
 */
public class GeneralUserAccountException extends Exception {

    // MARK: - Constructors
    public GeneralUserAccountException() {
        super();
    }

    public GeneralUserAccountException(String message) {
        super(message);
    }

    public GeneralUserAccountException(String message, Throwable cause) {
        super(message, cause);
    }

    public GeneralUserAccountException(Throwable cause) {
        super(cause);
    }

}
