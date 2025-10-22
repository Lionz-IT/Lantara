package com.example.lantara.controller;

import java.io.IOException;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

// --- IMPORT YANG DIPERLUKAN ---
import com.example.lantara.MainApp;
import com.example.lantara.model.DatabaseHelper;
import com.example.lantara.model.User;
import com.example.lantara.controller.DashboardController; // Import ini juga
// -----------------------------

public class LoginViewController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorMessageLabel;
    @FXML private VBox welcomeBox;
    @FXML private VBox loginBox;
    @FXML private Label titleLabel;

    private String selectedRole;

    @FXML
    public void initialize() {
        playIntroAnimation();
        setupKeyboardNavigation();
    }

    public void initRole(String role) {
        this.selectedRole = role;
    }

    @FXML
    protected void handleLoginButton() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        User authenticatedUser = authenticate(username, password);
        if (authenticatedUser != null) {
            openMainWindow(authenticatedUser);
            closeLoginWindow();
        } else {
            errorMessageLabel.setText("Username atau password salah untuk peran ini!");
        }
    }

    @FXML
    private void handleBackButton() {
        try {
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
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

    private void setupKeyboardNavigation() {
        usernameField.setOnAction(event -> passwordField.requestFocus());
        passwordField.setOnAction(event -> loginButton.fire());
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) passwordField.requestFocus();
        });
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) usernameField.requestFocus();
        });
    }

    private User authenticate(String username, String password) {
        return DatabaseHelper.validateUser(username, password, selectedRole);
    }

    private void playIntroAnimation() {
        welcomeBox.setOpacity(0);
        loginBox.setOpacity(0);
        FadeTransition ftWelcome = new FadeTransition(Duration.millis(800), welcomeBox);
        ftWelcome.setToValue(1);
        TranslateTransition ttWelcome = new TranslateTransition(Duration.millis(800), welcomeBox);
        ttWelcome.setFromY(-20);
        ttWelcome.setToY(0);
        ParallelTransition welcomeAnimation = new ParallelTransition(ftWelcome, ttWelcome);
        FadeTransition ftLogin = new FadeTransition(Duration.millis(800), loginBox);
        ftLogin.setToValue(1);
        TranslateTransition ttLogin = new TranslateTransition(Duration.millis(800), loginBox);
        ttLogin.setFromY(20);
        ttLogin.setToY(0);
        ParallelTransition loginAnimation = new ParallelTransition(ftLogin, ttLogin);
        SequentialTransition sequentialTransition = new SequentialTransition(welcomeAnimation, loginAnimation);
        sequentialTransition.setOnFinished(event -> usernameField.requestFocus());
        sequentialTransition.play();
    }

    private void openMainWindow(User user) {
        try {
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            double width = currentStage.getWidth();
            double height = currentStage.getHeight();
            boolean isMaximized = currentStage.isMaximized();

            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/com/example/lantara/view/dashboard-view.fxml"));
            if (fxmlLoader.getLocation() == null) {
                 throw new IOException("Cannot find FXML file: /com/example/lantara/view/dashboard-view.fxml");
            }
            Scene scene = new Scene(fxmlLoader.load(), width, height); 
            
            DashboardController dashboardController = fxmlLoader.getController();
            dashboardController.initData(user);

            Stage mainStage = new Stage();
            mainStage.setTitle("LANTARA - Manajemen Armada");
            mainStage.setScene(scene);
            if (isMaximized) {
                mainStage.setMaximized(true);
            }
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void closeLoginWindow() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();
    }
}