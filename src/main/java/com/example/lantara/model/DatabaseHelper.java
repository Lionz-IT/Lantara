package com.example.lantara.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DatabaseHelper {

    private static final String URL = "jdbc:sqlite:lantara.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS users ("
                   + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + " username TEXT NOT NULL UNIQUE,"
                   + " password TEXT NOT NULL,"
                   + " role TEXT NOT NULL"
                   + ");";
        
        // SQL BARU: Membuat tabel drivers
        String createDriverTableSql = "CREATE TABLE IF NOT EXISTS drivers ("
                   + " nik TEXT PRIMARY KEY,"
                   + " nama TEXT NOT NULL,"
                   + " no_sim TEXT NOT NULL UNIQUE"
                   + ");";
        
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUserTableSql);
            stmt.execute(createDriverTableSql); // Jalankan SQL untuk membuat tabel drivers

            // Logika untuk mengisi data awal user (jika perlu)
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO users(username, password, role) VALUES('manajer', 'manajer123', 'MANAJER')");
                stmt.execute("INSERT INTO users(username, password, role) VALUES('staf', 'staf123', 'STAF')");
            }

        } catch (SQLException e) {
            if (!e.getMessage().contains("UNIQUE constraint failed")) {
                System.out.println(e.getMessage());
            }
        }
    }
    
    // FUNGSI BARU: Menambahkan pengemudi ke database
    public static void addDriver(Driver driver) {
        String sql = "INSERT INTO drivers(nik, nama, no_sim) VALUES(?,?,?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, driver.getNomorIndukKaryawan());
            pstmt.setString(2, driver.getNama());
            pstmt.setString(3, driver.getNomorSIM());
            pstmt.executeUpdate();
            System.out.println("Pengemudi baru berhasil disimpan ke database.");
        } catch (SQLException e) {
            System.out.println("Gagal menyimpan pengemudi: " + e.getMessage());
        }
    }

    // FUNGSI BARU: Mengambil semua data pengemudi dari database
    public static ObservableList<Driver> getAllDrivers() {
        String sql = "SELECT * FROM drivers";
        ObservableList<Driver> drivers = FXCollections.observableArrayList();

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
            while (rs.next()) {
                drivers.add(new Driver(
                    rs.getString("nik"),
                    rs.getString("nama"),
                    rs.getString("no_sim")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return drivers;
    }

    public static User validateUser(String username, String password, String role) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password"), rs.getString("role"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}