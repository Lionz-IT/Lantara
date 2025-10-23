package com.example.lantara;

import java.io.*;
import java.nio.file.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import com.example.lantara.model.*;

/**
 * Kelas utama (entry point) aplikasi LANTARA.
 * Bertanggung jawab memuat database, data awal (CSV),
 * serta menampilkan tampilan pertama (landing/login page).
 */
public class MainApp extends Application {

    // === DATA GLOBAL YANG DAPAT DIAKSES SEMUA CONTROLLER ===
    public static ObservableList<Vehicle> allVehicles = FXCollections.observableArrayList();
    public static ObservableList<Driver> allDrivers = FXCollections.observableArrayList();
    public static ObservableList<Assignment> allAssignments = FXCollections.observableArrayList();
    // ========================================================

    // Lokasi file data (bisa disesuaikan sesuai kebutuhan)
    private static final String VEHICLE_FILE = "vehicles.csv";
    private static final String ASSIGNMENT_FILE = "assignments.csv";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/landing-page-view.fxml"));
        Scene scene = new Scene(loader.load(), 1280, 720);

        stage.setTitle("LANTARA - Lacak Armada Nusantara");
        setStageIcon(stage);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Entry point program JavaFX.
     * Inisialisasi database dan memuat semua data awal.
     */
    public static void main(String[] args) {
        DatabaseHelper.initializeDatabase();
        loadAllData();
        launch(args);
    }

    /**
     * Muat semua data: kendaraan, pengemudi, penugasan.
     */
    public static void loadAllData() {
        allDrivers.clear();
        allDrivers.addAll(DatabaseHelper.getAllDrivers());

        // ==================== KENDARAAN ====================
        allVehicles.clear();
        ensureFileExists(VEHICLE_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(VEHICLE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 7) continue;

                String nopol = data[0];
                String merek = data[1];
                String model = data[2];
                int tahun = Integer.parseInt(data[3]);
                String jenis = data[4];
                String status = data[5];

                Vehicle vehicle;
                if ("Mobil Penumpang".equalsIgnoreCase(jenis)) {
                    int kapasitas = Integer.parseInt(data[6]);
                    vehicle = new PassengerCar(nopol, merek, model, tahun, kapasitas);
                } else {
                    double kapasitas = Double.parseDouble(data[6]);
                    vehicle = new Truck(nopol, merek, model, tahun, kapasitas);
                }
                vehicle.updateStatus(status);
                allVehicles.add(vehicle);
            }
        } catch (IOException e) {
            System.err.println("⚠️ Gagal membaca vehicles.csv: " + e.getMessage());
        }

        // ==================== PENUGASAN ====================
        allAssignments.clear();
        ensureFileExists(ASSIGNMENT_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(ASSIGNMENT_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 7) continue;

                String kode = data[0];
                String nopol = data[1];
                String nik = data[2];
                String tujuan = data[3];
                String tglPinjam = data[4];
                String tglKembali = data[5];
                String status = data[6];

                Vehicle v = allVehicles.stream()
                        .filter(veh -> veh.getNomorPolisi().equalsIgnoreCase(nopol))
                        .findFirst()
                        .orElse(null);

                Driver d = allDrivers.stream()
                        .filter(drv -> drv.getNomorIndukKaryawan().equalsIgnoreCase(nik))
                        .findFirst()
                        .orElse(null);

                if (v != null && d != null) {
                    allAssignments.add(new Assignment(kode, v, d, tujuan, tglPinjam, tglKembali, status));
                }
            }
        } catch (IOException e) {
            System.err.println("⚠️ Gagal membaca assignments.csv: " + e.getMessage());
        }
    }

    /**
     * Simpan semua data kembali ke CSV.
     */
    public static void saveAllData() {
        // ==================== SIMPAN KENDARAAN ====================
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(VEHICLE_FILE))) {
            for (Vehicle v : allVehicles) {
                String jenis = (v instanceof PassengerCar) ? "Mobil Penumpang" : "Truk";
                String kapasitas = (v instanceof PassengerCar)
                        ? String.valueOf(((PassengerCar) v).getKapasitasPenumpang())
                        : String.valueOf(((Truck) v).getKapasitasAngkutTon());
                String line = String.join(",",
                        v.getNomorPolisi(),
                        v.getMerek(),
                        v.getModel(),
                        String.valueOf(v.getTahun()),
                        jenis,
                        v.getStatus(),
                        kapasitas
                );
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("⚠️ Gagal menyimpan vehicles.csv: " + e.getMessage());
        }

        // ==================== SIMPAN PENUGASAN ====================
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ASSIGNMENT_FILE))) {
            for (Assignment a : allAssignments) {
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
            System.err.println("⚠️ Gagal menyimpan assignments.csv: " + e.getMessage());
        }
    }

    /**
     * Membuat file kosong jika belum ada.
     */
    private static void ensureFileExists(String fileName) {
        try {
            Path path = Paths.get(fileName);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            System.err.println("⚠️ Tidak dapat membuat file: " + fileName);
        }
    }

    /**
     * Pasang ikon aplikasi.
     */
    public static void setStageIcon(Stage stage) {
        try {
            Image icon = new Image(MainApp.class.getResourceAsStream("/com/example/lantara/assets/logo.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("⚠️ Gagal memuat ikon aplikasi: " + e.getMessage());
        }
    }

    /**
     * Simpan semua data saat aplikasi ditutup.
     */
    @Override
    public void stop() {
        saveAllData();
        Platform.exit();
    }
}
