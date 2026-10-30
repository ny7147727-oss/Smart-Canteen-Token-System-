package com.smartcanteen.frontend;

import com.smartcanteen.model.MenuItem;
import com.smartcanteen.model.Order;
import com.smartcanteen.model.OrderItem;
import com.smartcanteen.model.OrderStatus;
import com.smartcanteen.model.Payment;
import com.smartcanteen.model.User;
import com.smartcanteen.backend.service.MenuService;
import com.smartcanteen.backend.service.OrderService;
import com.smartcanteen.backend.service.PaymentService;
import com.smartcanteen.frontend.util.SceneNavigator;
import com.smartcanteen.common.util.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class OrderConfirmationController {

    @FXML
    private Label studentNameLabel;

    @FXML
    private Label tokenNumberLabel;

    @FXML
    private Label tokenStatusBadgeLabel;

    @FXML
    private Label estimatedTimeLabel;

    @FXML
    private ProgressBar statusProgressBar;

    // Stepper Step Boxes
    @FXML
    private HBox stepConfirmedBox;
    @FXML
    private Label step1Icon;

    @FXML
    private HBox stepQueuedBox;
    @FXML
    private Label step2Icon;

    @FXML
    private HBox stepPreparingBox;
    @FXML
    private Label step3Icon;

    @FXML
    private HBox stepReadyBox;
    @FXML
    private Label step4Icon;

    @FXML
    private HBox stepCompletedBox;
    @FXML
    private Label step5Icon;

    // Receipt Fields
    @FXML
    private Label txnRefLabel;

    @FXML
    private Label orderIdLabel;

    @FXML
    private Label paymentMethodLabel;

    @FXML
    private Label amountPaidLabel;

    @FXML
    private Label currentStatusLabel;

    @FXML
    private ListView<String> orderSummaryListView;

    private Order currentOrder;
    private Timeline livePollingTimeline;

    private final OrderService orderService = new OrderService();
    private final PaymentService paymentService = new PaymentService();
    private final MenuService menuService = new MenuService();

    public void initData(Order order) {
        this.currentOrder = order;

        // 1. Setup Student Details
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            String name = (user.getFullName() != null && !user.getFullName().isEmpty())
                    ? user.getFullName()
                    : user.getUsername();
            studentNameLabel.setText("👤 " + name);
        }

        if (order != null) {
            // 2. Token Spotlight
            tokenNumberLabel.setText(String.format("TOKEN #%d", order.getTokenNumber()));
            orderIdLabel.setText(String.format("ORD-#%d", order.getOrderId()));
            amountPaidLabel.setText(String.format("₹%.2f", order.getTotalAmount()));

            int itemCount = (order.getItems() != null) ? order.getItems().size() : 1;
            int estMinutes = Math.min(6 + (itemCount * 2), 20);
            estimatedTimeLabel.setText(String.format("⏱ Est. Preparation: ~%d-%d mins", estMinutes - 2, estMinutes + 3));

            // 3. Load Payment Details for Transaction Reference and Method
            loadPaymentAudit(order);

            // 4. Ordered Items Breakdown
            loadOrderItemsSummary(order);

            // 5. Update Stepper Status
            updateStepperDisplay(order.getStatus());

            // 6. Setup Live Auto-Refresh (every 3 seconds)
            startLivePolling();
        }
    }

    public void initData(Object data) {
        if (data instanceof Order) {
            initData((Order) data);
        }
    }

    private void loadPaymentAudit(Order order) {
        try {
            List<Payment> payments = paymentService.getPaymentsByUser(order.getUserId());
            Payment matched = null;
            if (payments != null) {
                for (Payment p : payments) {
                    if (p.getOrderId() != null && p.getOrderId() == order.getOrderId()) {
                        matched = p;
                        break;
                    }
                }
                if (matched == null && !payments.isEmpty()) {
                    matched = payments.get(0);
                }
            }

            if (matched != null) {
                txnRefLabel.setText(matched.getTransactionReference());
                String methodDisplay = (matched.getMaskedDetails() != null && !matched.getMaskedDetails().isEmpty())
                        ? matched.getMaskedDetails()
                        : matched.getPaymentMethod().name();
                paymentMethodLabel.setText(methodDisplay);
            } else {
                txnRefLabel.setText("TXN-" + System.currentTimeMillis());
                paymentMethodLabel.setText("📱 UPI / Online");
            }
        } catch (Exception e) {
            txnRefLabel.setText("TXN-VERIFIED");
            paymentMethodLabel.setText("📱 UPI / Online");
        }
    }

    private void loadOrderItemsSummary(Order order) {
        List<String> summaryLines = new ArrayList<>();
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (OrderItem item : order.getItems()) {
                String itemName = "Item #" + item.getItemId();
                try {
                    MenuItem mi = menuService.getById(item.getItemId());
                    if (mi != null && mi.getItemName() != null) {
                        itemName = mi.getItemName();
                    }
                } catch (Exception ignored) {
                }
                summaryLines.add(String.format("%s  ×  %d  —  ₹%.2f",
                        itemName, item.getQuantity(), item.getSubtotal()));
            }
        } else {
            summaryLines.add("Fresh canteen items in preparation queue.");
        }
        orderSummaryListView.setItems(FXCollections.observableArrayList(summaryLines));
    }

    private void startLivePolling() {
        if (livePollingTimeline != null) {
            livePollingTimeline.stop();
        }
        livePollingTimeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> pollStatusUpdate()));
        livePollingTimeline.setCycleCount(Timeline.INDEFINITE);
        livePollingTimeline.play();
    }

    private void pollStatusUpdate() {
        if (currentOrder == null || currentOrder.getOrderId() <= 0) {
            return;
        }
        try {
            List<Order> activeOrders = orderService.getActiveOrders();
            if (activeOrders != null) {
                for (Order o : activeOrders) {
                    if (o.getOrderId() == currentOrder.getOrderId()) {
                        currentOrder.setStatus(o.getStatus());
                        updateStepperDisplay(o.getStatus());
                        return;
                    }
                }
            }
            // If not found in active orders, check order history
            List<Order> history = orderService.getOrderHistory(currentOrder.getUserId());
            if (history != null) {
                for (Order o : history) {
                    if (o.getOrderId() == currentOrder.getOrderId()) {
                        currentOrder.setStatus(o.getStatus());
                        updateStepperDisplay(o.getStatus());
                        return;
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void updateStepperDisplay(OrderStatus status) {
        if (status == null) {
            status = OrderStatus.PLACED;
        }

        currentStatusLabel.setText(status.name());

        // Reset step box styles
        resetStepBox(stepConfirmedBox, step1Icon, "○");
        resetStepBox(stepQueuedBox, step2Icon, "○");
        resetStepBox(stepPreparingBox, step3Icon, "○");
        resetStepBox(stepReadyBox, step4Icon, "○");
        resetStepBox(stepCompletedBox, step5Icon, "○");

        switch (status) {
            case PLACED:
                // Step 1 done, Step 2 active
                setStepDone(stepConfirmedBox, step1Icon);
                setStepActive(stepQueuedBox, step2Icon, "Queued");
                tokenStatusBadgeLabel.setText("● QUEUED IN KITCHEN");
                tokenStatusBadgeLabel.setStyle("-fx-background-color: #0284c7; -fx-text-fill: #ffffff; -fx-padding: 5px 14px; -fx-background-radius: 14px; -fx-font-weight: bold;");
                statusProgressBar.setProgress(0.4);
                break;

            case PREPARING:
                // Step 1, 2 done, Step 3 active
                setStepDone(stepConfirmedBox, step1Icon);
                setStepDone(stepQueuedBox, step2Icon);
                setStepActive(stepPreparingBox, step3Icon, "Preparing");
                tokenStatusBadgeLabel.setText("● PREPARING (In Kitchen)");
                tokenStatusBadgeLabel.setStyle("-fx-background-color: #2563eb; -fx-text-fill: #ffffff; -fx-padding: 5px 14px; -fx-background-radius: 14px; -fx-font-weight: bold;");
                statusProgressBar.setProgress(0.65);
                break;

            case READY:
                // Step 1, 2, 3 done, Step 4 active
                setStepDone(stepConfirmedBox, step1Icon);
                setStepDone(stepQueuedBox, step2Icon);
                setStepDone(stepPreparingBox, step3Icon);
                setStepActive(stepReadyBox, step4Icon, "Ready for Pickup");
                tokenStatusBadgeLabel.setText("✓ READY FOR PICKUP AT COUNTER");
                tokenStatusBadgeLabel.setStyle("-fx-background-color: #16a34a; -fx-text-fill: #ffffff; -fx-padding: 5px 14px; -fx-background-radius: 14px; -fx-font-weight: bold;");
                statusProgressBar.setProgress(0.9);
                break;

            case COMPLETED:
                // All done
                setStepDone(stepConfirmedBox, step1Icon);
                setStepDone(stepQueuedBox, step2Icon);
                setStepDone(stepPreparingBox, step3Icon);
                setStepDone(stepReadyBox, step4Icon);
                setStepDone(stepCompletedBox, step5Icon);
                tokenStatusBadgeLabel.setText("✓ ORDER COMPLETED (Served)");
                tokenStatusBadgeLabel.setStyle("-fx-background-color: #475569; -fx-text-fill: #ffffff; -fx-padding: 5px 14px; -fx-background-radius: 14px; -fx-font-weight: bold;");
                statusProgressBar.setProgress(1.0);
                break;

            case CANCELLED:
                tokenStatusBadgeLabel.setText("✗ ORDER CANCELLED");
                tokenStatusBadgeLabel.setStyle("-fx-background-color: #dc2626; -fx-text-fill: #ffffff; -fx-padding: 5px 14px; -fx-background-radius: 14px; -fx-font-weight: bold;");
                statusProgressBar.setProgress(0.0);
                break;
        }
    }

    private void setStepDone(HBox box, Label icon) {
        box.setStyle("-fx-background-color: #f0fdf4; -fx-border-color: #86efac; -fx-padding: 10px; -fx-background-radius: 6px; -fx-border-radius: 6px;");
        icon.setText("✓");
        icon.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #15803d;");
    }

    private void setStepActive(HBox box, Label icon, String stepName) {
        box.setStyle("-fx-background-color: #eff6ff; -fx-border-color: #93c5fd; -fx-padding: 10px; -fx-background-radius: 6px; -fx-border-radius: 6px;");
        icon.setText("●");
        icon.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1d4ed8;");
    }

    private void resetStepBox(HBox box, Label icon, String symbol) {
        box.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-padding: 10px; -fx-background-radius: 6px; -fx-border-radius: 6px;");
        icon.setText(symbol);
        icon.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");
    }

    @FXML
    public void handleRefreshStatus(ActionEvent event) {
        pollStatusUpdate();
    }

    @FXML
    public void handleTrackToken(ActionEvent event) {
        cleanup();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/token_status.fxml", currentOrder);
    }

    @FXML
    public void handleBackToMenu(ActionEvent event) {
        cleanup();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/menu.fxml", null);
    }

    @FXML
    public void handleGoToDashboard(ActionEvent event) {
        cleanup();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/student_dashboard.fxml", null);
    }

    private void cleanup() {
        if (livePollingTimeline != null) {
            livePollingTimeline.stop();
        }
    }
}
