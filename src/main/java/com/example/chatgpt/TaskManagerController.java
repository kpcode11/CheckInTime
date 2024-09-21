package com.example.chatgpt;

import com.example.chatgpt.DatabaseConnection;
import com.example.chatgpt.LoginController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TaskManagerController {

    private String loggedInUsername;

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    // Linking FXML components with TaskManager.fxml
    @FXML
    private TextField taskNameField;  // Text field for task name

    @FXML
    private ComboBox<String> categoryComboBox;  // ComboBox for category selection (Work, Personal, etc.)

    @FXML
    private DatePicker taskDatePicker;  // Date picker for task date

    @FXML
    private TextField taskTimeField;  // Text field for task time (HH:mm)

    @FXML
    private ComboBox<String> reminderComboBox;

    // Method to show alerts
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);  // You can set a header text or leave it null
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void initialize() {
        // Add items to the ComboBox when the controller is initialized
        categoryComboBox.setItems(FXCollections.observableArrayList("Work", "Personal", "Significant Date"));

        reminderComboBox.setItems(FXCollections.observableArrayList(
                "No Reminder",
                "15 Minutes Before",
                "30 Minutes Before",
                "1 Hour Before",
                "2 Hours Before",
                "1 Day Before"
        ));
    }
    @FXML
    private void handleAddTask() {
        // Get user input from the form
        String taskName = taskNameField.getText();
        String category = categoryComboBox.getValue();
        LocalDate taskDate = taskDatePicker.getValue();
        String taskTime = taskTimeField.getText();
        String reminder = reminderComboBox.getValue();

        // Check for required fields
        if (taskName.isEmpty() || category == null || taskDate == null || taskTime.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
            return;
        }

        // Validate the time input (HH:mm)
        try {
            LocalTime.parse(taskTime, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid time (HH:mm).");
            return;
        }

        // Retrieve the currently logged-in user (replace this line with actual logic for logged-in user)
        String loggedInUser = LoginController.loggedInUsername; // Assuming this variable is defined in LoginController

        if (loggedInUser == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user is logged in.");
            return;
        }

        // SQL query to insert task
        String insertTaskQuery = "INSERT INTO tasks (username, task_name, category, task_date, task_time, reminder) VALUES (?, ?, ?, ?, ?, ?)";

        // Execute database query
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO tasks (username, task_name, category, task_date, task_time, reminder) VALUES (?, ?, ?, ?, ?, ?)")) {

            // Set query parameters
            stmt.setString(1, loggedInUser);
            stmt.setString(2, taskName);
            stmt.setString(3, category);
            stmt.setDate(4, java.sql.Date.valueOf(taskDate));
            stmt.setString(5, taskTime);
            stmt.setString(6, reminder != null ? reminder : "No Reminder");

            // Execute the update
            stmt.executeUpdate();

            showAlert(Alert.AlertType.INFORMATION, "Task Added", "Your task has been added successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while saving the task: " + e.getMessage());
        }
    }
}
