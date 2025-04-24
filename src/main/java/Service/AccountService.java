package Service;

import Model.Account;
import DAO.AccountDAO;

// Service class to handle account-related operations
public class AccountService {

    // DAO instance for interacting with the account database
    private AccountDAO accountDAO;

    // Default constructor initializing the AccountDAO
    public AccountService() {
        accountDAO = new AccountDAO();
    }

    // Constructor for dependency injection of AccountDAO
    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    // Calls the DAO method to insert a new account, returning the account object without account_id
    public Account addAccount(Account account) {
        return accountDAO.insertAccount(account.getUsername(), account.getPassword());
    }

    // Calls the DAO method to log in an account with the provided username and password
    public Account loginAccount(Account account) {
        return accountDAO.loginAccount(account.getUsername(), account.getPassword());
    }
}
