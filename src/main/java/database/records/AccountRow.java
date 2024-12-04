package database.records;

/**
 * Represents a single account record in the database.
 * Provides a structured way to store account details retrieved from the database.
 *
 * @param id      the unique identifier for the account.
 * @param balance the current balance of the account.
 * @param name    the name of the account holder or account.
 * @param card    the card number or identifier associated with the account.
 * @param bank    the name of the bank associated with the account.
 */
public record AccountRow(int id, float balance, String name, String card, String bank) {
}
