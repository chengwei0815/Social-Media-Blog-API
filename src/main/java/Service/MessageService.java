package Service;

import Model.Message;
import DAO.MessageDAO;

import java.util.List;

// Message in the Service Layer
public class MessageService {
    // Instance to access Message database
    private MessageDAO messageDAO;

    // Constructor
    public MessageService() {
        messageDAO = new MessageDAO();
    }

    // Copy Constructor
    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    // Return the result of Message's DAO layer method for createMessage for given message object (no message_id)
    public Message addMessage(Message message) {
        return messageDAO.createMessage(message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
    }

    // Return the result of Message's DAO layer method for getAllMessages
    public List<Message> getAllMessages() {
        return messageDAO.getAllMessages();
    }

    // Return the result of Message's DAO layer method for getMessageByMessageID for given message_id
    public Message getMessage(int message_id) {
        return messageDAO.getMessageByMessageID(message_id);
    }

    // Return the result of Message's DAO layer method for deleteMessageByMessageID if given message_id exists in the database
    public Message deleteMessage(int message_id) {
        Message message = messageDAO.getMessageByMessageID(message_id);
        
        if (message != null) {
            messageDAO.deleteMessageByMessageID(message_id);
        }

        return message;
    }

    // Return the result of Message's DAO layer method for updateMessageByMessageID for given message_id
    public Message modifyMessage(String message_text, int message_id) {
        return messageDAO.updateMessageByMessageID(message_text, message_id) ? messageDAO.getMessageByMessageID(message_id) : null;
    }

    // Return the result of Message's DAO layer method for getAllMessageByUserID for given user_id
    public List<Message> getMessages(int user_id) {
        return messageDAO.getAllMessageByUserID(user_id);
    }
}