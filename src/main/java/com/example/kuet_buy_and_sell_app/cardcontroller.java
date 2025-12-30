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
    @FXML private Button btnAccept, btnDecline;
    @FXML private Label ownerNameLabel;

    private int itemId;
    private HelloController parentController;

    /**
     * Populates the card with data from the database.
     */


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

        if (btnAccept != null) { btnAccept.setVisible(false); btnAccept.setManaged(false); }
        if (btnDecline != null) { btnDecline.setVisible(false); btnDecline.setManaged(false); }

        if (isOwner) {
            // Seller Controls
            if (btnDelete != null) btnDelete.setVisible(true);
            if (btnAction != null) btnAction.setVisible(false); // Seller can't buy own item

            if ("Pending".equalsIgnoreCase(status)) {
                // Someone wants to buy! Show Accept/Decline
                if (btnAccept != null) { btnAccept.setVisible(true); btnAccept.setManaged(true); }
                if (btnDecline != null) { btnDecline.setVisible(true); btnDecline.setManaged(true); }
                if (btnMarkSold != null) btnMarkSold.setVisible(false);
            } else {
                if (btnMarkSold != null) btnMarkSold.setVisible(!"Sold".equalsIgnoreCase(status));
            }
        } else {
            // Buyer Controls
            if (btnDelete != null) btnDelete.setVisible(false);
            if (btnMarkSold != null) btnMarkSold.setVisible(false);

            // Show "Buy Now" only if item is Available
            if (btnAction != null) {
                btnAction.setVisible("Available".equalsIgnoreCase(status));
            }

            // Special message for Buyer if seller accepted
            if ("Accepted".equalsIgnoreCase(status) && statusLabel != null) {
                statusLabel.setText("ACCEPTED! Meet at KUET Cafeteria");
            }
        }
    }

    private void setDefaultImage() {
        try {
            InputStream is = getClass().getResourceAsStream("/img/default_item.png");
            if (is != null) itemImage.setImage(new Image(is));
        } catch (Exception ignored) {}
    }

    // NEW: Action for the "Buy Now" button
    @FXML
    private void handleBuyNow() {
        String myRoll = user.getRoll();
        if (db.b().requestPurchase(itemId, myRoll)) {
            refreshUI();
        }
    }

    // NEW: Action for Seller to Accept
    @FXML
    private void handleAccept() {
        if (db.b().updateItemStatus(itemId, "Accepted")) {
            refreshUI();
        }
    }

    // NEW: Action for Seller to Decline
    @FXML
    private void handleDecline() {
        if (db.b().updateItemStatus(itemId, "Available")) {
            refreshUI();
        }
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