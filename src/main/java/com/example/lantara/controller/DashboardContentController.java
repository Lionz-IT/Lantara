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
import com.example.lantara.model.User;

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

    public void initData(User user) {
        this.currentUser = user;
        // Muat data asli setelah informasi user diterima
        loadArmadaData();
    }

    @FXML
    public void initialize() {
        // Panggil metode untuk memuat data Armada dari file
        loadArmadaData();

        // Mengisi Panel Lain dengan Data Contoh (masih statis)
        Label notifLabel = new Label("Mobil belum dikembalikan");
        Label notifDetail = new Label("Toyota Innova");
        notifDetail.setStyle("-fx-font-weight: bold;");
        notificationBox.getChildren().addAll(notifLabel, notifDetail);

        Label penugasanLabel = new Label("Toyota Innova ditugaskan ke Budi");
        penugasanLabel.setStyle("-fx-font-weight: bold;");
        assignmentBox.getChildren().add(penugasanLabel);
    }

    /**
     * Metode untuk memuat data kendaraan dari file CSV,
     * menghitung statistik, dan memperbarui UI panel Armada.
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
                if (data.length > 4) {
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

        // 1. Perbarui Label Jumlah Kendaraan
        totalVehicleLabel.setText(String.valueOf(totalVehicles));

        // 2. Perbarui Diagram Pie dengan menyertakan jumlah pada teks legenda
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Tersedia (" + availableCount + ")", availableCount),
                        new PieChart.Data("Digunakan (" + usedCount + ")", usedCount));
        
        vehicleStatusChart.setData(pieChartData);
        vehicleStatusChart.setLabelsVisible(false); // Sembunyikan label di dalam irisan pie
        vehicleStatusChart.setLegendVisible(true);  // Pastikan legenda terlihat
    }
}