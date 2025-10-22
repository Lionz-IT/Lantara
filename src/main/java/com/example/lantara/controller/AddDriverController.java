package com.example.lantara.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.lantara.model.DatabaseHelper;
import com.example.lantara.model.Driver;

public class AddDriverController {

    @FXML private TextField nikField;
    @FXML private TextField namaField;
    @FXML private TextField simField;
    @FXML private Label errorLabel;
    
    private DriverViewController driverViewController; // Referensi ke parent

    // Metode untuk menerima referensi dari parent
    public void initData(DriverViewController controller) {
        this.driverViewController = controller;
    }

    @FXML
    private void handleSaveButton() {
        String nik = nikField.getText().trim();
        String nama = namaField.getText().trim();
        String sim = simField.getText().trim();

        if (nik.isEmpty() || nama.isEmpty() || sim.isEmpty()) {
            errorLabel.setText("Semua field harus diisi!");
            return;
        }

        Driver newDriver = new Driver(nik, nama, sim);
        
        // Panggil database untuk menyimpan
        boolean success = DatabaseHelper.addDriver(newDriver);
        
        if (success) {
            // Panggil refresh di parent controller jika referensinya ada
            if (driverViewController != null) {
                 driverViewController.loadDriverData(); // Panggil metode refresh yang benar
            }
            closeWindow();
        } else {
            errorLabel.setText("Gagal menyimpan. NIK atau No. SIM mungkin sudah ada.");
        }
    }

    @FXML
    private void handleCancelButton() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nikField.getScene().getWindow();
        stage.close();
    }
}