package com.restaurante.controller;

import com.restaurante.dao.MesaDAO;
import com.restaurante.model.Mesa;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GestionMesasController implements Initializable {

    @FXML private TableView<Mesa> tableMesas;
    @FXML private TableColumn<Mesa, Integer> colNumero, colCapacidad;
    @FXML private TableColumn<Mesa, String> colEstado;
    @FXML private TextField txtNumero, txtCapacidad;
    @FXML private Label lblMensaje;

    private final MesaDAO mesaDAO = new MesaDAO();
    private Mesa mesaSeleccionada = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNumero.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getNumero()).asObject());
        colCapacidad.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCapacidad()).asObject());
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado()));

        tableMesas.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, nuevo) -> {
                    if (nuevo != null) cargarFormulario(nuevo);
                }
        );

        cargarTabla();
    }

    private void cargarTabla() {
        tableMesas.setItems(FXCollections.observableArrayList(mesaDAO.obtenerTodas()));
    }

    private void cargarFormulario(Mesa m) {
        mesaSeleccionada = m;
        txtNumero.setText(String.valueOf(m.getNumero()));
        txtCapacidad.setText(String.valueOf(m.getCapacidad()));
    }

    @FXML
    private void guardar() {
        if (txtNumero.getText().isEmpty() || txtCapacidad.getText().isEmpty()) {
            mostrarMensaje("Rellena todos los campos.", false);
            return;
        }

        int numero, capacidad;
        try {
            numero = Integer.parseInt(txtNumero.getText().trim());
            capacidad = Integer.parseInt(txtCapacidad.getText().trim());
        } catch (NumberFormatException e) {
            mostrarMensaje("Número y capacidad deben ser enteros.", false);
            return;
        }

        if (numero <= 0 || capacidad <= 0) {
            mostrarMensaje("Los valores deben ser mayores que 0.", false);
            return;
        }

        if (mesaSeleccionada == null) {
            Mesa nueva = new Mesa(0, numero, capacidad, "libre");
            if (mesaDAO.insertar(nueva)) {
                mostrarMensaje("Mesa añadida correctamente.", true);
                cargarTabla();
                nueva();
            } else {
                mostrarMensaje("Error: ese número de mesa ya existe.", false);
            }
        } else {
            mesaSeleccionada.setNumero(numero);
            mesaSeleccionada.setCapacidad(capacidad);
            if (mesaDAO.actualizar(mesaSeleccionada)) {
                mostrarMensaje("Mesa actualizada correctamente.", true);
                cargarTabla();
                nueva();
            } else {
                mostrarMensaje("Error al actualizar la mesa.", false);
            }
        }
    }

    @FXML
    private void nueva() {
        mesaSeleccionada = null;
        txtNumero.clear();
        txtCapacidad.clear();
        lblMensaje.setText("");
        tableMesas.getSelectionModel().clearSelection();
    }

    @FXML
    private void eliminar() {
        if (mesaSeleccionada == null) {
            mostrarMensaje("Selecciona una mesa primero.", false);
            return;
        }
        if (!mesaSeleccionada.getEstado().equals("libre")) {
            mostrarMensaje("No se puede eliminar una mesa ocupada.", false);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar mesa");
        confirm.setContentText("¿Eliminar la mesa " + mesaSeleccionada.getNumero() + "?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (mesaDAO.eliminar(mesaSeleccionada.getId())) {
                    mostrarMensaje("Mesa eliminada.", true);
                    cargarTabla();
                    nueva();
                } else {
                    mostrarMensaje("Error al eliminar la mesa.", false);
                }
            }
        });
    }

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/restaurante/views/mesas.fxml")
            );
            Stage stage = (Stage) tableMesas.getScene().getWindow();
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