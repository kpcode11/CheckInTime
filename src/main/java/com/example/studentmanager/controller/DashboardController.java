package com.example.studentmanager.controller;

import com.example.studentmanager.model.Subject;
import com.example.studentmanager.service.SubjectService;
import com.example.studentmanager.util.FxUtils;
import com.example.studentmanager.util.SessionManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.util.List;

public class DashboardController {

    @FXML private BarChart<String, Number> marksBarChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private final SubjectService subjectService = new SubjectService();
    private String loggedInUsername;

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    @FXML
    public void initialize() {
        // If loggedInUsername is null, try to get it from LoginController
        if (loggedInUsername == null) {
            loggedInUsername = LoginController.loggedInUsername;
        }
        loadChartData();
    }

    private void loadChartData() {
        if (loggedInUsername == null) return;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Marks");

        List<Subject> subjects = subjectService.getSubjectsForUser(loggedInUsername);
        for (Subject subject : subjects) {
            series.getData().add(new XYChart.Data<>(subject.getName(), subject.getMarks()));
        }

        marksBarChart.getData().add(series);
    }

    @FXML
    private void goToPerformanceSection(ActionEvent event) {
        try {
            FxUtils.switchScene((Stage) ((Node) event.getSource()).getScene().getWindow(),
                    "/com/example/studentmanager/subjects.fxml", "Subjects Performance");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goHome() {
        showAlert("Home", "This is the Home section.");
    }

    @FXML
    private void openTaskManager(ActionEvent event) {
        try {
            FxUtils.switchScene((Stage) ((Node) event.getSource()).getScene().getWindow(),
                    "/com/example/studentmanager/TaskManager.fxml", "Task Manager");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAttendance(ActionEvent event) {
        try {
            FxUtils.switchScene((Stage) ((Node) event.getSource()).getScene().getWindow(),
                    "/com/example/studentmanager/AttendanceApp.fxml", "Attendance Tracker");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void logOut() {
        Stage stage = (Stage) Stage.getWindows().get(0);
        stage.close();
        try {
            FxUtils.switchScene(new Stage(), "/com/example/studentmanager/Login.fxml", "Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
