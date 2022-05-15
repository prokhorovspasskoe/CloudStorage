package ru.prokhorov.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("cloud_client.fxml")));
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("Simple cloud storage");
        primaryStage.show();
    }
}