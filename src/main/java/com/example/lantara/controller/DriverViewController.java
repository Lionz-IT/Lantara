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
import com.example.lantara.model.Assignment; // Import Assignment

public class DriverViewController {

    @FXML
    private FlowPane driverContainer;

    private User currentUser;
    private ObservableList<Driver> driverList;
    private Timeline autoRefreshTimeline;

    public void initData(User user) {
        this.currentUser = user;
        loadDriverData();
        setupAutoRefresh();
    }
    
    private void loadDriverData() {
        this.driverList = DatabaseHelper.getAllDrivers();
        populateDriverCards();
    }

    private void populateDriverCards() {
        if (driverContainer == null) return;
        
        driverContainer.getChildren().clear();
        
        // Ambil daftar penugasan yang sedang berlangsung
        ObservableList<Assignment> currentAssignments = AssignmentViewController.assignments;

        for (Driver driver : driverList) {
            try {
                // Cari penugasan aktif untuk driver ini
                Assignment activeAssignment = null;
                for (Assignment assignment : currentAssignments) {
                    if (assignment.getDriver().getNomorIndukKaryawan().equals(driver.getNomorIndukKaryawan()) 
                        && "Berlangsung".equals(assignment.getStatusTugas())) {
                        activeAssignment = assignment;
                        break;
                    }
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/lantara/view/driver-card.fxml"));
                Pane card = loader.load();
                DriverCardController controller = loader.getController();
                
                // Kirim driver DAN penugasannya (atau null) ke kartu
                controller.setData(driver, activeAssignment, this);
                
                driverContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setupAutoRefresh() {
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
            System.out.println("Resreshing data"); 
            loadDriverData();
        });
        autoRefreshTimeline = new Timeline(keyFrame);
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();
    }

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