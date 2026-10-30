package com.smartcanteen.frontend.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;

public class SceneNavigator {

    private SceneNavigator() {
        // Prevent instantiation
    }

    public static void switchScene(Stage stage, String fxmlPath) {
        switchScene(stage, fxmlPath, null);
    }

    public static void switchScene(Stage stage, String fxmlPath, Object data) {
        try {
            URL resource = SceneNavigator.class.getResource(fxmlPath);
            if (resource == null) {
                throw new IllegalArgumentException("Cannot find FXML file at path: " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();

            if (data != null) {
                Object controller = loader.getController();
                if (controller != null) {
                    passDataToController(controller, data);
                }
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML scene: " + fxmlPath, e);
        }
    }

    private static void passDataToController(Object controller, Object data) {
        for (Method method : controller.getClass().getMethods()) {
            if ((method.getName().equals("initData") || method.getName().equals("setData"))
                    && method.getParameterCount() == 1
                    && method.getParameterTypes()[0].isAssignableFrom(data.getClass())) {
                try {
                    method.invoke(controller, data);
                    return;
                } catch (Exception e) {
                    System.err.println("Warning: Failed to pass data to controller: " + e.getMessage());
                }
            }
        }
    }
}
