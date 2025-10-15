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

    @FXML
    private void handleSaveButton() {
        String nik = nikField.getText().trim();
        String nama = namaField.getText().trim();
        String sim = simField.getText().trim();

        if (nik.isEmpty() || nama.isEmpty() || sim.isEmpty()) {
            errorLabel.setText("Semua field harus diisi!");
            return;
        }

        // Buat objek Driver baru dan simpan ke database
        Driver newDriver = new Driver(nik, nama, sim);
        DatabaseHelper.addDriver(newDriver);

        closeWindow();
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