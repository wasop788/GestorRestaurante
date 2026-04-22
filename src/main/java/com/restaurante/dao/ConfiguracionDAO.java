package com.restaurante.dao;

import com.restaurante.util.Conexion;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ConfiguracionDAO {

    public Map<String, String> obtenerTodo() {
        Map<String, String> config = new HashMap<>();
        String sql = "SELECT clave, valor FROM configuracion";
        try (Statement st = Conexion.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                config.put(rs.getString("clave"), rs.getString("valor"));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener configuración: " + e.getMessage());
        }
        return config;
    }

    public String obtener(String clave) {
        String sql = "SELECT valor FROM configuracion WHERE clave = ?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, clave);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("valor");
        } catch (SQLException e) {
            System.err.println("Error al obtener config: " + e.getMessage());
        }
        return "";
    }

    public boolean actualizar(String clave, String valor) {
        String sql = "UPDATE configuracion SET valor = ? WHERE clave = ?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, valor);
            ps.setString(2, clave);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar config: " + e.getMessage());
            return false;
        }
    }
}