package com.smartcanteen.frontend;

import com.smartcanteen.common.exception.ValidationException;
import com.smartcanteen.model.Student;
import com.smartcanteen.backend.service.AuthService;
import com.smartcanteen.frontend.util.AlertUtil;
import com.smartcanteen.frontend.util.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField rollNumberField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void handleRegister(ActionEvent event) {
        errorLabel.setText("");

        String fullName = (fullNameField.getText() != null) ? fullNameField.getText().trim() : "";
        String rollNumber = (rollNumberField.getText() != null) ? rollNumberField.getText().trim() : "";
        String username = (usernameField.getText() != null) ? usernameField.getText().trim() : "";
        String password = (passwordField.getText() != null) ? passwordField.getText() : "";
        String confirmPassword = (confirmPasswordField.getText() != null) ? confirmPasswordField.getText() : "";
        String email = (emailField.getText() != null) ? emailField.getText().trim() : "";
        String phone = (phoneField.getText() != null) ? phoneField.getText().trim() : "";

        if (fullName.isEmpty() || rollNumber.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Please fill in all required fields marked with *.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        Student student = new Student();
        student.setFullName(fullName);
        student.setRollNumber(rollNumber);
        student.setUsername(username);
        student.setPassword(password);
        student.setEmail(email.isEmpty() ? null : email);
        student.setPhone(phone.isEmpty() ? null : phone);

        try {
            boolean success = authService.register(student);
            if (success) {
                AlertUtil.showInfo("Registration Successful", "Your account has been created! Please log in.");
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                SceneNavigator.switchScene(stage, "/fxml/login.fxml", null);
            } else {
                errorLabel.setText("Registration failed. Please try again.");
            }
        } catch (ValidationException e) {
            errorLabel.setText(e.getMessage());
        } catch (com.smartcanteen.common.exception.DatabaseException e) {
            System.err.println("[RegisterController] Database connection error during registration: " + e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
            errorLabel.setText("Database connection error. Please verify MySQL is running on localhost:3306 and database 'smart_canteen' is configured.");
        } catch (Exception e) {
            System.err.println("[RegisterController] Unexpected error during registration: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Error during registration: " + e.getMessage());
        }
    }

    @FXML
    public void handleBackToLogin(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        SceneNavigator.switchScene(stage, "/fxml/login.fxml", null);
    }
}
