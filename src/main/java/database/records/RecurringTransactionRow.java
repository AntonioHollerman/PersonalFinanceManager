package database.records;

import database.enums.RecurringRate;
import database.enums.TransactionType;

/**
 * Represents a single recurring transaction record in the database.
 * Provides a structured way to store recurring transaction details retrieved from the database.
 *
 * @param id                 the unique identifier for the recurring transaction.
 * @param acc_id             the account ID associated with the recurring transaction.
 * @param start_date         the start date of the recurring transaction as a Unix timestamp (in seconds).
 * @param name               the name or description of the recurring transaction.
 * @param type               the type of the transaction (withdraw or deposit).
 * @param recurringRate      the recurrence rate (e.g., weekly, monthly).
 * @param amount             the amount involved in the recurring transaction.
 * @param lastTimeTransacted the last time the transaction was processed, as a Unix timestamp (in seconds).
 */
public record RecurringTransactionRow(int id, int acc_id, double start_date, String name, TransactionType type,
                                      RecurringRate recurringRate, float amount, double lastTimeTransacted) {
}
