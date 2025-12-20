package com.example.kuet_buy_and_sell_app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloController {
    private final db databaseManager = db.b();

    @FXML private TextField txtNameSignup, txtEmailSignup, txtRollSignup;
    @FXML private PasswordField pfPasswordSignup;
    @FXML private Label lblSignupStatus;

    @FXML private TextField txtRollLogin;
    @FXML private PasswordField pfPasswordLogin;
    @FXML private Label lblLoginStatus;



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
                loadScene(event, "buyerloginview.fxml", "loginpage");
            } else {
                lblSignupStatus.setText("Roll/Email already exists.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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



    private void loadScene(ActionEvent event, String fxmlFile, String title) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
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

        loadScene(event, "buyerloginview.fxml", "Seller Login");
    }

    @FXML
    public void switch_to_marketview(ActionEvent event) throws IOException {
        loadScene(event, "buyersmarketplaceview.fxml", "Marketplace");
    }
}