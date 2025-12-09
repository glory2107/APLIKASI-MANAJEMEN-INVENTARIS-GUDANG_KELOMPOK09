-- Database schema untuk Inventory Management System

CREATE DATABASE inventory_db;
USE inventory_db;

-- Tabel barang
CREATE TABLE barang (
    kode VARCHAR(10) PRIMARY KEY,
    nama VARCHAR(100) NOT NULL,
    kategori VARCHAR(50) NOT NULL,
    stok INT DEFAULT 0,
    lokasi VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tabel riwayat_transaksi (DIPERBARUI dengan kolom keterangan)
CREATE TABLE riwayat_transaksi (
    id INT AUTO_INCREMENT PRIMARY KEY,
    kode_barang VARCHAR(10) NOT NULL,
    jenis ENUM('MASUK', 'KELUAR') NOT NULL,
    jumlah INT NOT NULL,
    keterangan VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (kode_barang) REFERENCES barang(kode) ON DELETE CASCADE
);

-- Index untuk pencarian
CREATE INDEX idx_barang_nama ON barang(nama);
CREATE INDEX idx_barang_kategori ON barang(kategori);
CREATE INDEX idx_transaksi_timestamp ON riwayat_transaksi(timestamp);
CREATE INDEX idx_transaksi_barang ON riwayat_transaksi(kode_barang);

select * from barang;

-- ==================== TABEL USERS BARU ===================--

CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    role ENUM('admin', 'pengguna') NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==================== DATA USER DEFAULT ===================--

INSERT INTO users (username, password, role, nama_lengkap) VALUES  
('glory', 'glory123', 'admin', 'Glory Gratia'),
('sara', 'sara123', 'admin', 'Sara Rajagukguk'),
('maria', 'maria123', 'admin', 'Maria Gurning'),
('bosco', 'bosco123', 'admin', 'Donbosco'),
('ruben', 'ruben123', 'admin', 'Ruben Sibarani');
SELECT * FROM users;

