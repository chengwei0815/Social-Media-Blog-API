package DAO;

import Model.Account;
import Util.ConnectionUtil;

import java.sql.*;

public class AccountDAO {

    // Return account object if record exists containing username
    public Boolean searchUsername(String username) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM account WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, username);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    // Return account object with generated ID for given username and password
    public Account insertAccount(String username, String password) {
        if (!this.searchUsername(username) && !username.isBlank() && password.length() >= 4) {
            try (Connection connection = ConnectionUtil.getConnection()) {
                String sql = "INSERT INTO account(username, password) VALUES(?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                preparedStatement.executeUpdate();
                ResultSet rs = preparedStatement.getGeneratedKeys();
                if(rs.next()){
                    int generated_account_id = (int) rs.getLong(1);
                    return new Account(generated_account_id, username, password);
                }
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    }

    // Return account object for given username and password
    public Account loginAccount(String username, String password) {
        try (Connection connection = ConnectionUtil.getConnection()) {
            String sql = "SELECT * FROM account WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Account account = new Account(rs.getInt("account_id"), 
                                        rs.getString("username"), 
                                        rs.getString("password"));
                return account;
            }
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}