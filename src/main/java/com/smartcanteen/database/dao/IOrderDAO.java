package com.smartcanteen.database.dao;

import com.smartcanteen.model.Order;
import com.smartcanteen.model.OrderStatus;

import java.sql.Connection;
import java.util.List;

public interface IOrderDAO {
    int insertOrder(Order order);
    int insertOrder(Connection connection, Order order);
    boolean updateStatus(int orderId, OrderStatus status);
    boolean updateStatus(Order order);
    Order getById(int orderId);
    List<Order> getOrdersByUser(int userId);
    List<Order> getActiveOrders();
    List<Order> getAllOrders();
}
