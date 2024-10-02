package com.example.chatgpt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import static com.example.chatgpt.LoginController.loggedInUsername;

public class ReminderScheduler {

    private Timer timer = new Timer();

    public void startReminderCheck() {
        // Schedule the task to check reminders every minute (60000ms)
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkForReminders();
            }
        }, 0, 60000);  // First check immediately, then every minute
    }

    private void checkForReminders() {
        List<Task> tasks = getUserTasksFromDatabase();  // Fetch tasks

        for (Task task : tasks) {
            // Calculate the reminder time
            LocalDateTime taskDateTime = LocalDateTime.of(task.getTaskDate(), task.getTaskTime());

            // Parse the reminder string and calculate the actual reminder time
            LocalDateTime reminderTime = calculateReminderTime(taskDateTime, task.getReminder());

            if (LocalDateTime.now().isAfter(reminderTime) && LocalDateTime.now().isBefore(taskDateTime)) {
                // Trigger notification if the reminder is due
                Platform.runLater(() -> {
                    showReminderNotification(task);
                });
            }
        }
    }

    // Helper method to calculate reminder time
    private LocalDateTime calculateReminderTime(LocalDateTime taskDateTime, String reminder) {
        switch (reminder) {
            case "15 Minutes Before":
                return taskDateTime.minusMinutes(15);
            case "30 Minutes Before":
                return taskDateTime.minusMinutes(30);
            case "1 Hour Before":
                return taskDateTime.minusHours(1);
            case "2 Hours Before":
                return taskDateTime.minusHours(2);
            case "1 Day Before":
                return taskDateTime.minusDays(1);
            default:
                return taskDateTime;  // No reminder or unrecognized setting
        }
    }

    // Method to fetch tasks from the database
    private List<Task> getUserTasksFromDatabase() {
        List<Task> tasks = new ArrayList<>();
        String query = "SELECT task_name, category, task_date, task_time, priority, reminder FROM tasks WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, loggedInUsername);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String taskName = rs.getString("task_name");
                String category = rs.getString("category");
                LocalDate taskDate = rs.getDate("task_date").toLocalDate();
                LocalTime taskTime = LocalTime.parse(rs.getString("task_time"));
                String priority = rs.getString("priority");
                String reminder = rs.getString("reminder");

                tasks.add(new Task(taskName, category, taskDate, taskTime, priority, reminder));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tasks;
    }

    // Method to display notification
    private void showReminderNotification(Task task) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Task Reminder");
        alert.setHeaderText("Reminder for: " + task.getTaskName());
        alert.setContentText("Category: " + task.getCategory() + "\nDue at: " + task.getTaskTime());
        alert.showAndWait();
    }


    public void stopReminderCheck() {
        timer.cancel();  // Stop the timer when needed
    }
}
