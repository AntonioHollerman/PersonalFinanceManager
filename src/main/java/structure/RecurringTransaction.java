package structure;

import database.SqlManager;
import database.enums.RecurringRate;
import database.enums.TransactionType;
import database.records.RecurringTransactionRow;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Represents a recurring transaction associated with an account.
 * Provides methods to manage recurring transaction details and persist updates in the database.
 */
public class RecurringTransaction {
    // Static database connection shared across all RecurringTransaction instances
    private static final SqlManager dbConn = SqlManager.DB_CONNECTION;

    // The account this recurring transaction belongs to
    private final Account account;

    // Recurring transaction details
    private final int id;  // Unique identifier for the recurring transaction
    private final int accId;  // Account ID associated with the recurring transaction
    private final LocalDate startDate;  // Start date of the recurring transaction
    private String name;  // Name/description of the transaction
    private TransactionType type;  // Type of transaction (e.g., debit, credit)
    private RecurringRate recurringRate;  // Frequency of recurrence
    private float amount;  // Amount of the recurring transaction

    /**
     * Constructor that initializes a RecurringTransaction instance using data from the database.
     *
     * @param row     the database record representing the recurring transaction.
     * @param account the account associated with this recurring transaction.
     */
    public RecurringTransaction(RecurringTransactionRow row, Account account) {
        this.id = row.id();
        this.accId = row.acc_id();
        this.startDate = Instant.ofEpochSecond((long) row.start_date())
                .atZone(ZoneOffset.UTC)
                .toLocalDate();
        this.name = row.name();
        this.type = row.type();
        this.recurringRate = row.recurringRate();
        this.amount = row.amount();
        this.account = account;
    }

    /**
     * Retrieves the ID of the recurring transaction.
     *
     * @return the transaction ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the account ID associated with this recurring transaction.
     *
     * @return the account ID.
     */
    public int getAccId() {
        return accId;
    }

    /**
     * Retrieves the start date of the recurring transaction.
     *
     * @return the start date.
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Retrieves the name/description of the recurring transaction.
     *
     * @return the transaction name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the type of the recurring transaction.
     *
     * @return the transaction type.
     */
    public TransactionType getType() {
        return type;
    }

    /**
     * Retrieves the recurrence rate of the recurring transaction.
     *
     * @return the recurrence rate.
     */
    public RecurringRate getRecurringRate() {
        return recurringRate;
    }

    /**
     * Retrieves the amount of the recurring transaction.
     *
     * @return the transaction amount.
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Updates the name/description of the recurring transaction.
     *
     * @param name the new transaction name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Updates the type of the recurring transaction and persists the change in the database.
     *
     * @param type the new transaction type.
     */
    public void setType(TransactionType type) {
        dbConn.setRecurringTransactionType(id, type.toString());
        this.type = type;
    }

    /**
     * Updates the recurrence rate of the recurring transaction and persists the change in the database.
     *
     * @param recurringRate the new recurrence rate.
     */
    public void setRecurringRate(RecurringRate recurringRate) {
        dbConn.setRecurringTransactionRate(id, recurringRate.toString());
        this.recurringRate = recurringRate;
    }

    /**
     * Updates the amount of the recurring transaction and persists the change in the database.
     *
     * @param amount the new transaction amount.
     */
    public void setAmount(float amount) {
        dbConn.setRecurringTransactionAmount(id, amount);
        this.amount = amount;
    }

    /**
     * Deletes the recurring transaction from the database and removes it from the associated account.
     */
    public void deleteRecurringTransaction() {
        account.deleteRecurringTransaction(id);
    }
}
