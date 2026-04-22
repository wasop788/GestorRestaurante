package com.restaurante.controller;

import com.restaurante.dao.MesaDAO;
import com.restaurante.model.Mesa;
import com.restaurante.util.Sesion;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MesasController implements Initializable {

    @FXML private FlowPane flowMesas;
    @FXML private Label lblUsuario;
    @FXML private Button btnGestionMesas;
    @FXML private Button btnGestionUsuarios;
    @FXML private Button btnConfiguracion;

    private final MesaDAO mesaDAO = new MesaDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblUsuario.setText("Hola, " + Sesion.getUsuarioActual().getNombre());
        boolean esAdmin = Sesion.getUsuarioActual().getRol().equals("admin");
        btnGestionMesas.setVisible(esAdmin);
        btnGestionUsuarios.setVisible(esAdmin);
        btnConfiguracion.setVisible(esAdmin);
        cargarMesas();

        Platform.runLater(() -> {
            Stage stage = (Stage) flowMesas.getScene().getWindow();
            stage.centerOnScreen();
        });
    }

    public void cargarMesas() {
        flowMesas.getChildren().clear();
        List<Mesa> mesas = mesaDAO.obtenerTodas();
        for (Mesa mesa : mesas) {
            flowMesas.getChildren().add(crearTarjetaMesa(mesa));
        }
    }

    private VBox crearTarjetaMesa(Mesa mesa) {
        boolean libre = mesa.getEstado().equals("libre");
        String color = libre ? "#4CAF50" : "#f44336";

        VBox tarjeta = new VBox(8);
        tarjeta.setAlignment(javafx.geometry.Pos.CENTER);
        tarjeta.setPrefSize(120, 100);
        tarjeta.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 10; -fx-cursor: hand;");

        Label lblNumero = new Label("Mesa " + mesa.getNumero());
        lblNumero.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");

        Label lblCapacidad = new Label("👥 " + mesa.getCapacidad() + " personas");
        lblCapacidad.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");

        Label lblEstado = new Label(libre ? "LIBRE" : "OCUPADA");
        lblEstado.setStyle("-fx-text-fill: white; -fx-font-size: 11px;");

        tarjeta.getChildren().addAll(lblNumero, lblCapacidad, lblEstado);
        tarjeta.setOnMouseClicked(e -> abrirPedido(mesa));

        return tarjeta;
    }

    private void abrirPedido(Mesa mesa) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/restaurante/views/pedido.fxml")
            );
            Stage stage = (Stage) flowMesas.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 850, 600));
            stage.setTitle("Mesa " + mesa.getNumero());
            stage.centerOnScreen();

            PedidoController controller = loader.getController();
            controller.setMesa(mesa, this, stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirGestionMesas() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/restaurante/views/gestion_mesas.fxml")
            );
            Stage stage = (Stage) flowMesas.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 750, 550));
            stage.setTitle("Gestión de Mesas");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirGestionUsuarios() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/restaurante/views/gestion_usuarios.fxml")
            );
            Stage stage = (Stage) flowMesas.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 750, 550));
            stage.setTitle("Gestión de Usuarios");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirConfiguracion() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/restaurante/views/configuracion.fxml")
            );
            Stage stage = (Stage) flowMesas.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 500, 450));
            stage.setTitle("Configuración del Restaurante");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirCarta() {
        if (!Sesion.getUsuarioActual().getRol().equals("admin")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Acceso denegado");
            alert.setContentText("Solo el administrador puede gestionar la carta.");
            alert.showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/restaurante/views/carta.fxml")
            );
            Stage stage = (Stage) flowMesas.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 750, 550));
            stage.setTitle("Gestión de Carta");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarSesion() {
        Sesion.cerrar();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/restaurante/views/login.fxml")
            );
            Stage stage = (Stage) flowMesas.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 400, 300));
            stage.setTitle("Gestor Restaurante");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}