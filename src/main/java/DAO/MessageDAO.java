package DAO;

import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    // Creates and inserts a new message record into the database
    // Returns the created message object if successful
    public Message createMessage(int posted_by, String message_text, long time_posted_epoch) {
        if (!message_text.isBlank() && message_text.length() < 256) {
            try (Connection connection = ConnectionUtil.getConnection()) {
                String sql = "INSERT INTO message(posted_by, message_text, time_posted_epoch) VALUES(?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                
                preparedStatement.setInt(1, posted_by); 
                preparedStatement.setString(2, message_text);
                preparedStatement.setLong(3, time_posted_epoch);

                preparedStatement.executeUpdate();
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if(rs.next()){
                    int generated_message_id = (int) rs.getLong(1);
                    return new Message(generated_message_id, posted_by, message_text, time_posted_epoch);
                }
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    // Retrieves and returns all messages stored in the database
    public List<Message> getAllMessages() {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), 
                                    rs.getInt("posted_by"), 
                                    rs.getString("message_text"), 
                                    rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }

    // Fetches a single message record by its unique message_id
    public Message getMessageByMessageID(int message_id) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, message_id);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), 
                                    rs.getInt("posted_by"), 
                                    rs.getString("message_text"), 
                                    rs.getLong("time_posted_epoch"));
                return message;
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    // Deletes a message record by its unique message_id and returns the deletion status
    public Boolean deleteMessageByMessageID(int message_id) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, message_id);

            return preparedStatement.executeUpdate() > 0;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // Updates the text of a message record for the given message_id
    // Returns true if the update was successful, false otherwise
    public Boolean updateMessageByMessageID(String message_text, int message_id) {
        if (!message_text.isBlank() && message_text.length() < 256) {
            try (Connection connection = ConnectionUtil.getConnection()) {
                String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(1, message_text);
                preparedStatement.setInt(2, message_id);

                return preparedStatement.executeUpdate() > 0;
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    // Retrieves all messages posted by a specific user based on their user_id
    public List<Message> getAllMessageByUserID(int user_id) {
        List<Message> messages = new ArrayList<>();
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, user_id);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), 
                                    rs.getInt("posted_by"), 
                                    rs.getString("message_text"), 
                                    rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return messages;
    }
}
