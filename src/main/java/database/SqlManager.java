package database;

import database.enums.RecurringRate;
import database.enums.TransactionType;
import database.records.AccountRow;
import database.records.RecurringTransactionRow;
import database.records.TableToRecordAPI;
import database.records.TransactionRow;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Manages SQLite database interactions for a personal finance management system.
 * Includes operations for creating, reading, updating, and deleting accounts, transactions,
 * and recurring transactions.
 */
public class SqlManager {

    /**
     * Singleton instance of the database manager.
     */
    public static final SqlManager DB_CONNECTION = new SqlManager();

    private final Connection conn;

    /**
     * Establishes a connection to the database and initializes tables.
     * Throws a RuntimeException if the connection fails.
     */
    protected SqlManager() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:C:\\JavaProjects\\PersonalFinanceManager\\src\\main\\resources\\finance.db");
            createTables();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    /**
     * Creates necessary database tables if they do not already exist.
     * Tables include `account`, `recurring_transaction`, and `user_transaction`.
     */
    public void createTables() {
        String tables = """
            CREATE TABLE IF NOT EXISTS account(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                balance REAL NOT NULL,
                name TEXT NOT NULL,
                card TEXT,
                bank TEXT
            );
            CREATE TABLE IF NOT EXISTS recurring_transaction(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                acc_id INTEGER REFERENCES account(id),
                start_date REAL NOT NULL,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                recurring_rate TEXT NOT NULL,
                amount REAL NOT NULL,
                last_time_transacted REAL
            );
            CREATE TABLE IF NOT EXISTS user_transaction(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                acc_id INTEGER REFERENCES account(id),
                date REAL NOT NULL,
                name TEXT,
                type TEXT,
                amount REAL NOT NULL,
                recurring BOOLEAN
            );""";
        try (PreparedStatement ps = conn.prepareStatement(tables)) {
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create tables", e);
        }
    }

    /**
     * Retrieves all accounts from the database.
     *
     * @return A list of {@link AccountRow} objects representing the accounts.
     */
    public List<AccountRow> getAccounts() {
        String sql = "SELECT * FROM account;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            return TableToRecordAPI.toAccounts(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve accounts", e);
        }
    }

    /**
     * Retrieves all transactions for specific account IDs.
     *
     * @param acc_ids The account IDs to filter transactions by.
     * @return A list of {@link TransactionRow} objects representing the transactions.
     */
    public List<TransactionRow> getTransactions(int... acc_ids) {
        StringBuilder sql = new StringBuilder("SELECT * FROM user_transaction WHERE acc_id IN (");
        for (int i = 0; i < acc_ids.length; i++) {
            sql.append(acc_ids[i]);
            if (i < acc_ids.length - 1) sql.append(", ");
        }
        sql.append(");");

        try (PreparedStatement ps = conn.prepareStatement(acc_ids.length > 0 ? sql.toString() : "SELECT * FROM user_transaction")) {
            return TableToRecordAPI.toTransactions(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve transactions", e);
        }
    }

    /**
     * Retrieves a single transaction by its ID.
     *
     * @param tran_id The transaction ID.
     * @return A {@link TransactionRow} object representing the transaction.
     */
    public TransactionRow getTransaction(int tran_id) {
        String sql = "SELECT * FROM user_transaction WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tran_id);
            return TableToRecordAPI.toTransactions(ps.executeQuery()).get(0);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve transaction with ID: " + tran_id, e);
        }
    }

    /**
     * Retrieves all recurring transactions for specific account IDs.
     *
     * @param acc_ids The account IDs to filter recurring transactions by.
     * @return A list of {@link RecurringTransactionRow} objects.
     */
    public List<RecurringTransactionRow> getRecurringTransactions(int... acc_ids) {
        StringBuilder sql = new StringBuilder("SELECT * FROM recurring_transaction WHERE acc_id IN (");
        for (int i = 0; i < acc_ids.length; i++) {
            sql.append(acc_ids[i]);
            if (i < acc_ids.length - 1) sql.append(", ");
        }
        sql.append(");");

        try (PreparedStatement ps = conn.prepareStatement(acc_ids.length > 0 ? sql.toString() : "SELECT * FROM recurring_transaction")) {
            return TableToRecordAPI.toRecurringTransactions(ps.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve recurring transactions", e);
        }
    }

    /**
     * Updates a single column in a table for a specific record ID.
     *
     * @param id       The ID of the record to update.
     * @param newValue The new value to set for the column.
     * @param column   The column name to update.
     * @param table    The table name containing the record.
     */
    private void updateTableInfo(int id, String newValue, String column, String table) {
        String sql = String.format("UPDATE %s SET %s = ? WHERE id = ?", table, column);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newValue);
            ps.setInt(2, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update record in " + table, e);
        }
    }

    /**
     * Updates the balance of an account based on a transaction.
     *
     * @param acc_id The account ID to update.
     * @param amount The amount to add or subtract.
     * @param type   The type of transaction (DEPOSIT or WITHDRAW).
     */
    private void updateAccountBalance(int acc_id, float amount, TransactionType type) {
        String sql = String.format("""
                UPDATE account
                SET balance = balance %s ?
                WHERE id = ?;
                """, type == TransactionType.DEPOSIT ? "+" : "-");

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setFloat(1, amount);
            ps.setInt(2, acc_id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update account balance", e);
        }
    }

    /**
     * Updates the last transaction time for a recurring transaction.
     *
     * @param rec_id               The recurring transaction ID to update.
     * @param lastTimeTransacted   The new last transaction date.
     */
    private void updateLastTimeTransacted(int rec_id, LocalDate lastTimeTransacted) {
        double epoch = lastTimeTransacted.atStartOfDay().toEpochSecond(ZoneOffset.UTC);
        String sql = "UPDATE recurring_transaction SET last_time_transacted = ? WHERE id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, epoch);
            ps.setInt(2, rec_id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update last transaction time for recurring transaction", e);
        }
    }

    /**
     * Sets the name of an account.
     *
     * @param acc_id   The account ID.
     * @param newValue The new name to set.
     */
    public void setAccountName(int acc_id, String newValue) {
        updateTableInfo(acc_id, newValue, "name", "account");
    }

    /**
     * Sets the card number for a specific account.
     *
     * @param acc_id   The account ID.
     * @param newValue The new card number to set.
     */
    public void setAccountCard(int acc_id, String newValue) {
        updateTableInfo(acc_id, newValue, "card", "account");
    }

    /**
     * Sets the bank name for a specific account.
     *
     * @param acc_id   The account ID.
     * @param newValue The new bank name to set.
     */
    public void setAccountBank(int acc_id, String newValue) {
        updateTableInfo(acc_id, newValue, "bank", "account");
    }

    /**
     * Updates the name of a specific transaction.
     *
     * @param tran_id  The transaction ID.
     * @param newValue The new name to set.
     */
    public void setTransactionName(int tran_id, String newValue) {
        updateTableInfo(tran_id, newValue, "name", "user_transaction");
    }

    /**
     * Updates the type of a specific transaction.
     *
     * @param tran_id  The transaction ID.
     * @param newValue The new transaction type to set.
     */
    public void setTransactionType(int tran_id, String newValue) {
        updateTableInfo(tran_id, newValue, "type", "user_transaction");
    }

    /**
     * Updates the name of a specific recurring transaction.
     *
     * @param rec_id   The recurring transaction ID.
     * @param newValue The new name to set.
     */
    public void setRecurringTransactionName(int rec_id, String newValue) {
        updateTableInfo(rec_id, newValue, "name", "recurring_transaction");
    }

    /**
     * Updates the type of a specific recurring transaction.
     *
     * @param rec_id   The recurring transaction ID.
     * @param newValue The new transaction type to set.
     */
    public void setRecurringTransactionType(int rec_id, String newValue) {
        updateTableInfo(rec_id, newValue, "type", "recurring_transaction");
    }

    /**
     * Updates the recurring rate of a specific recurring transaction.
     *
     * @param rec_id   The recurring transaction ID.
     * @param newValue The new recurring rate to set.
     */
    public void setRecurringTransactionRate(int rec_id, String newValue) {
        updateTableInfo(rec_id, newValue, "recurring_rate", "recurring_transaction");
    }

    /**
     * Updates the amount of a specific transaction and adjusts the account balance accordingly.
     *
     * @param tran_id   The transaction ID.
     * @param newAmount The new transaction amount.
     */
    public void setTransactionAmount(int tran_id, float newAmount) {
        // Retrieve the current transaction details
        TransactionRow transaction = getTransaction(tran_id);

        // Determine the opposite transaction type for balance adjustment
        TransactionType opposite = transaction.type() == TransactionType.DEPOSIT
                ? TransactionType.WITHDRAW
                : TransactionType.DEPOSIT;

        // Reverse the old transaction amount
        updateAccountBalance(transaction.acc_id(), transaction.amount(), opposite);

        // Apply the new transaction amount
        updateAccountBalance(transaction.acc_id(), newAmount, transaction.type());

        // Update the transaction amount in the database
        String sql = "UPDATE user_transaction SET amount = ? WHERE id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setFloat(1, newAmount);
            ps.setInt(2, tran_id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update transaction amount", e);
        }
    }

    /**
     * Updates the amount of a specific recurring transaction.
     *
     * @param rec_id    The recurring transaction ID.
     * @param newAmount The new amount for the recurring transaction.
     */
    public void setRecurringTransactionAmount(int rec_id, float newAmount) {
        String sql = "UPDATE recurring_transaction SET amount = ? WHERE id = ?;";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setFloat(1, newAmount);
            ps.setInt(2, rec_id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update recurring transaction amount", e);
        }
    }

    /**
     * Adds a new account to the database.
     *
     * @param name The account name.
     * @param card The associated card number.
     * @param bank The associated bank name.
     * @return The ID of the newly created account.
     */
    public int addAccount(String name, String card, String bank) {
        String sql = """
                INSERT INTO account (balance, name, card, bank)
                VALUES (?, ?, ?, ?)
                RETURNING id;""";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setFloat(1, 0); // Initial balance is zero
            ps.setString(2, name);
            ps.setString(3, card);
            ps.setString(4, bank);
            ps.execute();

            ResultSet rs = ps.getResultSet();
            rs.next();
            return rs.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert new account", e);
        }
    }

    /**
     * Adds a new user transaction and updates the associated account balance.
     *
     * @param acc_id   The account ID.
     * @param date     The transaction date.
     * @param name     The transaction name.
     * @param type     The transaction type (DEPOSIT or WITHDRAW).
     * @param amount   The transaction amount.
     * @param recurring Indicates if the transaction is part of a recurring transaction.
     * @return The ID of the newly created transaction.
     */
    public int addTransaction(int acc_id, LocalDate date, String name, TransactionType type, float amount, boolean recurring) {
        double epoch = date.atStartOfDay().toEpochSecond(ZoneOffset.UTC);

        String sql = """
                INSERT INTO user_transaction (acc_id, date, name, type, amount, recurring)
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING id;""";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, acc_id);
            ps.setDouble(2, epoch);
            ps.setString(3, name);
            ps.setString(4, type.toString());
            ps.setFloat(5, amount);
            ps.setBoolean(6, recurring);

            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.next();
            int trans_id = rs.getInt("id");

            // Update account balance
            updateAccountBalance(acc_id, amount, type);
            return trans_id;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert new transaction", e);
        }
    }

    /**
     * Adds a new recurring transaction to the database and schedules transactions
     * starting from the specified start date until the current date.
     *
     * @param acc_id    The ID of the associated account.
     * @param startDate The start date of the recurring transaction.
     * @param name      The name of the recurring transaction.
     * @param type      The type of transaction (e.g., DEPOSIT or WITHDRAW).
     * @param rate      The recurring rate (e.g., WEEKLY, MONTHLY).
     * @param amount    The amount for each recurring transaction.
     * @return The ID of the newly created recurring transaction.
     */
    public int addRecurringTransaction(int acc_id, LocalDate startDate, String name, TransactionType type, RecurringRate rate, float amount) {
        int rec_id; // ID of the newly created recurring transaction

        // Convert the start date to epoch time for database storage
        double startEpoch = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC);

        // Convert enums to their string representations for database storage
        String transType = type.toString();
        String recurRate = rate.toString();

        // SQL query to insert a new recurring transaction
        String sql = """
                INSERT INTO recurring_transaction (acc_id, start_date, name, type, recurring_rate, amount)
                VALUES (?, ?, ?, ?, ?)
                RETURNING id;""";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Bind values to the prepared statement
            ps.setInt(1, acc_id);
            ps.setDouble(2, startEpoch);
            ps.setString(3, name);
            ps.setString(4, transType);
            ps.setString(5, recurRate);
            ps.setFloat(6, amount);

            // Execute the statement and retrieve the generated ID
            ps.execute();
            ResultSet rs = ps.getResultSet();
            rs.next();
            rec_id = rs.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert new recurring transaction", e);
        }

        // Generate and update individual transactions from startDate to the current date
        LocalDate workingDate = startDate;
        while (workingDate.isBefore(LocalDate.now())) {
            // Add a transaction for the current date
            addTransaction(acc_id, workingDate, name, type, amount, true);

            // Update the last transaction date for the recurring transaction
            updateLastTimeTransacted(rec_id, workingDate);

            // Move to the next recurring interval
            workingDate = workingDate.plus(rate.getInterval());
        }

        return rec_id; // Return the generated recurring transaction ID
    }


    /**
     * Checks and processes all recurring transactions, creating transactions as needed
     * and updating the last transacted time for each.
     */
    public void checkRecurringTransactions() {
        for (RecurringTransactionRow row : getRecurringTransactions()) {
            LocalDate lastTransaction = Instant.ofEpochSecond((long) row.lastTimeTransacted())
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate();

            // Calculate next transaction date based on recurring interval
            LocalDate nextTransaction = lastTransaction.plus(row.recurringRate().getInterval());
            while (nextTransaction.isBefore(LocalDate.now())) {
                addTransaction(row.acc_id(), nextTransaction, row.name(), row.type(), row.amount(), true);
                updateLastTimeTransacted(row.acc_id(), nextTransaction);
                nextTransaction = nextTransaction.plus(row.recurringRate().getInterval());
            }
        }
    }

    /**
     * Deletes an account and all associated transactions (recurring and user transactions).
     *
     * @param acc_id The account ID to delete.
     */
    public void deleteAccount(int acc_id) {
        String sql = """
                DELETE account WHERE id = ?;
                DELETE recurring_transaction WHERE acc_id = ?;
                DELETE user_transaction WHERE acc_id = ?;""";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Set the account ID for all three deletion queries
            ps.setInt(1, acc_id); // Delete from `account`
            ps.setInt(2, acc_id); // Delete related `recurring_transaction`
            ps.setInt(3, acc_id); // Delete related `user_transaction`
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete account with ID: " + acc_id, e);
        }
    }

    /**
     * Deletes a specific user transaction and adjusts the associated account balance.
     *
     * @param tran_id The transaction ID to delete.
     */
    public void deleteTransaction(int tran_id) {
        // Retrieve the transaction details to adjust the account balance
        TransactionRow transaction = getTransaction(tran_id);

        // Determine the opposite transaction type for reversing the balance impact
        TransactionType opposite = transaction.type() == TransactionType.DEPOSIT
                ? TransactionType.WITHDRAW
                : TransactionType.DEPOSIT;

        String sql = "DELETE user_transaction WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Set the transaction ID for deletion
            ps.setInt(1, tran_id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete transaction with ID: " + tran_id, e);
        }

        // Adjust the account balance to reverse the impact of the deleted transaction
        updateAccountBalance(transaction.acc_id(), transaction.amount(), opposite);
    }

    /**
     * Deletes a specific recurring transaction from the database.
     *
     * @param rec_id The recurring transaction ID to delete.
     */
    public void deleteRecurringTransaction(int rec_id) {
        String sql = "DELETE recurring_transaction WHERE id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Set the recurring transaction ID for deletion
            ps.setInt(1, rec_id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete recurring transaction with ID: " + rec_id, e);
        }
    }
}
