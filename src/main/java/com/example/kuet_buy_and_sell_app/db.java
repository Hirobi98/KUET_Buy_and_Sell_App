package com.example.kuet_buy_and_sell_app;

import java.sql.*;
import java.util.logging.Logger;

/**
 * Singleton class to manage SQLite database operations.
 */
public class db {
    private Connection connection;
    private final Logger logger = Logger.getLogger(this.getClass().getName());
    private static db instance = null;

    // Fixed: The actual database file path string
    private final String DB_url = "jdbc:sqlite:kuet_marketplace4.db";

    private db() {}

    /**
     * Singleton accessor.
     */
    public static db b() {
        if (instance == null) {
            instance = new db();
            instance.c();
        }
        return instance;
    }

    /**
     * Connects to the database and initializes tables.
     */
    public void c() {
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
        String itemSql = "CREATE TABLE IF NOT EXISTS items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "item_name TEXT, "
                + "price REAL, "
                + "category TEXT, "
                + "description TEXT, "
                + "image_path TEXT, "
                + "seller_phone TEXT, "
                + "seller_name TEXT, "
                + "status TEXT DEFAULT 'Available');";

        try (Statement st = connection.createStatement()) {
            st.execute(buyerSql);
            st.execute(sellerSql);
            st.execute(itemSql);

            // Column check logic
            try {
                st.execute("ALTER TABLE items ADD COLUMN status TEXT DEFAULT 'Available'");
            } catch (SQLException e) {
                // Column already exists, safe to ignore
            }
        } catch (SQLException e) {
            logger.severe("Table Creation Error: " + e.getMessage());
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
            if (rs.next()) {
                seller.startSession(phone, rs.getString("name"));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getSellerNameByPhone(String phone) {
        String sql = "SELECT name FROM sellers WHERE phone = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("name");
            }
        } catch (SQLException e) {
            logger.severe("Error getting seller name: " + e.getMessage());
        }
        return "Seller";
    }

    // --- ITEM METHODS ---
    public boolean add_item(String name, double price, String category, String description, String imagePath, String sPhone, String sName) {
        String query = "INSERT INTO items (item_name, price, category, description, image_path, seller_phone, seller_name, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'Available')";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, category);
            ps.setString(4, description);
            ps.setString(5, imagePath);
            ps.setString(6, sPhone);
            ps.setString(7, sName);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateItemStatus(int id, String newStatus) {
        String query = "UPDATE items SET status = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, newStatus);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public ResultSet getAvailableItems() {
        // FIX: Explicitly include 'seller_name' in the query
        String sql = "SELECT id, item_name, price, category, description, image_path, status, seller_phone, seller_name FROM items WHERE status = 'Available'";
        try {
            Statement stmt = connection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            logger.severe("Error fetching available items: " + e.getMessage());
            return null;
        }
    }

    public ResultSet getSellerItems(String phone) {
        // FIX: Explicitly include 'seller_name' in the query
        String sql = "SELECT id, item_name, price, category, description, image_path, status, seller_phone, seller_name FROM items WHERE seller_phone = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, phone);
            return pstmt.executeQuery();
        } catch (SQLException e) {
            logger.severe("Error fetching seller items: " + e.getMessage());
            return null;
        }
    }

    public int getSellerPostCount(String phone) {
        String sql = "SELECT COUNT(*) as total FROM items WHERE seller_phone = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            logger.severe("Error counting posts: " + e.getMessage());
        }
        return 0;
    }

    public boolean deleteItem(int itemId) {
        String query = "DELETE FROM items WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, itemId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.severe("Error deleting item: " + e.getMessage());
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.severe("Error closing database connection: " + e.getMessage());
        }
    }

    // Helper to ensure connection is live for controllers
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                c();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}