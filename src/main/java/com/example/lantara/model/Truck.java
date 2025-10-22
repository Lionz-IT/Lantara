package com.example.lantara.model;

public class Truck extends Vehicle {
    private double kapasitasAngkutTon;

    public Truck(String nomorPolisi, String merek, String model, int tahun, double kapasitasAngkutTon) {
        super(nomorPolisi, merek, model, tahun);
        this.kapasitasAngkutTon = kapasitasAngkutTon;
    }

    // Getter untuk kapasitas (ini yang dibutuhkan)
    public double getKapasitasAngkutTon() {
        return kapasitasAngkutTon;
    }
    
    // Metode @Override getDetails() yang lama dihapus karena menyebabkan error
}