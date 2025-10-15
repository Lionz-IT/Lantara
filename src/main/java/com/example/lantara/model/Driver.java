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

    public void getDriverInfo() {
        System.out.println("Pengemudi: " + nama + " (NIK: " + nomorIndukKaryawan + ")");
    }

    public String getNama() {
        return nama;
    }

    // --- TAMBAHKAN DUA METODE GETTER DI BAWAH INI ---

    public String getNomorIndukKaryawan() {
        return nomorIndukKaryawan;
    }

    public String getNomorSIM() {
        return nomorSIM;
    }
}