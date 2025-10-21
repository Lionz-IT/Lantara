package com.example.lantara.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert; // Import Alert
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.lantara.MainApp; // Import MainApp untuk set ikon Alert
import com.example.lantara.model.DatabaseHelper;
import com.example.lantara.model.Driver;

public class EditDriverController {

    @FXML private TextField nikField;
    @FXML private TextField namaField;
    @FXML private TextField simField;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Driver currentDriver; // Driver yang sedang diedit
    private DriverViewController driverViewController; // Referensi ke parent controller

    /**
     * Inisialisasi data saat jendela edit dibuka.
     * Menerima objek Driver yang akan diedit dan referensi ke DriverViewController.
     * @param driver Objek Driver yang akan diedit.
     * @param controller Referensi ke DriverViewController.
     */
    public void initData(Driver driver, DriverViewController controller) {
        this.currentDriver = driver;
        this.driverViewController = controller;

        // Isi form dengan data driver saat ini
        if (currentDriver != null) {
            nikField.setText(currentDriver.getNomorIndukKaryawan());
            namaField.setText(currentDriver.getNama());
            simField.setText(currentDriver.getNomorSIM());
            nikField.setEditable(false); // NIK tidak boleh diubah
        } else {
            // Handle jika data driver null (seharusnya tidak terjadi)
            errorLabel.setText("Error: Data driver tidak ditemukan.");
            saveButton.setDisable(true);
        }
    }

    /**
     * Dipanggil saat tombol "Simpan Perubahan" ditekan.
     * Memvalidasi input, membuat objek Driver baru, dan memanggil DatabaseHelper.updateDriver().
     */
    @FXML
    private void handleSaveButton() {
        // Ambil data baru dari form (NIK tidak diambil karena tidak bisa diedit)
        String nama = namaField.getText().trim();
        String sim = simField.getText().trim();

        // Validasi input
        if (nama.isEmpty() || sim.isEmpty()) {
            errorLabel.setText("Nama dan Nomor SIM tidak boleh kosong!");
            return;
        }

        // Buat objek Driver baru dengan NIK asli dan data baru
        Driver updatedDriver = new Driver(currentDriver.getNomorIndukKaryawan(), nama, sim);

        // Panggil DatabaseHelper untuk update
        boolean success = DatabaseHelper.updateDriver(updatedDriver);

        if (success) {
            // Jika update berhasil:
            if (driverViewController != null) {
                driverViewController.refreshDriverList(); // Refresh daftar di parent
            }
            closeWindow(); // Tutup jendela edit
        } else {
            // Jika update gagal (misal: No SIM duplikat):
            errorLabel.setText("Gagal menyimpan perubahan. No SIM mungkin sudah digunakan.");
            // Optional: Tampilkan Alert
            // showAlertError("Gagal Update", "Nomor SIM mungkin sudah digunakan oleh driver lain.");
        }
    }

    /**
     * Dipanggil saat tombol "Batal" ditekan.
     * Menutup jendela edit tanpa menyimpan.
     */
    @FXML
    private void handleCancelButton() {
        closeWindow();
    }

    /**
     * Menutup jendela (Stage) saat ini.
     */
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