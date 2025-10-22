package com.example.lantara.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import com.example.lantara.model.Assignment;
import com.example.lantara.model.DatabaseHelper;
import com.example.lantara.model.Driver;
import com.example.lantara.model.PassengerCar;
import com.example.lantara.model.Truck;
import com.example.lantara.model.User;
import com.example.lantara.model.Vehicle;

public class AssignmentViewController {

    @FXML private GridPane formPenugasan;
    @FXML private ChoiceBox<Vehicle> vehicleChoiceBox;
    @FXML private ChoiceBox<Driver> driverChoiceBox;
    @FXML private TextField tujuanField;
    @FXML private Button ajukanButton;
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
    private static ObservableList<Assignment> assignments = FXCollections.observableArrayList();

    private final String VEHICLE_DATA_FILE = "vehicles.csv";

    public void initData(User user) {
        this.currentUser = user;
        setupVisibility();
        loadInitialData();
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        assignmentTable.setItems(assignments);
    }

    private void setupTableColumns() {
        colKode.setCellValueFactory(new PropertyValueFactory<>("kodePenugasan"));
        colTujuan.setCellValueFactory(new PropertyValueFactory<>("tujuan"));
        colTglPinjam.setCellValueFactory(cellData -> cellData.getValue().tanggalPenugasanFormattedProperty());
        colStatus.setCellValueFactory(new PropertyValueFactory<>("statusTugas"));

        colKendaraan.setCellValueFactory(cellData -> {
            Vehicle v = cellData.getValue().getVehicle();
            return new javafx.beans.property.SimpleStringProperty(v != null ? v.getNomorPolisi() : "N/A");
        });

        colPengemudi.setCellValueFactory(cellData -> {
            Driver d = cellData.getValue().getDriver();
            return new javafx.beans.property.SimpleStringProperty(d != null ? d.getNama() : "N/A");
        });
    }

    private void setupVisibility() {
        boolean isManager = currentUser != null && "MANAJER".equals(currentUser.getRole());
        formPenugasan.setVisible(isManager);
        formPenugasan.setManaged(isManager);
    }

    private void loadInitialData() {
        loadAvailableVehicles();
        loadAvailableDrivers();
    }

    private void loadAvailableVehicles() {
        availableVehicles.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(VEHICLE_DATA_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length > 4 && "Tersedia".equalsIgnoreCase(data[4].trim())) {
                    Vehicle vehicle = null;
                    if ("PASSENGER".equals(data[5]) && data.length > 6) {
                        vehicle = new PassengerCar(data[0], data[1], data[2], Integer.parseInt(data[3]), Integer.parseInt(data[6]));
                    } else if ("TRUCK".equals(data[5]) && data.length > 6) {
                        vehicle = new Truck(data[0], data[1], data[2], Integer.parseInt(data[3]), Double.parseDouble(data[6]));
                    }
                    if (vehicle != null) {
                        availableVehicles.add(vehicle);
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error memuat kendaraan tersedia: " + e.getMessage());
        }
        vehicleChoiceBox.setItems(availableVehicles);
    }

    private void loadAvailableDrivers() {
        availableDrivers = DatabaseHelper.getAllDrivers();
        driverChoiceBox.setItems(availableDrivers);
    }

    @FXML
    private void handleAjukanButton() {
        Vehicle selectedVehicle = vehicleChoiceBox.getValue();
        Driver selectedDriver = driverChoiceBox.getValue();
        String tujuan = tujuanField.getText().trim();

        if (selectedVehicle == null || selectedDriver == null || tujuan.isEmpty()) {
            showAlert(AlertType.ERROR, "Input Tidak Lengkap", "Harap pilih kendaraan, pengemudi, dan isi tujuan.");
            return;
        }

        String kode = "PJ" + String.format("%03d", assignments.size() + 1);
        Assignment newAssignment = new Assignment(kode, selectedVehicle, selectedDriver, tujuan);
        assignments.add(newAssignment);

        updateVehicleStatusInFile(selectedVehicle.getNomorPolisi(), "Digunakan");

        loadAvailableVehicles(); // Refresh dropdown kendaraan
        tujuanField.clear();
        vehicleChoiceBox.getSelectionModel().clearSelection();
        driverChoiceBox.getSelectionModel().clearSelection();

        showAlert(AlertType.INFORMATION, "Sukses", "Penugasan " + kode + " berhasil diajukan.");
    }

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
    
    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}