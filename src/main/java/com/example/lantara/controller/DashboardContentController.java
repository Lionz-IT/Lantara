package com.example.lantara.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import com.example.lantara.model.Assignment;
import com.example.lantara.model.User;
import com.example.lantara.model.Vehicle;

public class DashboardContentController {

    @FXML private Label totalVehicleLabel;
    @FXML private PieChart vehicleStatusChart;
    @FXML private VBox notificationBox;
    @FXML private VBox assignmentBox;
    @FXML private TableView<?> historyTable;
    @FXML private TableColumn<?, ?> colHistoryTanggal;
    @FXML private TableColumn<?, ?> colHistoryKendaraan;
    @FXML private TableColumn<?, ?> colHistoryPengemudi;

    private User currentUser;
    private final String DATA_FILE = "vehicles.csv";

    /**
     * Menerima data pengguna dan memuat data awal.
     */
    public void initData(User user) {
        this.currentUser = user;
        refreshDashboardData();
    }

    @FXML
    public void initialize() {
        // Inisialisasi awal jika ada
    }

    /**
     * Memuat ulang SEMUA data di dashboard.
     */
    private void refreshDashboardData() {
        loadArmadaData();
        loadDynamicDashboardData();
    }

    /**
     * Memuat data panel Armada dari file CSV.
     */
    private void loadArmadaData() {
        int totalVehicles = 0;
        int availableCount = 0;
        int usedCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                totalVehicles++;
                String[] data = line.split(",");
                if (data.length > 4) { // Asumsikan status ada di kolom ke-5
                    String status = data[4];
                    if ("Tersedia".equalsIgnoreCase(status.trim())) {
                        availableCount++;
                    } else {
                        usedCount++;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error saat membaca file kendaraan: " + e.getMessage());
        }

        totalVehicleLabel.setText(String.valueOf(totalVehicles));

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Tersedia (" + availableCount + ")", availableCount),
                        new PieChart.Data("Digunakan (" + usedCount + ")", usedCount));

        vehicleStatusChart.setData(pieChartData);
        vehicleStatusChart.setLabelsVisible(false);
        vehicleStatusChart.setLegendVisible(true);
    }

    /**
     * Memuat data dinamis untuk panel Notifikasi dan Penugasan.
     */
    private void loadDynamicDashboardData() {
        // Diambil dari variabel statis (perlu diperbaiki jika aplikasi multi-user)
        ObservableList<Assignment> allAssignments = AssignmentViewController.assignments;

        notificationBox.getChildren().clear();
        assignmentBox.getChildren().clear();

        int ongoingAssignments = 0;

        if (allAssignments != null) { // Tambahkan null check
            for (Assignment assignment : allAssignments) {
                if (assignment != null && "Berlangsung".equals(assignment.getStatusTugas())) { // Tambahkan null check
                    ongoingAssignments++;

                    Vehicle vehicle = assignment.getVehicle();
                    String vehicleName = (vehicle != null) ? vehicle.getMerek() : "N/A";
                    String nopol = (vehicle != null) ? vehicle.getNomorPolisi() : "N/A";
                    String driverName = (assignment.getDriver() != null) ? assignment.getDriver().getNama() : "N/A";

                    Label assignmentLabel = new Label(vehicleName + " ditugaskan ke " + driverName);
                    assignmentLabel.setStyle("-fx-font-weight: bold;");
                    assignmentBox.getChildren().add(assignmentLabel);

                    Label notifTitle = new Label("Mobil belum dikembalikan");
                    Label notifDetail = new Label(vehicleName + " (" + nopol + ")");
                    notifDetail.setStyle("-fx-font-weight: bold;");
                    notificationBox.getChildren().add(notifTitle);
                    notificationBox.getChildren().add(notifDetail);
                }
            }
        }

        if (ongoingAssignments == 0) {
            notificationBox.getChildren().add(new Label("Tidak ada notifikasi."));
            assignmentBox.getChildren().add(new Label("Tidak ada penugasan aktif."));
        }
    }
}