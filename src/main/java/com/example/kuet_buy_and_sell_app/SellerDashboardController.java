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
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.ResultSet;

public class SellerDashboardController extends HelloController {
    @FXML private VBox reviewContainer;

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

        if (reviewContainer != null) {
            reviewContainer.getChildren().clear();
            try (ResultSet rs = databaseManager.getSellerReviews(sPhone)) {
                while (rs != null && rs.next()) {
                    String buyer = rs.getString("buyer_roll");
                    String text = rs.getString("review_text");
                    int rating = rs.getInt("rating");

                    // Create a simple UI element for each review
                    VBox reviewBox = new VBox(5);
                    reviewBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-background-radius: 10; -fx-border-color: #dee2e6;");

                    Label lblBuyer = new Label("From Buyer: " + buyer);
                    lblBuyer.setStyle("-fx-font-weight: bold;");

                    Label lblRating = new Label("Rating: " + "★".repeat(rating));
                    lblRating.setStyle("-fx-text-fill: #f39c12;");

                    Label lblComment = new Label("\"" + text + "\"");
                    lblComment.setWrapText(true);

                    reviewBox.getChildren().addAll(lblBuyer, lblRating, lblComment);
                    reviewContainer.getChildren().add(reviewBox);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    @Override
    public void handleLogout(ActionEvent event) throws IOException {
        seller.logout();
        switch_to_home(event);
    }
}