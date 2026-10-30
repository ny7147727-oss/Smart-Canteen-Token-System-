package com.smartcanteen.frontend;

import com.smartcanteen.model.Order;
import com.smartcanteen.model.OrderStatus;
import com.smartcanteen.backend.service.OrderService;
import com.smartcanteen.frontend.util.AlertUtil;
import com.smartcanteen.frontend.util.SceneNavigator;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class LiveOrdersController implements Initializable {

    @FXML
    private TableView<Order> ordersTable;

    @FXML
    private TableColumn<Order, Integer> tokenColumn;

    @FXML
    private TableColumn<Order, Integer> userIdColumn;

    @FXML
    private TableColumn<Order, BigDecimal> totalColumn;

    @FXML
    private TableColumn<Order, String> statusColumn;

    @FXML
    private TableColumn<Order, String> timeColumn;

    private final OrderService orderService = new OrderService();
    private final ObservableList<Order> activeOrdersObservable = FXCollections.observableArrayList();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Setup Table Columns
        tokenColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getTokenNumber()).asObject());
        userIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUserId()).asObject());
        totalColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTotalAmount()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus() != null ? cellData.getValue().getStatus().name() : "PLACED"));
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getOrderTime() != null ? cellData.getValue().getOrderTime().format(timeFormatter) : ""));

        ordersTable.setItems(activeOrdersObservable);

        loadActiveOrders();
    }

    private void loadActiveOrders() {
        try {
            List<Order> orders = orderService.getActiveOrders();
            if (orders != null) {
                activeOrdersObservable.setAll(orders);
            }
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to load active orders: " + e.getMessage());
        }
    }

    private void updateSelectedOrderStatus(OrderStatus desiredStatus) {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showError("No Selection", "Please select an order from the table to update.");
            return;
        }

        try {
            Order orderToUpdate = new Order();
            orderToUpdate.setOrderId(selected.getOrderId());
            orderToUpdate.setStatus(desiredStatus);

            boolean success = orderService.updateOrderStatus(orderToUpdate);
            if (success) {
                AlertUtil.showInfo("Status Updated", "Order (Token #" + selected.getTokenNumber() + ") is now " + desiredStatus + ".");
                loadActiveOrders();
            } else {
                AlertUtil.showError("Error", "Could not update order status.");
            }
        } catch (Exception e) {
            AlertUtil.showError("Error", "Error updating order status: " + e.getMessage());
        }
    }

    @FXML
    public void handleMarkPreparing(ActionEvent event) {
        updateSelectedOrderStatus(OrderStatus.PREPARING);
    }

    @FXML
    public void handleMarkReady(ActionEvent event) {
        updateSelectedOrderStatus(OrderStatus.READY);
    }

    @FXML
    public void handleMarkCompleted(ActionEvent event) {
        updateSelectedOrderStatus(OrderStatus.COMPLETED);
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        loadActiveOrders();
    }

    @FXML
    public void handleBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/admin_dashboard.fxml", null);
    }
}
