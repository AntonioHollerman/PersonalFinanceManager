package database;

import database.records.AccountRow;
import database.records.RecurringTransactionRow;
import database.records.TableToRecordAPI;
import database.records.TransactionRow;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqlManager {
    private final Connection conn;
    protected SqlManager() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:C:\\JavaProjects\\PersonalFinanceManager\\resources\\finance.db");
            createTables();
        } catch (SQLException ignore){
            throw new RuntimeException("Fail to connect to database");
        }
    }
    public void createTables() {
        String tables = """
            CREATE TABLE IF NOT EXISTS  account(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                balance REAL NOT NULL,
                name TEXT NOT NULL,
                card TEXT,
                bank TEXT
            );
            CREATE TABLE IF NOT EXISTS recurring_transactions(
                acc_id INTEGER REFERENCES account(id),
                start_date REAL NOT NULL,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                recurring_rate TEXT NOT NULL,
                amount REAL NOT NULL
            );
            CREATE TABLE IF NOT EXISTS user_transaction(
                acc_id INTEGER REFERENCES account(id),
                date REAL NOT NULL,
                name TEXT,
                type TEXT,
                amount REAL NOT NULL
            );""";
        try (PreparedStatement ps = conn.prepareStatement(tables)){
            ps.execute();
        } catch (SQLException e){
            e.fillInStackTrace();
            throw new RuntimeException("Failed to create tables");
        }
    }
    public List<AccountRow> getAccounts(){
        String sql = """
                SELECT * FROM account;""";
        try (PreparedStatement ps = conn.prepareStatement(sql)){
            return TableToRecordAPI.toAccounts(ps.executeQuery());
        } catch (SQLException e){
            e.fillInStackTrace();
            throw new RuntimeException("Fail to get accounts");
        }
    }

    public List<TransactionRow> getTransactions(int[] acc_ids){
        StringBuilder sql = new StringBuilder("""
                SELECT * FROM user_transaction WHERE acc_id IN (""");
        for (int i=0; i < acc_ids.length; i++){
            if (i != acc_ids.length - 1){
                sql.append(acc_ids[i]).append(", ");
            } else {
                sql.append(acc_ids[i]).append(");");
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())){
            return TableToRecordAPI.toTransactions(ps.executeQuery());
        } catch (SQLException e){
            e.fillInStackTrace();
            throw new RuntimeException("Fail to get transactions");
        }
    }

    public List<RecurringTransactionRow> getRecurringTransactions(int[] acc_ids){
        StringBuilder sql = new StringBuilder("""
                SELECT * FROM recurring_transaction WHERE acc_id IN (""");
        for (int i=0; i < acc_ids.length; i++){
            if (i != acc_ids.length - 1){
                sql.append(acc_ids[i]).append(", ");
            } else {
                sql.append(acc_ids[i]).append(");");
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())){
            return TableToRecordAPI.toRecurringTransactions(ps.executeQuery());
        } catch (SQLException e){
            e.fillInStackTrace();
            throw new RuntimeException("Fail to get transactions");
        }
    }
}
