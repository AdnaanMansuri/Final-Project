package edu.neu.foodrescue.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton that holds the JDBC settings (from db.properties) and hands out connections.
 * Callers use try-with-resources so every connection is closed after use.
 */
public final class DBConnection {

    private static DBConnection instance;

    private final String url;
    private final String user;
    private final String password;

    private DBConnection() {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            // fall back to defaults below
        }
        url = props.getProperty("db.url",
                "jdbc:mysql://localhost:3306/food_rescue?useSSL=false&allowPublicKeyRetrieval=true");
        user = props.getProperty("db.user", "foodapp");
        password = props.getProperty("db.password", "foodapp123");
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    /** Returns true if the database is reachable. Used at startup. */
    public boolean testConnection() {
        try (Connection c = getConnection()) {
            return c.isValid(3);
        } catch (SQLException e) {
            return false;
        }
    }
}
