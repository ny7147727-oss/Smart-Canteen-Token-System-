package com.smartcanteen.frontend;

import com.smartcanteen.common.exception.PaymentException;
import com.smartcanteen.model.CartItem;
import com.smartcanteen.model.Order;
import com.smartcanteen.model.Payment;
import com.smartcanteen.model.PaymentMethod;
import com.smartcanteen.model.User;
import com.smartcanteen.backend.service.CartService;
import com.smartcanteen.backend.service.PaymentService;
import com.smartcanteen.frontend.util.AlertUtil;
import com.smartcanteen.frontend.util.SceneNavigator;
import com.smartcanteen.common.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {

    @FXML
    private Label paymentStudentLabel;

    // View Containers
    @FXML
    private ScrollPane paymentFormView;

    @FXML
    private VBox paymentSuccessView;

    @FXML
    private VBox paymentFailureView;

    // Form Controls
    @FXML
    private RadioButton upiRadioBtn;

    @FXML
    private RadioButton cardRadioBtn;

    @FXML
    private RadioButton walletRadioBtn;

    @FXML
    private ToggleGroup paymentMethodGroup;

    // Dynamic Input Boxes
    @FXML
    private VBox upiFormBox;

    @FXML
    private TextField upiIdField;

    @FXML
    private VBox cardFormBox;

    @FXML
    private TextField cardHolderNameField;

    @FXML
    private TextField cardNumberField;

    @FXML
    private TextField cardExpiryField;

    @FXML
    private PasswordField cardCvvField;

    @FXML
    private VBox walletFormBox;

    @FXML
    private Label walletBalanceLabel;

    // Summary & Actions
    @FXML
    private ListView<String> orderItemsPreviewList;

    @FXML
    private Label paymentSubtotalLabel;

    @FXML
    private Label paymentTotalLabel;

    @FXML
    private Button payAndConfirmBtn;

    // Success State Elements
    @FXML
    private Label successTxnRefLabel;

    @FXML
    private Label successTokenLabel;

    @FXML
    private Button trackTokenBtn;

    // Failure State Elements
    @FXML
    private Label failureReasonLabel;

    @FXML
    private Button testFailureBtn;

    private final CartService cartService = CartService.getInstance();
    private final PaymentService paymentService = new PaymentService();

    // Simulated campus wallet balance for demo
    private BigDecimal simulatedWalletBalance = new BigDecimal("500.00");
    private Order lastConfirmedOrder;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Setup Student Details
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            String name = (user.getFullName() != null && !user.getFullName().isEmpty())
                    ? user.getFullName()
                    : user.getUsername();
            paymentStudentLabel.setText("👤 " + name);
        }

        // 2. Setup Payment Methods Toggle
        paymentMethodGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean isUpi = (newVal == upiRadioBtn);
            boolean isCard = (newVal == cardRadioBtn);
            boolean isWallet = (newVal == walletRadioBtn);

            showForm(upiFormBox, isUpi);
            showForm(cardFormBox, isCard);
            showForm(walletFormBox, isWallet);
        });

        // 3. Show Form View by default
        showView(ViewMode.FORM);

        // 4. Load Cart Summary
        loadPaymentSummary();
    }

    private enum ViewMode {
        FORM,
        SUCCESS,
        FAILURE
    }

    private void showView(ViewMode mode) {
        paymentFormView.setVisible(mode == ViewMode.FORM);
        paymentFormView.setManaged(mode == ViewMode.FORM);

        paymentSuccessView.setVisible(mode == ViewMode.SUCCESS);
        paymentSuccessView.setManaged(mode == ViewMode.SUCCESS);

        paymentFailureView.setVisible(mode == ViewMode.FAILURE);
        paymentFailureView.setManaged(mode == ViewMode.FAILURE);
    }

    private void showForm(VBox box, boolean visible) {
        box.setVisible(visible);
        box.setManaged(visible);
    }

    private void loadPaymentSummary() {
        List<CartItem> items = cartService.getItems();
        List<String> displayList = new ArrayList<>();

        for (CartItem item : items) {
            if (item.getItem() != null) {
                displayList.add(String.format("%s × %d \t\t ₹%.2f",
                        item.getItem().getItemName(), item.getQuantity(), item.getSubtotal()));
            }
        }

        orderItemsPreviewList.setItems(FXCollections.observableArrayList(displayList));

        BigDecimal total = cartService.getTotal();
        paymentSubtotalLabel.setText(String.format("₹%.2f", total));
        paymentTotalLabel.setText(String.format("₹%.2f", total));
        payAndConfirmBtn.setText(String.format("PAY ₹%.2f", total));
        walletBalanceLabel.setText(String.format("₹%.2f", simulatedWalletBalance));
    }

    // Quick presets for UPI demo testing
    @FXML
    public void handlePresetUpi1(ActionEvent event) {
        upiIdField.setText("rahul@oksbi");
    }

    @FXML
    public void handlePresetUpi2(ActionEvent event) {
        upiIdField.setText("student@upi");
    }

    @FXML
    public void handlePresetUpi3(ActionEvent event) {
        upiIdField.setText("canteen@paytm");
    }

    /**
     * Handles SUCCESSFUL payment execution and order token generation.
     */
    @FXML
    public void handlePaySuccess(ActionEvent event) {
        executePayment(event, false);
    }

    /**
     * Handles SIMULATED FAILURE payment execution to verify no token is generated and cart remains.
     */
    @FXML
    public void handleSimulateFailure(ActionEvent event) {
        executePayment(event, true);
    }

    private void executePayment(ActionEvent event, boolean simulateFailure) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            AlertUtil.showError("Authentication Required", "Please log in before completing payment.");
            return;
        }

        if (cartService.getItems().isEmpty()) {
            AlertUtil.showError("Cart Empty", "Your cart is empty. Please add items from the menu.");
            return;
        }

        try {
            // 1. Build and validate Payment Model via setters
            Payment payment = new Payment();
            payment.setUserId(currentUser.getUserId());
            payment.setAmount(cartService.getTotal());
            payment.setCreatedAt(LocalDateTime.now());

            if (upiRadioBtn.isSelected()) {
                String upiId = upiIdField.getText();
                paymentService.validateUPI(upiId);
                payment.setPaymentMethod(PaymentMethod.UPI);
                payment.setMaskedDetails("UPI: " + upiId.trim());

            } else if (cardRadioBtn.isSelected()) {
                String masked = paymentService.validateAndMaskCard(
                        cardNumberField.getText(),
                        cardExpiryField.getText(),
                        cardCvvField.getText(),
                        cardHolderNameField.getText()
                );
                payment.setPaymentMethod(PaymentMethod.CARD);
                payment.setMaskedDetails(masked);

            } else if (walletRadioBtn.isSelected()) {
                paymentService.validateWallet(cartService.getTotal(), simulatedWalletBalance);
                payment.setPaymentMethod(PaymentMethod.WALLET);
                payment.setMaskedDetails(String.format("Wallet: Account #%d", currentUser.getUserId()));
                if (!simulateFailure) {
                    simulatedWalletBalance = simulatedWalletBalance.subtract(cartService.getTotal());
                }
            }

            // 2. Process payment via PaymentService (invokes OrderService -> Token strictly on SUCCESS)
            Order placedOrder = paymentService.processPaymentAndPlaceOrder(payment, cartService, simulateFailure);
            this.lastConfirmedOrder = placedOrder;

            // 3. Payment SUCCESS -> Show Visual Success Card with Token
            successTxnRefLabel.setText("Transaction ID: " + payment.getTransactionReference());
            successTokenLabel.setText(String.format("TOKEN #%d", placedOrder.getTokenNumber()));
            showView(ViewMode.SUCCESS);

        } catch (PaymentException pe) {
            // Payment FAILED / DECLINED -> NO token generated, NO queue entry, CART REMAINS
            failureReasonLabel.setText(pe.getMessage());
            showView(ViewMode.FAILURE);
            // Cart remains intact
            loadPaymentSummary();
        } catch (Exception e) {
            failureReasonLabel.setText("System Error: " + e.getMessage());
            showView(ViewMode.FAILURE);
            loadPaymentSummary();
        }
    }

    @FXML
    public void handleTryAgain(ActionEvent event) {
        showView(ViewMode.FORM);
    }

    @FXML
    public void handleTrackToken(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        if (lastConfirmedOrder != null) {
            SceneNavigator.switchScene(stage, "/fxml/order_confirmation.fxml", lastConfirmedOrder);
        } else {
            SceneNavigator.switchScene(stage, "/fxml/student_dashboard.fxml", null);
        }
    }

    @FXML
    public void handleOrderAgain(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/menu.fxml", null);
    }

    /**
     * Handles payment cancellation and safely returns to Cart without losing items.
     */
    @FXML
    public void handleCancelPayment(ActionEvent event) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null && !cartService.getItems().isEmpty()) {
            Payment cancelledPayment = new Payment();
            cancelledPayment.setUserId(currentUser.getUserId());
            cancelledPayment.setAmount(cartService.getTotal());
            cancelledPayment.setPaymentMethod(
                    upiRadioBtn.isSelected() ? PaymentMethod.UPI :
                    cardRadioBtn.isSelected() ? PaymentMethod.CARD : PaymentMethod.WALLET
            );
            cancelledPayment.setMaskedDetails("Cancelled by student at checkout");
            paymentService.recordCancelledPayment(cancelledPayment);
        }

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/cart.fxml", null);
    }
}
