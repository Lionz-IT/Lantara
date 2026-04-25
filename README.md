# 🚗 Lantara - Vehicle Management System

Lantara adalah aplikasi desktop berbasis **Java** yang dirancang untuk mengotomatisasi manajemen armada kendaraan, data pengemudi, dan penugasan operasional di lingkungan perusahaan. Proyek ini dibangun dengan penekanan kuat pada arsitektur **Object-Oriented Programming (OOP)** untuk menjamin kode yang modular dan mudah dikembangkan.

## ✨ Fitur Utama

* **Manajemen Armada:** Melacak status ketersediaan, pemeliharaan, dan penggunaan kendaraan secara *real-time*.
* **Manajemen Pengemudi:** Database terstruktur untuk mengelola profil dan informasi pengemudi perusahaan.
* **Sistem Penugasan:** Alokasi kendaraan kepada pengemudi dengan pelacakan status penugasan yang jelas.
* **Persistent Storage:** Integrasi penyimpanan data menggunakan basis data lokal (`lantara.db`) dan pemrosesan data *batch* melalui file CSV untuk fleksibilitas input/output.

## 🛠️ Tech Stack

* **Language:** Java
* **Build Tool:** Maven
* **Storage:** SQLite / Local Database & CSV File Processing
* **Paradigm:** Object-Oriented Programming (Encapsulation, Inheritance, Polymorphism)

## 🚀 Cara Menjalankan (Setup)

### Prasyarat
- **JDK 11** atau versi lebih baru.
- **Maven** (Opsional, sudah tersedia Maven Wrapper di dalam proyek).

### Langkah-langkah
1. **Clone Repositori:**
   ```bash
   git clone [https://github.com/RizWithYa/Lantara.git](https://github.com/RizWithYa/Lantara.git)
   cd Lantara
Build Proyek:

Bash
./mvnw clean install
Jalankan Aplikasi:

Bash
./mvnw exec:java -Dexec.mainClass="com.lantara.Main"
(Catatan: Sesuaikan com.lantara.Main dengan path Main Class utama di proyekmu)

📂 Struktur Data
Aplikasi ini menggunakan beberapa sumber data untuk memastikan integrasi yang lancar:

lantara.db: Basis data utama untuk penyimpanan permanen.

vehicles.csv, drivers.csv, assignments.csv: Digunakan untuk keperluan impor/ekspor data massal.

👥 Kontributor
Proyek ini dikembangkan secara kolaboratif oleh:

Muhammad Rizqi Putra (RizWithYa)

Rafif Ahmad Yudhistira (Lionz-IT)

Dibuat sebagai implementasi nyata konsep Pemrograman Berorientasi Objek.


Tinggal buat file baru bernama `README.md` di repositori GitHub Lantara kamu, lalu *paste* semua teks di dalam kotak ini. 

http://googleusercontent.com/interactive_content_block/0
