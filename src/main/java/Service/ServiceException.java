package Service;

/**
 * Custom exception class for handling service-related errors.
 * This exception extends RuntimeException, making it an unchecked exception.
 */
public class ServiceException extends RuntimeException {
    // Constructor to create a ServiceException with a specific message.
    public ServiceException(String message) {
        super(message); // Pass the message to the superclass constructor
    }
    // Constructor to create a ServiceException caused by another exception.
    public ServiceException(Throwable cause) {
        super(cause); // Pass the cause to the superclass constructor
    }
    // Constructor to create a ServiceException with both a message and a cause.
    public ServiceException(String message, Throwable cause) {
        super(message, cause); // Pass both message and cause to the superclass constructor
    }
}