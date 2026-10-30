package com.smartcanteen.database.impl;

import com.smartcanteen.database.dao.IPaymentDAO;
import com.smartcanteen.common.exception.DatabaseException;
import com.smartcanteen.model.Payment;
import com.smartcanteen.model.PaymentMethod;
import com.smartcanteen.model.PaymentStatus;
import com.smartcanteen.database.connection.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO implements IPaymentDAO {

    public PaymentDAO() {
        ensureTableExists();
    }

    private void ensureTableExists() {
        String sql = "CREATE TABLE IF NOT EXISTS payments (" +
                "payment_id INT PRIMARY KEY AUTO_INCREMENT, " +
                "user_id INT NOT NULL, " +
                "order_id INT NULL, " +
                "amount DECIMAL(10, 2) NOT NULL, " +
                "payment_method VARCHAR(20) NOT NULL, " +
                "payment_status VARCHAR(20) NOT NULL, " +
                "transaction_reference VARCHAR(64) UNIQUE NOT NULL, " +
                "masked_details VARCHAR(100), " +
                "paid_at TIMESTAMP NULL, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE, " +
                "FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE SET NULL, " +
                "INDEX idx_payments_user_id (user_id), " +
                "INDEX idx_payments_order_id (order_id), " +
                "INDEX idx_payments_status (payment_status), " +
                "INDEX idx_payments_txn_ref (transaction_reference)" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Notice: Payment table schema check: " + e.getMessage());
        }
    }

    @Override
    public int insertPayment(Payment payment) {
        try (Connection conn = DBConnection.getConnection()) {
            return insertPayment(conn, payment);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert payment: " + e.getMessage(), e);
        }
    }

    @Override
    public int insertPayment(Connection connection, Payment payment) {
        String sql = "INSERT INTO payments (user_id, order_id, amount, payment_method, payment_status, transaction_reference, masked_details, paid_at, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getUserId());
            if (payment.getOrderId() != null && payment.getOrderId() > 0) {
                stmt.setInt(2, payment.getOrderId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setBigDecimal(3, payment.getAmount() != null ? payment.getAmount() : BigDecimal.ZERO);
            stmt.setString(4, payment.getPaymentMethod() != null ? payment.getPaymentMethod().name() : PaymentMethod.UPI.name());
            stmt.setString(5, payment.getPaymentStatus() != null ? payment.getPaymentStatus().name() : PaymentStatus.PENDING.name());
            stmt.setString(6, payment.getTransactionReference());
            stmt.setString(7, payment.getMaskedDetails());
            stmt.setTimestamp(8, payment.getPaidAt() != null ? Timestamp.valueOf(payment.getPaidAt()) : null);
            stmt.setTimestamp(9, payment.getCreatedAt() != null ? Timestamp.valueOf(payment.getCreatedAt()) : new Timestamp(System.currentTimeMillis()));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        payment.setPaymentId(generatedId);
                        return generatedId;
                    }
                }
            }
            throw new DatabaseException("Payment insertion failed, no ID obtained.");
        } catch (SQLException e) {
            throw new DatabaseException("Failed to insert payment record: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateStatus(int paymentId, PaymentStatus status) {
        String sql = "UPDATE payments SET payment_status = ?, paid_at = (CASE WHEN ? = 'SUCCESS' THEN CURRENT_TIMESTAMP ELSE paid_at END) WHERE payment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, status.name());
            stmt.setInt(3, paymentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update payment status for payment ID: " + paymentId, e);
        }
    }

    @Override
    public boolean updatePaymentOrderId(Connection connection, int paymentId, int orderId) {
        String sql = "UPDATE payments SET order_id = ? WHERE payment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, paymentId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update payment order ID for payment ID: " + paymentId, e);
        }
    }

    @Override
    public Payment getById(int paymentId) {
        String sql = "SELECT payment_id, user_id, order_id, amount, payment_method, payment_status, transaction_reference, masked_details, paid_at, created_at " +
                     "FROM payments WHERE payment_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch payment by ID: " + paymentId, e);
        }
    }

    @Override
    public Payment getByTransactionReference(String transactionReference) {
        String sql = "SELECT payment_id, user_id, order_id, amount, payment_method, payment_status, transaction_reference, masked_details, paid_at, created_at " +
                     "FROM payments WHERE transaction_reference = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, transactionReference);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch payment by reference: " + transactionReference, e);
        }
    }

    @Override
    public List<Payment> getPaymentsByUser(int userId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT payment_id, user_id, order_id, amount, payment_method, payment_status, transaction_reference, masked_details, paid_at, created_at " +
                     "FROM payments WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch payments for user ID: " + userId, e);
        }
    }

    @Override
    public List<Payment> getPaymentsByOrder(int orderId) {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT payment_id, user_id, order_id, amount, payment_method, payment_status, transaction_reference, masked_details, paid_at, created_at " +
                     "FROM payments WHERE order_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch payments for order ID: " + orderId, e);
        }
    }

    @Override
    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT payment_id, user_id, order_id, amount, payment_method, payment_status, transaction_reference, masked_details, paid_at, created_at " +
                     "FROM payments ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all payments", e);
        }
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setUserId(rs.getInt("user_id"));

        int orderId = rs.getInt("order_id");
        if (!rs.wasNull()) {
            payment.setOrderId(orderId);
        } else {
            payment.setOrderId(null);
        }

        payment.setAmount(rs.getBigDecimal("amount"));

        String methodStr = rs.getString("payment_method");
        try {
            payment.setPaymentMethod(PaymentMethod.valueOf(methodStr));
        } catch (Exception ignored) {
            payment.setPaymentMethod(PaymentMethod.UPI);
        }

        String statusStr = rs.getString("payment_status");
        try {
            payment.setPaymentStatus(PaymentStatus.valueOf(statusStr));
        } catch (Exception ignored) {
            payment.setPaymentStatus(PaymentStatus.PENDING);
        }

        payment.setTransactionReference(rs.getString("transaction_reference"));
        payment.setMaskedDetails(rs.getString("masked_details"));

        Timestamp paidTs = rs.getTimestamp("paid_at");
        if (paidTs != null) {
            payment.setPaidAt(paidTs.toLocalDateTime());
        }

        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            payment.setCreatedAt(createdTs.toLocalDateTime());
        }

        return payment;
    }
}
