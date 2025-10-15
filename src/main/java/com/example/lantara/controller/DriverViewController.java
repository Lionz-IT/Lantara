package com.example.lantara.controller;

import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private FlowPane driverContainer;

    private User currentUser;
    private ObservableList<Driver> driverList;

    public void initData(User user) {
        this.currentUser = user;
        loadDriverData();
    }
    
    private void loadDriverData() {
        this.driverList = DatabaseHelper.getAllDrivers();
        populateDriverCards();
    }

    private void populateDriverCards() {
        driverContainer.getChildren().clear();
        for (Driver driver : driverList) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/lantara/view/driver-card.fxml"));
                Pane card = loader.load();
                DriverCardController controller = loader.getController();
                controller.setData(driver, this);
                driverContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAddDriver() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("view/add-driver-view.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Tambah Pengemudi Baru");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(driverContainer.getScene().getWindow());
            stage.showAndWait();
            loadDriverData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}