package com.example.kuet_buy_and_sell_app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class cardcontroller {
    @FXML private Label itemNameLabel, categoryLabel, priceLabel, descriptionLabel;
    @FXML private ImageView itemImage;

    public void setData(String name, String cat, double price, String desc, String imgPath) {
        itemNameLabel.setText(name);
        categoryLabel.setText("Category: " + cat);
        priceLabel.setText("Price: ৳ " + price);
        descriptionLabel.setText(desc);

        try {
            if (imgPath != null && !imgPath.isEmpty()) {
                itemImage.setImage(new Image(getClass().getResourceAsStream(imgPath)));
            } else {

                itemImage.setImage(new Image(getClass().getResourceAsStream("/img/default_item.png")));
            }
        } catch (Exception e) {
            System.out.println("Image not found: " + imgPath);
        }
    }
}