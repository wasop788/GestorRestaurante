package com.restaurante.dao;

import com.restaurante.model.Usuario;
import com.restaurante.util.Conexion;

import java.sql.*;

public class UsuarioDAO {

    public Usuario login(String usuario, String password) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND password = ? AND activo = true";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("usuario"),
                        rs.getString("password"),
                        rs.getString("rol"),
                        rs.getBoolean("activo")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error en login: " + e.getMessage());
        }
        return null;
    }
}