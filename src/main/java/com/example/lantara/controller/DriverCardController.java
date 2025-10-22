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

    // Variabel FXML baru
    @FXML private Label statusLabel;
    @FXML private VBox assignmentInfoBox;
    @FXML private Label vehicleInfoLabel;

    private Driver currentDriver;
    private DriverViewController driverViewController;

    // Metode setData diubah untuk menerima Assignment
    public void setData(Driver driver, Assignment assignment, DriverViewController controller) {
        this.currentDriver = driver;
        this.driverViewController = controller;

        namaLabel.setText(driver.getNama());
        nikLabel.setText("NIK: " + driver.getNomorIndukKaryawan());
        simLabel.setText("SIM: " + driver.getNomorSIM());

        // Logika untuk menampilkan status dan info penugasan
        if (assignment != null && "Berlangsung".equals(assignment.getStatusTugas())) {
            // Jika sedang bertugas
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
            // Jika tersedia
            statusLabel.setText("Tersedia");
            statusLabel.getStyleClass().removeAll("driver-status-bertugas");
            statusLabel.getStyleClass().add("driver-status-tersedia");
            
            assignmentInfoBox.setVisible(false);
            assignmentInfoBox.setManaged(false);
        }
    }

    @FXML
    private void handleEditAction() {
        System.out.println("Edit driver: " + currentDriver.getNama());
    }

    @FXML
    private void handleDeleteAction() {
        System.out.println("Hapus driver: " + currentDriver.getNama());
    }
}