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
import com.example.lantara.MainApp;
import com.example.lantara.model.DatabaseHelper;
import com.example.lantara.model.User;

public class LoginViewController {

    // Variabel FXML
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorMessageLabel;
    @FXML private VBox welcomeBox;
    @FXML private VBox loginBox;

    // Variabel data
    private String selectedRole;

    @FXML
    public void initialize() {
        playIntroAnimation();
        setupKeyboardNavigation();
    }

    /**
     * Menerima peran yang dipilih dari RoleSelectionController.
     */
    public void initRole(String role) {
        this.selectedRole = role;
    }

    /**
     * Menangani aksi saat tombol Login utama ditekan.
     */
    @FXML
    protected void handleLoginButton() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        User authenticatedUser = authenticate(username, password);

        if (authenticatedUser != null) {
            closeLoginWindow();
            openMainWindow(authenticatedUser);
        } else {
            errorMessageLabel.setText("Username atau password salah untuk peran ini!");
        }
    }

    /**
     * Menangani aksi saat tombol "Kembali" ditekan.
     */
    @FXML
    private void handleBackButton() {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource("view/role-selection-view.fxml"));
            Stage roleStage = new Stage();
            roleStage.setTitle("LANTARA - Pilih Peran");
            roleStage.setScene(new Scene(root));
            roleStage.show();

            closeLoginWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mengatur semua navigasi keyboard (Enter, Atas, Bawah).
     */
    private void setupKeyboardNavigation() {
        // Pindah fokus ke password saat Enter di username
        usernameField.setOnAction(event -> passwordField.requestFocus());
        
        // Klik tombol login saat Enter di password
        passwordField.setOnAction(event -> loginButton.fire());

        // Navigasi dengan panah Atas/Bawah
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                passwordField.requestFocus();
            }
        });
        
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                usernameField.requestFocus();
            }
        });
    }

    /**
     * Mengautentikasi pengguna menggunakan DatabaseHelper.
     */
    private User authenticate(String username, String password) {
        return DatabaseHelper.validateUser(username, password, selectedRole);
    }

    /**
     * Menjalankan animasi intro saat halaman dimuat.
     */
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
        
        sequentialTransition.setOnFinished(event -> {
            usernameField.requestFocus();
        });

        sequentialTransition.play();
    }

    /**
     * Membuka jendela dashboard utama setelah login berhasil.
     */
    private void openMainWindow(User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("view/dashboard-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load()); 
            DashboardController dashboardController = fxmlLoader.getController();
            dashboardController.initData(user);

            Stage mainStage = new Stage();
            mainStage.setTitle("LANTARA - Manajemen Armada");
            mainStage.setScene(scene);
            mainStage.setMaximized(true); // Buka dalam mode maximized
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Menutup jendela login saat ini.
     */
    private void closeLoginWindow() {
        Stage stage = (Stage) loginButton.getScene().getWindow();
        stage.close();
    }
}