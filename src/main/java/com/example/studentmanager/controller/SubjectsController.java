package com.example.studentmanager.controller;

import com.example.studentmanager.model.Subject;
import com.example.studentmanager.service.SubjectService;
import com.example.studentmanager.util.FxUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SubjectsController {

    @FXML private TextField subjectNameField;
    @FXML private TextField marksField;
    @FXML private VBox subjectListContainer;
    @FXML private Label totalMarksLabel;
    @FXML private Label totalPercentageLabel;

    private String loggedInUser;
    private List<Subject> subjects = new ArrayList<>();
    private final SubjectService subjectService = new SubjectService();

    @FXML
    public void initialize() {
        // Automatically set the logged-in user if available from LoginController
        if (LoginController.loggedInUsername != null) {
            setLoggedInUser(LoginController.loggedInUsername);
        }
    }

    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
        loadUserSubjects();
    }

    @FXML
    private void addSubject() {
        String subjectName = subjectNameField.getText();
        String marksText = marksField.getText();

        if (subjectName.trim().isEmpty() || marksText.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Both subject name and marks are required.");
            return;
        }

        try {
            int marks = Integer.parseInt(marksText);
            if (marks < 0) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Marks cannot be negative.");
                return;
            }

            Subject subject = new Subject(subjectName, marks);
            subjectService.addSubject(subject, loggedInUser);
            subjects.add(subject);
            displaySubject(subject);

            subjectNameField.clear();
            marksField.clear();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Marks must be a valid number.");
        }
    }

    private void displaySubject(Subject subject) {
        Text subjectDisplay = new Text(subject.getName() + ": " + subject.getMarks() + " marks");
        subjectDisplay.setFont(javafx.scene.text.Font.font("Arial Black", 14));
        subjectDisplay.setFill(javafx.scene.paint.Color.WHITE);
        subjectListContainer.getChildren().add(subjectDisplay);
    }

    @FXML
    private void calculateResults() {
        if (subjects.isEmpty()) {
            totalMarksLabel.setText("No subjects added.");
            totalPercentageLabel.setText("");
            return;
        }

        int totalMarks = 0;
        for (Subject subject : subjects) {
            totalMarks += subject.getMarks();
        }

        double totalPercentage = subjectService.calculateTotalPercentage(subjects);

        totalMarksLabel.setText("Total Marks: " + totalMarks);
        totalPercentageLabel.setText("Total Percentage: " + String.format("%.2f", totalPercentage) + "%");
    }

    private void loadUserSubjects() {
        subjects.clear();
        subjectListContainer.getChildren().clear();

        subjects = subjectService.getSubjectsForUser(loggedInUser);
        for (Subject subject : subjects) {
            displaySubject(subject);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void redirectToDashboard(ActionEvent event) {
        try {
            FxUtils.switchScene((Stage) ((Node) event.getSource()).getScene().getWindow(),
                    "/com/example/studentmanager/Dashboard.fxml", "Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
