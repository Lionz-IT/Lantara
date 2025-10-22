package com.example.lantara.model;

public class PassengerCar extends Vehicle {
    private int kapasitasPenumpang;

    public PassengerCar(String nomorPolisi, String merek, String model, int tahun, int kapasitasPenumpang) {
        super(nomorPolisi, merek, model, tahun);
        this.kapasitasPenumpang = kapasitasPenumpang;
    }

    // Getter untuk kapasitas (ini yang dibutuhkan)
    public int getKapasitasPenumpang() {
        return kapasitasPenumpang;
    }
    
    // Metode @Override getDetails() yang lama dihapus karena menyebabkan error
}