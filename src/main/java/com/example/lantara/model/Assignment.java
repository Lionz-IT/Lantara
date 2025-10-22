package com.example.lantara.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Assignment {
    private final StringProperty kodePenugasan;
    private final ObjectProperty<Vehicle> vehicle;
    private final ObjectProperty<Driver> driver;
    private final StringProperty tujuan;
    private final ObjectProperty<LocalDate> tanggalPenugasan;
    private final ObjectProperty<LocalDate> tanggalKembali;
    private final StringProperty statusTugas; // "Berlangsung", "Selesai"

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Assignment(String kodePenugasan, Vehicle vehicle, Driver driver, String tujuan) {
        this.kodePenugasan = new SimpleStringProperty(kodePenugasan);
        this.vehicle = new SimpleObjectProperty<>(vehicle);
        this.driver = new SimpleObjectProperty<>(driver);
        this.tujuan = new SimpleStringProperty(tujuan);
        this.tanggalPenugasan = new SimpleObjectProperty<>(LocalDate.now());
        this.tanggalKembali = new SimpleObjectProperty<>(null);
        this.statusTugas = new SimpleStringProperty("Berlangsung");

        if (vehicle != null) {
            vehicle.updateStatus("Digunakan");
        }
    }

    // --- JavaFX Property Getters ---
    public StringProperty kodePenugasanProperty() { return kodePenugasan; }
    public StringProperty tujuanProperty() { return tujuan; }
    public StringProperty statusTugasProperty() { return statusTugas; }
    public StringProperty tanggalPenugasanFormattedProperty() {
        // Metode ini memformat tanggal agar tampil bagus di tabel
        return new SimpleStringProperty(tanggalPenugasan.get().format(DATE_FORMATTER));
    }

    // --- Standard Getters ---
    public Vehicle getVehicle() { return vehicle.get(); }
    public Driver getDriver() { return driver.get(); }

    public void completeAssignment() {
        this.statusTugas.set("Selesai");
        this.tanggalKembali.set(LocalDate.now());
        if (vehicle.get() != null) {
            vehicle.get().updateStatus("Tersedia");
        }
    }
}