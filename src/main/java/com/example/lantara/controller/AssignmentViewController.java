package com.example.lantara.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import com.example.lantara.MainApp;
import com.example.lantara.model.Assignment;
import com.example.lantara.model.Driver;
import com.example.lantara.model.Vehicle;
import com.example.lantara.model.User;

public class AssignmentViewController {

    @FXML private GridPane formPenugasan;
    @FXML private ChoiceBox<Vehicle> vehicleChoiceBox;
    @FXML private ChoiceBox<Driver> driverChoiceBox;
    @FXML private TextField tujuanField;
    @FXML private TableView<Assignment> assignmentTable;
    @FXML private TableColumn<Assignment, String> colKode;
    @FXML private TableColumn<Assignment, String> colKendaraan;
    @FXML private TableColumn<Assignment, String> colPengemudi;
    @FXML private TableColumn<Assignment, String> colTujuan;
    @FXML private TableColumn<Assignment, String> colTglPinjam;
    @FXML private TableColumn<Assignment, String> colStatus;

    private User currentUser;
    private ObservableList<Vehicle> availableVehicles = FXCollections.observableArrayList();
    private ObservableList<Driver> availableDrivers = FXCollections.observableArrayList();

    private final String VEHICLE_DATA_FILE = "vehicles.csv";
    private final String ASSIGNMENT_DATA_FILE = "assignments.csv";

    // =====================================
    // Inisialisasi Awal Controller
    // =====================================
    public void initData(User user) {
        this.currentUser = user;
        setupVisibility();
        loadInitialData();
        refreshTableForUser();
    }

    @FXML
    public void initialize() {
        setupTableColumns();

        // Penting: tabel langsung menggunakan daftar global dari MainApp
        assignmentTable.setItems(MainApp.allAssignments);

        // Muat data awal kendaraan dan driver
        loadAvailableVehicles();
        loadAvailableDrivers();
    }

    // =====================================
    // Pengaturan Kolom Tabel
    // =====================================
    private void setupTableColumns() {
        colKode.setCellValueFactory(new PropertyValueFactory<>("kodePenugasan"));
        colTujuan.setCellValueFactory(new PropertyValueFactory<>("tujuan"));
        colTglPinjam.setCellValueFactory(cellData -> cellData.getValue().tanggalPenugasanFormattedProperty());
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusTugas"));

        colKendaraan.setCellValueFactory(cellData -> {
            Vehicle v = cellData.getValue().getVehicle();
            return new javafx.beans.property.SimpleStringProperty(
                v != null ? v.getNomorPolisi() : "-"
            );
        });

        colPengemudi.setCellValueFactory(cellData -> {
            Driver d = cellData.getValue().getDriver();
            return new javafx.beans.property.SimpleStringProperty(
                d != null ? d.getNama() : "-"
            );
        });
    }

    // =====================================
    // Tampilan hanya untuk Manajer
    // =====================================
    private void setupVisibility() {
        boolean isManager = currentUser != null && "MANAJER".equalsIgnoreCase(currentUser.getRole());
        formPenugasan.setVisible(isManager);
        formPenugasan.setManaged(isManager);
    }

    // =====================================
    // Muat Data Awal Kendaraan & Driver
    // =====================================
    private void loadInitialData() {
        loadAvailableVehicles();
        loadAvailableDrivers();
    }

    private void loadAvailableVehicles() {
        availableVehicles.clear();
        for (Vehicle v : MainApp.allVehicles) {
            if ("Tersedia".equalsIgnoreCase(v.getStatus())) {
                availableVehicles.add(v);
            }
        }
        vehicleChoiceBox.setItems(availableVehicles);
    }

    private void loadAvailableDrivers() {
        availableDrivers.setAll(MainApp.allDrivers);
        driverChoiceBox.setItems(availableDrivers);
    }

    // =====================================
    // Tombol Ajukan Penugasan
    // =====================================
    @FXML
    private void handleAjukanButton() {
        Vehicle selectedVehicle = vehicleChoiceBox.getValue();
        Driver selectedDriver = driverChoiceBox.getValue();
        String tujuan = tujuanField.getText().trim();

        if (selectedVehicle == null || selectedDriver == null || tujuan.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Tidak Lengkap",
                    "Harap pilih kendaraan, pengemudi, dan isi tujuan.");
            return;
        }

        String kode = "PJ" + String.format("%03d", MainApp.allAssignments.size() + 1);
        Assignment newAssignment = new Assignment(kode, selectedVehicle, selectedDriver, tujuan);

        // Tambahkan ke daftar global
        MainApp.allAssignments.add(newAssignment);

        // Simpan ke file CSV
        saveAssignmentsToFile();

        // Update status kendaraan jadi "Digunakan"
        updateVehicleStatusInFile(selectedVehicle.getNomorPolisi(), "Digunakan");

        // Perbarui tampilan tabel dan form
        assignmentTable.refresh();
        loadAvailableVehicles();
        tujuanField.clear();
        vehicleChoiceBox.getSelectionModel().clearSelection();
        driverChoiceBox.getSelectionModel().clearSelection();

        showAlert(AlertType.INFORMATION, "Sukses",
                "Penugasan " + kode + " berhasil diajukan dan disimpan.");
    }

    // =====================================
    // Simpan Daftar Penugasan ke File CSV
    // =====================================
    private void saveAssignmentsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ASSIGNMENT_DATA_FILE))) {
            for (Assignment a : MainApp.allAssignments) {
                String tglKembali = (a.getTanggalKembali() == null) ? "null" : a.getTanggalKembali().toString();
                String line = String.join(",",
                        a.getKodePenugasan(),
                        a.getVehicle().getNomorPolisi(),
                        a.getDriver().getNomorIndukKaryawan(),
                        a.getTujuan(),
                        a.getTanggalPenugasan().toString(),
                        tglKembali,
                        a.getStatusTugas()
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =====================================
    // Update Status Kendaraan di File CSV
    // =====================================
    private void updateVehicleStatusInFile(String nopolToUpdate, String newStatus) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(VEHICLE_DATA_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 0 && data[0].equals(nopolToUpdate)) {
                    data[4] = newStatus;
                    line = String.join(",", data);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(VEHICLE_DATA_FILE))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =====================================
    // Refresh Data Tabel Berdasarkan Role
    // =====================================
    private void refreshTableForUser() {
        if (currentUser == null) return;

        if ("MANAJER".equalsIgnoreCase(currentUser.getRole())) {
            assignmentTable.setItems(MainApp.allAssignments);
        } else if ("STAF".equalsIgnoreCase(currentUser.getRole())) {
            List<Assignment> myAssignments = MainApp.allAssignments.stream()
                    .filter(a -> a.getDriver() != null &&
                            a.getDriver().getNomorIndukKaryawan().equals(currentUser.getUsername()))
                    .collect(Collectors.toList());
            assignmentTable.setItems(FXCollections.observableArrayList(myAssignments));
        }
        assignmentTable.refresh();
    }

    // =====================================
    // Menyelesaikan Tugas
    // =====================================
    private void handleSelesaikanTugas(Assignment assignment) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Tugas");
        alert.setHeaderText("Selesaikan tugas " + assignment.getKodePenugasan() + "?");
        alert.setContentText("Ini akan mengubah status kendaraan menjadi 'Tersedia'.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            assignment.completeAssignment();
            saveAssignmentsToFile();
            updateVehicleStatusInFile(assignment.getVehicle().getNomorPolisi(), "Tersedia");
            loadAvailableVehicles();
            assignmentTable.refresh();
        }
    }

    // =====================================
    // Utilitas Notifikasi
    // =====================================
    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
