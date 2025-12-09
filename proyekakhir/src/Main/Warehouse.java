/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Warehouse extends BaseService implements WarehouseService {

    public Warehouse() throws InventoryException {
        super();
    }

    // ===========================
    // CREATE BARANG + LOG RIWAYAT
    // ===========================
    @Override
    public Item create(Item item) throws InventoryException {
        String sql = "INSERT INTO barang (kode, nama, kategori, stok, lokasi) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Cek dulu apakah kode sudah ada (biar lebih aman)
            if (findById(item.getKode()) != null) {
                throw new InventoryException("Kode barang sudah digunakan.");
            }

            stmt.setString(1, item.getKode());
            stmt.setString(2, item.getNama());
            stmt.setString(3, item.getKategori());
            stmt.setInt(4, item.getStok());
            stmt.setString(5, item.getLokasi());

            stmt.executeUpdate();

            // CATAT RIWAYAT otomatis
            logTransaksi(item.getKode(), "MASUK", item.getStok(), "Barang baru ditambahkan");

            return item;

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {  // Duplicate entry
                throw new InventoryException("Kode barang sudah digunakan.");
            }
            throw new InventoryException("Gagal menambah barang: " + e.getMessage(), e);
        }
    }

    // ===========================
    // READ SEMUA BARANG
    // ===========================
    @Override
    public List<Item> findAll() throws InventoryException {
        String sql = "SELECT kode, nama, kategori, stok, lokasi, created_at, updated_at FROM barang ORDER BY kode";
        List<Item> list = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Item it = new Item();
                it.setKode(rs.getString("kode"));
                it.setNama(rs.getString("nama"));
                it.setKategori(rs.getString("kategori"));
                it.setStok(rs.getInt("stok"));
                it.setLokasi(rs.getString("lokasi"));
                it.setCreatedAt(rs.getTimestamp("created_at"));
                it.setUpdatedAt(rs.getTimestamp("updated_at"));
                list.add(it);
            }
        } catch (SQLException e) {
            throw new InventoryException("Gagal mengambil daftar barang: " + e.getMessage(), e);
        }
        return list;
    }

    // ===========================
    // FIND BY KODE
    // ===========================
    @Override
    public Item findById(String kode) throws InventoryException {
        String sql = "SELECT kode, nama, kategori, stok, lokasi, created_at, updated_at FROM barang WHERE kode=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Item it = new Item();
                    it.setKode(rs.getString("kode"));
                    it.setNama(rs.getString("nama"));
                    it.setKategori(rs.getString("kategori"));
                    it.setStok(rs.getInt("stok"));
                    it.setLokasi(rs.getString("lokasi"));
                    it.setCreatedAt(rs.getTimestamp("created_at"));
                    it.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return it;
                }
                return null;
            }
        } catch (SQLException e) {
            throw new InventoryException("Gagal mencari barang: " + e.getMessage(), e);
        }
    }

    // ===========================
    // UPDATE BARANG + LOG RIWAYAT
    // ===========================
    @Override
    public boolean update(Item item) throws InventoryException {
        Item lama = findById(item.getKode());
        if (lama == null) {
            throw new InventoryException("Barang dengan kode " + item.getKode() + " tidak ditemukan.");
        }

        String sql = "UPDATE barang SET nama=?, kategori=?, stok=?, lokasi=? WHERE kode=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, item.getNama());
            stmt.setString(2, item.getKategori());
            stmt.setInt(3, item.getStok());
            stmt.setString(4, item.getLokasi());
            stmt.setString(5, item.getKode());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                // Hitung selisih stok untuk riwayat
                int selisih = item.getStok() - lama.getStok();
                if (selisih != 0) {
                    String jenis = selisih > 0 ? "MASUK" : "KELUAR";
                    logTransaksi(item.getKode(), jenis, Math.abs(selisih), "Update stok barang");
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new InventoryException("Gagal mengupdate barang: " + e.getMessage(), e);
        }
    }

    // ===========================
    // DELETE BARANG + LOG RIWAYAT
    // ===========================
    @Override
    public boolean delete(String kode) throws InventoryException {
        Item item = findById(kode);
        if (item == null) {
            throw new InventoryException("Barang dengan kode " + kode + " tidak ditemukan.");
        }

        String sql = "DELETE FROM barang WHERE kode=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kode);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                // Catat riwayat penghapusan
                logTransaksi(kode, "KELUAR", item.getStok(), "Barang dihapus dari sistem");
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new InventoryException("Gagal menghapus barang: " + e.getMessage(), e);
        }
    }

    // ==========================================================
    // FITUR RIWAYAT TRANSAKSI (dipakai otomatis di atas)
    // ==========================================================
    public void logTransaksi(String kode, String jenis, int jumlah, String keterangan) throws InventoryException {
        String sql = "INSERT INTO riwayat_transaksi (kode_barang, jenis, jumlah, keterangan, timestamp) " +
                     "VALUES (?, ?, ?, ?, NOW())";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kode);
            stmt.setString(2, jenis);
            stmt.setInt(3, jumlah);
            stmt.setString(4, keterangan);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new InventoryException("Gagal mencatat riwayat transaksi: " + e.getMessage(), e);
        }
    }

    // ==========================================================
    // PENCARIAN BARANG BERDASARKAN NAMA / KATEGORI
    // ==========================================================
    public List<Item> search(String keyword) throws InventoryException {
        String sql = "SELECT kode, nama, kategori, stok, lokasi, created_at, updated_at " +
                     "FROM barang WHERE nama LIKE ? OR kategori LIKE ? ORDER BY nama";

        List<Item> result = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String like = "%" + keyword + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Item it = new Item();
                    it.setKode(rs.getString("kode"));
                    it.setNama(rs.getString("nama"));
                    it.setKategori(rs.getString("kategori"));
                    it.setStok(rs.getInt("stok"));
                    it.setLokasi(rs.getString("lokasi"));
                    it.setCreatedAt(rs.getTimestamp("created_at"));
                    it.setUpdatedAt(rs.getTimestamp("updated_at"));
                    result.add(it);
                }
            }
        } catch (SQLException e) {
            throw new InventoryException("Error pencarian: " + e.getMessage(), e);
        }
        return result;
    }

    // Method-method riwayat tambahan (getAllTransaksi & getTransaksiByKode) tetap ada
    // tapi tidak dipakai langsung oleh menu utama karena sudah ada RiwayatService yang lebih lengkap.
    // Boleh dibiarkan atau dihapus kalau mau lebih bersih.
}