package com.example.lantara.controller;

import java.io.IOException;
import java.util.Optional; // Import Optional

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node; // Import Node
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert; // Import Alert
import javafx.scene.control.ButtonType; // Import ButtonType
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.example.lantara.MainApp;
import com.example.lantara.model.DatabaseHelper;
import com.example.lantara.model.Driver;
import com.example.lantara.model.User;

public class DriverViewController {

    @FXML
    private FlowPane driverContainer; // Wadah untuk kartu-kartu driver

    private User currentUser;
    private ObservableList<Driver> driverList; // Menyimpan daftar driver

    /**
     * Inisialisasi data awal saat view dimuat.
     * @param user Pengguna yang sedang login.
     */
    public void initData(User user) {
        this.currentUser = user;
        loadDriverData(); // Muat data driver saat inisialisasi
    }

    /**
     * Memuat atau memuat ulang data driver dari database dan menampilkan kartu.
     */
    private void loadDriverData() {
        this.driverList = DatabaseHelper.getAllDrivers(); // Ambil data terbaru
        populateDriverCards(); // Tampilkan kartu
    }

    /**
     * Menghapus semua kartu lama dan membuat kartu baru untuk setiap driver.
     */
    private void populateDriverCards() {
        driverContainer.getChildren().clear(); // Kosongkan container
        for (Driver driver : driverList) {
            try {
                // Muat FXML untuk satu kartu driver
                FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/driver-card.fxml"));
                Pane card = loader.load(); // Gunakan Pane atau VBox sesuai FXML Anda

                // Dapatkan controller kartu dan kirim data driver + referensi ke controller ini
                DriverCardController controller = loader.getController();
                controller.setData(driver, this); // 'this' merujuk ke instance DriverViewController ini

                // Tambahkan kartu ke FlowPane
                driverContainer.getChildren().add(card);
            } catch (IOException e) {
                System.err.println("Gagal memuat driver-card.fxml untuk driver: " + driver.getNama());
                e.printStackTrace();
            }
        }
    }

    /**
     * Menangani aksi saat tombol "Tambah Pengemudi" ditekan.
     * Membuka jendela modal untuk menambahkan driver baru.
     */
    @FXML
    private void handleAddDriver() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/add-driver-view.fxml"));
            Parent root = loader.load();

            // Dapatkan controller AddDriverController jika perlu mengirim data atau callback
            AddDriverController addController = loader.getController();
            addController.setParentController(this); // Kirim referensi DriverViewController

            Stage stage = new Stage();
            stage.setTitle("Tambah Pengemudi Baru");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Jendela modal
            stage.initOwner(driverContainer.getScene().getWindow()); // Set owner
            MainApp.setStageIcon(stage); // Set ikon

            stage.showAndWait(); // Tunggu sampai jendela ditutup

            // Muat ulang data setelah jendela ditutup (jika ada penambahan)
            // loadDriverData(); // Ini dipanggil dari AddDriverController jika berhasil

        } catch (IOException e) {
            System.err.println("Gagal membuka add-driver-view.fxml");
            e.printStackTrace();
        }
    }

    // --- METHOD BARU UNTUK EDIT ---
    /**
     * Membuka jendela modal untuk mengedit data driver yang dipilih.
     * Dipanggil oleh DriverCardController.
     * @param driverToEdit Objek Driver yang akan diedit.
     */
    public void openEditDriverWindow(Driver driverToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/edit-driver-view.fxml"));
            Parent root = loader.load();

            // Dapatkan controller EditDriverController dan kirim data driver
            EditDriverController editController = loader.getController();
            editController.initData(driverToEdit, this); // Kirim Driver dan referensi controller ini

            Stage stage = new Stage();
            stage.setTitle("Edit Data Pengemudi");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(driverContainer.getScene().getWindow());
            MainApp.setStageIcon(stage); // Set ikon

            stage.showAndWait(); // Tunggu sampai jendela edit ditutup

            // Muat ulang data setelah jendela ditutup (jika ada perubahan)
            // loadDriverData(); // Ini dipanggil dari EditDriverController jika berhasil

        } catch (IOException e) {
            System.err.println("Gagal membuka edit-driver-view.fxml");
            e.printStackTrace();
        }
    }

    // --- METHOD BARU UNTUK HAPUS ---
    /**
     * Menampilkan dialog konfirmasi sebelum menghapus driver.
     * Jika dikonfirmasi, panggil DatabaseHelper untuk menghapus dan muat ulang data.
     * Dipanggil oleh DriverCardController.
     * @param driverToDelete Objek Driver yang akan dihapus.
     */
    public void confirmAndDeleteDriver(Driver driverToDelete) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Konfirmasi Hapus");
        alert.setHeaderText("Hapus Pengemudi: " + driverToDelete.getNama());
        alert.setContentText("Apakah Anda yakin ingin menghapus data pengemudi ini secara permanen?");

        // Atur ikon untuk dialog Alert
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        MainApp.setStageIcon(stage);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Jika pengguna menekan OK
            boolean deleted = DatabaseHelper.deleteDriver(driverToDelete.getNomorIndukKaryawan()); // Panggil method hapus di DB Helper
            if (deleted) {
                loadDriverData(); // Muat ulang data jika berhasil dihapus
            } else {
                // Tampilkan pesan error jika gagal menghapus
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Gagal Menghapus");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Gagal menghapus data pengemudi dari database.");
                Stage errorStage = (Stage) errorAlert.getDialogPane().getScene().getWindow();
                MainApp.setStageIcon(errorStage); // Set ikon error
                errorAlert.showAndWait();
            }
        }
    }

    // --- METHOD BARU UNTUK REFRESH ---
    /**
     * Metode publik untuk memuat ulang daftar driver.
     * Bisa dipanggil dari controller lain (misal: AddDriverController, EditDriverController).
     */
    public void refreshDriverList() {
        loadDriverData();
    }

    // Metode initialize dipanggil secara otomatis setelah FXML dimuat
    // Kita pindahkan loadDriverData() ke initData() agar data user tersedia
    @FXML
    public void initialize() {
        // Kosongkan saja atau tambahkan inisialisasi lain jika perlu
        // loadDriverData() akan dipanggil setelah initData()
    }
}