package com.example.kuet_buy_and_sell_app;

import java.sql.*;
import java.util.logging.Logger;

public class db {
    private Connection connection;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private static db instance = null;
    private final String DB_url = "jdbc:sqlite:kuet_marketplace.db";

    private db() {

    }

    /**
     * Singleton instance getter
     */
    public static db b() {
        if (instance == null) {
            instance = new db();
            instance.c();
        }
        return instance;
    }

    /**
     * Initialize connection
     */
    void c() {
        try {
            Class.forName("org.sqlite.JDBC");
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_url);
                logger.info("Connected to db successfully");
                create_table();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("SQLITE driver not found");
        } catch (SQLException e) {
            logger.severe("Database connection error: " + e.getMessage());
        }
    }

    /**
     * Creates the table
     */
    public void create_table() {

        String sql = "CREATE TABLE IF NOT EXISTS buyers (\n"
                + "roll TEXT PRIMARY KEY,\n"
                + "fullName TEXT NOT NULL,\n"
                + "email TEXT NOT NULL UNIQUE,\n"
                + "password TEXT NOT NULL\n" // No comma here
                + ");";
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            logger.info("Table 'buyers' created or verified successfully");
        } catch (SQLException e) {
            logger.severe("Error creating db table: " + e.getMessage());
        }
    }

    /**
     * Registers user
     */
    public boolean register_user(user user1) {
        String sql = "INSERT INTO buyers(roll, fullName, email, password) VALUES(?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user1.getRoll());
            pstmt.setString(2, user1.getName());
            pstmt.setString(3, user1.getEmail());
            pstmt.setString(4, user1.getPassword());

            pstmt.executeUpdate();
            logger.info("Inserted data successfully for roll: " + user1.getRoll());
            return true;
        } catch (SQLException e) {

            if (e.getMessage().contains("UNIQUE constraint failed")) {
                logger.warning("Roll or Email already registered.");
                return false;
            }
            logger.severe("Error inserting data: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if email is already in use
     */
    public boolean is_email_registered(String email) {
        String sql = "SELECT roll FROM buyers WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            logger.severe("Error checking email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Finds a user by roll
     */
    public user find_user_by_roll(String roll) {
        String sql = "SELECT fullName, email, password FROM buyers WHERE roll = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, roll);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String fullName = rs.getString("fullName");
                String email = rs.getString("email");
                String password = rs.getString("password");
                return new user(fullName, email, roll, password);
            }
        } catch (SQLException e) {
            logger.severe("Error retrieving user: " + e.getMessage());
        }
        return null;
    }
}