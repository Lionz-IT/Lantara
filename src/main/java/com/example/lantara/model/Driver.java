package com.example.lantara.model;

public class Driver {
    private String nomorIndukKaryawan;
    private String nama;
    private String nomorSIM;

    public Driver(String nomorIndukKaryawan, String nama, String nomorSIM) {
        this.nomorIndukKaryawan = nomorIndukKaryawan;
        this.nama = nama;
        this.nomorSIM = nomorSIM;
    }

    public String getNama() { return nama; }
    public String getNomorIndukKaryawan() { return nomorIndukKaryawan; }
    public String getNomorSIM() { return nomorSIM; }

    // --- TAMBAHKAN METODE INI ---
    @Override
    public String toString() {
        // Ini akan ditampilkan di ChoiceBox
        return nama + " (" + nomorIndukKaryawan + ")";
    }
}