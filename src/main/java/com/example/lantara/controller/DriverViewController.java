package com.example.lantara.controller;

import java.io.IOException;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.example.lantara.MainApp;
import com.example.lantara.model.DatabaseHelper;
import com.example.lantara.model.Driver;
import com.example.lantara.model.User;

public class DriverViewController {

    @FXML
    private FlowPane driverContainer;

    private User currentUser;
    private ObservableList<Driver> driverList;
    private Timeline autoRefreshTimeline;

    /**
     * Menerima data pengguna dan memulai pemuatan data serta auto-refresh.
     */
    public void initData(User user) {
        this.currentUser = user;
        loadDriverData();
        setupAutoRefresh();
    }
    
    /**
     * Memuat data pengemudi dari database dan menampilkannya sebagai kartu.
     */
    private void loadDriverData() {
        this.driverList = DatabaseHelper.getAllDrivers();
        populateDriverCards();
    }

    /**
     * Mengosongkan kontainer dan menampilkan setiap pengemudi sebagai kartu baru.
     */
    private void populateDriverCards() {
        if (driverContainer == null) return; // Pengaman jika FXML belum siap
        
        driverContainer.getChildren().clear();
        for (Driver driver : driverList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/lantara/view/driver-card.fxml"));
                Pane card = loader.load();
                DriverCardController controller = loader.getController();
                controller.setData(driver, this);
                driverContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Mengatur dan memulai Timeline untuk auto-refresh setiap 1 detik.
     */
    private void setupAutoRefresh() {
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            System.out.println("Resreshing data"); 
            loadDriverData();
        });

        autoRefreshTimeline = new Timeline(keyFrame);
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();
    }

    /**
     * Menangani aksi tombol "Tambah Pengemudi".
     */
    @FXML
    private void handleAddDriver() {
        try {
            if (autoRefreshTimeline != null) autoRefreshTimeline.pause();

            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/add-driver-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Tambah Pengemudi Baru");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(driverContainer.getScene().getWindow());
            stage.showAndWait();

            loadDriverData();
            if (autoRefreshTimeline != null) autoRefreshTimeline.play();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}