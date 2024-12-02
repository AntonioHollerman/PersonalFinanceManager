package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlManager {
    private final Connection conn;
    public SqlManager() {
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
    
}
