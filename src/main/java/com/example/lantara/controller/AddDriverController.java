package com.example.lantara.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert; // Import Alert
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.lantara.MainApp; // Import MainApp untuk set ikon Alert
import com.example.lantara.model.DatabaseHelper;
import com.example.lantara.model.Driver;

public class AddDriverController {

    @FXML private TextField nikField;
    @FXML private TextField namaField;
    @FXML private TextField simField;
    @FXML private Label errorLabel;

    // --- TAMBAHAN: Referensi ke parent controller ---
    private DriverViewController driverViewController;

    // --- TAMBAHAN: Method untuk menerima parent controller ---
    public void setParentController(DriverViewController controller) {
        this.driverViewController = controller;
    }
    // ---------------------------------------------

    @FXML
    private void handleSaveButton() {
        String nik = nikField.getText().trim();
        String nama = namaField.getText().trim();
        String sim = simField.getText().trim();

        // Validasi input
        if (nik.isEmpty() || nama.isEmpty() || sim.isEmpty()) {
            errorLabel.setText("Semua field harus diisi!");
            return;
        }

        // Buat objek Driver baru
        Driver newDriver = new Driver(nik, nama, sim);

        // --- MODIFIKASI: Panggil addDriver dan cek hasilnya ---
        boolean success = DatabaseHelper.addDriver(newDriver);

        if (success) {
            // Jika berhasil disimpan:
            if (driverViewController != null) {
                driverViewController.refreshDriverList(); // Panggil refresh di parent
            }
            closeWindow(); // Tutup jendela
        } else {
            // Jika gagal disimpan (misal: NIK/SIM duplikat):
            errorLabel.setText("Gagal menyimpan. NIK atau No. SIM mungkin sudah terdaftar.");
            // Optional: Tampilkan Alert untuk error yang lebih jelas
            // showAlertError("Gagal Menyimpan", "NIK atau Nomor SIM mungkin sudah terdaftar di database.");
        }
        // ----------------------------------------------------
    }

    @FXML
    private void handleCancelButton() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nikField.getScene().getWindow();
        stage.close();
    }

    // Optional: Method helper untuk menampilkan Alert Error
    private void showAlertError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        MainApp.setStageIcon(stage); // Set ikon untuk alert
        alert.showAndWait();
    }
}