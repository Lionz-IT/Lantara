package com.example.lantara.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import com.example.lantara.model.PassengerCar;
import com.example.lantara.model.Truck;
import com.example.lantara.model.Vehicle;

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

    // Metode ini akan dipanggil saat FXML dimuat
    @FXML
    public void initialize() {
        setupArrowKeyNavigation();
        jenisChoiceBox.getItems().addAll("Mobil Penumpang", "Truk");

        // Listener untuk menampilkan field yang sesuai dan menyesuaikan ukuran jendela
        jenisChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isPassengerCar = "Mobil Penumpang".equals(newVal);
            boolean isTruck = "Truk".equals(newVal);

            kapasitasPenumpangBox.setVisible(isPassengerCar);
            kapasitasPenumpangBox.setManaged(isPassengerCar);

            kapasitasAngkutBox.setVisible(isTruck);
            kapasitasAngkutBox.setManaged(isTruck);
            
            Stage stage = (Stage) jenisChoiceBox.getScene().getWindow();
            if (stage != null) { 
                stage.sizeToScene();
            }
        });

        // --- PENAMBAHAN FUNGSI ENTER ---
        
        // 1. Pindahkan fokus saat Enter ditekan pada setiap field
        nopolField.setOnAction(event -> merekField.requestFocus());
        merekField.setOnAction(event -> modelField.requestFocus());
        modelField.setOnAction(event -> tahunField.requestFocus());
        tahunField.setOnAction(event -> jenisChoiceBox.requestFocus());
        
        // Saat 'Enter' ditekan di ChoiceBox, tampilkan dropdown
        jenisChoiceBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                jenisChoiceBox.show();
            }
        });        

        // 2. Jalankan aksi simpan saat Enter ditekan di field terakhir
        penumpangField.setOnAction(event -> saveButton.fire());
        angkutField.setOnAction(event -> saveButton.fire());
        
        // ------------------------------------
    }    
    
    // Metode ini dipanggil dari MainViewController untuk memberikan akses ke daftar kendaraan
    public void setVehicleList(ObservableList<Vehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    @FXML
    private void handleSaveButton() {
        // Validasi input dasar
        if (nopolField.getText().isEmpty() || merekField.getText().isEmpty() || jenisChoiceBox.getValue() == null) {
            errorLabel.setText("Nomor Polisi, Merek, dan Jenis harus diisi!");
            return;
        }

        try {
            String nopol = nopolField.getText();
            String merek = merekField.getText();
            String model = modelField.getText();
            int tahun = Integer.parseInt(tahunField.getText());

            Vehicle newVehicle = null;
            if ("Mobil Penumpang".equals(jenisChoiceBox.getValue())) {
                int kapasitas = Integer.parseInt(penumpangField.getText());
                newVehicle = new PassengerCar(nopol, merek, model, tahun, kapasitas);
            } else if ("Truk".equals(jenisChoiceBox.getValue())) {
                double kapasitas = Double.parseDouble(angkutField.getText());
                newVehicle = new Truck(nopol, merek, model, tahun, kapasitas);
            }
            
            // Tambahkan kendaraan baru ke daftar
            if (newVehicle != null) {
                vehicleList.add(newVehicle);
                closeWindow();
            }

        } catch (NumberFormatException e) {
            errorLabel.setText("Tahun atau kapasitas harus berupa angka!");
        }
    }

    @FXML
    private void handleCancelButton() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nopolField.getScene().getWindow();
        stage.close();
    }

    private void setupArrowKeyNavigation() {
        // Dari Nomor Polisi (hanya bisa ke bawah)
        nopolField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                merekField.requestFocus();
            }
        });

        // Dari Merek
        merekField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                modelField.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                nopolField.requestFocus();
            }
        });

        // Dari Model
        modelField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                tahunField.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                merekField.requestFocus();
            }
        });

        // Dari Tahun
        tahunField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN) {
                jenisChoiceBox.requestFocus();
            } else if (event.getCode() == KeyCode.UP) {
                modelField.requestFocus();
            }
        });

        // Dari Jenis Kendaraan
        jenisChoiceBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                jenisChoiceBox.show();
            } else if (event.getCode() == KeyCode.DOWN) {
                // Cek field mana yang terlihat untuk menentukan tujuan
                if (kapasitasPenumpangBox.isVisible()) {
                    penumpangField.requestFocus();
                } else if (kapasitasAngkutBox.isVisible()) {
                    angkutField.requestFocus();
                }
            } else if (event.getCode() == KeyCode.UP) {
                tahunField.requestFocus();
            }
        });

        // Dari Kapasitas Penumpang (hanya bisa ke atas)
        penumpangField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                jenisChoiceBox.requestFocus();
            }
        });

        // Dari Kapasitas Angkut (hanya bisa ke atas)
        angkutField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                jenisChoiceBox.requestFocus();
            }
        });
    }
}