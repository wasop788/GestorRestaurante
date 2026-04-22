package com.restaurante.controller;

import com.restaurante.dao.ConfiguracionDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class ConfiguracionController implements Initializable {

    @FXML private TextField txtNombre, txtDireccion, txtTelefono, txtCif;
    @FXML private Label lblMensaje;

    private final ConfiguracionDAO configuracionDAO = new ConfiguracionDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Map<String, String> config = configuracionDAO.obtenerTodo();
        txtNombre.setText(config.getOrDefault("nombre", ""));
        txtDireccion.setText(config.getOrDefault("direccion", ""));
        txtTelefono.setText(config.getOrDefault("telefono", ""));
        txtCif.setText(config.getOrDefault("cif", ""));
    }

    @FXML
    private void guardar() {
        if (txtNombre.getText().isEmpty()) {
            mostrarMensaje("El nombre del restaurante no puede estar vacío.", false);
            return;
        }

        configuracionDAO.actualizar("nombre", txtNombre.getText().trim());
        configuracionDAO.actualizar("direccion", txtDireccion.getText().trim());
        configuracionDAO.actualizar("telefono", txtTelefono.getText().trim());
        configuracionDAO.actualizar("cif", txtCif.getText().trim());

        mostrarMensaje("Configuración guardada correctamente.", true);
    }

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/restaurante/views/mesas.fxml")
            );
            Stage stage = (Stage) txtNombre.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 750, 550));
            stage.setTitle("Gestor Restaurante — Mesas");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarMensaje(String msg, boolean exito) {
        lblMensaje.setText(msg);
        lblMensaje.setStyle("-fx-text-fill: " + (exito ? "green" : "red") + "; -fx-font-size: 12px;");
    }
}