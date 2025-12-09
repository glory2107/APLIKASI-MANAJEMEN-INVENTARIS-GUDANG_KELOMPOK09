/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RiwayatService extends BaseService {

    public RiwayatService() throws InventoryException {
        super();
    }

    // ===================== TAMBAH RIWAYAT ======================
    public void tambahRiwayat(String kodeBarang, String jenis, int jumlah, String keterangan)
            throws InventoryException {

        String sql = "INSERT INTO riwayat_transaksi (kode_barang, jenis, jumlah, keterangan, timestamp) "
                   + "VALUES (?, ?, ?, ?, NOW())";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kodeBarang);
            stmt.setString(2, jenis);
            stmt.setInt(3, jumlah);
            stmt.setString(4, keterangan);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new InventoryException("Gagal menyimpan riwayat transaksi: " + e.getMessage(), e);
        }
    }

    // ===================== TAMPIL SEMUA RIWAYAT ======================
    public void tampilkanSemuaRiwayat() throws InventoryException {
        String sql = "SELECT * FROM riwayat_transaksi ORDER BY timestamp DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n===== SEMUA RIWAYAT TRANSAKSI =====");
            System.out.printf("%-5s %-10s %-10s %-10s %-30s %-20s%n",
                    "ID", "KODE", "JENIS", "JUMLAH", "KETERANGAN", "WAKTU");

            while (rs.next()) {
                System.out.printf("%-5d %-10s %-10s %-10d %-30s %-20s%n",
                        rs.getInt("id"),
                        rs.getString("kode_barang"),
                        rs.getString("jenis"),
                        rs.getInt("jumlah"),
                        rs.getString("keterangan"),
                        rs.getTimestamp("timestamp"));
            }

        } catch (SQLException e) {
            throw new InventoryException("Gagal mengambil riwayat: " + e.getMessage(), e);
        }
    }

    // ===================== TAMPIL RIWAYAT PER BARANG ======================
    public void tampilkanRiwayatBarang(String kodeBarang) throws InventoryException {
        String sql = "SELECT * FROM riwayat_transaksi WHERE kode_barang = ? ORDER BY timestamp DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, kodeBarang);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n===== RIWAYAT BARANG: " + kodeBarang + " =====");
            System.out.printf("%-5s %-10s %-10s %-10s %-30s %-20s%n",
                    "ID", "KODE", "JENIS", "JUMLAH", "KETERANGAN", "WAKTU");

            boolean ada = false;
            while (rs.next()) {
                ada = true;
                System.out.printf("%-5d %-10s %-10s %-10d %-30s %-20s%n",
                        rs.getInt("id"),
                        rs.getString("kode_barang"),
                        rs.getString("jenis"),
                        rs.getInt("jumlah"),
                        rs.getString("keterangan"),
                        rs.getTimestamp("timestamp"));
            }

            if (!ada) {
                System.out.println("Tidak ada riwayat untuk kode: " + kodeBarang);
            }

        } catch (SQLException e) {
            throw new InventoryException("Gagal mengambil riwayat barang: " + e.getMessage(), e);
        }
    }
}
