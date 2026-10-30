package com.smartcanteen.database.dao;

import com.smartcanteen.model.MenuItem;

import java.util.List;

public interface IMenuItemDAO {
    List<MenuItem> getAllItems();
    List<MenuItem> getAvailableItems();
    MenuItem getById(int id);
    MenuItem getByName(String itemName);
    boolean addItem(MenuItem item);
    boolean updateItem(MenuItem item);
    boolean deleteItem(int id);
    boolean deleteItem(MenuItem item);
}
