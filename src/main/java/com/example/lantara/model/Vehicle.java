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
        this.status = "Tersedia"; // Status awal selalu tersedia
    }

    // Method untuk menampilkan detail dasar kendaraan
    public void getDetails() {
        System.out.println("Nomor Polisi: " + nomorPolisi);
        System.out.println("Merek/Model: " + merek + " " + model + " (" + tahun + ")");
        System.out.println("Status: " + status);
    }

    // Method untuk mengubah status kendaraan
    public void updateStatus(String newStatus) { // Sesuai UML
        this.status = newStatus;
    }

    public String getNomorPolisi() {
        return nomorPolisi;
    }

    public String getStatus() {
        return status;
    }

    // --- PENAMBAHAN DIMULAI DI SINI ---
    // Getter yang ditambahkan agar data bisa tampil di TableView

    public String getMerek() {
        return merek;
    }

    public String getModel() {
        return model;
    }

    public int getTahun() {
        return tahun;
    }

        public void setNomorPolisi(String nomorPolisi) {
        this.nomorPolisi = nomorPolisi;
    }

    public void setMerek(String merek) {
        this.merek = merek;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setTahun(int tahun) {
        this.tahun = tahun;
    }
    
}