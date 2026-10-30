package com.smartcanteen;

import com.smartcanteen.database.impl.TokenDAO;
import com.smartcanteen.threading.AppThreadPool;
import com.smartcanteen.frontend.util.SceneNavigator;
import com.smartcanteen.common.util.TokenGenerator;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        // 1. Initialize TokenGenerator from maximum token number in database
        try {
            int maxTokenInDb = new TokenDAO().getMaxTokenNumber();
            TokenGenerator.initializeFrom(maxTokenInDb);
        } catch (Exception e) {
            System.err.println("Notice: Could not load initial token sequence from database: " + e.getMessage());
        }

        // 2. Ensure initial menu catalog is seeded/updated without duplicates
        com.smartcanteen.common.util.MenuDataSeeder.seedMenuCatalog();

        // 3. Start background workers
        AppThreadPool.startOrderProcessor();
        AppThreadPool.startTokenUpdater();

        // 3. Set window title and open Login screen
        stage.setTitle("SmartServe");
        SceneNavigator.switchScene(stage, "/fxml/login.fxml", null);
    }

    @Override
    public void stop() {
        // Gracefully shut down background thread pools
        AppThreadPool.shutdownAll();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
