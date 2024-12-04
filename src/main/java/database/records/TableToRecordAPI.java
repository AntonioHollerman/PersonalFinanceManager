package database.records;

import database.enums.RecurringRate;
import database.enums.TransactionType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for converting database query results into record objects.
 * Converts {@link ResultSet} rows into lists of corresponding record objects.
 */
public class TableToRecordAPI {

    /**
     * Converts a {@link ResultSet} into a list of {@link AccountRow} objects.
     *
     * @param rs the {@link ResultSet} containing account data.
     * @return a list of {@link AccountRow} objects.
     * @throws SQLException if an error occurs while accessing the {@link ResultSet}.
     */
    public static List<AccountRow> toAccounts(ResultSet rs) throws SQLException {
        List<AccountRow> rows = new ArrayList<>();
        try (rs) {  // Automatically closes ResultSet
            while (rs.next()) {
                rows.add(
                        new AccountRow(
                                rs.getInt("id"),
                                rs.getFloat("balance"),
                                rs.getString("name"),
                                rs.getString("card"),
                                rs.getString("bank")
                        )
                );
            }
        }
        return rows;
    }

    /**
     * Converts a {@link ResultSet} into a list of {@link TransactionRow} objects.
     *
     * @param rs the {@link ResultSet} containing transaction data.
     * @return a list of {@link TransactionRow} objects.
     * @throws SQLException if an error occurs while accessing the {@link ResultSet}.
     */
    public static List<TransactionRow> toTransactions(ResultSet rs) throws SQLException {
        List<TransactionRow> rows = new ArrayList<>();
        try (rs) {  // Automatically closes ResultSet
            while (rs.next()) {
                TransactionType type = rs.getString("type").equals("withdraw") ?
                        TransactionType.WITHDRAW : TransactionType.DEPOSIT;
                rows.add(
                        new TransactionRow(
                                rs.getInt("id"),
                                rs.getInt("acc_id"),
                                rs.getDouble("date"),
                                rs.getString("name"),
                                type,
                                rs.getFloat("amount"),
                                rs.getBoolean("recurring")
                        )
                );
            }
        }
        return rows;
    }

    /**
     * Converts a {@link ResultSet} into a list of {@link RecurringTransactionRow} objects.
     *
     * @param rs the {@link ResultSet} containing recurring transaction data.
     * @return a list of {@link RecurringTransactionRow} objects.
     * @throws SQLException if an error occurs while accessing the {@link ResultSet}.
     */
    public static List<RecurringTransactionRow> toRecurringTransactions(ResultSet rs) throws SQLException {
        List<RecurringTransactionRow> rows = new ArrayList<>();
        try (rs) {  // Automatically closes ResultSet
            while (rs.next()) {
                TransactionType type = rs.getString("type").equals("withdraw") ?
                        TransactionType.WITHDRAW : TransactionType.DEPOSIT;
                RecurringRate rate = switch (rs.getString("recurring_rate")) {
                    case "weekly" -> RecurringRate.WEEKLY;
                    case "bi-weekly" -> RecurringRate.BI_WEEKLY;
                    case "monthly" -> RecurringRate.MONTHLY;
                    case "quarterly" -> RecurringRate.QUARTERLY;
                    case "yearly" -> RecurringRate.YEARLY;
                    default -> throw new SQLException("Failed to retrieve recurring rate");
                };
                rows.add(
                        new RecurringTransactionRow(
                                rs.getInt("id"),
                                rs.getInt("acc_id"),
                                rs.getDouble("start_date"),
                                rs.getString("name"),
                                type,
                                rate,
                                rs.getFloat("amount"),
                                rs.getDouble("last_time_transacted")
                        )
                );
            }
        }
        return rows;
    }
}
