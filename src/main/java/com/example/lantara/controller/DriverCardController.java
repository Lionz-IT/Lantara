package com.example.lantara.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import com.example.lantara.model.Driver;
import com.example.lantara.model.Assignment;
import com.example.lantara.model.Vehicle;

public class DriverCardController {

    @FXML private ImageView avatarImageView;
    @FXML private Label namaLabel;
    @FXML private Label nikLabel;
    @FXML private Label simLabel;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Label statusLabel;
    @FXML private VBox assignmentInfoBox;
    @FXML private Label vehicleInfoLabel;

    private Driver currentDriver;
    private DriverViewController driverViewController;

    public void setData(Driver driver, Assignment assignment, DriverViewController controller) {
        this.currentDriver = driver;
        this.driverViewController = controller;

        namaLabel.setText(driver.getNama());
        nikLabel.setText("NIK: " + driver.getNomorIndukKaryawan());
        simLabel.setText("SIM: " + driver.getNomorSIM());

        if (assignment != null && "Berlangsung".equals(assignment.getStatusTugas())) {
            statusLabel.setText("Bertugas");
            statusLabel.getStyleClass().removeAll("driver-status-tersedia");
            statusLabel.getStyleClass().add("driver-status-bertugas");
            
            Vehicle v = assignment.getVehicle();
            if (v != null) {
                vehicleInfoLabel.setText(v.getMerek() + " (" + v.getNomorPolisi() + ")");
            } else {
                vehicleInfoLabel.setText("N/A");
            }
            assignmentInfoBox.setVisible(true);
            assignmentInfoBox.setManaged(true);
            
        } else {
            statusLabel.setText("Tersedia");
            statusLabel.getStyleClass().removeAll("driver-status-bertugas");
            statusLabel.getStyleClass().add("driver-status-tersedia");
            
            assignmentInfoBox.setVisible(false);
            assignmentInfoBox.setManaged(false);
        }
    }

    @FXML
    private void handleEditAction() {
        // Panggil metode openEditForm di controller utama
        driverViewController.openEditForm(currentDriver);
    }

    @FXML
    private void handleDeleteAction() {
        // Panggil metode deleteDriver di controller utama
        driverViewController.deleteDriver(currentDriver);
    }
}