package com.smartcanteen.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private int orderId;
    private int userId;
    private int tokenNumber;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime orderTime;
    private LocalDateTime completionTime;
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(int orderId, int userId, int tokenNumber, OrderStatus status, BigDecimal totalAmount, LocalDateTime orderTime, LocalDateTime completionTime, List<OrderItem> items) {
        this.orderId = orderId;
        this.userId = userId;
        this.tokenNumber = tokenNumber;
        this.status = status;
        this.totalAmount = totalAmount;
        this.orderTime = orderTime;
        this.completionTime = completionTime;
        this.items = (items != null) ? items : new ArrayList<>();
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(int tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = (items != null) ? items : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", tokenNumber=" + tokenNumber +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", orderTime=" + orderTime +
                ", completionTime=" + completionTime +
                ", items=" + items +
                '}';
    }
}
