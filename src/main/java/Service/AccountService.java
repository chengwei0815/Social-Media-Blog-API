package Service;

import Model.Account;
import DAO.AccountDAO;

// Account in the Service Layer
public class AccountService {
    // Instance to access Account database
    private AccountDAO accountDAO;

    // Constructor
    public AccountService() {
        accountDAO = new AccountDAO();
    }

    // Copy Constructor
    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    // Return the result of Account's DAO layer method for insertAccount for given account object (no account_id)
    public Account addAccount(Account account) {
        return accountDAO.insertAccount(account.getUsername(), account.getPassword());
    }

    // Return the result of Account's DAO layer method for loginAccount for given account object
    public Account loginAccount(Account account) {
        return accountDAO.loginAccount(account.getUsername(), account.getPassword());
    }
}