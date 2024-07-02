package com.example.stickheroapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class homePage extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("Welcome to Stick Hero!!");
        System.out.println("             -Kanishk & Ayaan");
        Parent root = FXMLLoader.load(Objects.requireNonNull(homePage.class.getResource("home-screen.fxml")));
        Scene scene = new Scene(root);
        stage.setTitle("Stick Hero");
        stage.getIcons().add(new Image(Objects.requireNonNull(homePage.class.getResourceAsStream("images/H-1.png"))));
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
    }


    public static void main(String[] args) {
        launch(args);
    }
}