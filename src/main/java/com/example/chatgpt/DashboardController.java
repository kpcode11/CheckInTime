package com.example.chatgpt;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    private String loggedInUsername;

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    @FXML
    private void goHome() {
        showAlert("Home", "This is the Home section.");
    }

    @FXML
    private void openTaskManager() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TaskManager.fxml"));
            Parent root = fxmlLoader.load();

            TaskManagerController taskManagerController = fxmlLoader.getController();
            taskManagerController.setLoggedInUsername(loggedInUsername);  // Pass the logged-in username to TaskManager

            Stage stage = new Stage();
            stage.setTitle("Task Manager");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAttendance() {
        showAlert("Attendance", "This is the Attendance section.");
    }

    @FXML
    private void openPerformance() {
        showAlert("Performance", "This is the Performance section.");
    }

    @FXML
    private void logOut() {
        // Close the current window (dashboard)
        Stage stage = (Stage) Stage.getWindows().get(0);
        stage.close();

        // Redirect to login page after logging out
        LoginController loginController = new LoginController();
        loginController.openLoginPage();
    }

    // Helper method to show a simple alert for now (you can later redirect to actual pages)
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
