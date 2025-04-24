package Service;

import Model.Message;
import DAO.MessageDAO;

import java.util.List;

// Service class to handle message-related operations
public class MessageService {

    // DAO instance for interacting with the message database
    private MessageDAO messageDAO;

    // Default constructor initializing the MessageDAO
    public MessageService() {
        messageDAO = new MessageDAO();
    }

    // Constructor for dependency injection of MessageDAO
    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    // Calls the DAO method to create a new message and returns the created message object
    public Message addMessage(Message message) {
        return messageDAO.createMessage(message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
    }

    // Calls the DAO method to retrieve all messages from the database
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    // Calls the DAO method to retrieve a message by its message_id
    public Message getMessage(int message_id) {
        return messageDAO.getMessageByMessageID(message_id);
    }

    // Calls the DAO method to delete a message by its message_id if the message exists
    public Message deleteMessage(int message_id) {
        Message message = messageDAO.getMessageByMessageID(message_id);
        
        if (message != null) {
            messageDAO.deleteMessageByMessageID(message_id);
        }

        return message;
    }

    // Calls the DAO method to update a message by its message_id and returns the updated message
    public Message modifyMessage(String message_text, int message_id) {
        return messageDAO.updateMessageByMessageID(message_text, message_id) ? messageDAO.getMessageByMessageID(message_id) : null;
    }

    // Calls the DAO method to retrieve all messages for a specific user by their user_id
    public List<Message> getMessages(int user_id) {
        return messageDAO.getAllMessageByUserID(user_id);
    }
}
