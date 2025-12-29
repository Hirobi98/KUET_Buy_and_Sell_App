package com.example.kuet_buy_and_sell_app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.ResultSet;

public class HelloController {
    private final db databaseManager = db.b();

    @FXML private TextField txtNameSignup, txtEmailSignup, txtRollSignup;
    @FXML private PasswordField pfPasswordSignup;
    @FXML private Label lblSignupStatus;

    @FXML private TextField txtRollLogin;
    @FXML private PasswordField pfPasswordLogin;
    @FXML private Label lblLoginStatus;


    @FXML private TextField txtSellerName, txtSellerEmail, txtSellerPhone, txtSellerShop;
    @FXML private PasswordField pfSellerPass;
    @FXML private Label lblSellerSignupStatus;

    @FXML private TextField txtSellerPhoneLogin;
    @FXML private PasswordField pfSellerPassLogin;
    @FXML private Label lblSellerLoginStatus;

    @FXML private TextField nameField, priceField, priceField1; // priceField1 is Category
    @FXML private VBox itemPostContainer;

    @FXML
    public void initialize() {

        if (itemPostContainer != null) {
            loadMarketplace();
        }
    }


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
                loadScene(event, "sellerloginview.fxml", "Seller Login");
            } else {
                lblSellerSignupStatus.setText("Error: Already registered.");
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void handleSellerLogin(ActionEvent event) throws IOException {
        if (databaseManager.authenticate_seller(txtSellerPhoneLogin.getText(), pfSellerPassLogin.getText())) {
            switch_to_post_item(event);
        } else {
            lblSellerLoginStatus.setText("Invalid credentials.");
        }
    }

    @FXML
    public void handlePostItem(ActionEvent event) {
        try {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            String cat = priceField1.getText();
            if (databaseManager.add_item(name, price, cat, "No description", "")) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Item posted!");
                switch_to_seller_marketview(event);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Check input fields.");
        }
    }

    public void loadMarketplace() {
        itemPostContainer.getChildren().clear();
        try {
            ResultSet rs = databaseManager.getAllItems();
            while (rs != null && rs.next()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("card.fxml"));
                Node card = loader.load();
                cardcontroller controller = loader.getController();
                controller.setData(rs.getString("item_name"), rs.getString("category"),
                        rs.getDouble("price"), rs.getString("description"), rs.getString("image_path"));
                itemPostContainer.getChildren().add(card);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }


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
    public void switch_to_scene3(ActionEvent event) throws IOException {

        loadScene(event, "sellerlogin.fxml", "Seller Login");
    }

    @FXML
    public void switch_to_marketview(ActionEvent event) throws IOException {
        loadScene(event, "buyersmarketplaceview.fxml", "Marketplace");
    }
    @FXML
    public void switch_to_seller_marketview(ActionEvent event) throws IOException {
        loadScene(event, "sellermarketview.fxml", "Marketplace");
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
    public void handleCancel(ActionEvent event) throws IOException {
        switch_to_home(event);
    }

    private void loadScene(ActionEvent event, String fxmlFile, String title) throws IOException {
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
}