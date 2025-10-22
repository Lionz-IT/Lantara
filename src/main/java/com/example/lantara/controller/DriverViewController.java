package com.example.lantara.controller;

import java.io.IOException;
import java.util.Optional;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.example.lantara.MainApp;
import com.example.lantara.model.DatabaseHelper;
import com.example.lantara.model.Driver;
import com.example.lantara.model.User;
import com.example.lantara.model.Assignment;

public class DriverViewController {

    @FXML
    private FlowPane driverContainer;

    private User currentUser;
    private ObservableList<Driver> driverList;
    private Timeline autoRefreshTimeline;

    public void initData(User user) {
        this.currentUser = user;
        loadDriverData();
    }
    
    // Ganti nama metode ini menjadi public agar bisa diakses
    public void loadDriverData() {
        this.driverList = DatabaseHelper.getAllDrivers();
        populateDriverCards();
    }

    private void populateDriverCards() {
        if (driverContainer == null) return;
        
        driverContainer.getChildren().clear();
        
        ObservableList<Assignment> currentAssignments = AssignmentViewController.assignments;

        for (Driver driver : driverList) {
            try {
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
                controller.setData(driver, activeAssignment, this);
                driverContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAddDriver() {
        try {
            if (autoRefreshTimeline != null) autoRefreshTimeline.pause();

            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/add-driver-view.fxml"));
            Parent root = loader.load();
            
            // Berikan referensi DriverViewController ke AddDriverController
            AddDriverController controller = loader.getController();
            controller.initData(this);

            Stage stage = new Stage();
            stage.setTitle("Tambah Pengemudi Baru");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(driverContainer.getScene().getWindow());
            stage.showAndWait();

            loadDriverData(); // Muat ulang setelah form ditutup
            if (autoRefreshTimeline != null) autoRefreshTimeline.play();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openEditForm(Driver driver) {
        try {
            if (autoRefreshTimeline != null) autoRefreshTimeline.pause();
            
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/edit-driver-view.fxml"));
            Parent root = loader.load();
            
            EditDriverController controller = loader.getController();
            controller.setDriver(driver); 

            Stage stage = new Stage();
            stage.setTitle("Edit Data Pengemudi");
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
    
    public void deleteDriver(Driver driver) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Pengemudi: " + driver.getNama());
        alert.setContentText("Apakah Anda yakin ingin menghapus data pengemudi ini?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DatabaseHelper.deleteDriver(driver.getNomorIndukKaryawan());
            loadDriverData();
        }
    }
}