/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

public interface AuthRepository {

    User login(String username, String password) throws InventoryException;

    void register(User user) throws InventoryException;

    void tampilSemuaAdmin() throws InventoryException;

    void hapusAdmin(String username) throws InventoryException;
}
