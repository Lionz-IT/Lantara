package com.example.lantara;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.lantara.model.DatabaseHelper;

// 1. Import kelas Image
import javafx.scene.image.Image;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("view/landing-page-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("LANTARA");

        // 2. Tambahkan baris ini untuk mengatur ikon
        stage.getIcons().add(new Image(MainApp.class.getResourceAsStream("view/assets/logo.png")));

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        DatabaseHelper.initializeDatabase();
        launch(args);
    }
}