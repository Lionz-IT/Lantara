package com.example.lantara.model;

public class Vehicle {
    private String nomorPolisi;
    private String merek;
    private String model;
    private int tahun;
    private String status; // "Tersedia" atau "Digunakan"

    public Vehicle(String nomorPolisi, String merek, String model, int tahun) {
        this.nomorPolisi = nomorPolisi;
        this.merek = merek;
        this.model = model;
        this.tahun = tahun;
        this.status = "Tersedia";
    }

    // Method untuk mengubah status kendaraan
    public void updateStatus(String newStatus) {
        this.status = newStatus;
    }

    // --- GETTER METHODS ---
    public String getNomorPolisi() { return nomorPolisi; }
    public String getStatus() { return status; }
    public String getMerek() { return merek; }
    public String getModel() { return model; }
    public int getTahun() { return tahun; }

    // --- SETTER METHODS ---
    public void setNomorPolisi(String nomorPolisi) { this.nomorPolisi = nomorPolisi; }
    public void setMerek(String merek) { this.merek = merek; }
    public void setModel(String model) { this.model = model; }
    public void setTahun(int tahun) { this.tahun = tahun; }

    // --- toString() METHOD ---
    @Override
    public String toString() {
        // Ini akan ditampilkan di ChoiceBox
        return nomorPolisi + " (" + merek + " " + model + ")";
    }
}