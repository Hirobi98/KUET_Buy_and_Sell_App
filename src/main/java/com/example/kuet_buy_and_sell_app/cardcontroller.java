package com.example.kuet_buy_and_sell_app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;

public class cardcontroller {
    @FXML private Label itemNameLabel, categoryLabel, priceLabel, descriptionLabel, statusLabel;
    @FXML private ImageView itemImage;
    @FXML private Button btnDelete, btnMarkSold, btnAction;

    private int itemId;
    private HelloController parentController;

    /**
     * Populates the card with data from the database.
     */
    @FXML private Label ownerNameLabel; // You must add this fx:id to your card.fxml

    public void setData(int id, String name, String cat, double price, String desc, String imgPath, String status, boolean isOwner, String ownerName, HelloController parent) {
        this.itemId = id;
        this.parentController = parent;

        if (itemNameLabel != null) itemNameLabel.setText(name);
        if (categoryLabel != null) categoryLabel.setText(cat);
        if (priceLabel != null) priceLabel.setText("৳ " + price);
        if (descriptionLabel != null) descriptionLabel.setText(desc);

        // NEW: Display the owner's name
        if (ownerNameLabel != null) {
            ownerNameLabel.setText("Seller: " + (ownerName != null ? ownerName : "Unknown"));
        }

        if (statusLabel != null) {
            statusLabel.setText(status);
            if ("Sold".equalsIgnoreCase(status)) statusLabel.setStyle("-fx-text-fill: red;");
            else statusLabel.setStyle("-fx-text-fill: green;");
        }

        // Image Loading Logic
        try {
            if (imgPath != null && !imgPath.isEmpty()) {
                InputStream is = getClass().getResourceAsStream(imgPath);
                if (is != null) {
                    itemImage.setImage(new Image(is));
                } else {
                    setDefaultImage();
                }
            } else {
                setDefaultImage();
            }
        } catch (Exception e) {
            setDefaultImage();
        }

        // Visibility logic: Seller sees Delete/Sold, Buyer sees "Buy Now" (btnAction)
        if (btnDelete != null) btnDelete.setVisible(isOwner);
        if (btnMarkSold != null) btnMarkSold.setVisible(isOwner && !"Sold".equalsIgnoreCase(status));

        // Hide the default marketplace "Buy Now" button if the owner is looking at their own card
        if (btnAction != null) btnAction.setVisible(!isOwner);
    }

    private void setDefaultImage() {
        try {
            InputStream is = getClass().getResourceAsStream("/img/default_item.png");
            if (is != null) itemImage.setImage(new Image(is));
        } catch (Exception ignored) {}
    }

    @FXML
    private void handleDelete() {
        if (db.b().deleteItem(itemId)) {
            refreshUI();
        }
    }

    @FXML
    private void handleMarkSold() {
        if (db.b().updateItemStatus(itemId, "Sold")) {
            refreshUI();
        }
    }

    private void refreshUI() {
        if (parentController instanceof SellerDashboardController) {
            ((SellerDashboardController) parentController).refreshDashboard();
        } else {
            parentController.loadMarketplace();
        }
    }




}