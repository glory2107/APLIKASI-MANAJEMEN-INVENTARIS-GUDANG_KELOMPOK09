/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import java.sql.*;

public class AuthService extends BaseService implements AuthRepository {

    public AuthService() throws InventoryException {
        super();
    }

    // LOGIN HANYA ROLE = 'admin'
    @Override
    public User login(String username, String password) throws InventoryException {
        String sql = "SELECT username, password, role, nama_lengkap FROM users WHERE username = ? AND password = ? AND role = 'admin'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("nama_lengkap")
                    );
                }
                return null;
            }
        } catch (SQLException e) {
            throw new InventoryException("Error saat login: " + e.getMessage(), e);
        }
    }

    // REGISTER ADMIN (role selalu 'admin')
    @Override
    public void register(User user) throws InventoryException {
        if (!user.isValid()) throw new InventoryException("Data admin tidak valid!");

        String sql = "INSERT INTO users (username, password, role, nama_lengkap) VALUES (?, ?, 'admin', ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getNamaLengkap());

            stmt.executeUpdate();
            System.out.println("Admin berhasil ditambahkan!");

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) throw new InventoryException("Username sudah digunakan!");
            throw new InventoryException("Gagal menambahkan admin: " + e.getMessage(), e);
        }
    }

    @Override
    public void tampilSemuaAdmin() throws InventoryException {
        String sql = "SELECT username, nama_lengkap FROM users WHERE role='admin' ORDER BY username";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n" + "=".repeat(60));
            System.out.printf("%30s%n", "DAFTAR ADMIN");
            System.out.println("=".repeat(60));
            System.out.printf("%-20s %-25s%n", "USERNAME", "NAMA LENGKAP");
            System.out.println("-".repeat(60));

            int count = 0;
            while (rs.next()) {
                System.out.printf("%-20s %-25s%n",
                        rs.getString("username"),
                        rs.getString("nama_lengkap")
                );
                count++;
            }

            System.out.println("-".repeat(60));
            System.out.println("Total admin: " + count);

        } catch (SQLException e) {
            throw new InventoryException("Gagal mengambil data admin: " + e.getMessage(), e);
        }
    }

    @Override
    public void hapusAdmin(String username) throws InventoryException {
        String sql = "DELETE FROM users WHERE username = ? AND role='admin'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            int rows = stmt.executeUpdate();
            if (rows > 0) System.out.println("Admin berhasil dihapus!");
            else System.out.println("Admin tidak ditemukan!");
        } catch (SQLException e) {
            throw new InventoryException("Gagal menghapus admin: " + e.getMessage(), e);
        }
    }
}
