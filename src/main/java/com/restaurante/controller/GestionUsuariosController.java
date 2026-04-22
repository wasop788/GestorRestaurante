package com.restaurante.controller;

import com.restaurante.dao.UsuarioDAO;
import com.restaurante.model.Usuario;
import com.restaurante.util.Sesion;
import javafx.beans.property.SimpleBooleanProperty;
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

public class GestionUsuariosController implements Initializable {

    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, String> colNombre, colUsuario, colRol;
    @FXML private TableColumn<Usuario, Boolean> colActivo;
    @FXML private TextField txtNombre, txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRol;
    @FXML private CheckBox chkActivo;
    @FXML private Label lblMensaje;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private Usuario usuarioSeleccionado = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colUsuario.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsuario()));
        colRol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRol()));
        colActivo.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isActivo()).asObject());

        cmbRol.setItems(FXCollections.observableArrayList("admin", "camarero"));

        tableUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, nuevo) -> {
                    if (nuevo != null) cargarFormulario(nuevo);
                }
        );

        cargarTabla();
    }

    private void cargarTabla() {
        tableUsuarios.setItems(FXCollections.observableArrayList(usuarioDAO.obtenerTodos()));
    }

    private void cargarFormulario(Usuario u) {
        usuarioSeleccionado = u;
        txtNombre.setText(u.getNombre());
        txtUsuario.setText(u.getUsuario());
        txtPassword.setText(u.getPassword());
        cmbRol.setValue(u.getRol());
        chkActivo.setSelected(u.isActivo());
    }

    @FXML
    private void guardar() {
        if (txtNombre.getText().isEmpty() || txtUsuario.getText().isEmpty()
                || txtPassword.getText().isEmpty() || cmbRol.getValue() == null) {
            mostrarMensaje("Rellena todos los campos.", false);
            return;
        }

        if (usuarioSeleccionado == null) {
            Usuario nuevo = new Usuario(
                    0,
                    txtNombre.getText().trim(),
                    txtUsuario.getText().trim(),
                    txtPassword.getText().trim(),
                    cmbRol.getValue(),
                    true
            );
            if (usuarioDAO.insertar(nuevo)) {
                mostrarMensaje("Usuario creado correctamente.", true);
                cargarTabla();
                nuevo();
            } else {
                mostrarMensaje("Error: ese nombre de usuario ya existe.", false);
            }
        } else {
            // No permitir que el admin se desactive a sí mismo
            if (usuarioSeleccionado.getId() == Sesion.getUsuarioActual().getId()
                    && !chkActivo.isSelected()) {
                mostrarMensaje("No puedes desactivar tu propia cuenta.", false);
                return;
            }
            usuarioSeleccionado.setNombre(txtNombre.getText().trim());
            usuarioSeleccionado.setUsuario(txtUsuario.getText().trim());
            usuarioSeleccionado.setPassword(txtPassword.getText().trim());
            usuarioSeleccionado.setRol(cmbRol.getValue());
            usuarioSeleccionado.setActivo(chkActivo.isSelected());
            if (usuarioDAO.actualizar(usuarioSeleccionado)) {
                mostrarMensaje("Usuario actualizado correctamente.", true);
                cargarTabla();
                nuevo();
            } else {
                mostrarMensaje("Error al actualizar el usuario.", false);
            }
        }
    }

    @FXML
    private void nuevo() {
        usuarioSeleccionado = null;
        txtNombre.clear();
        txtUsuario.clear();
        txtPassword.clear();
        cmbRol.setValue(null);
        chkActivo.setSelected(true);
        lblMensaje.setText("");
        tableUsuarios.getSelectionModel().clearSelection();
    }

    @FXML
    private void eliminar() {
        if (usuarioSeleccionado == null) {
            mostrarMensaje("Selecciona un usuario primero.", false);
            return;
        }
        if (usuarioSeleccionado.getId() == Sesion.getUsuarioActual().getId()) {
            mostrarMensaje("No puedes eliminar tu propia cuenta.", false);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Eliminar usuario");
        confirm.setContentText("¿Eliminar al usuario \"" + usuarioSeleccionado.getNombre() + "\"?");
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                if (usuarioDAO.eliminar(usuarioSeleccionado.getId())) {
                    mostrarMensaje("Usuario eliminado.", true);
                    cargarTabla();
                    nuevo();
                } else {
                    mostrarMensaje("Error al eliminar el usuario.", false);
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
            Stage stage = (Stage) tableUsuarios.getScene().getWindow();
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