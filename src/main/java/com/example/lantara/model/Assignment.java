package com.example.lantara.model;

import java.time.LocalDate;

public class Assignment {
    private String kodePenugasan;
    private Vehicle vehicle; // Objek dari Class Kendaraan [cite: 47]
    private Driver driver; // Objek dari Class Pengemudi [cite: 46]
    private String tujuan;
    private LocalDate tanggalPenugasan;
    private LocalDate tanggalKembali;
    private String statusTugas; // "Berlangsung" atau "Selesai" [cite: 50]

    // Constructor untuk membuat penugasan baru
    public Assignment(String kodePenugasan, Vehicle vehicle, Driver driver, String tujuan) {
        this.kodePenugasan = kodePenugasan;
        this.vehicle = vehicle;
        this.driver = driver;
        this.tujuan = tujuan;
        this.tanggalPenugasan = LocalDate.now();
        this.statusTugas = "Berlangsung";

        // Otomatis ubah status kendaraan menjadi "Digunakan"
        this.vehicle.updateStatus("Digunakan");
    }

    // Method untuk menyelesaikan penugasan
    public void completeAssignment() {
        this.statusTugas = "Selesai";
        this.tanggalKembali = LocalDate.now();

        // Otomatis ubah status kendaraan kembali menjadi "Tersedia"
        this.vehicle.updateStatus("Tersedia");
        System.out.println("\nPenugasan " + kodePenugasan + " telah selesai.");
    }

    public void printAssignmentDetails() {
        System.out.println("--- Detail Penugasan " + kodePenugasan + " ---");
        System.out.println("Tujuan: " + tujuan);
        System.out.println("Tanggal: " + tanggalPenugasan);
        driver.getDriverInfo();
        vehicle.getDetails();
        System.out.println("Status Tugas: " + statusTugas);
    }
}