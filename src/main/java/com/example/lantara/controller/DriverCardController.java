package com.example.lantara.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import com.example.lantara.model.Driver;

public class DriverCardController {

    @FXML private ImageView avatarImageView;
    @FXML private Label namaLabel;
    @FXML private Label nikLabel;
    @FXML private Label simLabel;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private Driver currentDriver;
    private DriverViewController driverViewController;

    public void setData(Driver driver, DriverViewController controller) {
        this.currentDriver = driver;
        this.driverViewController = controller;

        namaLabel.setText(driver.getNama());
        nikLabel.setText("NIK: " + driver.getNomorIndukKaryawan());
        simLabel.setText("SIM: " + driver.getNomorSIM());
    }

    @FXML
    private void handleEditAction() {
        // Logika untuk edit akan ditambahkan nanti
        System.out.println("Edit driver: " + currentDriver.getNama());
    }

    @FXML
    private void handleDeleteAction() {
        // Logika untuk hapus akan ditambahkan nanti
        System.out.println("Hapus driver: " + currentDriver.getNama());
    }
}