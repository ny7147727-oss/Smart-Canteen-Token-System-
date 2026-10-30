package com.smartcanteen.frontend;

import com.smartcanteen.common.exception.AuthenticationException;
import com.smartcanteen.model.Role;
import com.smartcanteen.model.User;
import com.smartcanteen.backend.service.AuthService;
import com.smartcanteen.frontend.util.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void handleLogin(ActionEvent event) {
        errorLabel.setText("");
        String username = (usernameField.getText() != null) ? usernameField.getText().trim() : "";
        String password = (passwordField.getText() != null) ? passwordField.getText() : "";

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        User credentials = new User();
        credentials.setUsername(username);
        credentials.setPassword(password);

        try {
            User user = authService.login(credentials);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            if (user.getRole() == Role.STUDENT) {
                SceneNavigator.switchScene(stage, "/fxml/student_dashboard.fxml", null);
            } else if (user.getRole() == Role.ADMIN) {
                SceneNavigator.switchScene(stage, "/fxml/admin_dashboard.fxml", null);
            } else {
                errorLabel.setText("Unknown user role: " + user.getRole());
            }
        } catch (AuthenticationException e) {
            errorLabel.setText(e.getMessage());
        } catch (com.smartcanteen.common.exception.DatabaseException e) {
            System.err.println("[LoginController] Database connection error during login: " + e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
            errorLabel.setText("Database connection error. Please verify MySQL is running on localhost:3306.");
        } catch (Exception e) {
            System.err.println("[LoginController] Unexpected login error: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Login error: " + e.getMessage());
        }
    }

    @FXML
    public void handleNavigateRegister(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/register.fxml", null);
    }
}
