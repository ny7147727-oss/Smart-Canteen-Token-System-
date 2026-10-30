package com.smartcanteen.backend.service;

import com.smartcanteen.database.dao.IOrderDAO;
import com.smartcanteen.database.dao.IOrderItemDAO;
import com.smartcanteen.database.dao.IPaymentDAO;
import com.smartcanteen.database.dao.ITokenDAO;
import com.smartcanteen.database.impl.OrderDAO;
import com.smartcanteen.database.impl.OrderItemDAO;
import com.smartcanteen.database.impl.PaymentDAO;
import com.smartcanteen.database.impl.TokenDAO;
import com.smartcanteen.common.exception.OrderException;
import com.smartcanteen.model.CartItem;
import com.smartcanteen.model.Order;
import com.smartcanteen.model.OrderItem;
import com.smartcanteen.model.OrderStatus;
import com.smartcanteen.model.Payment;
import com.smartcanteen.model.Token;
import com.smartcanteen.model.TokenStatus;
import com.smartcanteen.database.connection.DBConnection;
import com.smartcanteen.common.util.TokenGenerator;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final IOrderDAO orderDAO;
    private final IOrderItemDAO orderItemDAO;
    private final ITokenDAO tokenDAO;
    private final IPaymentDAO paymentDAO;

    public OrderService() {
        this.orderDAO = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.tokenDAO = new TokenDAO();
        this.paymentDAO = new PaymentDAO();
    }

    public OrderService(IOrderDAO orderDAO, IOrderItemDAO orderItemDAO, ITokenDAO tokenDAO, IPaymentDAO paymentDAO) {
        this.orderDAO = (orderDAO != null) ? orderDAO : new OrderDAO();
        this.orderItemDAO = (orderItemDAO != null) ? orderItemDAO : new OrderItemDAO();
        this.tokenDAO = (tokenDAO != null) ? tokenDAO : new TokenDAO();
        this.paymentDAO = (paymentDAO != null) ? paymentDAO : new PaymentDAO();
    }

    public Order placeOrder(int userId, CartService cart) throws OrderException {
        return placeOrder(userId, cart, null);
    }

    public Order placeOrder(Order order, CartService cart) throws OrderException {
        if (order == null) {
            throw new OrderException("Order cannot be null.");
        }
        return placeOrder(order.getUserId(), cart, null);
    }

    public Order placeOrder(int userId, CartService cart, Payment payment) throws OrderException {
        if (cart == null) {
            throw new OrderException("Cart cannot be null.");
        }

        List<CartItem> cartItems = cart.getItems();
        if (cartItems == null || cartItems.isEmpty()) {
            throw new OrderException("Cart is empty. Please add items before placing an order.");
        }

        BigDecimal totalAmount = cart.getTotal();
        // Generate next token strictly upon confirmed placement
        int tokenNumber = TokenGenerator.next();

        Order order = new Order();
        order.setUserId(userId);
        order.setTokenNumber(tokenNumber);
        order.setStatus(OrderStatus.PLACED);
        order.setTotalAmount(totalAmount);
        order.setOrderTime(LocalDateTime.now());

        Connection connection = null;
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            // 1. Insert Order
            int generatedOrderId = orderDAO.insertOrder(connection, order);
            order.setOrderId(generatedOrderId);

            // 2. Insert Order Items
            List<OrderItem> createdItems = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                if (cartItem.getItem() == null) {
                    continue;
                }
                OrderItem orderItem = new OrderItem();
                orderItem.setOrderId(generatedOrderId);
                orderItem.setItemId(cartItem.getItem().getItemId());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setSubtotal(cartItem.getSubtotal());

                orderItemDAO.insertOrderItem(connection, orderItem);
                createdItems.add(orderItem);
            }

            // 3. Insert Token (Generated ONLY after payment approval)
            Token token = new Token();
            token.setTokenNumber(tokenNumber);
            token.setOrderId(generatedOrderId);
            token.setStatus(TokenStatus.WAITING);
            token.setGeneratedAt(LocalDateTime.now());

            tokenDAO.insertToken(connection, token);

            // 4. Update Payment record with Order ID if provided
            if (payment != null && payment.getPaymentId() > 0) {
                paymentDAO.updatePaymentOrderId(connection, payment.getPaymentId(), generatedOrderId);
            }

            // 5. Commit Transaction
            connection.commit();

            // 6. Update Order state & clear cart ONLY after successful commit
            order.setItems(createdItems);
            cart.clear();

            // 7. Enqueue into background preparation queue
            com.smartcanteen.threading.OrderQueueManager.getInstance().enqueue(order);

            return order;

        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Warning: Transaction rollback failed: " + rollbackEx.getMessage());
                }
            }
            throw new OrderException("Failed to place order: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException closeEx) {
                    System.err.println("Warning: Closing connection failed: " + closeEx.getMessage());
                }
            }
        }
    }

    public List<Order> getOrderHistory(int userId) {
        return orderDAO.getOrdersByUser(userId);
    }

    public boolean updateOrderStatus(int orderId, OrderStatus status) {
        boolean updated = orderDAO.updateStatus(orderId, status);
        if (updated) {
            try {
                Order order = orderDAO.getById(orderId);
                if (order != null && order.getTokenNumber() > 0) {
                    if (status == OrderStatus.READY) {
                        tokenDAO.updateStatus(order.getTokenNumber(), TokenStatus.CALLED);
                    } else if (status == OrderStatus.COMPLETED) {
                        tokenDAO.updateStatus(order.getTokenNumber(), TokenStatus.SERVED);
                    } else if (status == OrderStatus.PLACED || status == OrderStatus.PREPARING) {
                        tokenDAO.updateStatus(order.getTokenNumber(), TokenStatus.WAITING);
                    }
                }
            } catch (Exception e) {
                System.err.println("Notice: Token status sync: " + e.getMessage());
            }
        }
        return updated;
    }

    public boolean updateOrderStatus(Order order) {
        if (order == null) {
            return false;
        }
        return updateOrderStatus(order.getOrderId(), order.getStatus());
    }

    public List<Order> getActiveOrders() {
        return orderDAO.getActiveOrders();
    }
}
