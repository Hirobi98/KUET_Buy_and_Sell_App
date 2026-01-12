package com.example.myapplication.model;

public class User {
    private String name;
    private String email;
    private String password;
    private String phone; // For Sellers
    private String roll; // For Buyers
    private String shopName; // For Sellers
    private String role; // "Buyer" or "Seller"

    public User() {
    } // Required for Firestore

    // Constructor for Buyer
    public User(String name, String email, String password, String roll, String role, boolean isBuyer) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.roll = roll;
        this.role = role;
    }

    // Constructor for Seller
    public User(String name, String email, String password, String phone, String shopName, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.shopName = shopName;
        this.role = role;
    }

    // Generic Constructor (for seeding or legacy)
    public User(String name, String email, String password, String phone, String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getRoll() {
        return roll;
    }

    public String getShopName() {
        return shopName;
    }

    public String getRole() {
        return role;
    }

    public boolean isSeller() {
        return "Seller".equalsIgnoreCase(role);
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
