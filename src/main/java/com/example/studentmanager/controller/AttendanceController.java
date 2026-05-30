package com.example.studentmanager.controller;

import com.example.studentmanager.model.AttendanceRecord;
import com.example.studentmanager.model.UserSubject;
import com.example.studentmanager.service.AttendanceService;
import com.example.studentmanager.util.FxUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class AttendanceController {

    private String loggedInUsername;
    private final AttendanceService attendanceService = new AttendanceService();

    @FXML private TextField subjectNameField;
    @FXML private TextField minPercentageField;
    @FXML private VBox subjectList;
    @FXML private Label totalAttendanceLabel;

    public void setLoggedInUser(String username) {
        this.loggedInUsername = username;
        loadSubjectsForUser();
    }

    @FXML
    public void initialize() {
        if (LoginController.loggedInUsername != null) {
            setLoggedInUser(LoginController.loggedInUsername);
        }
    }

    @FXML
    private void goBackToDashboard(ActionEvent event) {
        try {
            FxUtils.switchScene((Stage) ((Node) event.getSource()).getScene().getWindow(),
                    "/com/example/studentmanager/Dashboard.fxml", "Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddSubject() {
        String subjectName = subjectNameField.getText();
        String minPercentage = minPercentageField.getText();

        if (subjectName.isEmpty() || minPercentage.isEmpty()) {
            showAlert("Error", "Subject name and minimum percentage cannot be empty.");
            return;
        }

        try {
            int minPercentageValue = Integer.parseInt(minPercentage);
            if (minPercentageValue < 0) {
                showAlert("Error", "Minimum percentage cannot be negative.");
                return;
            }

            UserSubject userSubject = new UserSubject(0, loggedInUsername, subjectName, minPercentageValue);
            attendanceService.addSubject(userSubject);

            subjectNameField.clear();
            minPercentageField.clear();
            loadSubjectsForUser();

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number for the minimum percentage.");
        }
    }

    public void loadSubjectsForUser() {
        if (loggedInUsername == null) return;
        subjectList.getChildren().clear();

        List<UserSubject> subjects = attendanceService.getSubjectsForUser(loggedInUsername);
        double totalPercentageSum = 0;
        int subjectCount = subjects.size();

        for (UserSubject subject : subjects) {
            HBox subjectRow = new HBox(10);
            subjectRow.setAlignment(Pos.CENTER_LEFT);

            Label subjectNameLabel = new Label(subject.getSubjectName() + " (Min: " + subject.getMinPercentage() + "%)");
            subjectNameLabel.setTextFill(Color.WHITE);

            Button presentButton = new Button("Present");
            presentButton.setStyle("-fx-background-color: #281352; -fx-text-fill: white; -fx-padding: 8px 15px; -fx-background-radius: 20px;");

            Button absentButton = new Button("Absent");
            absentButton.setStyle("-fx-background-color: #281352; -fx-text-fill: white; -fx-padding: 8px 15px; -fx-background-radius: 20px;");

            double attendancePercentage = attendanceService.calculateAttendancePercentage(loggedInUsername, subject.getSubjectName());
            totalPercentageSum += attendancePercentage;

            Label attendancePercentageLabel = new Label("Attendance: " + (int)attendancePercentage + "%");
            attendancePercentageLabel.setTextFill(Color.WHITE);
            updateAttendanceLabelColor(attendancePercentageLabel, (int)attendancePercentage, subject.getMinPercentage());

            presentButton.setOnAction(e -> {
                attendanceService.recordAttendance(new AttendanceRecord(0, loggedInUsername, subject.getSubjectName(), true, LocalDate.now()));
                loadSubjectsForUser();
            });

            absentButton.setOnAction(e -> {
                attendanceService.recordAttendance(new AttendanceRecord(0, loggedInUsername, subject.getSubjectName(), false, LocalDate.now()));
                loadSubjectsForUser();
            });

            subjectRow.getChildren().addAll(subjectNameLabel, presentButton, absentButton, attendancePercentageLabel);
            subjectList.getChildren().add(subjectRow);
        }

        updateTotalAttendancePercentage(subjectCount == 0 ? 0 : (int)(totalPercentageSum / subjectCount));
    }

    private void updateTotalAttendancePercentage(int totalPercentage) {
        totalAttendanceLabel.setText("Total Attendance: " + totalPercentage + "%");
        if (totalPercentage < 75) {
            totalAttendanceLabel.setTextFill(Color.RED);
        } else {
            totalAttendanceLabel.setTextFill(Color.WHITE);
        }
    }

    @FXML
    private void refreshSubjectList() {
        loadSubjectsForUser();
    }

    private void updateAttendanceLabelColor(Label attendanceLabel, int attendancePercentage, int minPercentage) {
        if (attendancePercentage < minPercentage) {
            attendanceLabel.setTextFill(Color.RED);
        } else {
            attendanceLabel.setTextFill(Color.BLACK);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
