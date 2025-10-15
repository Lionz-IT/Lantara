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
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("view/role-selection-view.fxml"));
        // Ukuran jendela default disesuaikan untuk dashboard
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720); 
        stage.setTitle("LANTARA - Pilih Peran");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        // Inisialisasi database sebelum aplikasi JavaFX berjalan
        DatabaseHelper.initializeDatabase(); 
        launch(args);
    }
}