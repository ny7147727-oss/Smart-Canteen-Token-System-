package com.smartcanteen.database.impl;

import com.smartcanteen.database.dao.IOrderItemDAO;
import com.smartcanteen.common.exception.DatabaseException;
import com.smartcanteen.model.OrderItem;
import com.smartcanteen.database.connection.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO implements IOrderItemDAO {

    @Override
    public boolean insertOrderItem(OrderItem orderItem) {
        try (Connection conn = DBConnection.getConnection()) {
            return insertOrderItem(conn, orderItem);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert order item for order ID: " + (orderItem != null ? orderItem.getOrderId() : 0), e);
        }
    }

    @Override
    public boolean insertOrderItem(Connection connection, OrderItem orderItem) {
        String sql = "INSERT INTO order_items (order_id, item_id, quantity, subtotal) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, orderItem.getOrderId());
            stmt.setInt(2, orderItem.getItemId());
            stmt.setInt(3, orderItem.getQuantity());
            stmt.setBigDecimal(4, orderItem.getSubtotal());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderItem.setOrderItemId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert order item inside transaction for order ID: " + (orderItem != null ? orderItem.getOrderId() : 0), e);
        }
    }

    @Override
    public List<OrderItem> getItemsByOrderId(int orderId) {
        String sql = "SELECT order_item_id, order_id, item_id, quantity, subtotal FROM order_items WHERE order_id = ?";
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve order items for order ID: " + orderId, e);
        }
        return items;
    }

    private OrderItem mapRow(ResultSet rs) throws SQLException {
        int orderItemId = rs.getInt("order_item_id");
        int orderId = rs.getInt("order_id");
        int itemId = rs.getInt("item_id");
        int quantity = rs.getInt("quantity");
        BigDecimal subtotal = rs.getBigDecimal("subtotal");

        return new OrderItem(orderItemId, orderId, itemId, quantity, subtotal);
    }
}
