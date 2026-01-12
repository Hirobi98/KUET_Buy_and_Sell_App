package com.example.myapplication.model;

public class Item {
    private String id;
    private String name;
    private double price;
    private String description;
    private String category;
    private String sellerName;
    private String sellerEmail;
    private String sellerPhone;
    private String imageUrl; // For now, can be a resource ID name or empty
    private String status; // "Available", "Pending", "Accepted", "Sold"

    // Buying Cycle Fields
    private String buyerEmail;
    private String review;
    private float rating;

    public Item() {
    } // Required for Firestore

    public Item(String id, String name, double price, String description, String category, String sellerName,
            String sellerEmail, String sellerPhone, String imageUrl, String status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.sellerName = sellerName;
        this.sellerEmail = sellerEmail;
        this.sellerPhone = sellerPhone;
        this.imageUrl = imageUrl;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
