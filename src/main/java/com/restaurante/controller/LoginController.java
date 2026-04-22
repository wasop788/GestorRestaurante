package com.restaurante.controller;

import com.restaurante.dao.UsuarioDAO;
import com.restaurante.model.Usuario;
import com.restaurante.util.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            lblError.setText("Introduce usuario y contraseña.");
            return;
        }

        Usuario u = usuarioDAO.login(usuario, password);
        if (u == null) {
            lblError.setText("Usuario o contraseña incorrectos.");
            return;
        }

        Sesion.setUsuarioActual(u);
        abrirMesas();
    }

    private void abrirMesas() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/restaurante/views/mesas.fxml")
            );
            Stage stage = (Stage) txtUsuario.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 750, 550));
            stage.setTitle("Gestor Restaurante — Mesas");
        } catch (Exception e) {
            lblError.setText("Error al cargar la pantalla.");
            e.printStackTrace();
        }
    }
}