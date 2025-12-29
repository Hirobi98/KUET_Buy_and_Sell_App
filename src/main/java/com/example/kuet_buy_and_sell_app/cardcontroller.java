package com.example.kuet_buy_and_sell_app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;

public class cardcontroller {
    @FXML private Label itemNameLabel, categoryLabel, priceLabel, statusLabel;
    @FXML private Button btnDelete, btnMarkSold;

    private int itemId;
    private HelloController parentController;

    public void setData(int id, String name, String cat, double price, String desc, String imgPath, String status, boolean isOwner, HelloController parent) {
        this.itemId = id;
        this.parentController = parent;

        if (itemNameLabel != null) itemNameLabel.setText(name);
        if (priceLabel != null) priceLabel.setText("৳ " + price);
        if (statusLabel != null) statusLabel.setText(status);

        if (btnDelete != null) btnDelete.setVisible(isOwner);
        if (btnMarkSold != null) btnMarkSold.setVisible(isOwner && !"Sold".equalsIgnoreCase(status));
    }

    @FXML
    private void handleDelete() {
        if (db.b().deleteItem(itemId)) {
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