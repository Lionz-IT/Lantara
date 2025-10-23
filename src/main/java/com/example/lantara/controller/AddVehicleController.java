package com.example.lantara.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.example.lantara.MainApp;
import com.example.lantara.model.PassengerCar;
import com.example.lantara.model.Truck;
import com.example.lantara.model.Vehicle;

import java.util.Optional;

public class AddVehicleController {

    @FXML private TextField nopolField;
    @FXML private TextField merekField;
    @FXML private TextField modelField;
    @FXML private TextField tahunField;
    @FXML private ChoiceBox<String> jenisChoiceBox;
    @FXML private VBox kapasitasPenumpangBox;
    @FXML private TextField penumpangField;
    @FXML private VBox kapasitasAngkutBox;
    @FXML private TextField angkutField;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;

    private ObservableList<Vehicle> vehicleList;

    @FXML
    public void initialize() {
        // Isi pilihan jenis
        jenisChoiceBox.getItems().addAll("Mobil Penumpang", "Truk");

        // Tampilkan field kapasitas sesuai jenis
        jenisChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean passenger = "Mobil Penumpang".equals(newVal);
            boolean truck     = "Truk".equals(newVal);

            kapasitasPenumpangBox.setVisible(passenger);
            kapasitasPenumpangBox.setManaged(passenger);

            kapasitasAngkutBox.setVisible(truck);
            kapasitasAngkutBox.setManaged(truck);

            // Sesuaikan tinggi dialog otomatis
            Stage stage = (Stage) jenisChoiceBox.getScene().getWindow();
            if (stage != null) stage.sizeToScene();
        });

        // Navigasi cepat pakai ENTER / panah
        setupArrowKeyNavigation();

        // ENTER di field kapasitas = tekan tombol Simpan
        penumpangField.setOnAction(e -> saveButton.fire());
        angkutField.setOnAction(e -> saveButton.fire());
    }

    public void setVehicleList(ObservableList<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @FXML
    private void handleSaveButton() {
        errorLabel.setText("");

        // Validasi dasar
        if (nopolField.getText().isEmpty() || merekField.getText().isEmpty()
                || jenisChoiceBox.getValue() == null || tahunField.getText().isEmpty()) {
            errorLabel.setText("Nomor Polisi, Merek, Tahun, dan Jenis harus diisi!");
            return;
        }

        // Siapkan objek kendaraan (belum ditambahkan ke list)
        Vehicle newVehicle;
        try {
            String nopol = nopolField.getText().trim();
            String merek = merekField.getText().trim();
            String model = modelField.getText().trim();
            int tahun    = Integer.parseInt(tahunField.getText().trim());

            if ("Mobil Penumpang".equals(jenisChoiceBox.getValue())) {
                int kapasitas = Integer.parseInt(penumpangField.getText().trim());
                newVehicle = new PassengerCar(nopol, merek, model, tahun, kapasitas);
            } else {
                double kapasitas = Double.parseDouble(angkutField.getText().trim());
                newVehicle = new Truck(nopol, merek, model, tahun, kapasitas);
            }
            newVehicle.updateStatus("Tersedia");
        } catch (NumberFormatException ex) {
            errorLabel.setText("Tahun/kapasitas harus berupa angka yang valid!");
            return;
        }

        // ===== Inti permintaanmu: form disembunyikan saat konfirmasi =====
        Stage formStage  = (Stage) saveButton.getScene().getWindow();
        Stage ownerStage = (Stage) formStage.getOwner(); // jendela daftar kendaraan
        formStage.hide(); // Sembunyikan form sebelum konfirmasi ditampilkan

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Simpan");
        confirm.setHeaderText("Simpan Kendaraan Baru");
        confirm.setContentText("Apakah Anda yakin ingin menyimpan data kendaraan ini?");
        if (ownerStage != null) {
            confirm.initOwner(ownerStage);
            confirm.initModality(Modality.WINDOW_MODAL);
        }

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) {
            // User batal → tampilkan kembali form agar bisa mengubah data
            formStage.show();
            return;
        }

        // OK → simpan & tutup form permanen
        if (vehicleList != null) {
            vehicleList.add(newVehicle);
        }
        MainApp.saveAllData();   // persist ke CSV
        formStage.close();       // form tidak muncul lagi

        // (opsional) notifikasi sukses kecil
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Sukses");
        info.setHeaderText(null);
        info.setContentText("Kendaraan berhasil disimpan.");
        if (ownerStage != null) {
            info.initOwner(ownerStage);
            info.initModality(Modality.WINDOW_MODAL);
        }
        info.showAndWait();
    }

    @FXML
    private void handleCancelButton() {
        ((Stage) nopolField.getScene().getWindow()).close();
    }

    // ---------- Navigasi keyboard ----------
    private void setupArrowKeyNavigation() {
        nopolField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.DOWN) merekField.requestFocus(); });

        merekField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN) modelField.requestFocus();
            else if (e.getCode() == KeyCode.UP) nopolField.requestFocus();
        });

        modelField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN) tahunField.requestFocus();
            else if (e.getCode() == KeyCode.UP) merekField.requestFocus();
        });

        tahunField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DOWN) jenisChoiceBox.requestFocus();
            else if (e.getCode() == KeyCode.UP) modelField.requestFocus();
        });

        jenisChoiceBox.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                jenisChoiceBox.show();
            } else if (e.getCode() == KeyCode.DOWN) {
                if (kapasitasPenumpangBox.isVisible()) penumpangField.requestFocus();
                else if (kapasitasAngkutBox.isVisible()) angkutField.requestFocus();
            } else if (e.getCode() == KeyCode.UP) {
                tahunField.requestFocus();
            }
        });

        penumpangField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.UP) jenisChoiceBox.requestFocus(); });
        angkutField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.UP) jenisChoiceBox.requestFocus(); });
    }
}
