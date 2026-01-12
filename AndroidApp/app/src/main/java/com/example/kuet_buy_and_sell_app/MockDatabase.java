package com.example.kuet_buy_and_sell_app;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MockDatabase implements DatabaseInterface {
    private static MockDatabase instance;
    private List<Item> items = new ArrayList<>();
    // In a real mock, we might store users/sellers too,
    // but for UI testing we'll focus on Items and assume generic success for auth.

    private MockDatabase() {
        // Seed some data
        items.add(new Item(1, "Calculus Book", "Books", 250.0, "Used Calculus 101 Book",
                "book_img_path", "Available", "Rahim", "01700000001", null));
        items.add(new Item(2, "Scientific Calculator", "Electronics", 1200.0, "Casio fx-991EX",
                "calc_img_path", "Available", "Karim", "01700000002", null));
        items.add(new Item(3, "Drawing Board", "Stationery", 500.0, "A2 Size Board",
                "board_img_path", "Pending", "Rahim", "01700000001", "1807001")); // Pending item for Rahim
        items.add(new Item(4, "Bicycle", "Vehicle", 5000.0, "Phoenix Cycle",
                "cycle_img_path", "Sold", "Karim", "01700000002", "1807002"));
    }

    public static synchronized MockDatabase getInstance() {
        if (instance == null)
            instance = new MockDatabase();
        return instance;
    }

    @Override
    public boolean registerUser(String name, String email, String roll, String password) {
        return true;
    }

    @Override
    public boolean loginUser(String roll, String password) {
        return "1234".equals(password); // Simple mock check
    }

    @Override
    public boolean registerSeller(String name, String email, String phone, String shopName, String password) {
        return true;
    }

    @Override
    public boolean loginSeller(String phone, String password) {
        return "1234".equals(password);
    }

    @Override
    public String getSellerNameByPhone(String phone) {
        return "Mock Seller";
    }

    @Override
    public List<Item> getAllItems() {
        // Return only Available items for general marketplace
        return items.stream()
                .filter(i -> "Available".equalsIgnoreCase(i.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsByCategory(String category) {
        return items.stream()
                .filter(i -> "Available".equalsIgnoreCase(i.getStatus()) && i.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsBySeller(String phone) {
        return items.stream()
                .filter(i -> i.getSellerPhone().equals(phone))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemsByBuyer(String roll) {
        return items.stream()
                .filter(i -> roll.equals(i.getBuyerRoll()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean addItem(String name, double price, String category, String desc, String imgPath, String sellerPhone,
            String sellerName) {
        int newId = items.size() + 1;
        items.add(new Item(newId, name, category, price, desc, imgPath, "Available", sellerName, sellerPhone, null));
        return true;
    }

    @Override
    public boolean deleteItem(int itemId) {
        items.removeIf(i -> i.getId() == itemId);
        return true;
    }

    @Override
    public boolean requestPurchase(int itemId, String buyerRoll) {
        for (Item i : items) {
            if (i.getId() == itemId) {
                i.setStatus("Pending");
                i.setBuyerRoll(buyerRoll);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateItemStatus(int itemId, String status) {
        for (Item i : items) {
            if (i.getId() == itemId) {
                i.setStatus(status);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addReview(int itemId, String buyerRoll, int rating, String comment) {
        // In real DB, would save review table. For mock, just return true.
        return true;
    }
}
