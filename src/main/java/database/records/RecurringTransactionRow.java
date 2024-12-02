package database.records;

import database.enums.RecurringRate;
import database.enums.TransactionType;

public record RecurringTransactionRow(int acc_id, double start_date, String name, TransactionType type,
                                      RecurringRate recurringRate, float amount) {
}
