package com.example.kuet_buy_and_sell_app;

import java.util.List;

public interface DatabaseInterface {
    // User/Seller Auth
    boolean registerUser(String name, String email, String roll, String password);

    boolean loginUser(String roll, String password);

    boolean registerSeller(String name, String email, String phone, String shopName, String password);

    boolean loginSeller(String phone, String password);

    String getSellerNameByPhone(String phone);

    // Items
    List<Item> getAllItems();

    List<Item> getItemsByCategory(String category);

    List<Item> getItemsBySeller(String phone);

    List<Item> getItemsByBuyer(String roll); // For My Orders

    boolean addItem(String name, double price, String category, String desc, String imgPath, String sellerPhone,
            String sellerName);

    boolean deleteItem(int itemId);

    // Status Flow
    boolean requestPurchase(int itemId, String buyerRoll);

    boolean updateItemStatus(int itemId, String status); // For Accept/Decline/Mark Sold

    boolean addReview(int itemId, String buyerRoll, int rating, String comment);
}
