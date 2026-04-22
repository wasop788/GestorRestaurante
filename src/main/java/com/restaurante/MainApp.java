package com.restaurante;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/restaurante/views/login.fxml")
        );
        Scene scene = new Scene(loader.load(), 400, 300);
        stage.setTitle("Gestor Restaurante");
        stage.setScene(scene);

        // Icono de la ventana
        try {
            Image icono = new Image(
                    getClass().getResourceAsStream("/com/restaurante/images/icono.png")
            );
            stage.getIcons().add(icono);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono: " + e.getMessage());
        }

        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
