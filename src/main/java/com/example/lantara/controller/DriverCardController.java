package com.example.lantara.controller;

import com.example.lantara.model.Driver;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView; // Pastikan ImageView diimpor jika Anda menggunakannya

public class DriverCardController {

    @FXML private ImageView avatarImageView; // Sesuaikan jika Anda menggunakan ImageView
    @FXML private Label namaLabel;
    @FXML private Label nikLabel;
    @FXML private Label simLabel;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private Driver currentDriver;
    private DriverViewController driverViewController; // Referensi ke controller utama

    /**
     * Mengatur data driver untuk kartu ini dan menyimpan referensi ke controller utama.
     * @param driver Objek Driver yang akan ditampilkan.
     * @param controller Controller utama (DriverViewController) untuk callback.
     */
    public void setData(Driver driver, DriverViewController controller) {
        this.currentDriver = driver;
        this.driverViewController = controller;

        // Set teks label berdasarkan data driver
        namaLabel.setText(driver.getNama());
        nikLabel.setText("NIK: " + driver.getNomorIndukKaryawan());
        simLabel.setText("SIM: " + driver.getNomorSIM());

        // Di sini Anda juga bisa mengatur avatarImageView jika perlu
        // Contoh: avatarImageView.setImage(new Image(...));
    }

    /**
     * Dipanggil saat tombol 'Edit' pada kartu diklik.
     * Metode ini akan memanggil metode openEditDriverWindow di DriverViewController.
     */
    @FXML
    private void handleEditButton() { // Nama diubah agar cocok dengan contoh FXML
        if (driverViewController != null && currentDriver != null) {
            System.out.println("Meminta edit untuk driver: " + currentDriver.getNama());
            driverViewController.openEditDriverWindow(currentDriver); // Panggil metode di parent controller
        } else {
            System.err.println("Error: DriverViewController atau currentDriver belum diinisialisasi.");
        }
    }

    /**
     * Dipanggil saat tombol 'Hapus' pada kartu diklik.
     * Metode ini akan memanggil metode confirmAndDeleteDriver di DriverViewController.
     */
    @FXML
    private void handleDeleteButton() { // Nama diubah agar cocok dengan contoh FXML
        if (driverViewController != null && currentDriver != null) {
            System.out.println("Meminta hapus untuk driver: " + currentDriver.getNama());
            driverViewController.confirmAndDeleteDriver(currentDriver); // Panggil metode di parent controller
        } else {
            System.err.println("Error: DriverViewController atau currentDriver belum diinisialisasi.");
        }
    }
}