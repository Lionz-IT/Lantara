package com.example.lantara.model;

public class Truck extends Vehicle {
    private double kapasitasAngkutTon; // Atribut spesifik untuk Truck

    public Truck(String nomorPolisi, String merek, String model, int tahun, double kapasitasAngkutTon) {
        super(nomorPolisi, merek, model, tahun); // Memanggil constructor dari Vehicle
        this.kapasitasAngkutTon = kapasitasAngkutTon;
    }

    @Override
    public void getDetails() {
        super.getDetails(); // Menampilkan detail dari Vehicle
        System.out.println("Jenis: Truk Angkutan");
        System.out.println("Kapasitas Angkut: " + kapasitasAngkutTon + " Ton");
    }

    // Getter untuk mengambil data kapasitas angkut
    public double getKapasitasAngkutTon() {
        return kapasitasAngkutTon;
    }
}