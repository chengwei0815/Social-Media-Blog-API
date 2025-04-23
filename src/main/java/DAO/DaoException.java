package DAO;

/**
 * Custom exception class for handling data access object (DAO) related errors.
 * This exception is used to wrap SQL exceptions or other issues occurring
 * within the DAO layer, providing more context to the calling code.
 */
public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 1L; // Ensures compatibility during serialization.

    // Constructs a new DaoException with the specified detail message.
    public DaoException(String message) {
        super(message);
    }
    // Constructs a new DaoException with the specified detail message and cause.
    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}