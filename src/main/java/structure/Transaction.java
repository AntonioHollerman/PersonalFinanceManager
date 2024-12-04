package structure;

import database.SqlManager;
import database.enums.TransactionType;
import database.records.TransactionRow;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Represents a transaction associated with an account.
 * Provides methods to manage transaction details and perform updates in the database.
 */
public class Transaction {
    // Static database connection shared across all Transaction instances
    private static final SqlManager dbConn = SqlManager.DB_CONNECTION;

    // The account this transaction belongs to
    private final Account account;

    // Transaction details
    private final int id;  // Unique identifier for the transaction
    private final int accId;  // Account ID associated with the transaction
    private LocalDate date;  // Transaction date
    private String name;  // Transaction description
    private TransactionType type;  // Type of transaction (e.g., debit, credit)
    private float amount;  // Amount of the transaction
    private final boolean recurring;  // Flag indicating if the transaction is recurring

    /**
     * Constructor that initializes a Transaction instance using data from the database.
     *
     * @param row     the database record representing the transaction.
     * @param account the account associated with this transaction.
     */
    public Transaction(TransactionRow row, Account account) {
        this.id = row.id();
        this.accId = row.acc_id();
        this.date = Instant.ofEpochSecond((long) row.date())
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
        this.name = row.name();
        this.type = row.type();
        this.amount = row.amount();
        this.recurring = row.recurring();
        this.account = account;
    }

    /**
     * Retrieves the transaction ID.
     *
     * @return the transaction ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the account ID associated with this transaction.
     *
     * @return the account ID.
     */
    public int getAccId() {
        return accId;
    }

    /**
     * Retrieves the date of the transaction.
     *
     * @return the transaction date.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Retrieves the name/description of the transaction.
     *
     * @return the transaction name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the type of the transaction.
     *
     * @return the transaction type.
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Retrieves the amount involved in the transaction, rounded to two decimal places.
     *
     * @return the transaction amount rounded to two decimal places.
     */
    public float getAmount() {
        return Math.round(amount * 100.0f) / 100.0f;
    }

    /**
     * Indicates whether the transaction is recurring.
     *
     * @return {@code true} if the transaction is recurring; {@code false} otherwise.
     */
    public boolean isRecurring() {
        return recurring;
    }

    /**
     * Updates the date of the transaction and persists the change in the database.
     *
     * @param date the new transaction date.
     */
    public void setDate(LocalDate date) {
        dbConn.setTransactionDate(id, date);
        this.date = date;
    }

    /**
     * Updates the name/description of the transaction and persists the change in the database.
     *
     * @param name the new transaction name.
     */
    public void setName(String name) {
        dbConn.setTransactionName(id, name);
        this.name = name;
    }

    /**
     * Updates the type of the transaction and persists the change in the database.
     *
     * @param type the new transaction type.
     */
    public void setType(TransactionType type) {
        dbConn.setTransactionType(id, type.toString());
        this.type = type;
    }

    /**
     * Updates the amount of the transaction and persists the change in the database.
     *
     * @param amount the new transaction amount.
     */
    public void setAmount(float amount) {
        dbConn.setTransactionAmount(id, amount);
        this.amount = amount;
    }

    /**
     * Deletes the transaction from the database and removes it from its associated account.
     */
    public void deleteTransaction() {
        account.deleteTransaction(id);
    }
}
