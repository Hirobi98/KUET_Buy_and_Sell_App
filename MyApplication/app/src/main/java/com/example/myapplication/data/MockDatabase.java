package com.example.myapplication.data;

import com.example.myapplication.model.Item;
import com.example.myapplication.model.User;

import java.util.ArrayList;
import java.util.List;

public class  MockDatabase {
    private static MockDatabase instance;
    private List<User> users;
    private List<Item> items;
    private User currentUser;

    private MockDatabase() {
        users = new ArrayList<>();
        items = new ArrayList<>();

        // Seed Users
        // Buyer: Roll, Pass
        users.add(new User("Test Buyer", "buyer@kuet.ac.bd", "123456", "1907000", "Buyer", true));
        // Seller: Phone, Pass
        users.add(new User("Test Seller", "seller@gmail.com", "123456", "01700000000", "My Shop", "Seller"));

        // Seed Items (Updated Constructor: ID, Name, Price, Desc, Category, SellerName,
        // SellerEmail, SellerPhone, Img, Status)
        items.add(new Item("1", "Calculus Book", 250.0, "Used calculus book, good condition", "Books-Notes",
                "Test Seller", "seller@gmail.com", "01700000000", "book_image", "Available"));

        items.add(new Item("2", "Scientific Calculator", 800.0, "Casio fx-991EX", "Electronics",
                "Test Seller", "seller@gmail.com", "01700000000", "calc_image", "Pending"));

        items.add(new Item("3", "Drafting Table", 1500.0, "Wooden drafting table", "Furniture",
                "Test Seller", "seller@gmail.com", "01700000000", "table_image", "Sold"));
    }

    public static synchronized MockDatabase getInstance() {
        if (instance == null) {
            instance = new MockDatabase();
        }
        return instance;
    }

    public User login(String identifier, String password, String role) {
        for (User u : users) {
            // Check Role First
            if (!u.getRole().equalsIgnoreCase(role))
                continue;

            // Check Credentials
            if (role.equalsIgnoreCase("Buyer")) {
                if (u.getRoll().equals(identifier) && u.getPassword().equals(password)) {
                    currentUser = u;
                    return u;
                }
            } else {
                if (u.getPhone().equals(identifier) && u.getPassword().equals(password)) {
                    currentUser = u;
                    return u;
                }
            }
        }
        return null;
    }

    public void signUp(User user) {
        users.add(user);
        currentUser = user;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void deleteItem(Item item) {
        items.remove(item);
    }

    public void updateItemStatus(String itemId, String newStatus) {
        for (Item item : items) {
            if (item.getId().equals(itemId)) {
                item.setStatus(newStatus);
                break;
            }
        }
    }
}
