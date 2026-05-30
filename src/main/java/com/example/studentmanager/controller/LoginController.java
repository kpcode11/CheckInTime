package com.example.studentmanager.controller;

import com.example.studentmanager.service.AuthService;
import com.example.studentmanager.util.FxUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final AuthService authService = new AuthService();

    public static String loggedInUsername; // Keeping this for compatibility with other parts that might use it directly

    @FXML
    private void login(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error", "Please fill in all fields.");
            return;
        }

        if (authService.login(username, password)) {
            loggedInUsername = username; // Store for legacy code compatibility
            showAlert(Alert.AlertType.INFORMATION, "Success", "Login successful.");
            try {
                FxUtils.switchScene(
                    (Stage) usernameField.getScene().getWindow(),
                    "/com/example/studentmanager/Dashboard.fxml",
                    "Dashboard"
                );
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
        }
    }

    @FXML
    private void openSignPage(ActionEvent event) {
        try {
            FxUtils.switchScene(
                (Stage) usernameField.getScene().getWindow(),
                "/com/example/studentmanager/SignUp.fxml",
                "SignUp Page"
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
