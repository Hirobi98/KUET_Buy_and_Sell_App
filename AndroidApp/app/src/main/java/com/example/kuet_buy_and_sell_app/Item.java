package com.example.kuet_buy_and_sell_app;

public class Item {
    private int id;
    private String name;
    private String category;
    private double price;
    private String description;
    private String imagePath;
    private String status;
    private String sellerName;
    private String sellerPhone;
    private String buyerRoll;

    public Item(int id, String name, String category, double price, String description,
            String imagePath, String status, String sellerName, String sellerPhone, String buyerRoll) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.description = description;
        this.imagePath = imagePath;
        this.status = status;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
        this.buyerRoll = buyerRoll;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getStatus() {
        return status;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public String getBuyerRoll() {
        return buyerRoll;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBuyerRoll(String buyerRoll) {
        this.buyerRoll = buyerRoll;
    }
}
