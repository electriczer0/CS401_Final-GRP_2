package lib.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class Connection_Factory {

    // Private constructor to prevent instantiation
    private Connection_Factory() {
    }

    // Return a connection to the database if its existence is uncertain or known not to exist
    public static Connection getConnection(String dbURL, String schema) throws SQLException {
        File dbFile = new File(dbURL);
        boolean databaseExists = dbFile.exists();

        Connection connection = DriverManager.getConnection(dbURL);
        if (!databaseExists) {
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate(schema);
            }
        }

        return connection;
    }

    // Method to return a connection to a database known to exist
    public static Connection getConnection(String dbURL) throws SQLException {
        File dbFile = new File(dbURL);
        if (!dbFile.exists()) {
            throw new SQLException("Database does not exist at " + dbURL);
        }

        return DriverManager.getConnection("jdbc:sqlite:" + dbURL);
    }
}

