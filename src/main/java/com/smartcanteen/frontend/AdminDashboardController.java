package com.smartcanteen.frontend;

import com.smartcanteen.common.exception.ValidationException;
import com.smartcanteen.model.AdminOrderDTO;
import com.smartcanteen.model.OrderStatus;
import com.smartcanteen.model.User;
import com.smartcanteen.backend.service.AdminService;
import com.smartcanteen.frontend.util.AlertUtil;
import com.smartcanteen.frontend.util.SceneNavigator;
import com.smartcanteen.common.util.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML
    private Label adminWelcomeLabel;

    // Top Summary KPI Cards
    @FXML
    private Label totalOrdersLabel;

    @FXML
    private Label activeOrdersLabel;

    @FXML
    private Label preparingOrdersLabel;

    @FXML
    private Label readyOrdersLabel;

    @FXML
    private Label completedOrdersLabel;

    @FXML
    private Label totalSalesLabel;

    // Filter Buttons
    @FXML
    private ToggleGroup orderFilterGroup;

    @FXML
    private ToggleButton filterActiveBtn;

    @FXML
    private ToggleButton filterQueuedBtn;

    @FXML
    private ToggleButton filterPreparingBtn;

    @FXML
    private ToggleButton filterReadyBtn;

    @FXML
    private ToggleButton filterCompletedBtn;

    @FXML
    private ToggleButton filterAllBtn;

    // Orders TableView
    @FXML
    private TableView<AdminOrderDTO> ordersTableView;

    @FXML
    private TableColumn<AdminOrderDTO, String> orderIdCol;

    @FXML
    private TableColumn<AdminOrderDTO, String> tokenNumberCol;

    @FXML
    private TableColumn<AdminOrderDTO, String> studentCol;

    @FXML
    private TableColumn<AdminOrderDTO, String> itemsCol;

    @FXML
    private TableColumn<AdminOrderDTO, String> amountCol;

    @FXML
    private TableColumn<AdminOrderDTO, String> paymentStatusCol;

    @FXML
    private TableColumn<AdminOrderDTO, String> orderStatusCol;

    @FXML
    private TableColumn<AdminOrderDTO, String> timeCol;

    private final AdminService adminService = new AdminService();
    private String currentFilter = "ACTIVE";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Setup Admin Profile
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            String name = (user.getFullName() != null && !user.getFullName().isEmpty())
                    ? user.getFullName()
                    : user.getUsername();
            adminWelcomeLabel.setText("Welcome, " + name + " (Admin)");
        }

        // 2. Setup Table Columns
        setupTableColumns();

        // 3. Load Stats and Table
        loadDashboardStats();
        loadOrdersTable("ACTIVE");
    }

    private void setupTableColumns() {
        orderIdCol.setCellValueFactory(data ->
                new SimpleStringProperty("ORD-#" + data.getValue().getOrderId()));

        tokenNumberCol.setCellValueFactory(data ->
                new SimpleStringProperty("TOKEN #" + data.getValue().getTokenNumber()));

        studentCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCustomerDisplay()));

        itemsCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getItemsSummary()));

        amountCol.setCellValueFactory(data ->
                new SimpleStringProperty(String.format("₹%.2f", data.getValue().getAmount())));

        paymentStatusCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getPaymentStatus()));

        orderStatusCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getOrderStatus() != null ? data.getValue().getOrderStatus().name() : "N/A"));

        timeCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFormattedTime()));

        // Styling for Order Status Column
        orderStatusCol.setCellFactory(column -> new TableCell<AdminOrderDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equalsIgnoreCase("READY")) {
                        setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
                    } else if (item.equalsIgnoreCase("PREPARING")) {
                        setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");
                    } else if (item.equalsIgnoreCase("PLACED")) {
                        setStyle("-fx-text-fill: #d97706; -fx-font-weight: bold;");
                    } else if (item.equalsIgnoreCase("COMPLETED")) {
                        setStyle("-fx-text-fill: #475569; -fx-font-weight: bold;");
                    } else if (item.equalsIgnoreCase("CANCELLED")) {
                        setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                    }
                }
            }
        });

        // Styling for Payment Status Column
        paymentStatusCol.setCellFactory(column -> new TableCell<AdminOrderDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("SUCCESS") || item.contains("PAID")) {
                        setStyle("-fx-text-fill: #15803d; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #b91c1c; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void loadDashboardStats() {
        try {
            Map<String, Object> stats = adminService.getDashboardStats();
            if (stats != null) {
                int totalOrders = (Integer) stats.getOrDefault("totalOrders", 0);
                int activeOrders = (Integer) stats.getOrDefault("activeOrders", 0);
                int preparingOrders = (Integer) stats.getOrDefault("preparingOrders", 0);
                int readyOrders = (Integer) stats.getOrDefault("readyOrders", 0);
                int completedOrders = (Integer) stats.getOrDefault("completedOrders", 0);
                BigDecimal totalSales = (BigDecimal) stats.getOrDefault("totalSales", BigDecimal.ZERO);

                totalOrdersLabel.setText(String.valueOf(totalOrders));
                activeOrdersLabel.setText(String.valueOf(activeOrders));
                preparingOrdersLabel.setText(String.valueOf(preparingOrders));
                readyOrdersLabel.setText(String.valueOf(readyOrders));
                completedOrdersLabel.setText(String.valueOf(completedOrders));
                totalSalesLabel.setText(String.format("₹%.2f", totalSales));
            }
        } catch (Exception e) {
            System.err.println("Error loading admin dashboard stats: " + e.getMessage());
        }
    }

    private void loadOrdersTable(String filter) {
        this.currentFilter = filter;
        try {
            List<AdminOrderDTO> orders = adminService.getAdminOrders(filter);
            ordersTableView.setItems(FXCollections.observableArrayList(orders));
        } catch (Exception e) {
            System.err.println("Error loading admin orders table: " + e.getMessage());
        }
    }

    // Filter Navigation Handlers
    @FXML
    public void handleFilterActive(ActionEvent event) {
        loadOrdersTable("ACTIVE");
    }

    @FXML
    public void handleFilterQueued(ActionEvent event) {
        loadOrdersTable("QUEUED");
    }

    @FXML
    public void handleFilterPreparing(ActionEvent event) {
        loadOrdersTable("PREPARING");
    }

    @FXML
    public void handleFilterReady(ActionEvent event) {
        loadOrdersTable("READY");
    }

    @FXML
    public void handleFilterCompleted(ActionEvent event) {
        loadOrdersTable("COMPLETED");
    }

    @FXML
    public void handleFilterAll(ActionEvent event) {
        loadOrdersTable("ALL");
    }

    @FXML
    public void handleRefreshTable(ActionEvent event) {
        loadDashboardStats();
        loadOrdersTable(currentFilter);
    }

    // Order Status Transition Handlers
    @FXML
    public void handleSetPreparing(ActionEvent event) {
        transitionSelectedOrder(OrderStatus.PREPARING);
    }

    @FXML
    public void handleSetReady(ActionEvent event) {
        transitionSelectedOrder(OrderStatus.READY);
    }

    @FXML
    public void handleSetCompleted(ActionEvent event) {
        transitionSelectedOrder(OrderStatus.COMPLETED);
    }

    private void transitionSelectedOrder(OrderStatus newStatus) {
        AdminOrderDTO selected = ordersTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showWarning("No Order Selected", "Please select an order from the table to update its status.");
            return;
        }

        try {
            boolean success = adminService.updateOrderStatus(selected.getOrderId(), newStatus);
            if (success) {
                AlertUtil.showInfo("Status Updated",
                        String.format("Order #%d (Token #%d) status transitioned to %s.",
                                selected.getOrderId(), selected.getTokenNumber(), newStatus.name()));
                loadDashboardStats();
                loadOrdersTable(currentFilter);
            } else {
                AlertUtil.showError("Update Failed", "Could not update order status.");
            }
        } catch (ValidationException ve) {
            AlertUtil.showError("Workflow Violation", ve.getMessage());
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to update order: " + e.getMessage());
        }
    }

    // Navigation Handlers
    @FXML
    public void handleManageMenu(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/manage_menu.fxml", null);
    }

    @FXML
    public void handleLiveOrders(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/live_orders.fxml", null);
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        SessionManager.clearSession();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/login.fxml", null);
    }
}
