package com.smartcanteen.backend.service;

import com.smartcanteen.database.dao.IMenuItemDAO;
import com.smartcanteen.database.impl.MenuItemDAO;
import com.smartcanteen.model.MenuItem;
import com.smartcanteen.common.util.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuService {

    private final IMenuItemDAO menuItemDAO;

    public MenuService() {
        this.menuItemDAO = new MenuItemDAO();
    }

    public MenuService(IMenuItemDAO menuItemDAO) {
        this.menuItemDAO = (menuItemDAO != null) ? menuItemDAO : new MenuItemDAO();
    }

    public List<MenuItem> getAllItems() {
        return menuItemDAO.getAllItems();
    }

    public List<MenuItem> getAvailableItems() {
        return menuItemDAO.getAvailableItems();
    }

    public MenuItem getById(int itemId) {
        if (itemId <= 0) {
            return null;
        }
        return menuItemDAO.getById(itemId);
    }

    public MenuItem getByName(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return null;
        }
        return menuItemDAO.getByName(itemName.trim());
    }

    public MenuItem getByName(MenuItem item) {
        if (item == null || item.getItemName() == null) {
            return null;
        }
        return getByName(item.getItemName());
    }

    public Map<String, List<MenuItem>> groupByCategory() {
        List<MenuItem> items = menuItemDAO.getAllItems();
        Map<String, List<MenuItem>> grouped = new HashMap<>();

        for (MenuItem item : items) {
            String category = (item.getCategory() != null && !item.getCategory().trim().isEmpty())
                    ? item.getCategory().trim()
                    : "Uncategorized";
            grouped.computeIfAbsent(category, k -> new ArrayList<>()).add(item);
        }

        return grouped;
    }

    public boolean addItem(MenuItem item) {
        if (item == null) {
            return false;
        }
        if (!Validator.isNotEmpty(item.getItemName()) || !Validator.isNotEmpty(item.getCategory())) {
            return false;
        }
        if (!Validator.isPositiveNumber(item.getPrice())) {
            return false;
        }
        return menuItemDAO.addItem(item);
    }

    public boolean updateItem(MenuItem item) {
        if (item == null || item.getItemId() <= 0) {
            return false;
        }
        if (!Validator.isNotEmpty(item.getItemName()) || !Validator.isNotEmpty(item.getCategory())) {
            return false;
        }
        if (!Validator.isPositiveNumber(item.getPrice())) {
            return false;
        }
        return menuItemDAO.updateItem(item);
    }

    public boolean deleteItem(int itemId) {
        if (itemId <= 0) {
            return false;
        }
        return menuItemDAO.deleteItem(itemId);
    }

    public boolean deleteItem(MenuItem item) {
        if (item == null) {
            return false;
        }
        return deleteItem(item.getItemId());
    }
}
