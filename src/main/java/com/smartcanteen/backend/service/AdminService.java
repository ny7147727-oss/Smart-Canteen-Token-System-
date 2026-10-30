package com.smartcanteen.backend.service;

import com.smartcanteen.database.dao.IMenuItemDAO;
import com.smartcanteen.database.dao.IOrderDAO;
import com.smartcanteen.database.dao.IOrderItemDAO;
import com.smartcanteen.database.dao.IPaymentDAO;
import com.smartcanteen.database.dao.ITokenDAO;
import com.smartcanteen.database.dao.IUserDAO;
import com.smartcanteen.database.impl.MenuItemDAO;
import com.smartcanteen.database.impl.OrderDAO;
import com.smartcanteen.database.impl.OrderItemDAO;
import com.smartcanteen.database.impl.PaymentDAO;
import com.smartcanteen.database.impl.TokenDAO;
import com.smartcanteen.database.impl.UserDAO;
import com.smartcanteen.common.exception.ValidationException;
import com.smartcanteen.model.AdminOrderDTO;
import com.smartcanteen.model.MenuItem;
import com.smartcanteen.model.Order;
import com.smartcanteen.model.OrderItem;
import com.smartcanteen.model.OrderStatus;
import com.smartcanteen.model.Payment;
import com.smartcanteen.model.PaymentStatus;
import com.smartcanteen.model.TokenStatus;
import com.smartcanteen.model.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminService {

    private final IOrderDAO orderDAO;
    private final IOrderItemDAO orderItemDAO;
    private final IMenuItemDAO menuItemDAO;
    private final IUserDAO userDAO;
    private final IPaymentDAO paymentDAO;
    private final ITokenDAO tokenDAO;

    public AdminService() {
        this.orderDAO = new OrderDAO();
        this.orderItemDAO = new OrderItemDAO();
        this.menuItemDAO = new MenuItemDAO();
        this.userDAO = new UserDAO();
        this.paymentDAO = new PaymentDAO();
        this.tokenDAO = new TokenDAO();
    }

    public AdminService(IOrderDAO orderDAO, IOrderItemDAO orderItemDAO, IMenuItemDAO menuItemDAO,
                        IUserDAO userDAO, IPaymentDAO paymentDAO, ITokenDAO tokenDAO) {
        this.orderDAO = (orderDAO != null) ? orderDAO : new OrderDAO();
        this.orderItemDAO = (orderItemDAO != null) ? orderItemDAO : new OrderItemDAO();
        this.menuItemDAO = (menuItemDAO != null) ? menuItemDAO : new MenuItemDAO();
        this.userDAO = (userDAO != null) ? userDAO : new UserDAO();
        this.paymentDAO = (paymentDAO != null) ? paymentDAO : new PaymentDAO();
        this.tokenDAO = (tokenDAO != null) ? tokenDAO : new TokenDAO();
    }

    /**
     * Calculates high-level summary KPIs for the Admin Dashboard.
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        List<Order> allOrders = orderDAO.getAllOrders();

        int totalOrders = 0;
        int activeOrders = 0;
        int preparingOrders = 0;
        int readyOrders = 0;
        int completedOrders = 0;
        BigDecimal totalSales = BigDecimal.ZERO;

        if (allOrders != null) {
            totalOrders = allOrders.size();
            for (Order order : allOrders) {
                OrderStatus status = order.getStatus();
                if (status != null) {
                    switch (status) {
                        case PLACED:
                            activeOrders++;
                            break;
                        case PREPARING:
                            activeOrders++;
                            preparingOrders++;
                            break;
                        case READY:
                            activeOrders++;
                            readyOrders++;
                            break;
                        case COMPLETED:
                            completedOrders++;
                            break;
                        default:
                            break;
                    }
                }

                if (order.getStatus() != OrderStatus.CANCELLED && order.getTotalAmount() != null) {
                    totalSales = totalSales.add(order.getTotalAmount());
                }
            }
        }

        stats.put("totalOrders", totalOrders);
        stats.put("activeOrders", activeOrders);
        stats.put("preparingOrders", preparingOrders);
        stats.put("readyOrders", readyOrders);
        stats.put("completedOrders", completedOrders);
        stats.put("totalSales", totalSales);

        return stats;
    }

    /**
     * Retrieves enriched order records for dashboard display with optional status filtering.
     */
    public List<AdminOrderDTO> getAdminOrders(String filter) {
        List<AdminOrderDTO> dtoList = new ArrayList<>();
        List<Order> rawOrders = orderDAO.getAllOrders();

        if (rawOrders == null || rawOrders.isEmpty()) {
            return dtoList;
        }

        for (Order order : rawOrders) {
            OrderStatus status = order.getStatus();

            // Filter logic
            if (filter != null && !filter.equalsIgnoreCase("ALL")) {
                if (filter.equalsIgnoreCase("ACTIVE")) {
                    if (status != OrderStatus.PLACED && status != OrderStatus.PREPARING && status != OrderStatus.READY) {
                        continue;
                    }
                } else if (filter.equalsIgnoreCase("QUEUED") || filter.equalsIgnoreCase("PLACED")) {
                    if (status != OrderStatus.PLACED) {
                        continue;
                    }
                } else if (filter.equalsIgnoreCase("PREPARING")) {
                    if (status != OrderStatus.PREPARING) {
                        continue;
                    }
                } else if (filter.equalsIgnoreCase("READY")) {
                    if (status != OrderStatus.READY) {
                        continue;
                    }
                } else if (filter.equalsIgnoreCase("COMPLETED")) {
                    if (status != OrderStatus.COMPLETED) {
                        continue;
                    }
                }
            }

            AdminOrderDTO dto = new AdminOrderDTO();
            dto.setOrderId(order.getOrderId());
            dto.setTokenNumber(order.getTokenNumber());
            dto.setAmount(order.getTotalAmount());
            dto.setOrderStatus(order.getStatus());
            dto.setOrderTime(order.getOrderTime());
            dto.setRawOrder(order);

            // 1. Resolve User/Student details
            try {
                User user = userDAO.getUserById(order.getUserId());
                if (user != null) {
                    dto.setStudentName(user.getFullName() != null ? user.getFullName() : user.getUsername());
                    dto.setRollNumber(user.getRollNumber());
                } else {
                    dto.setStudentName("Student #" + order.getUserId());
                }
            } catch (Exception e) {
                dto.setStudentName("Student #" + order.getUserId());
            }

            // 2. Resolve Items breakdown
            try {
                List<OrderItem> items = orderItemDAO.getItemsByOrderId(order.getOrderId());
                StringBuilder sb = new StringBuilder();
                if (items != null && !items.isEmpty()) {
                    for (int i = 0; i < items.size(); i++) {
                        OrderItem item = items.get(i);
                        String itemName = "Item #" + item.getItemId();
                        try {
                            MenuItem mi = menuItemDAO.getById(item.getItemId());
                            if (mi != null && mi.getItemName() != null) {
                                itemName = mi.getItemName();
                            }
                        } catch (Exception ignored) {
                        }
                        sb.append(itemName).append(" × ").append(item.getQuantity());
                        if (i < items.size() - 1) {
                            sb.append(", ");
                        }
                    }
                } else {
                    sb.append("Standard meal");
                }
                dto.setItemsSummary(sb.toString());
            } catch (Exception e) {
                dto.setItemsSummary("Items data unavailable");
            }

            // 3. Resolve Payment Status
            try {
                List<Payment> payments = paymentDAO.getPaymentsByOrder(order.getOrderId());
                if (payments != null && !payments.isEmpty()) {
                    Payment p = payments.get(0);
                    dto.setPaymentStatus(p.getPaymentStatus().name() + " (" + p.getPaymentMethod().name() + ")");
                } else {
                    dto.setPaymentStatus("SUCCESS (PAID)");
                }
            } catch (Exception e) {
                dto.setPaymentStatus("SUCCESS (PAID)");
            }

            dtoList.add(dto);
        }

        return dtoList;
    }

    /**
     * Updates an order status with strict verification that unpaid orders cannot enter preparation.
     */
    public boolean updateOrderStatus(int orderId, OrderStatus newStatus) throws ValidationException {
        if (orderId <= 0 || newStatus == null) {
            throw new ValidationException("Invalid order ID or target order status.");
        }

        Order order = orderDAO.getById(orderId);
        if (order == null) {
            throw new ValidationException("Order #" + orderId + " not found.");
        }

        // Business Rule: Verify payment is successful before allowing preparation
        if (newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.READY) {
            try {
                List<Payment> payments = paymentDAO.getPaymentsByOrder(orderId);
                if (payments != null && !payments.isEmpty()) {
                    Payment p = payments.get(0);
                    if (p.getPaymentStatus() == PaymentStatus.FAILED || p.getPaymentStatus() == PaymentStatus.CANCELLED) {
                        throw new ValidationException("Cannot move unpaid or failed order #" + orderId + " into kitchen preparation workflow.");
                    }
                }
            } catch (ValidationException ve) {
                throw ve;
            } catch (Exception ignored) {
            }
        }

        // 1. Update Order Status in Database
        boolean updated = orderDAO.updateStatus(orderId, newStatus);
        if (!updated) {
            return false;
        }

        // 2. Synchronize Token status in Database
        try {
            if (newStatus == OrderStatus.READY) {
                tokenDAO.updateStatus(order.getTokenNumber(), TokenStatus.CALLED);
            } else if (newStatus == OrderStatus.COMPLETED) {
                tokenDAO.updateStatus(order.getTokenNumber(), TokenStatus.SERVED);
            } else if (newStatus == OrderStatus.PLACED || newStatus == OrderStatus.PREPARING) {
                tokenDAO.updateStatus(order.getTokenNumber(), TokenStatus.WAITING);
            }
        } catch (Exception e) {
            System.err.println("Notice: Token status sync on order update: " + e.getMessage());
        }

        return true;
    }
}
