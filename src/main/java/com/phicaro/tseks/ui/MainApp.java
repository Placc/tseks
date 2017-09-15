package com.phicaro.tseks.ui;

import com.phicaro.tseks.util.Resources;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setMaximized(true);
        stage.setTitle(Resources.getString("LAB_ApplicationName"));
        stage.getIcons().add(Resources.getPreviewBackground());

        BorderPane root = FXMLLoader.load(getClass().getResource("/fxml/pages/Main.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");

        stage.setScene(scene);
        stage.show();
    }
}
