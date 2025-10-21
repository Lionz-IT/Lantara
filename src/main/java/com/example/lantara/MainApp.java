package com.example.lantara;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.lantara.model.DatabaseHelper;

// Pastikan import Image ada
import javafx.scene.image.Image;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Path ke FXML pertama Anda (sesuaikan jika perlu)
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("view/landing-page-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("LANTARA");

        // Panggil method helper untuk mengatur ikon
        MainApp.setStageIcon(stage);

        stage.setScene(scene);
        stage.show();
    }

    // --- METHOD HELPER YANG PERLU DITAMBAHKAN ---
    /**
     * Mengatur ikon aplikasi untuk Stage yang diberikan.
     * @param stage Stage yang akan diatur ikonnya.
     */
    public static void setStageIcon(Stage stage) {
        // Pastikan path ke logo.png sudah benar
        // Path ini mencari: src/main/resources/com/example/lantara/assets/logo.png
        try {
            Image icon = new Image(MainApp.class.getResourceAsStream("/com/example/lantara/assets/logo.png"));
            stage.getIcons().add(icon);
        } catch (NullPointerException e) {
            System.err.println("Gagal memuat ikon aplikasi: logo.png tidak ditemukan di path yang benar.");
            // e.printStackTrace(); // Uncomment jika ingin melihat detail error
        } catch (Exception e) {
            System.err.println("Terjadi error saat memuat ikon aplikasi.");
            e.printStackTrace();
        }
    }
    // ---------------------------------------------

    public static void main(String[] args) {
        DatabaseHelper.initializeDatabase(); // Pastikan method ini ada dan berfungsi
        launch(args);
    }
}