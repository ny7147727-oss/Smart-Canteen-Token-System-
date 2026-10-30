package com.smartcanteen.database.impl;

import com.smartcanteen.database.dao.IMenuItemDAO;
import com.smartcanteen.common.exception.DatabaseException;
import com.smartcanteen.model.MenuItem;
import com.smartcanteen.database.connection.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MenuItemDAO implements IMenuItemDAO {

    @Override
    public List<MenuItem> getAllItems() {
        String sql = "SELECT item_id, item_name, category, price, available, description, created_at " +
                     "FROM menu_items ORDER BY category, item_name";
        List<MenuItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all menu items", e);
        }
        return items;
    }

    @Override
    public List<MenuItem> getAvailableItems() {
        String sql = "SELECT item_id, item_name, category, price, available, description, created_at " +
                     "FROM menu_items WHERE available = TRUE ORDER BY category, item_name";
        List<MenuItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve available menu items", e);
        }
        return items;
    }

    @Override
    public MenuItem getById(int id) {
        String sql = "SELECT item_id, item_name, category, price, available, description, created_at " +
                     "FROM menu_items WHERE item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve menu item with ID: " + id, e);
        }
        return null;
    }

    @Override
    public MenuItem getByName(String itemName) {
        String sql = "SELECT item_id, item_name, category, price, available, description, created_at " +
                     "FROM menu_items WHERE LOWER(TRIM(item_name)) = LOWER(TRIM(?))";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, itemName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve menu item with name: " + itemName, e);
        }
        return null;
    }

    @Override
    public boolean addItem(MenuItem item) {
        String sql = "INSERT INTO menu_items (item_name, category, price, available, description) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getCategory());
            stmt.setBigDecimal(3, item.getPrice());
            stmt.setBoolean(4, item.isAvailable());
            stmt.setString(5, item.getDescription());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        item.setItemId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to add menu item: " + (item != null ? item.getItemName() : "null"), e);
        }
    }

    @Override
    public boolean updateItem(MenuItem item) {
        String sql = "UPDATE menu_items SET item_name = ?, category = ?, price = ?, available = ?, description = ? " +
                     "WHERE item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getItemName());
            stmt.setString(2, item.getCategory());
            stmt.setBigDecimal(3, item.getPrice());
            stmt.setBoolean(4, item.isAvailable());
            stmt.setString(5, item.getDescription());
            stmt.setInt(6, item.getItemId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update menu item with ID: " + (item != null ? item.getItemId() : 0), e);
        }
    }

    @Override
    public boolean deleteItem(int id) {
        String sql = "DELETE FROM menu_items WHERE item_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete menu item with ID: " + id, e);
        }
    }

    @Override
    public boolean deleteItem(MenuItem item) {
        if (item == null) {
            return false;
        }
        return deleteItem(item.getItemId());
    }

    private MenuItem mapRow(ResultSet rs) throws SQLException {
        int itemId = rs.getInt("item_id");
        String itemName = rs.getString("item_name");
        String category = rs.getString("category");
        BigDecimal price = rs.getBigDecimal("price");
        boolean available = rs.getBoolean("available");
        String description = rs.getString("description");
        Timestamp ts = rs.getTimestamp("created_at");
        LocalDateTime createdAt = (ts != null) ? ts.toLocalDateTime() : null;

        return new MenuItem(itemId, itemName, category, price, available, description, createdAt);
    }
}
