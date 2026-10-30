package com.smartcanteen.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AdminOrderDTO {
    private int orderId;
    private int tokenNumber;
    private String studentName;
    private String rollNumber;
    private String itemsSummary;
    private BigDecimal amount;
    private String paymentStatus;
    private OrderStatus orderStatus;
    private LocalDateTime orderTime;
    private Order rawOrder;

    public AdminOrderDTO() {
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(int tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getCustomerDisplay() {
        if (rollNumber != null && !rollNumber.isEmpty()) {
            return studentName + " (" + rollNumber + ")";
        }
        return studentName != null ? studentName : "Student";
    }

    public String getItemsSummary() {
        return itemsSummary;
    }

    public void setItemsSummary(String itemsSummary) {
        this.itemsSummary = itemsSummary;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(LocalDateTime orderTime) {
        this.orderTime = orderTime;
    }

    public String getFormattedTime() {
        if (orderTime != null) {
            return orderTime.format(DateTimeFormatter.ofPattern("hh:mm a, dd MMM"));
        }
        return "Just now";
    }

    public Order getRawOrder() {
        return rawOrder;
    }

    public void setRawOrder(Order rawOrder) {
        this.rawOrder = rawOrder;
    }
}
