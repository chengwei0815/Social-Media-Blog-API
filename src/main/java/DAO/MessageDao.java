package DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Model.Message;
import Util.ConnectionUtil;

public class MessageDao implements FirstDao<Message> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageDao.class);

    // Handles SQL exceptions by logging detailed error information and throwing a DaoException
    private void handleSQLException(SQLException e, String sql, String errorMessage) {
        LOGGER.error("SQLException Details: {}", e.getMessage());
        LOGGER.error("SQL State: {}", e.getSQLState());
        LOGGER.error("Error Code: {}", e.getErrorCode());
        LOGGER.error("SQL: {}", sql);
        throw new DaoException(errorMessage, e);
    }

    @Override
    public Optional<Message> getById(int id) {
        String sql = "SELECT * FROM message WHERE message_id = ?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); // Set the message ID parameter
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMessage(rs)); // Map result to a Message object
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while retrieving the message with id: " + id);
        }
        return Optional.empty(); // Return empty if no message is found
    }

    @Override
    public List<Message> getAll() {
        String sql = "SELECT * FROM message";
        Connection conn = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapResultSetToMessage(rs)); // Add each result to the list
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while retrieving all messages");
        }
        return messages; // Return the list of messages
    }

    public List<Message> getMessagesByAccountId(int accountId) {
        String sql = "SELECT * FROM message WHERE posted_by = ?";
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId); // Set the account ID parameter
            try (ResultSet rs = ps.executeQuery()) {
                return mapResultSetToList(rs); // Map results to a list of messages
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while retrieving messages by account ID: " + accountId);
        }
        return new ArrayList<>(); // Return an empty list if no messages are found
    }

    @Override
    public Message insert(Message message) {
        String sql = "INSERT INTO message(posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
        Connection conn = ConnectionUtil.getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, message.getPosted_by()); // Set the posted_by parameter
            ps.setString(2, message.getMessage_text()); // Set the message_text parameter
            ps.setLong(3, message.getTime_posted_epoch()); // Set the time_posted_epoch parameter

            ps.executeUpdate(); // Execute the insert query

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1); // Retrieve the generated ID
                    return new Message(generatedId, message.getPosted_by(), message.getMessage_text(),
                            message.getTime_posted_epoch());
                } else {
                    throw new DaoException("Failed to insert message, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while inserting a message");
        }
        throw new DaoException("Failed to insert message");
    }

    @Override
    public boolean update(Message message) {
        String sql = "UPDATE message SET posted_by = ?, message_text = ?, time_posted_epoch = ? WHERE message_id = ?";
        int rowsUpdated = 0;
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, message.getPosted_by()); // Set the posted_by parameter
            ps.setString(2, message.getMessage_text()); // Set the message_text parameter
            ps.setLong(3, message.getTime_posted_epoch()); // Set the time_posted_epoch parameter
            ps.setInt(4, message.getMessage_id()); // Set the message_id parameter
            rowsUpdated = ps.executeUpdate(); // Execute the update query
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while updating the message with id: " + message.getMessage_id());
        }
        return rowsUpdated > 0; // Return true if the update was successful
    }

    @Override
    public boolean delete(Message message) {
        String sql = "DELETE FROM message WHERE message_id = ?";
        int rowsUpdated = 0;
        Connection conn = ConnectionUtil.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, message.getMessage_id()); // Set the message_id parameter
            rowsUpdated = ps.executeUpdate(); // Execute the delete query
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while deleting the message with id: " + message.getMessage_id());
        }
        return rowsUpdated > 0; // Return true if the deletion was successful
    }

    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        int messageId = rs.getInt("message_id"); // Retrieve message_id from the result set
        int postedBy = rs.getInt("posted_by"); // Retrieve posted_by from the result set
        String messageText = rs.getString("message_text"); // Retrieve message_text from the result set
        long timePostedEpoch = rs.getLong("time_posted_epoch"); // Retrieve time_posted_epoch from the result set
        return new Message(messageId, postedBy, messageText, timePostedEpoch); // Map to a Message object
    }

    private List<Message> mapResultSetToList(ResultSet rs) throws SQLException {
        List<Message> messages = new ArrayList<>();
        while (rs.next()) {
            messages.add(mapResultSetToMessage(rs)); // Map each row to a Message object
        }
        return messages; // Return the list of messages
    }
}