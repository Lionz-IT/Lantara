package com.example.lantara;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import com.example.lantara.model.Assignment;
import com.example.lantara.model.DatabaseHelper;
import com.example.lantara.model.Driver;
import com.example.lantara.model.PassengerCar;
import com.example.lantara.model.Truck;
import com.example.lantara.model.Vehicle;

/**
 * Kelas utama (entry point) aplikasi LANTARA.
 * Bertanggung jawab memuat database, data awal (CSV),
 * serta menampilkan tampilan pertama (landing/login page).
 */
public class MainApp extends Application {

    // --- DAFTAR DATA GLOBAL YANG BISA DIAKSES DARI CONTROLLER LAIN ---
    public static ObservableList<Vehicle> allVehicles = FXCollections.observableArrayList();
    public static ObservableList<Driver> allDrivers = FXCollections.observableArrayList();
    public static ObservableList<Assignment> allAssignments = FXCollections.observableArrayList();
    // -----------------------------------------------------------------

    @Override
    public void start(Stage stage) throws IOException {
        // Muat tampilan awal (landing page)
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("view/landing-page-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        stage.setTitle("LANTARA - Lacak Armada Nusantara");
        setStageIcon(stage); // Pasang ikon aplikasi
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Titik masuk utama program.
     * Inisialisasi database, load data CSV, lalu jalankan JavaFX.
     */
    public static void main(String[] args) {
        DatabaseHelper.initializeDatabase();
        loadAllData(); // Muat data kendaraan, pengemudi, dan penugasan
        launch(args);
    }

    /**
     * Memuat semua data dari database dan file CSV.
     */
    public static void loadAllData() {
        // 1️⃣ Muat data pengemudi dari Database
        allDrivers.clear();
        allDrivers.addAll(DatabaseHelper.getAllDrivers());

        // 2️⃣ Muat data kendaraan dari CSV
        allVehicles.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("vehicles.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 6) continue;

                Vehicle vehicle = null;
                if ("PASSENGER".equalsIgnoreCase(data[5]) && data.length > 6) {
                    vehicle = new PassengerCar(
                            data[0], data[1], data[2],
                            Integer.parseInt(data[3]), Integer.parseInt(data[6])
                    );
                } else if ("TRUCK".equalsIgnoreCase(data[5]) && data.length > 6) {
                    vehicle = new Truck(
                            data[0], data[1], data[2],
                            Integer.parseInt(data[3]), Double.parseDouble(data[6])
                    );
                }

                if (vehicle != null) {
                    vehicle.updateStatus(data[4]);
                    allVehicles.add(vehicle);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("⚠️ Gagal memuat vehicles.csv: " + e.getMessage());
        }

        // 3️⃣ Muat data penugasan dari CSV (dan hubungkan dengan Vehicle & Driver)
        allAssignments.clear();
        try (BufferedReader br = new BufferedReader(new FileReader("assignments.csv"))) {
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

                // Hubungkan dengan objek Vehicle & Driver
                Vehicle v = allVehicles.stream()
                        .filter(veh -> veh.getNomorPolisi().equals(nopol))
                        .findFirst()
                        .orElse(null);
                Driver d = allDrivers.stream()
                        .filter(drv -> drv.getNomorIndukKaryawan().equals(nik))
                        .findFirst()
                        .orElse(null);

                if (v != null && d != null) {
                    Assignment assignment = new Assignment(
                            kode, v, d, tujuan, tglPinjam, tglKembali, status
                    );
                    allAssignments.add(assignment);
                }
            }
        } catch (IOException e) {
            System.err.println("⚠️ Gagal memuat assignments.csv: " + e.getMessage());
        }
    }

    /**
     * Mengatur ikon jendela aplikasi.
     * Dipanggil dari MainApp maupun controller lain (misal: LoginViewController).
     */
    public static void setStageIcon(Stage stage) {
        try {
            // Pastikan ikon berada di path: src/main/resources/com/example/lantara/assets/lantara_logo.png
            Image icon = new Image(MainApp.class.getResourceAsStream("assets/lantara_logo.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("⚠️ Gagal memuat ikon aplikasi: " + e.getMessage());
        }
    }
}
