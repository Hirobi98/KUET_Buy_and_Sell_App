package com.example.kuet_buy_and_sell_app;


public class seller {
    private static String loggedInPhone = null;
    private static String loggedInName = null;

    public static void startSession(String phone, String name) {
        loggedInPhone = phone;
        loggedInName = name;
    }

    public static String getPhone() { return loggedInPhone; }
    public static String getName() { return loggedInName; }

    public static void clear() {
        loggedInPhone = null;
        loggedInName = null;
    }

    public static void logout() {
        clear();
    }
}