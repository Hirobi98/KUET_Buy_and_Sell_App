package com.example.kuet_buy_and_sell_app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;
    private boolean l = false;


    @FXML
    public void switch_to_scene2(ActionEvent event) throws IOException {

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("buyerloginview.fxml"));
        Parent root = loader.load();

        Scene newScene = new Scene(root, currentStage.getScene().getWidth(), currentStage.getScene().getHeight());

        newScene.getStylesheets().addAll(currentStage.getScene().getStylesheets());

        currentStage.setScene(newScene);
        currentStage.setTitle("Buyer Login");
        currentStage.show();
    }

    @FXML
    public void switch_to_scene3(ActionEvent event) throws IOException {

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("buyerloginview.fxml"));
        Parent root = loader.load();

        Scene newScene = new Scene(root, currentStage.getScene().getWidth(), currentStage.getScene().getHeight());

        newScene.getStylesheets().addAll(currentStage.getScene().getStylesheets());

        currentStage.setScene(newScene);
        currentStage.setTitle("Seller Login");
        currentStage.show();
    }
    @FXML
    public void switch_to_home(ActionEvent event) throws IOException {

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Parent root = loader.load();

        Scene newScene = new Scene(root, currentStage.getScene().getWidth(), currentStage.getScene().getHeight());

        newScene.getStylesheets().addAll(currentStage.getScene().getStylesheets());

        currentStage.setScene(newScene);
        currentStage.setTitle("Welcome Page");
        currentStage.show();
    }
    public void switch_to_signup(ActionEvent event) throws IOException {

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("buyersignup.fxml"));
        Parent root = loader.load();

        Scene newScene = new Scene(root, currentStage.getScene().getWidth(), currentStage.getScene().getHeight());

        newScene.getStylesheets().addAll(currentStage.getScene().getStylesheets());

        currentStage.setScene(newScene);
        currentStage.setTitle("Seller Login");
        currentStage.show();
    }

    public void switch_to_marketview(ActionEvent event) throws IOException {

        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();


        FXMLLoader loader = new FXMLLoader(getClass().getResource("buyersmarketplaceview.fxml"));
        Parent root = loader.load();

        Scene newScene = new Scene(root, currentStage.getScene().getWidth(), currentStage.getScene().getHeight());

        newScene.getStylesheets().addAll(currentStage.getScene().getStylesheets());

        currentStage.setScene(newScene);
        currentStage.setTitle("MarketPlace view");
        currentStage.show();
    }

}
