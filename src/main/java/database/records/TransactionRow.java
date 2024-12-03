package database.records;

import database.enums.TransactionType;

public record TransactionRow(int id, int acc_id, double date, String name, TransactionType type, float amount, boolean recurring) {
}
