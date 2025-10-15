package com.example.lantara.controller;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.example.lantara.MainApp;

import java.io.IOException;

public class SplashViewController {

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        // Mulai animasi saat FXML dimuat
        playWelcomeAnimation();
    }

    private void playWelcomeAnimation() {
        // 1. Buat efek blur awal
        GaussianBlur blur = new GaussianBlur(10.0);
        welcomeLabel.setEffect(blur);
        welcomeLabel.setOpacity(0); // Mulai dari transparan

        // 2. Animasi Gerakan (Translate Transition)
        TranslateTransition translate = new TranslateTransition(Duration.seconds(2.5), welcomeLabel);
        translate.setFromY(-100); // Mulai dari posisi 100 piksel di atas
        translate.setToY(0);      // Berakhir di posisi tengah

        // 3. Animasi Blur dan Opacity (Timeline)
        Timeline timeline = new Timeline();
        // KeyValue untuk membuat blur menjadi 0 (jelas)
        KeyValue kvBlur = new KeyValue(blur.radiusProperty(), 0.0);
        // KeyValue untuk membuat opacity menjadi 1 (terlihat)
        KeyValue kvOpacity = new KeyValue(welcomeLabel.opacityProperty(), 1.0);
        
        KeyFrame kf = new KeyFrame(Duration.seconds(2.5), kvBlur, kvOpacity);
        timeline.getKeyFrames().add(kf);

        // 4. Gabungkan kedua animasi untuk berjalan bersamaan
        ParallelTransition parallelTransition = new ParallelTransition(translate, timeline);

        // 5. Aksi setelah animasi selesai
        parallelTransition.setOnFinished(event -> {
            // Tunggu sebentar lalu buka jendela login
            try {
                Thread.sleep(500); // Jeda 0.5 detik
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            openLoginScreen();
            closeSplashScreen();
        });

        parallelTransition.play();
    }

    private void openLoginScreen() {
        try {
            Parent root = FXMLLoader.load(MainApp.class.getResource("view/login-view.fxml"));
            Stage loginStage = new Stage();
            loginStage.setTitle("LANTARA - Login");
            loginStage.setScene(new Scene(root, 400, 300));
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSplashScreen() {
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.close();
    }
}