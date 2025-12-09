/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Main;

import java.util.List;

public interface CrudRepository<T, ID> {
    T create(T t) throws InventoryException;
    List<T> findAll() throws InventoryException;
    T findById(ID id) throws InventoryException;
    boolean update(T t) throws InventoryException;
    boolean delete(ID id) throws InventoryException;
}
