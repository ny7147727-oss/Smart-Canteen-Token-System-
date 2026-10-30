package com.smartcanteen.frontend;

import com.smartcanteen.model.CartItem;
import com.smartcanteen.model.MenuItem;
import com.smartcanteen.model.User;
import com.smartcanteen.backend.service.CartService;
import com.smartcanteen.frontend.util.AlertUtil;
import com.smartcanteen.frontend.util.SceneNavigator;
import com.smartcanteen.common.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CartController implements Initializable {

    @FXML
    private Label cartItemCountHeader;

    @FXML
    private VBox emptyCartView;

    @FXML
    private HBox filledCartView;

    @FXML
    private Label cartItemsHeadingLabel;

    @FXML
    private VBox cartItemsContainer;

    @FXML
    private Label subtotalLabel;

    @FXML
    private Label totalLabel;

    @FXML
    private Button proceedToPaymentBtn;

    private final CartService cartService = CartService.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshCartView();
    }

    private void refreshCartView() {
        List<CartItem> items = cartService.getItems();

        if (items.isEmpty()) {
            emptyCartView.setVisible(true);
            emptyCartView.setManaged(true);
            filledCartView.setVisible(false);
            filledCartView.setManaged(false);
            cartItemCountHeader.setText("0 Items");
            return;
        }

        emptyCartView.setVisible(false);
        emptyCartView.setManaged(false);
        filledCartView.setVisible(true);
        filledCartView.setManaged(true);

        int totalCount = 0;
        for (CartItem item : items) {
            totalCount += item.getQuantity();
        }

        cartItemCountHeader.setText(totalCount + " Item" + (totalCount == 1 ? "" : "s"));
        cartItemsHeadingLabel.setText("Items in Your Cart (" + items.size() + ")");

        // Render Cart Item Cards
        cartItemsContainer.getChildren().clear();
        for (CartItem cartItem : items) {
            cartItemsContainer.getChildren().add(createCartItemRow(cartItem));
        }

        // Update Order Summary
        BigDecimal total = cartService.getTotal();
        subtotalLabel.setText(String.format("₹%.2f", total));
        totalLabel.setText(String.format("₹%.2f", total));
    }

    private Node createCartItemRow(CartItem cartItem) {
        MenuItem item = cartItem.getItem();
        if (item == null) {
            return new VBox();
        }

        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("card");
        row.setStyle("-fx-padding: 16px 20px;");

        // 1. Food Details (Name & Unit Price)
        VBox infoBox = new VBox(4);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPrefWidth(260);

        Label nameLabel = new Label(item.getItemName());
        nameLabel.getStyleClass().add("food-card-title");
        nameLabel.setStyle("-fx-font-size: 15px;");

        Label categoryAndPrice = new Label(String.format("%s  •  ₹%.2f each",
                item.getCategory() != null ? item.getCategory() : "General", item.getPrice()));
        categoryAndPrice.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        infoBox.getChildren().addAll(nameLabel, categoryAndPrice);

        // 2. Quantity Controls: [-] Qty [+]
        HBox qtyBox = new HBox(6);
        qtyBox.setAlignment(Pos.CENTER);

        Button minusBtn = new Button("-");
        minusBtn.getStyleClass().add("qty-btn");
        minusBtn.setStyle("-fx-min-width: 32px; -fx-min-height: 32px; -fx-font-size: 14px;");

        Label qtyLabel = new Label(String.valueOf(cartItem.getQuantity()));
        qtyLabel.getStyleClass().add("qty-label");
        qtyLabel.setStyle("-fx-font-size: 14px; -fx-min-width: 28px;");

        Button plusBtn = new Button("+");
        plusBtn.getStyleClass().add("qty-btn");
        plusBtn.setStyle("-fx-min-width: 32px; -fx-min-height: 32px; -fx-font-size: 14px;");

        minusBtn.setOnAction(e -> {
            int currentQty = cartItem.getQuantity();
            if (currentQty > 1) {
                cartService.updateQuantity(item.getItemId(), currentQty - 1);
            } else {
                cartService.removeFromCart(item.getItemId());
            }
            refreshCartView();
        });

        plusBtn.setOnAction(e -> {
            int currentQty = cartItem.getQuantity();
            if (currentQty < 50) {
                cartService.updateQuantity(item.getItemId(), currentQty + 1);
            }
            refreshCartView();
        });

        qtyBox.getChildren().addAll(minusBtn, qtyLabel, plusBtn);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 3. Subtotal Amount
        Label subtotalText = new Label(String.format("₹%.2f", cartItem.getSubtotal()));
        subtotalText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #047857; -fx-min-width: 80px; -fx-alignment: center-right;");

        // 4. Remove Item Button [🗑]
        Button deleteBtn = new Button("🗑");
        deleteBtn.getStyleClass().add("button-secondary");
        deleteBtn.setStyle("-fx-text-fill: #dc2626; -fx-border-color: #fca5a5; -fx-padding: 6px 10px; -fx-font-size: 13px;");
        deleteBtn.setOnAction(e -> {
            cartService.removeFromCart(item.getItemId());
            refreshCartView();
        });

        row.getChildren().addAll(infoBox, qtyBox, spacer, subtotalText, deleteBtn);
        return row;
    }

    @FXML
    public void handleProceedToPayment(ActionEvent event) {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            AlertUtil.showError("Authentication Required", "Please log in before proceeding to checkout.");
            return;
        }

        if (cartService.getItems().isEmpty()) {
            AlertUtil.showError("Cart Empty", "Your cart is empty. Please add items from the menu before proceeding.");
            return;
        }

        // Navigate to payment screen WITHOUT clearing the cart
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/payment.fxml", null);
    }

    @FXML
    public void handleClearCart(ActionEvent event) {
        if (cartService.getItems().isEmpty()) {
            return;
        }

        boolean confirmed = AlertUtil.showConfirmation("Clear Cart", "Are you sure you want to remove all items from your cart?");
        if (confirmed) {
            cartService.clear();
            refreshCartView();
        }
    }

    @FXML
    public void handleBackToMenu(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/menu.fxml", null);
    }
}
