package com.example.lantara;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.lantara.model.DatabaseHelper;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Arahkan ke landing-page-view.fxml sebagai tampilan awal
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("view/landing-page-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("LANTARA");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        // Inisialisasi database sebelum aplikasi JavaFX berjalan
        DatabaseHelper.initializeDatabase(); 
        launch(args);
    }
}