package com.example.studentmanager.controller;

import com.example.studentmanager.model.Task;
import com.example.studentmanager.service.TaskService;
import com.example.studentmanager.util.FxUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class ShowTasksController {

    @FXML private FlowPane tasksContainer;

    private String loggedInUser;
    private final TaskService taskService = new TaskService();

    @FXML
    public void initialize() {
        if (LoginController.loggedInUsername != null) {
            setLoggedInUser(LoginController.loggedInUsername);
        }
    }

    public void setLoggedInUser(String username) {
        this.loggedInUser = username;
        loadUserTasks();
    }

    private void loadUserTasks() {
        List<Task> tasks = taskService.getTasksForUser(loggedInUser);
        for (Task task : tasks) {
            StackPane taskPane = createTaskPane(task);
            tasksContainer.getChildren().add(taskPane);
        }
    }

    private StackPane createTaskPane(Task task) {
        StackPane stackPane = new StackPane();
        Rectangle rectangle = new Rectangle(150, 100);
        rectangle.setArcWidth(20);
        rectangle.setArcHeight(20);
        rectangle.setFill(Color.LIGHTBLUE);

        switch (task.getPriority()) {
            case "High": rectangle.setFill(Color.RED); break;
            case "Medium": rectangle.setFill(Color.YELLOW); break;
            case "Low": rectangle.setFill(Color.LIGHTGREEN); break;
            default: rectangle.setFill(Color.LIGHTGRAY);
        }

        Text taskText = new Text(task.getTaskName() + "\n" + task.getCategory() + "\n" + task.getTaskDate());
        taskText.setStyle("-fx-font-size: 12px;");

        stackPane.getChildren().addAll(rectangle, taskText);
        return stackPane;
    }

    @FXML
    private void redirectToTaskManager(ActionEvent event) {
        try {
            FxUtils.switchScene((Stage) ((Node) event.getSource()).getScene().getWindow(),
                    "/com/example/studentmanager/TaskManager.fxml", "Task Manager");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
