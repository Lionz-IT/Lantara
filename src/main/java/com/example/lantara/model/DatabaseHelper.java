package com.example.lantara.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
        
        String createDriverTableSql = "CREATE TABLE IF NOT EXISTS drivers ("
                   + " nik TEXT PRIMARY KEY,"
                   + " nama TEXT NOT NULL,"
                   + " no_sim TEXT NOT NULL UNIQUE"
                   + ");";
        
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUserTableSql);
            stmt.execute(createDriverTableSql);

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO users(username, password, role) VALUES('manajer', 'manajer123', 'MANAJER')");
                stmt.execute("INSERT INTO users(username, password, role) VALUES('staf', 'staf123', 'STAF')");
            }
        } catch (SQLException e) {
            if (!e.getMessage().contains("UNIQUE constraint failed")) {
                System.err.println("Error saat inisialisasi database: " + e.getMessage());
            }
        }
    }
    
    public static boolean addDriver(Driver driver) {
        String sql = "INSERT INTO drivers(nik, nama, no_sim) VALUES(?,?,?)";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, driver.getNomorIndukKaryawan());
            pstmt.setString(2, driver.getNama());
            pstmt.setString(3, driver.getNomorSIM());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan pengemudi: " + e.getMessage());
            return false;
        }
    }
    
    // --- METODE BARU UNTUK UPDATE PENGEMUDI ---
    public static boolean updateDriver(String nik, String nama, String noSim) {
        String sql = "UPDATE drivers SET nama = ?, no_sim = ? WHERE nik = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama);
            pstmt.setString(2, noSim);
            pstmt.setString(3, nik);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Gagal mengupdate driver: " + e.getMessage());
            return false;
        }
    }

    // --- METODE BARU UNTUK HAPUS PENGEMUDI ---
    public static boolean deleteDriver(String nik) {
        String sql = "DELETE FROM drivers WHERE nik = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nik);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menghapus driver: " + e.getMessage());
            return false;
        }
    }

    public static ObservableList<Driver> getAllDrivers() {
        String sql = "SELECT nik, nama, no_sim FROM drivers ORDER BY nama ASC";
        ObservableList<Driver> drivers = FXCollections.observableArrayList();
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                drivers.add(new Driver(
                        rs.getString("nik"),
                        rs.getString("nama"),
                        rs.getString("no_sim")));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data pengemudi: " + e.getMessage());
        }
        return drivers;
    }

    public static User validateUser(String username, String password, String role) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND UPPER(role) = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password"), rs.getString("role"));
            }
        } catch (SQLException e) {
            System.err.println("Error saat validasi user: " + e.getMessage());
        }
        return null;
    }
}