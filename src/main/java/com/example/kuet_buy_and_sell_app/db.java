package com.example.kuet_buy_and_sell_app;

import java.sql.*;
import java.util.logging.Logger;

public class db {
    private Connection connection;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private static db instance = null;
    private final String DB_url = "jdbc:sqlite:kuet_marketplace2.db";

    private db() {}

    public static db b() {
        if (instance == null) {
            instance = new db();
            instance.c();
        }
        return instance;
    }

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

    public void create_table() {
        String buyerSql = "CREATE TABLE IF NOT EXISTS buyers (roll TEXT PRIMARY KEY, fullName TEXT NOT NULL, email TEXT NOT NULL UNIQUE, password TEXT NOT NULL);";
        String sellerSql = "CREATE TABLE IF NOT EXISTS sellers (phone TEXT PRIMARY KEY, name TEXT NOT NULL, email TEXT NOT NULL UNIQUE, shopName TEXT NOT NULL, password TEXT NOT NULL);";
        String itemsSql = "CREATE TABLE IF NOT EXISTS items (id INTEGER PRIMARY KEY AUTOINCREMENT, item_name TEXT NOT NULL, price REAL NOT NULL, category TEXT, description TEXT, image_path TEXT);";

        try (Statement statement = connection.createStatement()) {
            statement.execute(buyerSql);
            statement.execute(sellerSql);
            statement.execute(itemsSql);
            logger.info("All tables verified successfully");
        } catch (SQLException e) {
            logger.severe("Error creating db table: " + e.getMessage());
        }
    }

    // --- BUYER METHODS ---
    public boolean register_user(user user1) {
        String sql = "INSERT INTO buyers(roll, fullName, email, password) VALUES(?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user1.getRoll());
            pstmt.setString(2, user1.getName());
            pstmt.setString(3, user1.getEmail());
            pstmt.setString(4, user1.getPassword());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public user find_user_by_roll(String roll) {
        String sql = "SELECT fullName, email, password FROM buyers WHERE roll = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, roll);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new user(rs.getString("fullName"), rs.getString("email"), roll, rs.getString("password"));
            }
        } catch (SQLException e) {
            logger.severe("Error retrieving user: " + e.getMessage());
        }
        return null;
    }

    // --- SELLER METHODS ---
    public boolean register_seller(String name, String email, String phone, String shop, String pass) {
        String sql = "INSERT INTO sellers(phone, name, email, shopName, password) VALUES(?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setString(4, shop);
            pstmt.setString(5, pass);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean authenticate_seller(String phone, String password) {
        String sql = "SELECT name FROM sellers WHERE phone = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    // --- ITEM METHODS ---
    public boolean add_item(String name, double price, String category, String description, String imagePath) {
        String query = "INSERT INTO items (item_name, price, category, description, image_path) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, category);
            ps.setString(4, description);
            ps.setString(5, imagePath);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public ResultSet getAllItems() {
        try {
            String query = "SELECT * FROM items ORDER BY id DESC";
            Statement st = connection.createStatement();
            return st.executeQuery(query);
        } catch (SQLException e) {
            return null;
        }
    }
}