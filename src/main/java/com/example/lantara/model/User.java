package com.example.lantara.model;

public class User {
    private String username;
    private String password;
    private String role; // Atribut baru untuk peran

    // Konstruktor diperbarui untuk menerima peran
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Metode getter baru untuk mengambil peran
    public String getRole() {
        return role;
    }
}