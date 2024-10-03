package com.example.chatgpt;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttendanceController {

    private String loggedInUsername;

    public void setLoggedInUser(String username) {
        this.loggedInUsername = username;
        // Load subjects when the user is set
        loadSubjectsForUser();
    }

    // Method to handle the "Back to Dashboard" button click
    @FXML
    private void goBackToDashboard(ActionEvent event) {
        try {
            // Load the Dashboard.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/chatgpt/Dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Pass the logged-in user back to the dashboard if needed
            DashboardController dashboardController = loader.getController();
            dashboardController.setLoggedInUsername(loggedInUsername);

            // Get the current stage and set the new scene for the dashboard
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setScene(new Scene(dashboardRoot));
            currentStage.setTitle("Dashboard");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TextField subjectNameField;

    @FXML
    private TextField minPercentageField;

    @FXML
    private VBox subjectList;

    private List<AttendanceSubject> subjects = new ArrayList<>();

    @FXML
    public void initialize() {
        // This can be left empty or you can load existing subjects here
    }

    @FXML
    private void handleAddSubject() {
        String subjectName = subjectNameField.getText();
        String minPercentage = minPercentageField.getText();

        if (subjectName.isEmpty() || minPercentage.isEmpty()) {
            showAlert("Error", "Subject name and minimum percentage cannot be empty.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO user_subjects (subject_name, min_percentage, username) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, subjectName);
            stmt.setString(2, minPercentage);
            stmt.setString(3, loggedInUsername);  // Store the logged-in user's username

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add subject to the database.");
        }

        // Clear the fields after adding the subject
        subjectNameField.clear();
        minPercentageField.clear();

        // Refresh the subject list after adding a new subject
        loadSubjectsForUser();
    }

    // Load the subjects for the logged-in user
    public void loadSubjectsForUser() {
        subjectList.getChildren().clear();  // Clear the current list
        subjects.clear(); // Clear the list of subjects

        String query = "SELECT subject_name, min_percentage FROM user_subjects WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, loggedInUsername);  // Fetch subjects for the logged-in user
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String subjectName = rs.getString("subject_name");
                int minPercentage = rs.getInt("min_percentage");

                // Create an AttendanceSubject object for this subject
                AttendanceSubject subject = new AttendanceSubject(subjectName, minPercentage);
                subjects.add(subject); // Store the subject for later use

                // Create a UI element for the subject
                HBox subjectRow = new HBox(10);
                subjectRow.setAlignment(Pos.CENTER_LEFT);

                Label subjectNameLabel = new Label(subject.getName() + " (Min: " + subject.getMinPercentage() + "%)");
                Button presentButton = new Button("Present");
                Button absentButton = new Button("Absent");
                Label attendancePercentageLabel = new Label("Attendance: " + subject.getAttendancePercentage() + "%");

                // Button actions
                presentButton.setOnAction(e -> {
                    subject.markPresent();
                    attendancePercentageLabel.setText("Attendance: " + subject.getAttendancePercentage() + "%");
                });

                absentButton.setOnAction(e -> {
                    subject.markAbsent();
                    attendancePercentageLabel.setText("Attendance: " + subject.getAttendancePercentage() + "%");
                });

                // Add all components to the subject row
                subjectRow.getChildren().addAll(subjectNameLabel, presentButton, absentButton, attendancePercentageLabel);
                subjectList.getChildren().add(subjectRow);  // Add the row to the VBox
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load subjects.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class to represent attendance details of a subject
    class AttendanceSubject {
        private String name;
        private int minPercentage;
        private int attended;
        private int totalClasses;

        public AttendanceSubject(String name, int minPercentage) {
            this.name = name;
            this.minPercentage = minPercentage;
            this.attended = 0;
            this.totalClasses = 0;
        }

        public String getName() {
            return name;
        }

        public int getMinPercentage() {
            return minPercentage;
        }

        public void markPresent() {
            attended++;
            totalClasses++;
        }

        public void markAbsent() {
            totalClasses++;
        }

        public int getAttendancePercentage() {
            if (totalClasses == 0) return 0;
            return (attended * 100) / totalClasses;
        }
    }
}
