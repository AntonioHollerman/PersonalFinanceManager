package structure;

import database.SqlManager;
import database.enums.RecurringRate;
import database.enums.TransactionType;
import database.records.AccountRow;
import database.records.RecurringTransactionRow;
import database.records.TransactionRow;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an account with its associated transactions and recurring transactions.
 * Provides methods to manage account details, transactions, and recurring transactions.
 */
public class Account {
    // Static database connection shared across all Account instances
    private static final SqlManager dbConn = SqlManager.DB_CONNECTION;

    // Lists to hold transactions and recurring transactions for this account
    private final List<Transaction> transactions;
    private final List<RecurringTransaction> recurringTransactions;

    // Account details
    private final int id;
    private String name;
    private String card;
    private String bank;

    /**
     * Constructor that initializes an Account instance using data from the database.
     * It loads associated transactions and recurring transactions for the account.
     *
     * @param row the database record representing the account.
     */
    public Account(AccountRow row) {
        this.id = row.id();
        this.name = row.name();
        this.card = row.card();
        this.bank = row.bank();

        this.transactions = new ArrayList<>();
        this.recurringTransactions = new ArrayList<>();

        // Load existing transactions from the database
        for (TransactionRow tranRow : dbConn.getTransactions(id)) {
            transactions.add(new Transaction(tranRow, this));
        }

        // Load existing recurring transactions from the database
        for (RecurringTransactionRow recurRow : dbConn.getRecurringTransactions(id)) {
            recurringTransactions.add(new RecurringTransaction(recurRow, this));
        }
    }

    /**
     * Creates and returns a new Account instance, adding it to the database.
     *
     * @param name the name of the account.
     * @param card the associated card identifier.
     * @param bank the name of the bank.
     * @return a new Account instance.
     */
    public static Account createNewAccount(String name, String card, String bank) {
        return new Account(
                new AccountRow(
                        dbConn.addAccount(name, card, bank),
                        0, name, card, bank
                )
        );
    }

    /**
     * Adds a new transaction to the account and persists it in the database.
     *
     * @param date   the transaction date.
     * @param name   the transaction description.
     * @param type   the type of transaction (e.g., debit or credit).
     * @param amount the amount involved in the transaction.
     */
    public void addNewTransaction(LocalDate date, String name, TransactionType type, float amount) {
        transactions.add(
                new Transaction(
                        new TransactionRow(
                                dbConn.addTransaction(id, date, name, type, amount, false),
                                id,
                                date.atStartOfDay().toEpochSecond(ZoneOffset.UTC),
                                name, type, amount, false
                        ),
                        this
                )
        );
    }

    /**
     * Adds a new recurring transaction to the account and persists it in the database.
     *
     * @param startDate the start date of the recurring transaction.
     * @param name      the description of the transaction.
     * @param type      the type of transaction (e.g., debit or credit).
     * @param rate      the recurrence rate (e.g., daily, monthly).
     * @param amount    the amount involved in the recurring transaction.
     */
    public void addNewRecurringTransaction(LocalDate startDate, String name, TransactionType type, RecurringRate rate, float amount) {
        recurringTransactions.add(
                new RecurringTransaction(
                        new RecurringTransactionRow(
                                dbConn.addRecurringTransaction(id, startDate, name, type, rate, amount),
                                id,
                                startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC),
                                name, type, rate, amount, -1
                        ),
                        this
                )
        );
    }

    /**
     * Retrieves all transactions associated with this account.
     *
     * @return a list of transactions.
     */
    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    /**
     * Retrieves all recurring transactions associated with this account.
     *
     * @return a list of recurring transactions.
     */
    public List<RecurringTransaction> getRecurringTransactions() {
        return new ArrayList<>(recurringTransactions);
    }

    /**
     * Retrieves the account's unique identifier.
     *
     * @return the account ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the current balance of the account.
     *
     * @return the account balance.
     */
    public float getBalance() {
        return dbConn.getAccount(id).balance();
    }

    /**
     * Retrieves the account's name.
     *
     * @return the account name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the card associated with the account.
     *
     * @return the card identifier.
     */
    public String getCard() {
        return card;
    }

    /**
     * Retrieves the bank name associated with the account.
     *
     * @return the bank name.
     */
    public String getBank() {
        return bank;
    }

    /**
     * Updates the account's name in the database and locally.
     *
     * @param name the new account name.
     */
    public void setName(String name) {
        dbConn.setAccountName(id, name);
        this.name = name;
    }

    /**
     * Updates the card associated with the account in the database and locally.
     *
     * @param card the new card identifier.
     */
    public void setCard(String card) {
        dbConn.setAccountCard(id, card);
        this.card = card;
    }

    /**
     * Updates the bank name associated with the account in the database and locally.
     *
     * @param bank the new bank name.
     */
    public void setBank(String bank) {
        dbConn.setAccountBank(id, bank);
        this.bank = bank;
    }

    /**
     * Deletes the account from the database.
     */
    public void deleteAccount() {
        dbConn.deleteAccount(id);
    }

    /**
     * Deletes a specific transaction from the account and database.
     *
     * @param tranId the ID of the transaction to delete.
     */
    protected void deleteTransaction(int tranId) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getId() == tranId) {
                dbConn.deleteTransaction(tranId);
                transactions.remove(i);
                break;
            }
        }
    }

    /**
     * Deletes a specific recurring transaction from the account and database.
     *
     * @param recurId the ID of the recurring transaction to delete.
     */
    protected void deleteRecurringTransaction(int recurId) {
        for (int i = 0; i < recurringTransactions.size(); i++) {
            if (recurringTransactions.get(i).getId() == recurId) {
                dbConn.deleteRecurringTransaction(recurId);
                recurringTransactions.remove(i);
                break;
            }
        }
    }
}
