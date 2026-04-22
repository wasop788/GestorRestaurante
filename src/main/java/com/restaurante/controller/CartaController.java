package com.restaurante.controller;

import com.restaurante.dao.ProductoDAO;
import com.restaurante.model.Producto;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CartaController implements Initializable {

    @FXML private TableView<Producto> tableProductos;
    @FXML private TableColumn<Producto, String> colNombre, colCategoria;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Boolean> colDisponible;
    @FXML private TextField txtNombre, txtDescripcion, txtPrecio;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private CheckBox chkDisponible;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private Producto productoSeleccionado = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colCategoria.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategoria()));
        colPrecio.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrecio()).asObject());
        colDisponible.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isDisponible()).asObject());

        cmbCategoria.setItems(FXCollections.observableArrayList(
                "entrante", "principal", "postre", "bebida"
        ));

        tableProductos.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, nuevo) -> {
                    if (nuevo != null) cargarFormulario(nuevo);
                }
        );

        cargarTabla();
    }

    private void cargarTabla() {
        List<Producto> productos = productoDAO.obtenerTodos();
        tableProductos.setItems(FXCollections.observableArrayList(productos));
    }

    private void cargarFormulario(Producto p) {
        productoSeleccionado = p;
        txtNombre.setText(p.getNombre());
        txtDescripcion.setText(p.getDescripcion());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        cmbCategoria.setValue(p.getCategoria());
        chkDisponible.setSelected(p.isDisponible());
    }

    @FXML
    private void guardar() {
        if (txtNombre.getText().isEmpty() || txtPrecio.getText().isEmpty()
                || cmbCategoria.getValue() == null) {
            mostrarAlerta("Rellena nombre, precio y categoría.");
            return;
        }
        double precio;
        try {
            precio = Double.parseDouble(txtPrecio.getText().replace(",", "."));
        } catch (NumberFormatException e) {
            mostrarAlerta("El precio debe ser un número válido.");
            return;
        }

        if (productoSeleccionado == null) {
            Producto nuevo = new Producto(0, txtNombre.getText(),
                    txtDescripcion.getText(), precio,
                    cmbCategoria.getValue(), true);
            productoDAO.insertar(nuevo);
        } else {
            productoSeleccionado.setNombre(txtNombre.getText());
            productoSeleccionado.setDescripcion(txtDescripcion.getText());
            productoSeleccionado.setPrecio(precio);
            productoSeleccionado.setCategoria(cmbCategoria.getValue());
            productoSeleccionado.setDisponible(chkDisponible.isSelected());
            productoDAO.actualizar(productoSeleccionado);
        }
        cargarTabla();
        nuevo();
    }

    @FXML
    private void nuevo() {
        productoSeleccionado = null;
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        cmbCategoria.setValue(null);
        chkDisponible.setSelected(true);
        tableProductos.getSelectionModel().clearSelection();
    }

    @FXML
    private void eliminar() {
        if (productoSeleccionado == null) {
            mostrarAlerta("Selecciona un producto primero.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("¿Desactivar el producto \"" + productoSeleccionado.getNombre() + "\"?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                productoDAO.eliminar(productoSeleccionado.getId());
                cargarTabla();
                nuevo();
            }
        });
    }

    @FXML
    private void volver() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/restaurante/views/mesas.fxml")
            );
            Stage stage = (Stage) tableProductos.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 750, 550));
            stage.setTitle("Gestor Restaurante — Mesas");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}