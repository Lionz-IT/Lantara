package com.example.lantara.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.example.lantara.MainApp;

public class LandingPageController {

    @FXML
    private Button loginButton;

    @FXML
    private void handleLoginButton() {
        try {
            // Dapatkan stage & ukurannya saat ini
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            double width = currentStage.getWidth();
            double height = currentStage.getHeight();
            boolean isMaximized = currentStage.isMaximized();
            currentStage.close();

            // Buka jendela baru dengan ukuran yang sama
            Parent root = FXMLLoader.load(MainApp.class.getResource("view/role-selection-view.fxml"));
            Stage roleStage = new Stage();
            roleStage.setTitle("LANTARA - Pilih Peran");
            roleStage.setScene(new Scene(root, width, height)); // Gunakan ukuran yang disimpan

            if (isMaximized) {
                roleStage.setMaximized(true);
            }
            
            roleStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}