package com.example.studentmanager.controller;

import com.example.studentmanager.model.Task;
import com.example.studentmanager.service.TaskService;
import com.example.studentmanager.util.FxUtils;
import com.example.studentmanager.util.NotificationManager;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TaskManagerController {

    private NotificationManager notificationManager;
    private final TaskService taskService = new TaskService();
    private String loggedInUsername;

    @FXML private Button taskManagerButton;
    @FXML private Button showTasksButton;
    @FXML private Button attendanceButton;
    @FXML private Button homeButton;
    @FXML private Button performanceButton;

    @FXML private TextField taskNameField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker taskDatePicker;
    @FXML private TextField taskTimeField;
    @FXML private ComboBox<String> reminderComboBox;
    @FXML private ComboBox<String> priorityComboBox;

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    @FXML
    public void initialize() {
        if (loggedInUsername == null) {
            loggedInUsername = LoginController.loggedInUsername;
        }
        notificationManager = new NotificationManager();

        categoryComboBox.setItems(FXCollections.observableArrayList("Work", "Personal", "Significant Date"));
        reminderComboBox.setItems(FXCollections.observableArrayList(
                "No Reminder", "15 Minutes Before", "30 Minutes Before",
                "1 Hour Before", "2 Hours Before", "1 Day Before"
        ));
        priorityComboBox.setItems(FXCollections.observableArrayList("High", "Medium", "Low"));
    }

    @FXML
    private void handleAddTask() {
        String taskName = taskNameField.getText();
        String category = categoryComboBox.getValue();
        LocalDate taskDate = taskDatePicker.getValue();
        String taskTime = taskTimeField.getText();
        String reminder = reminderComboBox.getValue();
        String priority = priorityComboBox.getValue();

        if (taskName.isEmpty() || category == null || taskDate == null || taskTime.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
            return;
        }

        if (taskDate.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select a valid future date.");
            return;
        }

        LocalTime selectedTime;
        try {
            selectedTime = LocalTime.parse(taskTime, DateTimeFormatter.ofPattern("HH:mm"));
            if (taskDate.equals(LocalDate.now()) && selectedTime.isBefore(LocalTime.now())) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a future time.");
                return;
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid time (HH:mm).");
            return;
        }

        if (loggedInUsername == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user logged in.");
            return;
        }

        LocalDateTime reminderTimeObj = calculateReminderTime(taskDate, selectedTime, reminder);
        Task task = new Task(taskName, category, taskDate.toString(), taskTime, reminder, reminderTimeObj, priority);

        taskService.addTask(task, loggedInUsername);
        showAlert(Alert.AlertType.INFORMATION, "Task Added", "Your task has been added successfully.");
    }

    private LocalDateTime calculateReminderTime(LocalDate taskDate, LocalTime selectedTime, String reminder) {
        LocalDateTime taskDateTime = LocalDateTime.of(taskDate, selectedTime);
        if (reminder == null) return null;
        switch (reminder) {
            case "15 Minutes Before": return taskDateTime.minusMinutes(15);
            case "30 Minutes Before": return taskDateTime.minusMinutes(30);
            case "1 Hour Before": return taskDateTime.minusHours(1);
            case "2 Hours Before": return taskDateTime.minusHours(2);
            case "1 Day Before": return taskDateTime.minusDays(1);
            default: return null;
        }
    }

    @FXML
    private void showTaskManagerAlert() {
        showAlert(Alert.AlertType.INFORMATION, "Task Manager", "This is the Task Manager section.");
    }

    @FXML
    private void showTasks(ActionEvent event) {
        try {
            FxUtils.switchScene((Stage) ((Node) event.getSource()).getScene().getWindow(),
                    "/com/example/studentmanager/ShowTasks.fxml", "User Tasks");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAttendance(ActionEvent event) {
        try {
            FxUtils.switchScene((Stage) ((Node) event.getSource()).getScene().getWindow(),
                    "/com/example/studentmanager/AttendanceApp.fxml", "Attendance");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        try {
            FxUtils.switchScene((Stage) ((Node) event.getSource()).getScene().getWindow(),
                    "/com/example/studentmanager/Dashboard.fxml", "Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToPerformance(ActionEvent event) {
        try {
            FxUtils.switchScene((Stage) ((Node) event.getSource()).getScene().getWindow(),
                    "/com/example/studentmanager/subjects.fxml", "Subjects");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logOut() {
        if (notificationManager != null) {
            notificationManager.shutdown();
        }
        Stage stage = (Stage) Stage.getWindows().get(0);
        stage.close();
        try {
            FxUtils.switchScene(new Stage(), "/com/example/studentmanager/Login.fxml", "Login");
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
