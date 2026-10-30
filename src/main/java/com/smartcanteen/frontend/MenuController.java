package com.smartcanteen.frontend;

import com.smartcanteen.model.CartItem;
import com.smartcanteen.model.MenuItem;
import com.smartcanteen.model.User;
import com.smartcanteen.backend.service.CartService;
import com.smartcanteen.backend.service.MenuService;
import com.smartcanteen.frontend.util.AlertUtil;
import com.smartcanteen.frontend.util.FoodImageResolver;
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
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private Label studentProfileLabel;

    @FXML
    private Button cartHeaderBtn;

    @FXML
    private TextField searchField;

    @FXML
    private Label resultsCountLabel;

    @FXML
    private HBox categoryPillsContainer;

    @FXML
    private FlowPane foodCardsContainer;

    private final MenuService menuService = new MenuService();
    private final CartService cartService = CartService.getInstance();
    private final List<MenuItem> masterItemList = new ArrayList<>();

    private String currentSelectedCategory = "All";
    private final Map<String, Button> categoryButtonMap = new LinkedHashMap<>();

    // Category mapping: Display Label -> Target DB Category Filter
    private static final Map<String, String> CATEGORY_NAV_MAP = new LinkedHashMap<>();
    static {
        CATEGORY_NAV_MAP.put("All", "All");
        CATEGORY_NAV_MAP.put("North Indian", "North Indian Meals");
        CATEGORY_NAV_MAP.put("Chaat", "Chaat");
        CATEGORY_NAV_MAP.put("Healthy", "Healthy Food");
        CATEGORY_NAV_MAP.put("Breakfast", "Breakfast");
        CATEGORY_NAV_MAP.put("Snacks", "Snacks");
        CATEGORY_NAV_MAP.put("Beverages", "Beverages");
        CATEGORY_NAV_MAP.put("Healthy Drinks", "Healthy Beverages");
        CATEGORY_NAV_MAP.put("Combos", "Combos");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Setup Student Profile Info
        updateUserProfileDisplay();

        // 2. Setup Cart Header Counter
        updateCartHeaderButton();

        // 3. Build Category Pills Navigation
        buildCategoryPills();

        // 4. Setup Live Search Listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> renderFilteredCards());

        // 5. Load Menu Items from Database
        loadMenuItems();
    }

    private void updateUserProfileDisplay() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            String name = (user.getFullName() != null && !user.getFullName().isEmpty())
                    ? user.getFullName()
                    : user.getUsername();
            String roll = (user.getRollNumber() != null && !user.getRollNumber().isEmpty())
                    ? " (" + user.getRollNumber() + ")"
                    : "";
            studentProfileLabel.setText("👤 " + name + roll);
        } else {
            studentProfileLabel.setText("👤 Student Portal");
        }
    }

    private void updateCartHeaderButton() {
        int totalQuantity = 0;
        for (CartItem item : cartService.getItems()) {
            totalQuantity += item.getQuantity();
        }
        BigDecimal totalAmount = cartService.getTotal();
        if (totalQuantity > 0) {
            cartHeaderBtn.setText(String.format("🛒 Cart (%d) • ₹%.2f", totalQuantity, totalAmount));
        } else {
            cartHeaderBtn.setText("🛒 Cart (0)");
        }
    }

    private void buildCategoryPills() {
        categoryPillsContainer.getChildren().clear();
        categoryButtonMap.clear();

        for (Map.Entry<String, String> entry : CATEGORY_NAV_MAP.entrySet()) {
            String displayLabel = entry.getKey();
            String filterValue = entry.getValue();

            Button pillBtn = new Button(displayLabel);
            if (displayLabel.equals("All")) {
                pillBtn.getStyleClass().add("category-pill-active");
            } else {
                pillBtn.getStyleClass().add("category-pill");
            }

            pillBtn.setOnAction(e -> {
                selectCategory(displayLabel, filterValue);
            });

            categoryButtonMap.put(displayLabel, pillBtn);
            categoryPillsContainer.getChildren().add(pillBtn);
        }
    }

    private void selectCategory(String displayLabel, String filterValue) {
        this.currentSelectedCategory = filterValue;

        for (Map.Entry<String, Button> entry : categoryButtonMap.entrySet()) {
            Button btn = entry.getValue();
            btn.getStyleClass().remove("category-pill-active");
            btn.getStyleClass().remove("category-pill");
            if (entry.getKey().equals(displayLabel)) {
                btn.getStyleClass().add("category-pill-active");
            } else {
                btn.getStyleClass().add("category-pill");
            }
        }

        renderFilteredCards();
    }

    private void loadMenuItems() {
        masterItemList.clear();
        try {
            List<MenuItem> items = menuService.getAllItems();
            if (items != null) {
                masterItemList.addAll(items);
            }
        } catch (Exception e) {
            AlertUtil.showError("Error Loading Menu", "Failed to load menu items: " + e.getMessage());
        }
        renderFilteredCards();
    }

    private void renderFilteredCards() {
        String query = (searchField.getText() != null) ? searchField.getText().trim().toLowerCase() : "";
        foodCardsContainer.getChildren().clear();

        List<MenuItem> filtered = new ArrayList<>();
        for (MenuItem item : masterItemList) {
            boolean matchesQuery = query.isEmpty() ||
                    (item.getItemName() != null && item.getItemName().toLowerCase().contains(query)) ||
                    (item.getCategory() != null && item.getCategory().toLowerCase().contains(query)) ||
                    (item.getDescription() != null && item.getDescription().toLowerCase().contains(query));

            boolean matchesCategory = currentSelectedCategory.equalsIgnoreCase("All") ||
                    (item.getCategory() != null && item.getCategory().equalsIgnoreCase(currentSelectedCategory));

            if (matchesQuery && matchesCategory) {
                filtered.add(item);
            }
        }

        resultsCountLabel.setText("Showing " + filtered.size() + " food item" + (filtered.size() == 1 ? "" : "s"));

        if (filtered.isEmpty()) {
            VBox emptyBox = new VBox(12);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40, 20, 40, 20));
            emptyBox.setPrefWidth(850);

            Label emptyLabel = new Label("🍽 No food items found matching your search or category.");
            emptyLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #64748b; -fx-font-weight: bold;");

            Button clearBtn = new Button("Clear Search & Filters");
            clearBtn.getStyleClass().add("button-secondary");
            clearBtn.setOnAction(e -> {
                searchField.clear();
                selectCategory("All", "All");
            });

            emptyBox.getChildren().addAll(emptyLabel, clearBtn);
            foodCardsContainer.getChildren().add(emptyBox);
            return;
        }

        for (MenuItem item : filtered) {
            Node card = createFoodCard(item);
            foodCardsContainer.getChildren().add(card);
        }
    }

    private Node createFoodCard(MenuItem item) {
        VBox card = new VBox(0);
        card.setPrefWidth(280);
        card.setMaxWidth(280);
        card.setMinWidth(280);

        if (item.isAvailable()) {
            card.getStyleClass().add("food-card");
        } else {
            card.getStyleClass().add("food-card-unavailable");
        }

        // --- 1. Top Image StackPane (Image + Overlay Badges) ---
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(280, 150);
        imageContainer.setMinSize(280, 150);
        imageContainer.setMaxSize(280, 150);
        imageContainer.getStyleClass().add("food-card-img-container");

        ImageView imageView = new ImageView(FoodImageResolver.getImageFor(item));
        imageView.setFitWidth(280);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);

        // Rounded top corners clip
        Rectangle clip = new Rectangle(280, 150);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        imageView.setClip(clip);

        // Floating Badges Overlay (Category & Availability)
        HBox badgeOverlay = new HBox(8);
        badgeOverlay.setAlignment(Pos.TOP_LEFT);
        badgeOverlay.setPadding(new Insets(10, 10, 0, 10));

        Label categoryBadge = new Label(item.getCategory() != null ? item.getCategory() : "General");
        categoryBadge.getStyleClass().addAll("badge-category", getCategoryStyleClass(item.getCategory()));

        Region overlaySpacer = new Region();
        HBox.setHgrow(overlaySpacer, Priority.ALWAYS);

        Label statusBadge = new Label(item.isAvailable() ? "✓ Available" : "✗ Sold Out");
        statusBadge.getStyleClass().add(item.isAvailable() ? "badge-available" : "badge-unavailable");

        badgeOverlay.getChildren().addAll(categoryBadge, overlaySpacer, statusBadge);

        imageContainer.getChildren().addAll(imageView, badgeOverlay);

        // --- 2. Card Content Body ---
        VBox contentBox = new VBox(8);
        contentBox.setPadding(new Insets(14, 14, 14, 14));

        // Food Name Title
        Label titleLabel = new Label(item.getItemName());
        titleLabel.getStyleClass().add("food-card-title");
        titleLabel.setWrapText(true);
        titleLabel.setMinHeight(38);
        titleLabel.setMaxHeight(38);

        // Food Description
        Label descLabel = new Label(item.getDescription() != null ? item.getDescription() : "Freshly prepared meal in canteen");
        descLabel.getStyleClass().add("food-card-desc");
        descLabel.setWrapText(true);
        descLabel.setMinHeight(34);
        descLabel.setMaxHeight(34);

        // --- 3. Price & Quantity / Add To Cart Row ---
        HBox bottomRow = new HBox(6);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        bottomRow.setPadding(new Insets(4, 0, 0, 0));

        Label priceLabel = new Label(String.format("₹%.2f", item.getPrice()));
        priceLabel.getStyleClass().add("food-card-price");

        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        // Quantity controls
        int[] quantityHolder = new int[]{1};
        HBox qtyBox = new HBox(3);
        qtyBox.setAlignment(Pos.CENTER);

        Button minusBtn = new Button("-");
        minusBtn.getStyleClass().add("qty-btn");

        Label qtyLabel = new Label("1");
        qtyLabel.getStyleClass().add("qty-label");

        Button plusBtn = new Button("+");
        plusBtn.getStyleClass().add("qty-btn");

        minusBtn.setOnAction(e -> {
            if (quantityHolder[0] > 1) {
                quantityHolder[0]--;
                qtyLabel.setText(String.valueOf(quantityHolder[0]));
            }
        });

        plusBtn.setOnAction(e -> {
            if (quantityHolder[0] < 20) {
                quantityHolder[0]++;
                qtyLabel.setText(String.valueOf(quantityHolder[0]));
            }
        });

        qtyBox.getChildren().addAll(minusBtn, qtyLabel, plusBtn);

        // Add to Cart Button
        Button addBtn = new Button("+ Add");
        addBtn.getStyleClass().add("card-add-btn");

        if (!item.isAvailable()) {
            minusBtn.setDisable(true);
            plusBtn.setDisable(true);
            addBtn.setDisable(true);
            addBtn.setText("Sold Out");
        } else {
            addBtn.setOnAction(e -> {
                int qty = quantityHolder[0];
                CartItem cartItem = new CartItem();
                cartItem.setItem(item);
                cartItem.setQuantity(qty);

                cartService.addToCart(cartItem);
                updateCartHeaderButton();

                // Reset quantity counter back to 1
                quantityHolder[0] = 1;
                qtyLabel.setText("1");

                AlertUtil.showInfo("Added to Cart", qty + "x " + item.getItemName() + " added to your cart!");
            });
        }

        bottomRow.getChildren().addAll(priceLabel, bottomSpacer, qtyBox, addBtn);
        contentBox.getChildren().addAll(titleLabel, descLabel, bottomRow);

        card.getChildren().addAll(imageContainer, contentBox);
        return card;
    }

    private String getCategoryStyleClass(String category) {
        if (category == null) {
            return "badge-north-indian";
        }
        String lower = category.toLowerCase();
        if (lower.contains("north") || lower.contains("meal")) {
            return "badge-north-indian";
        } else if (lower.contains("chaat")) {
            return "badge-chaat";
        } else if (lower.contains("healthy") && lower.contains("beverage")) {
            return "badge-healthy-drinks";
        } else if (lower.contains("healthy")) {
            return "badge-healthy";
        } else if (lower.contains("breakfast")) {
            return "badge-breakfast";
        } else if (lower.contains("snack")) {
            return "badge-snacks";
        } else if (lower.contains("beverage")) {
            return "badge-beverages";
        } else if (lower.contains("combo")) {
            return "badge-combos";
        }
        return "badge-north-indian";
    }

    @FXML
    public void handleAddToCart(ActionEvent event) {
        // Fallback for legacy handlers
        if (!masterItemList.isEmpty()) {
            MenuItem first = masterItemList.get(0);
            CartItem cartItem = new CartItem();
            cartItem.setItem(first);
            cartItem.setQuantity(1);
            cartService.addToCart(cartItem);
            updateCartHeaderButton();
        }
    }

    @FXML
    public void handleRefresh(ActionEvent event) {
        loadMenuItems();
        updateCartHeaderButton();
        updateUserProfileDisplay();
    }

    @FXML
    public void handleViewCart(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/cart.fxml", null);
    }

    @FXML
    public void handleBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/student_dashboard.fxml", null);
    }
}
