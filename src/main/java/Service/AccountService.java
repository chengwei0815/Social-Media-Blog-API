package Service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import DAO.AccountDao;
import DAO.DaoException;
import Model.Account;

public class AccountService {
    private AccountDao accountDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    // Default constructor initializing the AccountDao
    public AccountService() {
        accountDao = new AccountDao();
    }

    // Constructor that allows dependency injection of AccountDao
    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    // Fetch account by its ID
    public Optional<Account> getAccountById(int id) {
        LOGGER.info("Fetching account with ID: {}", id);
        try {
            Optional<Account> account = accountDao.getById(id); // Call DAO to fetch account by ID
            LOGGER.info("Fetched account: {}", account.orElse(null)); // Log the fetched account
            return account;
        } catch (DaoException e) {
            throw new ServiceException("Exception occurred while fetching account", e);
        }
    }

    // Get all accounts from the database
    public List<Account> getAllAccounts() {
        LOGGER.info("Fetching all accounts");
        try {
            List<Account> accounts = accountDao.getAll(); // Fetch all accounts
            LOGGER.info("Fetched {} accounts", accounts.size()); // Log the count of accounts fetched
            return accounts;
        } catch (DaoException e) {
            throw new ServiceException("Exception occurred while fetching accounts", e);
        }
    }

    // Find an account by its username
    public Optional<Account> findAccountByUsername(String username) {
        LOGGER.info("Finding account by username: {}", username);
        try {
            Optional<Account> account = accountDao.findAccountByUsername(username); // Call DAO to find account
            LOGGER.info("Found account: {}", account.orElse(null)); // Log found account or null if not found
            return account;
        } catch (DaoException e) {
            throw new ServiceException("Exception occurred while finding account by username " + username, e);
        }
    }

    // Validate login credentials
    public Optional<Account> validateLogin(Account account) {
        LOGGER.info("Validating login for username: {}", account.getUsername());
        try {
            Optional<Account> validatedAccount = accountDao.validateLogin(account.getUsername(), account.getPassword()); // Validate credentials
            LOGGER.info("Login validation result: {}", validatedAccount.isPresent()); // Log whether login is successful
            return validatedAccount;
        } catch (DaoException e) {
            throw new ServiceException("Exception occurred while validating login", e);
        }
    }

    // Create a new account
    public Account createAccount(Account account) {
        LOGGER.info("Creating account: {}", account);
        try {
            validateAccount(account); // Validate account details
            Optional<Account> searchedAccount = findAccountByUsername(account.getUsername()); // Check if the username already exists
            if (searchedAccount.isPresent()) {
                throw new ServiceException("Account already exists"); // Throw exception if account already exists
            }
            Account createdAccount = accountDao.insert(account); // Insert the new account into the database
            LOGGER.info("Created account: {}", createdAccount); // Log created account
            return createdAccount;
        } catch (DaoException e) {
            throw new ServiceException("Exception occurred while creating account", e);
        }
    }

    // Update an existing account
    public boolean updateAccount(Account account) {
        LOGGER.info("Updating account: {}", account);
        try {
            account.setPassword(account.password); // Ensure the password is updated
            boolean updated = accountDao.update(account); // Update the account in the database
            LOGGER.info("Updated account: {}. Update successful: {}", account, updated); // Log result of update
            return updated;
        } catch (DaoException e) {
            throw new ServiceException("Exception occurred while updating account", e);
        }
    }

    // Delete an account
    public boolean deleteAccount(Account account) {
        LOGGER.info("Deleting account: {}", account);
        if (account.getAccount_id() == 0) {
            throw new IllegalArgumentException("Account ID cannot be null"); // Validate account ID before deletion
        }
        try {
            boolean deleted = accountDao.delete(account); // Delete the account from the database
            LOGGER.info("Deleted account: {}. Deletion successful: {}", account, deleted); // Log deletion result
            return deleted;
        } catch (DaoException e) {
            throw new ServiceException("Exception occurred while deleting account", e);
        }
    }

    // Helper method to validate account data
    private void validateAccount(Account account) {
        LOGGER.info("Validating account: {}", account);
        try {
            String username = account.getUsername().trim();
            String password = account.getPassword().trim();

            // Check for empty username or password
            if (username.isEmpty()) {
                throw new ServiceException("Username cannot be blank");
            }
            if (password.isEmpty()) {
                throw new ServiceException("Password cannot be empty");
            }

            // Ensure password is of minimum length
            if (password.length() < 4) {
                throw new ServiceException("Password must be at least 4 characters long");
            }

            // Ensure the username is unique
            if (accountDao.doesUsernameExist(account.getUsername())) {
                throw new ServiceException("The username must be unique");
            }
        } catch (DaoException e) {
            throw new ServiceException("Exception occurred while validating account", e);
        }
    }

    // Check if an account exists by its ID
    public boolean accountExists(int accountId) {
        LOGGER.info("Checking account existence with ID: {}", accountId);
        try {
            Optional<Account> account = accountDao.getById(accountId); // Check existence of account by ID
            boolean exists = account.isPresent(); // Check if account is present
            LOGGER.info("Account existence: {}", exists); // Log account existence result
            return exists;
        } catch (DaoException e) {
            throw new ServiceException("Exception occurred while checking account existence", e);
        }
    }
}