package com.example.studentmanager.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    // credentials are read from environment variables for security.  A local
    // `.env` file may be used during development (see README).  Do **not**
    // commit any real passwords to version control; `.env` is already ignored in
    // `.gitignore`.
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:mysql://localhost:3306/userdb");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASS", "");

    public static Connection getConnection() throws SQLException {
        // basic sanity checks so we fail fast if configuration is missing
        if (USER == null || USER.isBlank()) {
            throw new SQLException("Database user is not specified (DB_USER)");
        }
        if (PASSWORD == null || PASSWORD.isBlank()) {
            // MySQL will reject empty passwords unless the account is configured
            // that way; alert the developer so they don't keep forgetting to set
            // the variable.
            throw new SQLException("Database password is not specified (DB_PASS)");
        }

        // load driver class explicitly; necessary on some environments
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Unable to load MySQL JDBC driver", e);
        }

        // Establish and return the connection using environment values
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
