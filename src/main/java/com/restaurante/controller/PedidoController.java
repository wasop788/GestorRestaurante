package com.restaurante.controller;

import com.restaurante.dao.MesaDAO;
import com.restaurante.dao.PedidoDAO;
import com.restaurante.dao.ProductoDAO;
import com.restaurante.model.LineaPedido;
import com.restaurante.model.Mesa;
import com.restaurante.model.Pedido;
import com.restaurante.model.Producto;
import com.restaurante.util.Sesion;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PedidoController implements Initializable {

    @FXML private Label lblTitulo, lblTotal;
    @FXML private ComboBox<String> cmbCategoria;
    @FXML private ListView<Producto> listProductos;
    @FXML private TableView<LineaPedido> tablePedido;
    @FXML private TableColumn<LineaPedido, String> colProducto;
    @FXML private TableColumn<LineaPedido, Integer> colCantidad;
    @FXML private TableColumn<LineaPedido, Double> colPrecio, colSubtotal;

    private Mesa mesa;
    private Pedido pedidoActual;
    private MesasController mesasController;
    private Stage stage;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final MesaDAO mesaDAO = new MesaDAO();

    private List<Producto> todosProductos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colProducto.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreProducto()));
        colCantidad.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getCantidad()).asObject());
        colPrecio.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrecioUnitario()).asObject());
        colSubtotal.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getSubtotal()).asObject());
    }

    public void setMesa(Mesa mesa, MesasController mesasController, Stage stage) {
        this.mesa = mesa;
        this.mesasController = mesasController;
        this.stage = stage;

        lblTitulo.setText("Mesa " + mesa.getNumero());

        // Buscar pedido abierto existente o crear uno nuevo
        pedidoActual = pedidoDAO.obtenerPedidoAbiertoByMesa(mesa.getId());
        if (pedidoActual == null) {
            pedidoActual = pedidoDAO.crearPedido(mesa.getId(), Sesion.getUsuarioActual().getId());
            // NO marcamos la mesa como ocupada aquí todavía
        }

        cargarProductos();
        cargarLineas();
    }

    private void cargarProductos() {
        todosProductos = productoDAO.obtenerTodos();
        listProductos.setItems(FXCollections.observableArrayList(todosProductos));

        List<String> categorias = todosProductos.stream()
                .map(Producto::getCategoria).distinct().sorted()
                .collect(Collectors.toList());
        categorias.add(0, "Todas");
        cmbCategoria.setItems(FXCollections.observableArrayList(categorias));
        cmbCategoria.getSelectionModel().selectFirst();
    }

    @FXML
    private void filtrarProductos() {
        String cat = cmbCategoria.getValue();
        if (cat == null || cat.equals("Todas")) {
            listProductos.setItems(FXCollections.observableArrayList(todosProductos));
        } else {
            ObservableList<Producto> filtrados = FXCollections.observableArrayList(
                    todosProductos.stream()
                            .filter(p -> p.getCategoria().equals(cat))
                            .collect(Collectors.toList())
            );
            listProductos.setItems(filtrados);
        }
    }

    @FXML
    private void agregarProducto() {
        Producto seleccionado = listProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Selecciona un producto de la carta.");
            return;
        }
        LineaPedido linea = new LineaPedido(
                pedidoActual.getId(),
                seleccionado.getId(),
                seleccionado.getNombre(),
                1,
                seleccionado.getPrecio()
        );
        pedidoDAO.agregarLinea(linea);

        // Marcar mesa como ocupada solo al añadir el primer producto
        if (tablePedido.getItems().isEmpty()) {
            mesaDAO.actualizarEstado(mesa.getId(), "ocupada");
        }

        cargarLineas();
    }

    private void cargarLineas() {
        List<LineaPedido> lineas = pedidoDAO.obtenerLineas(pedidoActual.getId());
        tablePedido.setItems(FXCollections.observableArrayList(lineas));
        pedidoActual = pedidoDAO.obtenerPorId(pedidoActual.getId());
        lblTotal.setText("Total: " + String.format("%.2f", pedidoActual.getTotal()) + " €");
    }

    @FXML
    private void cobrar() {
        if (tablePedido.getItems().isEmpty()) {
            mostrarAlerta("El pedido está vacío.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cobrar");
        confirm.setContentText("¿Cobrar " + String.format("%.2f", pedidoActual.getTotal()) + " € y liberar la mesa?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                pedidoDAO.cerrarPedido(pedidoActual.getId());
                mesaDAO.actualizarEstado(mesa.getId(), "libre");

                List<LineaPedido> lineas = pedidoDAO.obtenerLineas(pedidoActual.getId());
                String rutaPdf = com.restaurante.util.PdfTicket.generarTicket(pedidoActual, mesa, lineas);

                if (rutaPdf != null) {
                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Ticket generado");
                    exito.setHeaderText("Cobro registrado correctamente");
                    exito.setContentText("Ticket guardado en:\n" + rutaPdf + "\n\n¿Abrir el PDF ahora?");

                    ButtonType btnAbrir = new ButtonType("Abrir");
                    ButtonType btnCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
                    exito.getButtonTypes().setAll(btnAbrir, btnCerrar);

                    exito.showAndWait().ifPresent(resp -> {
                        if (resp == btnAbrir) {
                            try {
                                java.awt.Desktop.getDesktop().open(new java.io.File(rutaPdf));
                            } catch (Exception ex) {
                                System.err.println("No se pudo abrir el PDF: " + ex.getMessage());
                            }
                        }
                    });
                }

                volver();
            }
        });
    }

    @FXML
    private void volver() {
        // Si el pedido está vacío al salir, lo eliminamos y dejamos la mesa libre
        if (tablePedido.getItems().isEmpty()) {
            pedidoDAO.eliminarPedidoVacio(pedidoActual.getId());
            mesaDAO.actualizarEstado(mesa.getId(), "libre");
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/restaurante/views/mesas.fxml")
            );
            stage.setScene(new Scene(loader.load(), 750, 550));
            stage.setTitle("Gestor Restaurante — Mesas");
            stage.centerOnScreen();
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