package com.smartcanteen.database.dao;

import com.smartcanteen.model.Payment;
import com.smartcanteen.model.PaymentStatus;

import java.sql.Connection;
import java.util.List;

public interface IPaymentDAO {
    int insertPayment(Payment payment);
    int insertPayment(Connection connection, Payment payment);
    boolean updateStatus(int paymentId, PaymentStatus status);
    boolean updatePaymentOrderId(Connection connection, int paymentId, int orderId);
    Payment getById(int paymentId);
    Payment getByTransactionReference(String transactionReference);
    List<Payment> getPaymentsByUser(int userId);
    List<Payment> getPaymentsByOrder(int orderId);
    List<Payment> getAllPayments();
}
