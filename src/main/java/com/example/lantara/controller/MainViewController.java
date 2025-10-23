package com.example.lantara.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import com.example.lantara.MainApp;
import com.example.lantara.model.PassengerCar;
import com.example.lantara.model.Truck;
import com.example.lantara.model.User;
import com.example.lantara.model.Vehicle;

public class MainViewController {

    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> colNomorPolisi;
    @FXML private TableColumn<Vehicle, String> colMerek;
    @FXML private TableColumn<Vehicle, String> colModel;
    @FXML private TableColumn<Vehicle, Integer> colTahun;
    @FXML private TableColumn<Vehicle, String> colStatus;
    @FXML private TableColumn<Vehicle, String> colJenis;
    @FXML private TableColumn<Vehicle, String> colKapasitas;
    @FXML private Button addNewVehicleButton;
    

    private final ObservableList<Vehicle> vehicleList = FXCollections.observableArrayList();
    private User currentUser;
    private final String DATA_FILE = "vehicles.csv";

    public void initData(User user) {
        this.currentUser = user;
        boolean isManager = "MANAJER".equals(currentUser.getRole());
        
        if (addNewVehicleButton != null) {
            addNewVehicleButton.setVisible(isManager);
            addNewVehicleButton.setManaged(isManager);
        }
        vehicleTable.refresh();
    }

    @SuppressWarnings("unchecked")
    @FXML
    public void initialize() {
        colNomorPolisi.setCellValueFactory(new PropertyValueFactory<>("nomorPolisi"));
        colMerek.setCellValueFactory(new PropertyValueFactory<>("merek"));
        colModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        colTahun.setCellValueFactory(new PropertyValueFactory<>("tahun"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colJenis.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty((cellData.getValue() instanceof PassengerCar) ? "Mobil Penumpang" : "Truk"));

        colKapasitas.setCellValueFactory(cellData -> {
            Vehicle vehicle = cellData.getValue();
            String kapasitas = "";
            if (vehicle instanceof PassengerCar) {
                kapasitas = ((PassengerCar) vehicle).getKapasitasPenumpang() + " Penumpang";
            } else if (vehicle instanceof Truck) {
                kapasitas = ((Truck) vehicle).getKapasitasAngkutTon() + " Ton";
            }
            return new javafx.beans.property.SimpleStringProperty(kapasitas);
        });
        
        TableColumn<Vehicle, Void> aksiCol = new TableColumn<>("Aksi");
        Callback<TableColumn<Vehicle, Void>, TableCell<Vehicle, Void>> cellFactory = param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Hapus");
            private final HBox pane = new HBox(5, editBtn, deleteBtn);
            {
                editBtn.getStyleClass().add("table-action-button");
                deleteBtn.getStyleClass().add("table-action-button");
                editBtn.setOnAction(event -> openEditForm(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(event -> deleteVehicle(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    if (currentUser != null && "MANAJER".equals(currentUser.getRole())) {
                        setGraphic(pane);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        };
        aksiCol.setCellFactory(cellFactory);
        
        vehicleTable.getColumns().add(aksiCol);
        vehicleTable.setItems(MainApp.allVehicles);
    }
    
    @FXML
    protected void handleRefreshButton() {
        MainApp.loadAllData();
    }

    @FXML
    protected void handleAddNewVehicle() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/add-vehicle-view.fxml"));
            Parent root = loader.load();
            AddVehicleController controller = loader.getController();
            controller.setVehicleList(MainApp.allVehicles);            Stage stage = new Stage();
            stage.setTitle("Tambah Kendaraan Baru");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(vehicleTable.getScene().getWindow());
            stage.showAndWait();
            MainApp.loadAllData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // --- METODE-METODE YANG HILANG ADA DI SINI ---

    public void openEditForm(Vehicle vehicle) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/edit-vehicle-view.fxml"));
            Parent root = loader.load();
            EditVehicleController controller = loader.getController();
            controller.setVehicle(vehicle);

            Stage stage = new Stage();
            stage.setTitle("Edit Kendaraan");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(vehicleTable.getScene().getWindow());
            stage.showAndWait();
            saveDataToFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteVehicle(Vehicle vehicle) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Kendaraan: " + vehicle.getNomorPolisi());
        alert.setContentText("Apakah Anda yakin?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            MainApp.allVehicles.remove(vehicle); // Hapus dari daftar global
            saveDataToFile(); // Simpan perubahan
        }   
    }
    
    private void saveDataToFile() {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Vehicle v : MainApp.allVehicles) { // Loop daftar global
                // ... (logika save CSV Anda) ...
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}