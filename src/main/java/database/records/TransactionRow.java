package database.records;

import database.enums.TransactionType;

/**
 * Represents a single transaction record in the database.
 * Provides a structured way to store transaction details retrieved from the database.
 *
 * @param id        the unique identifier for the transaction.
 * @param acc_id    the account ID associated with the transaction.
 * @param date      the transaction date as a Unix timestamp (in seconds).
 * @param name      the name or description of the transaction.
 * @param type      the type of the transaction (withdraw or deposit).
 * @param amount    the amount involved in the transaction.
 * @param recurring a flag indicating whether the transaction is recurring.
 */
public record TransactionRow(int id, int acc_id, double date, String name, TransactionType type, float amount, boolean recurring) {
}
