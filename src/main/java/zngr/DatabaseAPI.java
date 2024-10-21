package zngr;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseAPI {

    protected final String url = "jdbc:sqlite:" + System.getProperty("user.dir") + File.separator + "data" + File.separator + "db.sqlite";

    public DatabaseAPI() {
        System.out.println("Database URL: " + url);
        ensureDatabaseExists();
    }

    private void ensureDatabaseExists() { // Checks whether 'data' directory and 'db.sqlite' exists and creates otherwise
        try {
            File dataDir = new File(System.getProperty("user.dir") + "/data");
            File databaseFile = new File(url.substring(12));

            if (!dataDir.exists()) {
                dataDir.mkdir();
                System.out.println("Created data directory: " + dataDir.getAbsolutePath());
            }

            if (!databaseFile.exists()) {
                try (Connection conn = DriverManager.getConnection(url)) {
                    if (conn != null) {
                        System.out.println("Database file created: " + databaseFile.getAbsolutePath());
                    }
                } catch (SQLException e) {
                    System.out.println("Error creating database: " + e.getMessage());
                }
            } else {
                System.out.println("Database file already exists: " + databaseFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("Error ensuring database existence: " + e.getMessage());
        }
    }

    public void createTable(String tableName, String fields) { // Creates table if not existing
        try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                var sql = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "';";
                var stmt = conn.createStatement();
                var rs = stmt.executeQuery(sql);

                if (rs.next()) {
                    System.out.println("Table " + tableName + " already exists.");
                } else {
                    sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n " + fields + ");";
                    stmt.executeUpdate(sql);
                    System.out.println("A new table " + tableName + " has been created.");
                }
                stmt.close();
            }
        } catch (SQLException e) {
            System.out.println("Error in createTable: " + e.getMessage());
        }
    }

    public void insert(String tableName, String fields, String values) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                conn.setAutoCommit(false);
                var stmt = conn.createStatement();
                var sql = "INSERT INTO " + tableName + "("  + fields + ") VALUES (" + values +")";
                stmt.executeUpdate(sql);

                stmt.close();
                conn.commit();
                conn.close();
            }
            System.out.println("Insert in " + tableName + " is done");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getValue(String tableName, String keyName, String keyValue, String fieldName) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                var stmt = conn.createStatement();

                var sql = "SELECT * FROM " + tableName + " WHERE " + keyName + " == " + keyValue;
                var rs = stmt.executeQuery(sql);
                try {
                    var exS = rs.getString(fieldName);
                    stmt.close();
                    conn.close();
                    return exS;
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    stmt.close();
                }
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean isKeyAvailable(String tableName, String keyName, String keyValue) {
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                Statement stmt = conn.createStatement();
                String sql = "SELECT * FROM " + tableName + " WHERE " + keyName + " = " + keyValue;
                System.out.println("Executing SQL: " + sql);

                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    String exS = rs.getString(keyName);
                    System.out.println("Key value " + exS + " from table " + tableName + " exists.");
                    stmt.close();
                    conn.close();
                    return true;
                } else {
                    System.out.println("Key value " + keyValue + " not found in table " + tableName);
                }
                stmt.close();
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("SQL Error in isKeyAvailable: " + e.getMessage());
        }
        return false;
    }
}
