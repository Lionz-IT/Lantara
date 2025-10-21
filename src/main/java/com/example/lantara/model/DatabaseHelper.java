package com.example.lantara.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;

public class DatabaseHelper {

    private static final String URL = "jdbc:sqlite:lantara.db"; // URL koneksi ke database SQLite

    /**
     * Membuat koneksi ke database SQLite.
     * @return Objek Connection.
     * @throws SQLException Jika koneksi gagal.
     */
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Menginisialisasi database. Membuat tabel 'users' dan 'drivers' jika belum ada.
     * Juga mengisi data user awal jika tabel 'users' kosong.
     */
    public static void initializeDatabase() {
        // SQL untuk membuat tabel users
        String createUserTableSql = "CREATE TABLE IF NOT EXISTS users ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " username TEXT NOT NULL UNIQUE,"
                + " password TEXT NOT NULL,"
                + " role TEXT NOT NULL"
                + ");";

        // SQL untuk membuat tabel drivers
        String createDriverTableSql = "CREATE TABLE IF NOT EXISTS drivers ("
                + " nik TEXT PRIMARY KEY," // NIK sebagai Primary Key
                + " nama TEXT NOT NULL,"
                + " no_sim TEXT NOT NULL UNIQUE" // Nomor SIM harus unik
                + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // Eksekusi pembuatan tabel
            stmt.execute(createUserTableSql);
            stmt.execute(createDriverTableSql);

            // Cek apakah tabel users kosong, jika ya, isi data awal
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO users(username, password, role) VALUES('manajer', 'manajer123', 'Manajer')"); // Sesuaikan Role case
                stmt.execute("INSERT INTO users(username, password, role) VALUES('staf', 'staf123', 'Staf')");          // Sesuaikan Role case
                System.out.println("Data user awal berhasil ditambahkan.");
            }

        } catch (SQLException e) {
            // Abaikan error jika data user awal sudah ada (UNIQUE constraint failed)
            if (!e.getMessage().contains("UNIQUE constraint failed")) {
                System.err.println("Error saat inisialisasi database: " + e.getMessage());
            }
        }
    }

    /**
     * Menambahkan data pengemudi baru ke tabel 'drivers'.
     * @param driver Objek Driver yang akan ditambahkan.
     * @return true jika berhasil, false jika gagal.
     */
    public static boolean addDriver(Driver driver) {
        String sql = "INSERT INTO drivers(nik, nama, no_sim) VALUES(?,?,?)";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, driver.getNomorIndukKaryawan());
            pstmt.setString(2, driver.getNama());
            pstmt.setString(3, driver.getNomorSIM());
            pstmt.executeUpdate();
            System.out.println("Pengemudi baru berhasil disimpan: " + driver.getNama());
            return true;
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan pengemudi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Mengambil semua data pengemudi dari tabel 'drivers'.
     * @return ObservableList berisi objek Driver.
     */
    public static ObservableList<Driver> getAllDrivers() {
        String sql = "SELECT nik, nama, no_sim FROM drivers ORDER BY nama ASC"; // Ambil kolom yang benar & urutkan
        ObservableList<Driver> drivers = FXCollections.observableArrayList();

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Loop melalui hasil query dan buat objek Driver
            while (rs.next()) {
                drivers.add(new Driver(
                        rs.getString("nik"),
                        rs.getString("nama"),
                        rs.getString("no_sim"))); // Pastikan nama kolom sesuai
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data pengemudi: " + e.getMessage());
        }
        return drivers;
    }

    /**
     * Memvalidasi kredensial pengguna berdasarkan username, password, dan role.
     * @param username Username yang dimasukkan.
     * @param password Password yang dimasukkan.
     * @param role Role yang dipilih.
     * @return Objek User jika valid, null jika tidak valid.
     */
    public static User validateUser(String username, String password, String role) {
        // Gunakan UPPER() untuk perbandingan case-insensitive pada role
        String sql = "SELECT username, password, role FROM users WHERE username = ? AND password = ? AND UPPER(role) = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role.toUpperCase()); // Ubah input role ke huruf besar
            ResultSet rs = pstmt.executeQuery();

            // Jika ada hasil, berarti pengguna valid
            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password"), rs.getString("role"));
            }
        } catch (SQLException e) {
            System.err.println("Error saat validasi user: " + e.getMessage());
        }
        return null; // Kembalikan null jika tidak ditemukan atau error
    }

    // --- METHOD BARU UNTUK UPDATE (EDIT) ---
    /**
     * Memperbarui data pengemudi di database berdasarkan NIK.
     * @param driver Objek Driver dengan data yang sudah diperbarui.
     * @return true jika update berhasil, false jika gagal.
     */
    public static boolean updateDriver(Driver driver) {
        // NIK tidak diupdate karena itu Primary Key
        String sql = "UPDATE drivers SET nama = ?, no_sim = ? WHERE nik = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameter berdasarkan data dari objek Driver
            pstmt.setString(1, driver.getNama());
            pstmt.setString(2, driver.getNomorSIM());
            pstmt.setString(3, driver.getNomorIndukKaryawan()); // NIK untuk klausa WHERE

            // Jalankan perintah update
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Update pengemudi " + driver.getNomorIndukKaryawan() + ": " + (affectedRows > 0 ? "Berhasil" : "Gagal"));
            // Cek apakah ada baris yang terpengaruh (berarti update berhasil)
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Gagal mengupdate driver: " + e.getMessage());
            return false; // Kembalikan false jika terjadi error
        }
    }

    // --- METHOD BARU UNTUK DELETE ---
    /**
     * Menghapus data pengemudi dari database berdasarkan NIK.
     * @param nik Nomor Induk Karyawan dari pengemudi yang akan dihapus.
     * @return true jika delete berhasil, false jika gagal.
     */
    public static boolean deleteDriver(String nik) {
        String sql = "DELETE FROM drivers WHERE nik = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Set parameter NIK untuk klausa WHERE
            pstmt.setString(1, nik);

            // Jalankan perintah delete
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Hapus pengemudi " + nik + ": " + (affectedRows > 0 ? "Berhasil" : "Gagal"));
            // Cek apakah ada baris yang terpengaruh (berarti delete berhasil)
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Gagal menghapus driver: " + e.getMessage());
            return false; // Kembalikan false jika terjadi error
        }
    }

} // <-- Akhir dari kelas DatabaseHelper