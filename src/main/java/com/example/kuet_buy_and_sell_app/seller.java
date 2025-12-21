package com.example.kuet_buy_and_sell_app;


public class seller {
    private static String currentSellerPhone;
    private static String currentSellerName;

    public static void startSession(String phone, String name) {
        currentSellerPhone = phone;
        currentSellerName = name;
    }

    public static String getPhone() { return currentSellerPhone; }
    public static String getName() { return currentSellerName; }

    public static void clear() {
        currentSellerPhone = null;
        currentSellerName = null;
    }
}