package com.example.kuet_buy_and_sell_app;

public class SessionManager {
    private static SessionManager instance;

    private String userRoll; // For Buyer
    private String userName;

    private String sellerPhone; // For Seller
    private String sellerName;

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Buyer Session
    public void startUserSession(String roll, String name) {
        this.userRoll = roll;
        this.userName = name;
        this.sellerPhone = null; // mutually exclusive usually, or can handle both
    }

    public String getUserRoll() {
        return userRoll;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isUserLoggedIn() {
        return userRoll != null;
    }

    // Seller Session
    public void startSellerSession(String phone, String name) {
        this.sellerPhone = phone;
        this.sellerName = name;
        this.userRoll = null;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public String getSellerName() {
        return sellerName;
    }

    public boolean isSellerLoggedIn() {
        return sellerPhone != null;
    }

    public void logout() {
        userRoll = null;
        userName = null;
        sellerPhone = null;
        sellerName = null;
    }
}
