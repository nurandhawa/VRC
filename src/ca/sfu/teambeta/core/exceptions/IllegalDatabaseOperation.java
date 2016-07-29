package ca.sfu.teambeta.core.exceptions;

/**
 * This exception is thrown when a request to the database
 * cannot be fulfilled due to user clearance issues or when
 * the database is asked to do something it shouldn't.
 *
 */

public class IllegalDatabaseOperation extends Exception {

    // MARK: - Constructors
    public IllegalDatabaseOperation() {
        super();
    }

    public IllegalDatabaseOperation(String message) {
        super(message);
    }

    public IllegalDatabaseOperation(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalDatabaseOperation(Throwable cause) {
        super(cause);
    }

}
