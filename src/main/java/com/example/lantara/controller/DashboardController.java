package com.example.lantara.controller;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import com.example.lantara.MainApp;
import com.example.lantara.model.User;

public class DashboardController {

    @FXML private AnchorPane contentArea;
    @FXML private Button btnDashboard;
    @FXML private Button btnKendaraan;
    @FXML private Button btnPengemudi;
    @FXML private Button btnPenugasan;
    @FXML private Button btnLogout;
    @FXML private Label welcomeLabel;

    private User currentUser;
    private Button currentButton; // Variabel untuk melacak tombol yang aktif

    public void initData(User user) {
        this.currentUser = user;
        String username = user.getUsername();
        String capitalizedUsername = username.substring(0, 1).toUpperCase() + username.substring(1);
        welcomeLabel.setText("Selamat Datang, " + capitalizedUsername + "!");
        
        handleBtnDashboard();
    }
    
    @FXML
    private void handleBtnDashboard() {
        setActiveButton(btnDashboard);
        loadView("dashboard-content-view.fxml");
    }
    
    @FXML
    private void handleBtnKendaraan() {
        setActiveButton(btnKendaraan);
        loadView("main-view.fxml");
    }

    @FXML
    private void handleBtnPengemudi() {
        setActiveButton(btnPengemudi);
        loadView("driver-view.fxml"); 
    }

    @FXML
    private void handleBtnPenugasan() {
        setActiveButton(btnPenugasan);
        loadView("assignment-view.fxml");
    }
    
    @FXML
    private void handleBtnLogout() {
        try {
            Stage currentStage = (Stage) btnLogout.getScene().getWindow();
            currentStage.close();
            Parent root = FXMLLoader.load(MainApp.class.getResource("view/role-selection-view.fxml"));
            Stage roleStage = new Stage();
            roleStage.setTitle("LANTARA - Pilih Peran");
            roleStage.setScene(new Scene(root));
            roleStage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * Metode baru untuk mengatur gaya tombol yang aktif.
     */
    private void setActiveButton(Button button) {
        // Hapus gaya 'selected' dari tombol sebelumnya jika ada
        if (currentButton != null) {
            currentButton.getStyleClass().remove("selected");
        }
        // Tambahkan gaya 'selected' ke tombol yang baru ditekan
        button.getStyleClass().add("selected");
        // Perbarui referensi tombol saat ini
        currentButton = button;
    }

    private void loadView(String fxmlFile) {
        try {
            URL fileUrl = MainApp.class.getResource("view/" + fxmlFile);
            if (fileUrl == null) {
                throw new java.io.FileNotFoundException("File FXML tidak ditemukan: " + fxmlFile);
            }
            FXMLLoader loader = new FXMLLoader(fileUrl);
            Pane view = loader.load();
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);

            Object controller = loader.getController();
            if (controller instanceof MainViewController) {
                ((MainViewController) controller).initData(currentUser);
            } else if (controller instanceof AssignmentViewController) {
                ((AssignmentViewController) controller).initData(currentUser);
            } else if (controller instanceof DashboardContentController) {
                ((DashboardContentController) controller).initData(currentUser);
            } else if (controller instanceof DriverViewController) {
                ((DriverViewController) controller).initData(currentUser);
            }
            contentArea.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}