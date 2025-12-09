/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseService {
    // TIDAK DIUBAH (sesuai permintaan)
    protected static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    protected static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/inventory_db";
    protected static final String DB_USER = "root";
    protected static final String DB_PASSWORD = "";

    protected Connection connection;

    protected BaseService() throws InventoryException {
        connect();
    }

    private void connect() throws InventoryException {
        try {
            Class.forName(DB_DRIVER);
            connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new InventoryException("Koneksi database gagal: " + e.getMessage(), e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException e) {
            System.out.println("Error menutup koneksi: " + e.getMessage());
        }
    }
}
