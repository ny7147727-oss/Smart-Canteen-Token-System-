package com.smartcanteen.frontend;

import com.smartcanteen.model.Order;
import com.smartcanteen.model.OrderStatus;
import com.smartcanteen.model.Token;
import com.smartcanteen.model.TokenStatus;
import com.smartcanteen.model.User;
import com.smartcanteen.backend.service.OrderService;
import com.smartcanteen.backend.service.TokenService;
import com.smartcanteen.threading.AppThreadPool;
import com.smartcanteen.frontend.util.SceneNavigator;
import com.smartcanteen.common.util.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class TokenStatusController implements Initializable {

    @FXML
    private Label tokenNumberLabel;

    @FXML
    private Label statusBadgeLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label queueInfoLabel;

    // Stepper Step Boxes & Icons
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

    private int currentTokenNumber = -1;
    private Timeline pollTimeline;
    private Consumer<Integer> backgroundCallback;

    private final OrderService orderService = new OrderService();
    private final TokenService tokenService = new TokenService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Setup Background Token Callback
        backgroundCallback = readyTokenNumber -> {
            Platform.runLater(() -> {
                if (currentTokenNumber > 0 && readyTokenNumber == currentTokenNumber) {
                    updateStepperDisplay(OrderStatus.READY);
                }
            });
        };
        AppThreadPool.getTokenDisplayUpdater().registerCallback(backgroundCallback);

        // 2. Setup Timeline Polling on JavaFX Application Thread (every 2s)
        pollTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> pollTokenStatus()));
        pollTimeline.setCycleCount(Timeline.INDEFINITE);
        pollTimeline.play();
    }

    public void initData(Object data) {
        if (data instanceof Order) {
            Order order = (Order) data;
            this.currentTokenNumber = order.getTokenNumber();
            tokenNumberLabel.setText("TOKEN #" + currentTokenNumber);
            updateStepperDisplay(order.getStatus());
        } else if (data instanceof Integer) {
            this.currentTokenNumber = (Integer) data;
            tokenNumberLabel.setText("TOKEN #" + currentTokenNumber);
            pollTokenStatus();
        } else {
            // Default: find latest active order for logged-in user
            findLatestUserToken();
        }
    }

    private void findLatestUserToken() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            try {
                List<Order> orders = orderService.getOrderHistory(user.getUserId());
                if (orders != null && !orders.isEmpty()) {
                    Order latestOrder = orders.get(0);
                    this.currentTokenNumber = latestOrder.getTokenNumber();
                    tokenNumberLabel.setText("TOKEN #" + currentTokenNumber);
                    updateStepperDisplay(latestOrder.getStatus());
                    return;
                }
            } catch (Exception ignored) {
            }
        }
        tokenNumberLabel.setText("NO ACTIVE TOKEN");
        statusBadgeLabel.setText("STATUS: N/A");
        statusBadgeLabel.setStyle("-fx-background-color: #64748b; -fx-text-fill: #ffffff; -fx-padding: 6px 18px; -fx-background-radius: 16px; -fx-font-weight: bold;");
        progressBar.setProgress(0.0);
    }

    private void pollTokenStatus() {
        if (currentTokenNumber <= 0) {
            findLatestUserToken();
            return;
        }

        try {
            // 1. Check active orders first
            List<Order> activeOrders = orderService.getActiveOrders();
            if (activeOrders != null) {
                for (Order order : activeOrders) {
                    if (order.getTokenNumber() == currentTokenNumber) {
                        updateStepperDisplay(order.getStatus());
                        return;
                    }
                }
            }

            // 2. Fallback to TokenService lookup
            Token token = tokenService.getToken(currentTokenNumber);
            if (token != null) {
                if (token.getStatus() == TokenStatus.WAITING) {
                    updateStepperDisplay(OrderStatus.PLACED);
                } else if (token.getStatus() == TokenStatus.CALLED) {
                    updateStepperDisplay(OrderStatus.READY);
                } else if (token.getStatus() == TokenStatus.SERVED) {
                    updateStepperDisplay(OrderStatus.COMPLETED);
                }
            }
        } catch (Exception e) {
            System.err.println("Notice: Token polling: " + e.getMessage());
        }
    }

    private void updateStepperDisplay(OrderStatus status) {
        if (status == null) {
            status = OrderStatus.PLACED;
        }

        // Reset step boxes
        resetStepBox(stepConfirmedBox, step1Icon, "○");
        resetStepBox(stepQueuedBox, step2Icon, "○");
        resetStepBox(stepPreparingBox, step3Icon, "○");
        resetStepBox(stepReadyBox, step4Icon, "○");
        resetStepBox(stepCompletedBox, step5Icon, "○");

        switch (status) {
            case PLACED:
                setStepDone(stepConfirmedBox, step1Icon);
                setStepActive(stepQueuedBox, step2Icon);
                statusBadgeLabel.setText("● QUEUED IN KITCHEN");
                statusBadgeLabel.setStyle("-fx-background-color: #0284c7; -fx-text-fill: #ffffff; -fx-padding: 6px 18px; -fx-background-radius: 16px; -fx-font-weight: bold;");
                progressBar.setProgress(0.4);
                queueInfoLabel.setText("Order is in queue. Preparing will begin shortly.");
                break;

            case PREPARING:
                setStepDone(stepConfirmedBox, step1Icon);
                setStepDone(stepQueuedBox, step2Icon);
                setStepActive(stepPreparingBox, step3Icon);
                statusBadgeLabel.setText("● PREPARING (In Kitchen)");
                statusBadgeLabel.setStyle("-fx-background-color: #2563eb; -fx-text-fill: #ffffff; -fx-padding: 6px 18px; -fx-background-radius: 16px; -fx-font-weight: bold;");
                progressBar.setProgress(0.65);
                queueInfoLabel.setText("Chefs are cooking your meal right now.");
                break;

            case READY:
                setStepDone(stepConfirmedBox, step1Icon);
                setStepDone(stepQueuedBox, step2Icon);
                setStepDone(stepPreparingBox, step3Icon);
                setStepActive(stepReadyBox, step4Icon);
                statusBadgeLabel.setText("✓ READY FOR PICKUP AT COUNTER");
                statusBadgeLabel.setStyle("-fx-background-color: #16a34a; -fx-text-fill: #ffffff; -fx-padding: 6px 18px; -fx-background-radius: 16px; -fx-font-weight: bold;");
                progressBar.setProgress(0.9);
                queueInfoLabel.setText("🔔 Token called! Please collect your food at counter.");
                break;

            case COMPLETED:
                setStepDone(stepConfirmedBox, step1Icon);
                setStepDone(stepQueuedBox, step2Icon);
                setStepDone(stepPreparingBox, step3Icon);
                setStepDone(stepReadyBox, step4Icon);
                setStepDone(stepCompletedBox, step5Icon);
                statusBadgeLabel.setText("✓ COMPLETED & SERVED");
                statusBadgeLabel.setStyle("-fx-background-color: #475569; -fx-text-fill: #ffffff; -fx-padding: 6px 18px; -fx-background-radius: 16px; -fx-font-weight: bold;");
                progressBar.setProgress(1.0);
                queueInfoLabel.setText("Food has been collected. Thank you!");
                break;

            case CANCELLED:
                statusBadgeLabel.setText("✗ CANCELLED");
                statusBadgeLabel.setStyle("-fx-background-color: #dc2626; -fx-text-fill: #ffffff; -fx-padding: 6px 18px; -fx-background-radius: 16px; -fx-font-weight: bold;");
                progressBar.setProgress(0.0);
                queueInfoLabel.setText("Order was cancelled.");
                break;
        }
    }

    private void setStepDone(HBox box, Label icon) {
        box.setStyle("-fx-background-color: #f0fdf4; -fx-border-color: #86efac; -fx-padding: 12px 16px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        icon.setText("✓");
        icon.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #15803d;");
    }

    private void setStepActive(HBox box, Label icon) {
        box.setStyle("-fx-background-color: #eff6ff; -fx-border-color: #93c5fd; -fx-padding: 12px 16px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        icon.setText("●");
        icon.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1d4ed8;");
    }

    private void resetStepBox(HBox box, Label icon, String symbol) {
        box.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-padding: 12px 16px; -fx-background-radius: 8px; -fx-border-radius: 8px;");
        icon.setText(symbol);
        icon.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        pollTokenStatus();
    }

    @FXML
    public void handleBackToMenu(ActionEvent event) {
        cleanup();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/menu.fxml", null);
    }

    @FXML
    public void handleBackToDashboard(ActionEvent event) {
        cleanup();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/student_dashboard.fxml", null);
    }

    private void cleanup() {
        if (pollTimeline != null) {
            pollTimeline.stop();
        }
        if (backgroundCallback != null) {
            AppThreadPool.getTokenDisplayUpdater().unregisterCallback(backgroundCallback);
        }
    }
}
