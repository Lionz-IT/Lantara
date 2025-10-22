package com.example.lantara.controller;

import java.io.IOException;
import java.net.URL;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

// --- IMPORT UNTUK KELAS-KELAS YANG DIPERLUKAN ---
import com.example.lantara.MainApp;
import com.example.lantara.model.User;
import com.example.lantara.controller.MainViewController;
import com.example.lantara.controller.AssignmentViewController;
import com.example.lantara.controller.DashboardContentController; // Ini yang hilang
import com.example.lantara.controller.DriverViewController;
// ---------------------------------------------

public class DashboardController {

    @FXML private AnchorPane contentArea;
    @FXML private Button btnDashboard;
    @FXML private Button btnKendaraan;
    @FXML private Button btnPengemudi;
    @FXML private Button btnPenugasan;
    @FXML private Button btnLogout;
    @FXML private Label welcomeLabel;

    private User currentUser;
    private Button currentButton;

    public void initData(User user) {
        this.currentUser = user;
        String username = user.getUsername();
        String capitalizedUsername = username.substring(0, 1).toUpperCase() + username.substring(1);
        welcomeLabel.setText("Selamat Datang, " + capitalizedUsername + "!");
        handleBtnDashboard();
    }
    
    @FXML private void handleBtnDashboard() { setActiveButton(btnDashboard); loadView("dashboard-content-view.fxml"); }
    @FXML private void handleBtnKendaraan() { setActiveButton(btnKendaraan); loadView("main-view.fxml"); }
    @FXML private void handleBtnPengemudi() { setActiveButton(btnPengemudi); loadView("driver-view.fxml"); }
    @FXML private void handleBtnPenugasan() { setActiveButton(btnPenugasan); loadView("assignment-view.fxml"); }
    
    @FXML
    private void handleBtnLogout() {
        try {
            Stage currentStage = (Stage) btnLogout.getScene().getWindow();
            double width = currentStage.getWidth();
            double height = currentStage.getHeight();
            boolean isMaximized = currentStage.isMaximized();
            currentStage.close();

            Parent root = FXMLLoader.load(MainApp.class.getResource("view/role-selection-view.fxml"));
            Stage roleStage = new Stage();
            roleStage.setTitle("LANTARA - Pilih Peran");
            roleStage.setScene(new Scene(root, width, height));
            if (isMaximized) {
                roleStage.setMaximized(true);
            }
            roleStage.show();
        } catch (IOException e) { 
            e.printStackTrace(); 
        }
    }

    private void setActiveButton(Button button) {
        if (currentButton != null) {
            currentButton.getStyleClass().remove("selected");
        }
        button.getStyleClass().add("selected");
        currentButton = button;
        playRippleAnimation(button);
    }
    
    private void playRippleAnimation(Button button) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.05); st.setToY(1.05);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        FadeTransition ft = new FadeTransition(Duration.millis(200), button);
        ft.setFromValue(0.7); ft.setToValue(1.0);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        ParallelTransition pt = new ParallelTransition(st, ft);
        pt.play();
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