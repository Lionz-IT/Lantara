package com.example.lantara.controller;

import java.io.IOException;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import com.example.lantara.MainApp;
import com.example.lantara.model.DatabaseHelper;
import com.example.lantara.model.Driver;
import com.example.lantara.model.User;

public class DriverViewController {

    @FXML private Button addDriverButton;     // dari driver-view.fxml
    @FXML private FlowPane driverContainer;   // dari driver-view.fxml

    private User currentUser;

    /** dipanggil DashboardController setelah load FXML */
    public void initData(User user) {
        this.currentUser = user;

        boolean isManager = user != null && "MANAJER".equalsIgnoreCase(user.getRole());
        if (addDriverButton != null) {
            addDriverButton.setVisible(isManager);
            addDriverButton.setManaged(isManager);
        }

        // load dari DB -> masukin ke allDrivers -> render kartu
        loadDriverData();

        // auto refresh kalau data driver berubah
        MainApp.allDrivers.addListener((ListChangeListener<Driver>) c -> renderDriverCards());
    }

    @FXML
    public void initialize() {
        // aman utk SceneBuilder
        renderDriverCards();
    }

    // ===== Handlers dari FXML =====
    @FXML private void handleAddDriver()     { openAddDialog(); }
    @FXML private void handleAddNewDriver()  { openAddDialog(); } // kalau FXML lama masih pakai nama ini

    private void openAddDialog() {
        if (currentUser == null || !"MANAJER".equalsIgnoreCase(currentUser.getRole())) {
            new Alert(Alert.AlertType.WARNING, "Fitur tambah pengemudi khusus manajer.").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/add-driver-view.fxml"));
            Parent root = loader.load();

            AddDriverController c = loader.getController();
            if (c != null) c.initData(this);

            Stage owner = (Stage) addDriverButton.getScene().getWindow();
            Stage stage = new Stage();
            stage.setTitle("Tambah Pengemudi");
            stage.setScene(new Scene(root));
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();

            // sync ulang dari DB (jaga-jaga)
            loadDriverData();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal membuka form tambah pengemudi.").showAndWait();
        }
    }

    // ===== Data =====
    public void loadDriverData() {
        MainApp.allDrivers.setAll(DatabaseHelper.getAllDrivers());
        renderDriverCards();
    }

    private void renderDriverCards() {
        if (driverContainer == null) return;
        driverContainer.getChildren().clear();

        boolean isManager = currentUser != null && "MANAJER".equalsIgnoreCase(currentUser.getRole());

        if (MainApp.allDrivers.isEmpty()) {
            driverContainer.getChildren().add(new Label("Tidak ada data pengemudi."));
            return;
        }

        for (Driver d : MainApp.allDrivers) {
            try {
                FXMLLoader cardLoader = new FXMLLoader(MainApp.class.getResource("view/driver-card.fxml"));
                Node card = cardLoader.load();

                DriverCardController cc = cardLoader.getController();
                if (cc != null) {
                    cc.setDriver(d);              // akan cek assignment aktif & set status
                    cc.setReadOnly(!isManager);   // sembunyiin tombol Edit/Hapus utk staf
                    cc.setParentController(this); // supaya kartu bisa panggil edit/hapus
                }

                driverContainer.getChildren().add(card);
            } catch (IOException e) {
                System.err.println("Gagal memuat kartu pengemudi: " + d.getNama());
                e.printStackTrace();
            }
        }
    }

    // ===== Dipanggil dari kartu =====
    public void openEditForm(Driver driver) {
        if (currentUser == null || !"MANAJER".equalsIgnoreCase(currentUser.getRole())) {
            new Alert(Alert.AlertType.WARNING, "Fitur edit pengemudi khusus manajer.").showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/edit-driver-view.fxml"));
            Parent root = loader.load();
            EditDriverController controller = loader.getController();
            if (controller != null) controller.setDriver(driver);

            Stage owner = (Stage) addDriverButton.getScene().getWindow();
            Stage stage = new Stage();
            stage.setTitle("Edit Pengemudi");
            stage.setScene(new Scene(root));
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.showAndWait();

            loadDriverData();
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal membuka form edit pengemudi.").showAndWait();
        }
    }

    public void deleteDriver(Driver driver) {
        if (currentUser == null || !"MANAJER".equalsIgnoreCase(currentUser.getRole())) {
            new Alert(Alert.AlertType.WARNING, "Fitur hapus pengemudi khusus manajer.").showAndWait();
            return;
        }
        if (DatabaseHelper.deleteDriver(driver.getNomorIndukKaryawan())) {
            loadDriverData();
        } else {
            new Alert(Alert.AlertType.ERROR, "Gagal menghapus pengemudi.").showAndWait();
        }
    }
}
