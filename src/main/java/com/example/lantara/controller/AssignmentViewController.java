package com.example.lantara.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane; // Import GridPane
import com.example.lantara.model.*;

public class AssignmentViewController {

    @FXML private GridPane formPenugasan; // Variabel FXML baru untuk form
    @FXML private ChoiceBox<Vehicle> vehicleChoiceBox;
    @FXML private ChoiceBox<Driver> driverChoiceBox;
    @FXML private TextField tujuanField;
    @FXML private Button ajukanButton;
    @FXML private TableView<Assignment> assignmentTable;
    // ... (variabel FXML kolom Anda) ...

    private User currentUser; // Variabel untuk menyimpan user
    private ObservableList<Vehicle> availableVehicles = FXCollections.observableArrayList();
    private ObservableList<Driver> availableDrivers = FXCollections.observableArrayList();
    private ObservableList<Assignment> assignments = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // ... (kode setup kolom tabel Anda) ...
        
        loadSampleData();

        vehicleChoiceBox.setItems(availableVehicles);
        driverChoiceBox.setItems(availableDrivers);
        assignmentTable.setItems(assignments);
        
        // Sembunyikan form secara default
        formPenugasan.setVisible(false);
        formPenugasan.setManaged(false);
    }

    // Metode baru untuk menerima data dari DashboardController
    public void initData(User user) {
        this.currentUser = user;
        
        // Tampilkan form hanya jika peran adalah MANAJER
        if (currentUser != null && "MANAJER".equals(currentUser.getRole())) {
            formPenugasan.setVisible(true);
            formPenugasan.setManaged(true);
        }
    }

    @FXML
    private void handleAjukanButton() {
        // ... (kode handleAjukanButton Anda tidak berubah) ...
    }

    private void loadSampleData() {
        // ... (kode loadSampleData Anda tidak berubah) ...
    }
}