package com.example.kuet_buy_and_sell_app;

public class user {
    private String name;
    private String email;
    private static String roll;
    private String password;

    public user(String name, String email, String roll, String password) {
        this.name = name;
        this.email = email;
        this.roll = roll;
        this.password = password;
    }

    //getfunctions
    public String getName(){
        return name;
    }
    public String getEmail(){
        return email;
    }
    public static String getRoll(){
        return roll;
    }
    public String getPassword(){
        return password;
    }
}
