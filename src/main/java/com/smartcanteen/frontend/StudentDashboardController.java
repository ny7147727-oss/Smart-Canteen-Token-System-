package com.smartcanteen.frontend;

import com.smartcanteen.model.User;
import com.smartcanteen.frontend.util.SceneNavigator;
import com.smartcanteen.common.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentDashboardController implements Initializable {

    @FXML
    private Label welcomeLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            String name = (user.getFullName() != null && !user.getFullName().isEmpty())
                    ? user.getFullName()
                    : user.getUsername();
            welcomeLabel.setText("Welcome, " + name + " (" + user.getUsername() + ")");
        } else {
            welcomeLabel.setText("Welcome, Student");
        }
    }

    @FXML
    public void handleBrowseMenu(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/menu.fxml", null);
    }

    @FXML
    public void handleViewCart(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/cart.fxml", null);
    }

    @FXML
    public void handleTokenStatus(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/token_status.fxml", null);
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        SessionManager.clearSession();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/login.fxml", null);
    }
}
