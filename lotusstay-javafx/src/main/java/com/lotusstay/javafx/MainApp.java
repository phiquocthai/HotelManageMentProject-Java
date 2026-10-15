package com.lotusstay.javafx;

import com.lotusstay.javafx.ui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp {

    public static Stage primaryStage;

    public static class HotelApp extends Application {
        @Override
        public void start(Stage stage) {
            primaryStage = stage;
            stage.setTitle("LotusStay — Phần mềm quản lý khách sạn");
            stage.setMinWidth(1100);
            stage.setMinHeight(700);

            Scene loginScene = new Scene(LoginView.build(), 1100, 700);
            loginScene.getStylesheets().add(
                    MainApp.class.getResource("/com/lotusstay/javafx/theme.css").toExternalForm());
            stage.setScene(loginScene);
            stage.show();
        }
    }

    public static void main(String[] args) {
        Application.launch(HotelApp.class, args);
    }
}

