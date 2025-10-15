package com.example.lantara.model;

public class PassengerCar extends Vehicle {
    private int kapasitasPenumpang; // Atribut spesifik untuk PassengerCar

    public PassengerCar(String nomorPolisi, String merek, String model, int tahun, int kapasitasPenumpang) {
        super(nomorPolisi, merek, model, tahun);
        this.kapasitasPenumpang = kapasitasPenumpang;
    }

    @Override
    public void getDetails() {
        super.getDetails(); // Menampilkan detail dari Vehicle
        System.out.println("Jenis: Mobil Penumpang");
        System.out.println("Kapasitas: " + kapasitasPenumpang + " Penumpang");
    }

    // Getter untuk mengambil data kapasitas penumpang
    public int getKapasitasPenumpang() {
        return kapasitasPenumpang;
    }
}