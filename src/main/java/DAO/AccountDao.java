package DAO;

import Model.Account;
import Util.ConnectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Data Access Object (DAO) for the Account model.
 * This class handles all database operations related to accounts, including retrieval, insertion, updating, 
 * and deletion of account records in the database.
 */
public class AccountDao implements FirstDao<Account> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDao.class);
    /**
     * Logs detailed information about an SQLException and throws a DaoException with a custom error message.
     *
     * @param e            the SQLException that occurred.
     * @param sql          the SQL query that caused the exception.
     * @param errorMessage a custom error message to include in the DaoException.
     */
    private void handleSQLException(SQLException e, String sql, String errorMessage) {
        LOGGER.error("SQLException Details: {}", e.getMessage());
        LOGGER.error("SQL State: {}", e.getSQLState());
        LOGGER.error("Error Code: {}", e.getErrorCode());
        LOGGER.error("SQL: {}", sql);
        throw new DaoException(errorMessage, e);
    }

    // Retrieves an account from the database by its ID.
    @Override
    public Optional<Account> getById(int id) {
        String sql = "SELECT * FROM account WHERE account_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Construct an Account object from the result set and return it.
                    return Optional.of(new Account(
                            rs.getInt("account_id"),
                            rs.getString("username"),
                            rs.getString("password")));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while retrieving the account with ID: " + id);
        }
        return Optional.empty();
    }

    // Retrieves all accounts from the database.
    @Override
    public List<Account> getAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM account";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                // Add each account retrieved from the result set to the accounts list.
                accounts.add(new Account(
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password")));
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while retrieving all accounts");
        }
        return accounts;
    }

    // Searches for an account in the database by username.
    public Optional<Account> findAccountByUsername(String username) {
        String sql = "SELECT * FROM account WHERE username = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // If the account exists, return it as an Optional.
                    return Optional.of(new Account(
                            rs.getInt("account_id"),
                            rs.getString("username"),
                            rs.getString("password")));
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while finding account with username: " + username);
        }
        return Optional.empty();
    }

    // Validates login credentials by checking if the provided username and password match an existing account.
    public Optional<Account> validateLogin(String username, String password) {
        String sql = "SELECT * FROM account WHERE username = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Retrieve account information from the result set.
                    Account account = new Account(
                            rs.getInt("account_id"),
                            rs.getString("username"),
                            rs.getString("password"));

                    // Verify that the provided password matches the account's password.
                    if (Objects.equals(password, account.getPassword())) {
                        return Optional.of(account);
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while validating login for username: " + username);
        }
        return Optional.empty();
    }

    // Checks if a username already exists in the database.
    public boolean doesUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM account WHERE username = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                // Check if the count is greater than zero.
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            handleSQLException(e, sql, "Error while checking if username exists: " + username);
        }
        return false;
    }

    // Inserts a new account into the database.
    @Override
    public Account insert(Account account) {
        String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.executeUpdate();

            // Retrieve the auto-generated account ID.
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Account(generatedKeys.getInt(1), account.getUsername(), account.getPassword());
                } else {
                    throw new DaoException("Creating account failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Creating account failed due to SQL error", e);
        }
    }

    //Updates an existing account in the database.
     
    @Override
    public boolean update(Account account) {
        String sql = "UPDATE account SET username = ?, password = ? WHERE account_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.setInt(3, account.getAccount_id());

            // Check if any rows were affected by the update.
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Updating account failed due to SQL error", e);
        }
    }

    /**
     * Deletes an account from the database.
     */
    @Override
    public boolean delete(Account account) {
        String sql = "DELETE FROM account WHERE account_id = ?";
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, account.getAccount_id());

            // Check if any rows were affected by the deletion.
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException("Deleting account failed due to SQL error", e);
        }
    }
}