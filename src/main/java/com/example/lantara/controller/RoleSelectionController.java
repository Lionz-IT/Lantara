package com.example.lantara.controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.example.lantara.MainApp;

public class RoleSelectionController {

    @FXML
    private Button managerButton;

    @FXML
    private void handleManagerButton() {
        openLoginScreen("MANAJER");
    }

    @FXML
    private void handleStaffButton() {
        openLoginScreen("STAF");
    }

    private void openLoginScreen(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/login-view.fxml"));
            Parent root = loader.load();

            LoginViewController loginController = loader.getController();
            loginController.initRole(role);

            Stage stage = new Stage();
            stage.setTitle("LANTARA - Login " + role.substring(0, 1).toUpperCase() + role.substring(1).toLowerCase());

            // --- UBAH UKURAN TINGGI JENDELA DI SINI ---
            Scene scene = new Scene(root, 600, 600); // Tinggi diubah dari 400 menjadi 550
            
            stage.setScene(scene);
            stage.show();

            closeWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) managerButton.getScene().getWindow();
        stage.close();
    }
}