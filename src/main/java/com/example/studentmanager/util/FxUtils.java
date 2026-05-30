package com.example.studentmanager.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class FxUtils {
    private FxUtils() {}

    public static void switchScene(Stage stage, String fxmlPath, String title)
            throws Exception {
        Parent root = FXMLLoader.load(
            FxUtils.class.getResource(fxmlPath));
        stage.setScene(new Scene(root));
        stage.setTitle(title);
        stage.show();
    }
}
