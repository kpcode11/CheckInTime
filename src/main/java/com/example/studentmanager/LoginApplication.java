package com.example.studentmanager;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 875, 450);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();

        LoginController controller = fxmlLoader.getController();
        try {
            controller.testDatabaseConnection();
        } catch (Exception e) {
            // ignore; connection may fail if environment not configured yet
        }

    }

    public static void main(String[] args) {
        launch();
    }
}
