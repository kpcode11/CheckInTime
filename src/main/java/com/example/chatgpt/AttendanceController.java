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
import java.util.ArrayList;
import java.util.List;

public class AttendanceController {

    private String loggedInUsername;

    public void setLoggedInUser(String username) {
        this.loggedInUsername = username;
        // You can use this username to load specific attendance data for the logged-in user
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
        // Initialization logic if needed
    }

    @FXML
    public void handleAddSubject() {
        String subjectName = subjectNameField.getText();
        String minPercentage = minPercentageField.getText();

        if (!subjectName.isEmpty() && !minPercentage.isEmpty()) {
            AttendanceSubject newSubject = new AttendanceSubject(subjectName, Integer.parseInt(minPercentage));
            subjects.add(newSubject);  // Add the subject to the list

            // Reset fields
            subjectNameField.clear();
            minPercentageField.clear();

            // Refresh the subject list UI
            refreshSubjectList();
        } else {
            showAlert("Error", "Please fill in both fields.");
        }
    }

    private void refreshSubjectList() {
        subjectList.getChildren().clear();  // Clear the list before re-populating

        for (AttendanceSubject subject : subjects) {
            HBox subjectRow = new HBox(10);
            subjectRow.setAlignment(Pos.CENTER_LEFT);

            Label subjectNameLabel = new Label(subject.getName() + " (Min: " + subject.getMinPercentage() + "%)");
            Button presentButton = new Button("Present");
            Button absentButton = new Button("Absent");

            // Button actions
            presentButton.setOnAction(e -> {
                subject.markPresent();
                refreshSubjectList();  // Refresh after marking
            });

            absentButton.setOnAction(e -> {
                subject.markAbsent();
                refreshSubjectList();  // Refresh after marking
            });

            // Display attendance percentage
            Label attendancePercentageLabel = new Label("Attendance: " + subject.getAttendancePercentage() + "%");
            subjectRow.getChildren().addAll(subjectNameLabel, presentButton, absentButton, attendancePercentageLabel);

            subjectList.getChildren().add(subjectRow);  // Add each subject row to the list
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

