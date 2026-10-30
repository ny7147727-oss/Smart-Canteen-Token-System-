package com.smartcanteen.database.impl;

import com.smartcanteen.database.dao.IOrderDAO;
import com.smartcanteen.database.dao.IOrderItemDAO;
import com.smartcanteen.common.exception.DatabaseException;
import com.smartcanteen.model.Order;
import com.smartcanteen.model.OrderStatus;
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

public class OrderDAO implements IOrderDAO {

    private final IOrderItemDAO orderItemDAO;

    public OrderDAO() {
        this.orderItemDAO = new OrderItemDAO();
    }

    public OrderDAO(IOrderItemDAO orderItemDAO) {
        this.orderItemDAO = (orderItemDAO != null) ? orderItemDAO : new OrderItemDAO();
    }

    @Override
    public int insertOrder(Order order) {
        try (Connection conn = DBConnection.getConnection()) {
            return insertOrder(conn, order);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert order for user ID: " + (order != null ? order.getUserId() : 0), e);
        }
    }

    @Override
    public int insertOrder(Connection connection, Order order) {
        String sql = "INSERT INTO orders (user_id, token_number, status, total_amount, order_time, completion_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getUserId());
            stmt.setInt(2, order.getTokenNumber());
            stmt.setString(3, (order.getStatus() != null ? order.getStatus() : OrderStatus.PLACED).name());
            stmt.setBigDecimal(4, order.getTotalAmount());
            stmt.setTimestamp(5, order.getOrderTime() != null ? Timestamp.valueOf(order.getOrderTime()) : new Timestamp(System.currentTimeMillis()));
            stmt.setTimestamp(6, order.getCompletionTime() != null ? Timestamp.valueOf(order.getCompletionTime()) : null);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        order.setOrderId(generatedId);
                        return generatedId;
                    }
                }
            }
            throw new DatabaseException("Order insertion failed, no ID obtained.");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert order in transaction for user ID: " + (order != null ? order.getUserId() : 0), e);
        }
    }

    @Override
    public boolean updateStatus(int orderId, OrderStatus status) {
        String sql = "UPDATE orders SET status = ?, completion_time = (CASE WHEN ? = 'COMPLETED' THEN CURRENT_TIMESTAMP ELSE completion_time END) WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, status.name());
            stmt.setInt(3, orderId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update order status for order ID: " + orderId, e);
        }
    }

    @Override
    public boolean updateStatus(Order order) {
        if (order == null) {
            return false;
        }
        return updateStatus(order.getOrderId(), order.getStatus());
    }

    @Override
    public Order getById(int orderId) {
        String sql = "SELECT order_id, user_id, token_number, status, total_amount, order_time, completion_time " +
                     "FROM orders WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = mapRow(rs);
                    order.setItems(orderItemDAO.getItemsByOrderId(orderId));
                    return order;
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve order with ID: " + orderId, e);
        }
        return null;
    }

    @Override
    public List<Order> getOrdersByUser(int userId) {
        String sql = "SELECT order_id, user_id, token_number, status, total_amount, order_time, completion_time " +
                     "FROM orders WHERE user_id = ? ORDER BY order_time DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = mapRow(rs);
                    order.setItems(orderItemDAO.getItemsByOrderId(order.getOrderId()));
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve orders for user ID: " + userId, e);
        }
        return orders;
    }

    @Override
    public List<Order> getActiveOrders() {
        String sql = "SELECT order_id, user_id, token_number, status, total_amount, order_time, completion_time " +
                     "FROM orders WHERE status IN ('PLACED', 'PREPARING', 'READY') ORDER BY order_time ASC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Order order = mapRow(rs);
                order.setItems(orderItemDAO.getItemsByOrderId(order.getOrderId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve active orders", e);
        }
        return orders;
    }

    @Override
    public List<Order> getAllOrders() {
        String sql = "SELECT order_id, user_id, token_number, status, total_amount, order_time, completion_time " +
                     "FROM orders ORDER BY order_time DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Order order = mapRow(rs);
                order.setItems(orderItemDAO.getItemsByOrderId(order.getOrderId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to retrieve all orders", e);
        }
        return orders;
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        int orderId = rs.getInt("order_id");
        int userId = rs.getInt("user_id");
        int tokenNumber = rs.getInt("token_number");
        String statusStr = rs.getString("status");
        OrderStatus status = (statusStr != null) ? OrderStatus.valueOf(statusStr) : OrderStatus.PLACED;
        BigDecimal totalAmount = rs.getBigDecimal("total_amount");
        Timestamp orderTimeTs = rs.getTimestamp("order_time");
        LocalDateTime orderTime = (orderTimeTs != null) ? orderTimeTs.toLocalDateTime() : null;
        Timestamp completionTimeTs = rs.getTimestamp("completion_time");
        LocalDateTime completionTime = (completionTimeTs != null) ? completionTimeTs.toLocalDateTime() : null;

        return new Order(orderId, userId, tokenNumber, status, totalAmount, orderTime, completionTime, new ArrayList<>());
    }
}
