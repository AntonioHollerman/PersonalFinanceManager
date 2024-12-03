package database.records;

import database.enums.RecurringRate;
import database.enums.TransactionType;

public record RecurringTransactionRow(int id, int acc_id, double start_date, String name, TransactionType type,
                                      RecurringRate recurringRate, float amount) {
}
