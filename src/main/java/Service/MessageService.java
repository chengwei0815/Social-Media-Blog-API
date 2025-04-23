package Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DAO.MessageDao;
import DAO.DaoException;
import Model.Account;
import Model.Message;
import io.javalin.http.NotFoundResponse;

public class MessageService {
    private MessageDao messageDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageService.class);
    private static final String DB_ACCESS_ERROR_MSG = "Error accessing the database";

    // Default constructor initializing the MessageDao
    public MessageService() {
        messageDao = new MessageDao();
    }

    // Constructor that allows dependency injection of MessageDao
    public MessageService(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    // Fetch a message by its ID
    public Optional<Message> getMessageById(int id) {
        LOGGER.info("Fetching message with ID: {}", id);
        try {
            Optional<Message> message = messageDao.getById(id); // Fetch message from DAO by ID
            if (!message.isPresent()) {
                throw new ServiceException("Message not found"); // Throw exception if message is not found
            }
            LOGGER.info("Fetched message: {}", message.orElse(null)); // Log the fetched message
            return message;
        } catch (DaoException e) {
            throw new ServiceException(DB_ACCESS_ERROR_MSG, e);
        }
    }

    // Get all messages
    public List<Message> getAllMessages() {
        LOGGER.info("Fetching all messages");
        try {
            List<Message> messages = messageDao.getAll(); // Fetch all messages
            LOGGER.info("Fetched {} messages", messages.size()); // Log the number of fetched messages
            return messages;
        } catch (DaoException e) {
            throw new ServiceException(DB_ACCESS_ERROR_MSG, e);
        }
    }

    // Get messages posted by a specific account ID
    public List<Message> getMessagesByAccountId(int accountId) {
        LOGGER.info("Fetching messages posted by account ID: {}", accountId);
        try {
            List<Message> messages = messageDao.getMessagesByAccountId(accountId); // Fetch messages by account ID
            LOGGER.info("Fetched {} messages", messages.size()); // Log the number of fetched messages
            return messages;
        } catch (DaoException e) {
            throw new ServiceException(DB_ACCESS_ERROR_MSG, e);
        }
    }

    // Create a new message
    public Message createMessage(Message message, Optional<Account> account) {
        LOGGER.info("Creating message: {}", message);

        if (!account.isPresent()) {
            throw new ServiceException("Account must exist when posting a new message"); // Ensure account exists
        }
        validateMessage(message); // Validate the message content

        checkAccountPermission(account.get(), message.getPosted_by()); // Ensure account has permission to post message
        try {
            Message createdMessage = messageDao.insert(message); // Insert the new message into the database
            LOGGER.info("Created message: {}", createdMessage); // Log created message
            return createdMessage;
        } catch (DaoException e) {
            throw new ServiceException(DB_ACCESS_ERROR_MSG, e);
        }
    }

    // Update an existing message
    public Message updateMessage(Message message) {
        LOGGER.info("Updating message: {}", message.getMessage_id());

        Optional<Message> retrievedMessage = this.getMessageById(message.getMessage_id()); // Fetch the existing message

        if (!retrievedMessage.isPresent()) {
            throw new ServiceException("Message not found"); // Throw exception if message is not found
        }
        retrievedMessage.get().setMessage_text(message.getMessage_text()); // Update the message text
        validateMessage(retrievedMessage.get()); // Validate the updated message content
        try {
            messageDao.update(retrievedMessage.get()); // Update the message in the database
            LOGGER.info("Updated message: {}", message); // Log updated message
            return retrievedMessage.get();
        } catch (DaoException e) {
            throw new ServiceException(DB_ACCESS_ERROR_MSG, e);
        }
    }

    // Delete a message
    public void deleteMessage(Message message) {
        LOGGER.info("Deleting message: {}", message);
        try {
            boolean hasDeletedMessage = messageDao.delete(message); // Delete the message from the database
            if (hasDeletedMessage) {
                LOGGER.info("Deleted message {}", message); // Log successful deletion
            } else {
                throw new NotFoundResponse("Message to delete not found"); // Throw error if message not found
            }
        } catch (DaoException e) {
            throw new ServiceException(DB_ACCESS_ERROR_MSG, e);
        }
    }

    // Validate the message content
    private void validateMessage(Message message) {
        LOGGER.info("Validating message: {}", message);
        if (message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()) {
            throw new ServiceException("Message text cannot be null or empty"); // Check for empty message text
        }
        if (message.getMessage_text().length() > 254) {
            throw new ServiceException("Message text cannot exceed 254 characters"); // Check for message length limit
        }
    }

    // Check if the account has permission to modify the message
    private void checkAccountPermission(Account account, int postedBy) {
        LOGGER.info("Checking account permissions for messages");
        if (account.getAccount_id() != postedBy) {
            throw new ServiceException("Account not authorized to modify this message"); // Ensure the account is authorized
        }
    }
}