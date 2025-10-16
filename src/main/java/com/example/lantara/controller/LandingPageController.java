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
            // Dapatkan stage saat ini dan tutup
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();

            // Buka jendela pemilihan peran
            Parent root = FXMLLoader.load(MainApp.class.getResource("view/role-selection-view.fxml"));
            Stage roleStage = new Stage();
            roleStage.setTitle("LANTARA - Pilih Peran");
            roleStage.setScene(new Scene(root));
            roleStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}