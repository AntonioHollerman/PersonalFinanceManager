package database.records;

import database.enums.RecurringRate;
import database.enums.TransactionType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableToRecordAPI {
    public static List<AccountRow> toAccounts(ResultSet rs) throws SQLException {
        List<AccountRow> rows = new ArrayList<>();
        try (rs){
            while (rs.next()){
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

    public static List<TransactionRow> toTransactions(ResultSet rs) throws SQLException{
        List<TransactionRow> rows = new ArrayList<>();
        try (rs){
            while (rs.next()){
                TransactionType type = rs.getString("type").equals("withdraw") ?
                        TransactionType.WITHDRAW : TransactionType.DEPOSIT;
                rows.add(new TransactionRow(
                        rs.getInt("id"),
                        rs.getInt("acc_id"),
                        rs.getDouble("date"),
                        rs.getString("name"),
                        type,
                        rs.getFloat("amount"),
                        rs.getBoolean("recurring")
                ));
            }
        }
        return rows;
    }

    public static List<RecurringTransactionRow> toRecurringTransactions(ResultSet rs) throws SQLException{
        List<RecurringTransactionRow> rows = new ArrayList<>();
        try (rs){
            while (rs.next()){
                TransactionType type = rs.getString("type").equals("withdraw") ?
                        TransactionType.WITHDRAW : TransactionType.DEPOSIT;
                RecurringRate rate = switch (rs.getString("recurring_rate")){
                    case "weekly" -> RecurringRate.WEEKLY;
                    case "bi-weekly" -> RecurringRate.BI_WEEKLY;
                    case "monthly" -> RecurringRate.MONTHLY;
                    case "quarterly" -> RecurringRate.QUARTERLY;
                    case "yearly" -> RecurringRate.YEARLY;
                    default -> throw new SQLException("Fail to get recurring rate");
                };
                rows.add(new RecurringTransactionRow(
                        rs.getInt("id"),
                        rs.getInt("acc_id"),
                        rs.getDouble("start_date"),
                        rs.getString("name"),
                        type,
                        rate,
                        rs.getFloat("amount"),
                        rs.getDouble("last_time_transacted")
                ));
            }
        }
        return rows;
    }
}
