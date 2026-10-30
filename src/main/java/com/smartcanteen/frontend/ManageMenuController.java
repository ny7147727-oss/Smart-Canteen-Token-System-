package com.smartcanteen.frontend;

import com.smartcanteen.model.MenuItem;
import com.smartcanteen.backend.service.MenuService;
import com.smartcanteen.frontend.util.AlertUtil;
import com.smartcanteen.frontend.util.SceneNavigator;
import com.smartcanteen.common.util.Validator;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ManageMenuController implements Initializable {

    @FXML
    private TableView<MenuItem> menuTable;

    @FXML
    private TableColumn<MenuItem, Integer> idColumn;

    @FXML
    private TableColumn<MenuItem, String> nameColumn;

    @FXML
    private TableColumn<MenuItem, String> categoryColumn;

    @FXML
    private TableColumn<MenuItem, BigDecimal> priceColumn;

    @FXML
    private TableColumn<MenuItem, String> availableColumn;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private TextField priceField;

    @FXML
    private TextField descriptionField;

    @FXML
    private CheckBox availableCheckBox;

    private final MenuService menuService = new MenuService();
    private final ObservableList<MenuItem> menuItemsObservable = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Setup Table Columns
        idColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getItemId()).asObject());
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItemName()));
        categoryColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCategory()));
        priceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getPrice()));
        availableColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().isAvailable() ? "Yes" : "No"));

        menuTable.setItems(menuItemsObservable);

        // 2. Setup Category options
        categoryComboBox.setItems(FXCollections.observableArrayList(
                "North Indian Meals",
                "Chaat",
                "Healthy Food",
                "Breakfast",
                "Snacks",
                "Beverages",
                "Healthy Beverages",
                "Combos"
        ));

        // 3. Setup Selection Listener to populate form
        menuTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null) {
                nameField.setText(selected.getItemName());
                categoryComboBox.setValue(selected.getCategory());
                priceField.setText(selected.getPrice() != null ? selected.getPrice().toPlainString() : "");
                descriptionField.setText(selected.getDescription() != null ? selected.getDescription() : "");
                availableCheckBox.setSelected(selected.isAvailable());
            }
        });

        loadAllMenuItems();
    }

    private void loadAllMenuItems() {
        try {
            List<MenuItem> items = menuService.getAllItems();
            if (items != null) {
                menuItemsObservable.setAll(items);
            }
        } catch (Exception e) {
            AlertUtil.showError("Error", "Failed to load menu items: " + e.getMessage());
        }
    }

    @FXML
    public void handleAdd(ActionEvent event) {
        String name = (nameField.getText() != null) ? nameField.getText().trim() : "";
        String category = categoryComboBox.getValue();
        String priceText = (priceField.getText() != null) ? priceField.getText().trim() : "";
        String description = (descriptionField.getText() != null) ? descriptionField.getText().trim() : "";
        boolean available = availableCheckBox.isSelected();

        if (name.isEmpty() || category == null || category.isEmpty() || priceText.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please fill in Item Name, Category, and Price.");
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceText);
            if (!Validator.isPositiveNumber(price)) {
                AlertUtil.showError("Validation Error", "Price must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            AlertUtil.showError("Validation Error", "Price must be a valid numeric amount.");
            return;
        }

        MenuItem newItem = new MenuItem();
        newItem.setItemName(name);
        newItem.setCategory(category);
        newItem.setPrice(price);
        newItem.setAvailable(available);
        newItem.setDescription(description);

        boolean success = menuService.addItem(newItem);
        if (success) {
            AlertUtil.showInfo("Success", "Menu item added successfully!");
            loadAllMenuItems();
            handleClearForm(event);
        } else {
            AlertUtil.showError("Error", "Could not add menu item.");
        }
    }

    @FXML
    public void handleUpdate(ActionEvent event) {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showError("No Selection", "Please select a menu item from the table to update.");
            return;
        }

        String name = (nameField.getText() != null) ? nameField.getText().trim() : "";
        String category = categoryComboBox.getValue();
        String priceText = (priceField.getText() != null) ? priceField.getText().trim() : "";
        String description = (descriptionField.getText() != null) ? descriptionField.getText().trim() : "";
        boolean available = availableCheckBox.isSelected();

        if (name.isEmpty() || category == null || category.isEmpty() || priceText.isEmpty()) {
            AlertUtil.showError("Validation Error", "Please fill in Item Name, Category, and Price.");
            return;
        }

        BigDecimal price;
        try {
            price = new BigDecimal(priceText);
            if (!Validator.isPositiveNumber(price)) {
                AlertUtil.showError("Validation Error", "Price must be greater than 0.");
                return;
            }
        } catch (NumberFormatException e) {
            AlertUtil.showError("Validation Error", "Price must be a valid numeric amount.");
            return;
        }

        selected.setItemName(name);
        selected.setCategory(category);
        selected.setPrice(price);
        selected.setDescription(description);
        selected.setAvailable(available);

        boolean success = menuService.updateItem(selected);
        if (success) {
            AlertUtil.showInfo("Success", "Menu item updated successfully!");
            loadAllMenuItems();
        } else {
            AlertUtil.showError("Error", "Could not update menu item.");
        }
    }

    @FXML
    public void handleDelete(ActionEvent event) {
        MenuItem selected = menuTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtil.showError("No Selection", "Please select a menu item from the table to delete.");
            return;
        }

        boolean confirmed = AlertUtil.showConfirmation("Confirm Delete",
                "Are you sure you want to delete '" + selected.getItemName() + "'?");
        if (confirmed) {
            MenuItem itemToDelete = new MenuItem();
            itemToDelete.setItemId(selected.getItemId());
            boolean success = menuService.deleteItem(itemToDelete);
            if (success) {
                AlertUtil.showInfo("Success", "Menu item deleted successfully!");
                loadAllMenuItems();
                handleClearForm(event);
            } else {
                AlertUtil.showError("Error", "Could not delete menu item.");
            }
        }
    }

    @FXML
    public void handleClearForm(ActionEvent event) {
        menuTable.getSelectionModel().clearSelection();
        nameField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        priceField.clear();
        descriptionField.clear();
        availableCheckBox.setSelected(true);
    }

    @FXML
    public void handleBack(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/admin_dashboard.fxml", null);
    }
}
