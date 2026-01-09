package com.example.kuet_buy_and_sell_app;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HelloController {
    final db databaseManager = db.b();

    // Buyer Signup/Login
    @FXML private TextField txtNameSignup, txtEmailSignup, txtRollSignup;
    @FXML private PasswordField pfPasswordSignup;
    @FXML private Label lblSignupStatus;
    @FXML private TextField txtRollLogin;
    @FXML private PasswordField pfPasswordLogin;
    @FXML private Label lblLoginStatus;

    // Seller Signup/Login
    @FXML private TextField txtSellerName, txtSellerEmail, txtSellerPhone, txtSellerShop;
    @FXML private PasswordField pfSellerPass;
    @FXML private Label lblSellerSignupStatus;
    @FXML private TextField txtSellerPhoneLogin;
    @FXML private PasswordField pfSellerPassLogin;
    @FXML private Label lblSellerLoginStatus;

    // Item Posting & Dashboard
    @FXML private TextField nameField, priceField, priceField1; // priceField1 is Category

   // For dashboard count
    @FXML protected VBox itemPostContainer;
    @FXML protected Label lblSellerPostCount;

    @FXML private Label lblImagePath;

    private String selectedImagePath = "";


    @FXML
    public void initialize() {
        // Use Platform.runLater to ensure FXML nodes are fully injected before loading data
        Platform.runLater(() -> {
            if (itemPostContainer != null && !(this instanceof SellerDashboardController)) {
                loadMarketplace();
            }
        });
    }

    @FXML
    public void handleSelectImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            // Convert to URI so JavaFX Image class can load it easily from disk
            selectedImagePath = selectedFile.toURI().toString();
            lblImagePath.setText(selectedFile.getName());
        }
    }

    public void loadMarketplace() {
        if (itemPostContainer == null) return;
        itemPostContainer.getChildren().clear();

        ResultSet rs = databaseManager.getAvailableItems();
        try {
            while (rs != null && rs.next()) {
                loadCardIntoContainer(rs, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected void loadCardIntoContainer(ResultSet rs, boolean isOwnerView) throws SQLException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("card.fxml"));
            VBox cardBox = loader.load();
            cardcontroller controller = loader.getController();

            int id = rs.getInt("id");
            // FIX: Changed "name" to "item_name" to match your DB schema
            String name = rs.getString("item_name");
            String cat = rs.getString("category");
            double price = rs.getDouble("price");
            String desc = rs.getString("description");
            String img = rs.getString("image_path");


            // NEW: Passing status and owner name to the card
            String status = rs.getString("status");
            String ownerName = rs.getString("seller_name");

            // UPDATED: Now passing all 10 parameters required by cardcontroller
            controller.setData(id, name, cat, price, desc, img, status, isOwnerView, ownerName, this);

            itemPostContainer.getChildren().add(cardBox);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // --- REGISTRATION & LOGIN LOGIC ---

    @FXML
    public void handleSignup(ActionEvent event) {
        try {
            String name = txtNameSignup.getText();
            String email = txtEmailSignup.getText();
            String roll = txtRollSignup.getText();
            String pass = pfPasswordSignup.getText();

            if (name.isEmpty() || email.isEmpty() || roll.isEmpty() || pass.isEmpty()) {
                lblSignupStatus.setText("Fill all fields!");
                return;
            }

            user newUser = new user(name, email, roll, pass);
            if (databaseManager.register_user(newUser)) {
                lblSignupStatus.setText("Success! Please Login.");
                loadScene(event, "buyerloginview.fxml", "Buyer Login");
            } else {
                lblSignupStatus.setText("Roll/Email already exists.");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void handleLogin(ActionEvent event) throws IOException {
        String roll = txtRollLogin.getText();
        String pass = pfPasswordLogin.getText();
        user u = databaseManager.find_user_by_roll(roll);
        if (u != null && u.getPassword().equals(pass)) {
            switch_to_marketview(event);
        } else {
            lblLoginStatus.setText("Invalid Roll or Password.");
        }
    }

    @FXML
    public void handleSellerSignup(ActionEvent event) {
        try {
            if (databaseManager.register_seller(txtSellerName.getText(), txtSellerEmail.getText(),
                    txtSellerPhone.getText(), txtSellerShop.getText(), pfSellerPass.getText())) {
                loadScene(event, "sellerlogin.fxml", "Seller Login");
            } else {
                lblSellerSignupStatus.setText("Error: Already registered.");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void handleSellerLogin(ActionEvent event) {
        String phone = txtSellerPhoneLogin.getText();
        String pass = pfSellerPassLogin.getText();

        if (databaseManager.authenticate_seller(phone, pass)) {
            String name = databaseManager.getSellerNameByPhone(phone);
            seller.startSession(phone, name);
            try {
                switch_to_seller_dashboard(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            lblSellerLoginStatus.setText("Invalid phone or password!");
        }
    }


    // --- ITEM & MARKETPLACE LOGIC ---

    @FXML
    public void handlePostItem(ActionEvent event) {
        try {
            String name = nameField.getText();
            String priceStr = priceField.getText();
            String cat = priceField1.getText(); // This is the Category field
            String sPhone = seller.getPhone();
            String sName = seller.getName();

            if (name.isEmpty() || priceStr.isEmpty() || sPhone == null) {
                System.out.println("Please login and fill all fields");
                return;
            }

            if (databaseManager.add_item(name, Double.parseDouble(priceStr), cat, "No description", selectedImagePath, sPhone, sName)) {
                switch_to_seller_dashboard(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // --- NAVIGATION LOGIC ---

    @FXML
    public void switch_to_home(ActionEvent event) throws IOException {
        loadScene(event, "hello-view.fxml", "Welcome Page");
    }

    @FXML
    public void switch_to_signup(ActionEvent event) throws IOException {
        loadScene(event, "buyersignup.fxml", "Sign Up");
    }

    @FXML
    public void switch_to_scene2(ActionEvent event) throws IOException {
        loadScene(event, "buyerloginview.fxml", "Buyer Login");
    }

    @FXML
    public void switch_to_marketview(ActionEvent event) throws IOException {
        loadScene(event, "buyersmarketplaceview.fxml", "Marketplace");
    }

    @FXML
    public void switch_to_seller_login(ActionEvent event) throws IOException {
        loadScene(event, "sellerlogin.fxml", "Seller Login");
    }

    @FXML
    public void switch_to_seller_signup(ActionEvent event) throws IOException {
        loadScene(event, "sellersignup.fxml", "Seller Signup");
    }

    @FXML
    public void switch_to_post_item(ActionEvent event) throws IOException {
        loadScene(event, "sellerview.fxml", "Post Item");
    }

    @FXML
    public void switch_to_seller_dashboard(ActionEvent event) throws IOException {
        loadScene(event, "seller_dashboard.fxml", "Seller Dashboard");
    }

    @FXML
    public void handleLogout(ActionEvent event) throws IOException {
        seller.clear();
        switch_to_home(event);
    }

    @FXML
    public void handleCancel(ActionEvent event) throws IOException {
        if (seller.getPhone() != null) {
            switch_to_seller_dashboard(event);
        } else {
            switch_to_home(event);
        }
    }

    protected void loadScene(ActionEvent event, String fxmlFile, String title) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    public void handleFilterCategory(ActionEvent event) {
        // Get the category name from the button text (Electronics, Books, etc.)
        Button btn = (Button) event.getSource();
        String category = btn.getText();

        if (itemPostContainer == null) return;
        itemPostContainer.getChildren().clear();

        // Fetch and load only items from this category
        try (ResultSet rs = databaseManager.getItemsByCategory(category)) {
            while (rs != null && rs.next()) {
                loadCardIntoContainer(rs, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void handleShowAll(ActionEvent event) {
        // Simply reloads the full marketplace view
        loadMarketplace();
    }
    @FXML
    public void showMyOrders(ActionEvent event) {
        if (itemPostContainer == null) return;
        itemPostContainer.getChildren().clear();

        String currentBuyerRoll = user.getRoll();

        try (ResultSet rs = databaseManager.getBuyerPurchases(currentBuyerRoll)) {
            while (rs != null && rs.next()) {
                loadCardIntoContainer(rs, false); // This will now show the Give Review button!
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}