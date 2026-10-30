package com.smartcanteen.database.dao;

import com.smartcanteen.model.OrderItem;

import java.sql.Connection;
import java.util.List;

public interface IOrderItemDAO {
    boolean insertOrderItem(OrderItem orderItem);
    boolean insertOrderItem(Connection connection, OrderItem orderItem);
    List<OrderItem> getItemsByOrderId(int orderId);
}
