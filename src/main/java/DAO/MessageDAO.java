package DAO;

import Model.Message;

import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    // Return message object for record with posted_by, message_text, and time_posted_epoch
    public Message createMessage(int posted_by, String message_text, long time_posted_epoch) {
        if (!message_text.isBlank() && message_text.length() < 256) {
            try (Connection connection = ConnectionUtil.getConnection()) {
                String sql = "INSERT INTO message(posted_by, message_text, time_posted_epoch) VALUES(?, ?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                
                preparedStatement.setInt(1, posted_by); // rs will be empty if posted_by does not exist
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

    // Return list of message objects containing all messages in the database
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

    // Return message object for record for given message_id
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

    // Return boolean for the deletion of a message given message_id
    public Boolean deleteMessageByMessageID(int message_id) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setInt(1, message_id);

            return preparedStatement.executeUpdate() > 0 ? true : false;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // Return boolean for the update of a message given message_text and message_id
    public Boolean updateMessageByMessageID(String message_text, int message_id) {
        if (!message_text.isBlank() && message_text.length() < 256) {
            try (Connection connection = ConnectionUtil.getConnection()) {
                String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                preparedStatement.setString(1, message_text);
                preparedStatement.setInt(2, message_id);

                return preparedStatement.executeUpdate() > 0 ? true : false;
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }

    // Return list of message objects containing all messages for given user_id
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