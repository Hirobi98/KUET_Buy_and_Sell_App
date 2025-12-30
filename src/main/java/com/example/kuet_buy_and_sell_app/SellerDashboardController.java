package com.example.kuet_buy_and_sell_app;

import com.example.kuet_buy_and_sell_app.cardcontroller;
import com.example.kuet_buy_and_sell_app.seller;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.io.IOException;
import java.sql.ResultSet;

public class SellerDashboardController extends HelloController {

    @FXML
    @Override
    public void initialize() {
        // Run refresh after FXML nodes are loaded
        Platform.runLater(this::refreshDashboard);
    }

    public void refreshDashboard() {
        String sPhone = seller.getPhone();
        if (sPhone == null || itemPostContainer == null) return;

        // Update post count label
        int count = databaseManager.getSellerPostCount(sPhone);
        if (lblSellerPostCount != null) {
            lblSellerPostCount.setText(String.valueOf(count));
        }

        // Load items specific to this seller
        itemPostContainer.getChildren().clear();
        try (ResultSet rs = databaseManager.getSellerItems(sPhone)) {
            while (rs != null && rs.next()) {
                // Pass true so buttons (delete/sold) appear for the owner
                loadCardIntoContainer(rs, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    @Override
    public void handleLogout(ActionEvent event) throws IOException {
        seller.logout();
        switch_to_home(event);
    }
}