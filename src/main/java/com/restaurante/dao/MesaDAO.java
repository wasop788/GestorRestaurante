package com.restaurante.dao;

import com.restaurante.model.Mesa;
import com.restaurante.util.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesaDAO {

    public List<Mesa> obtenerTodas() {
        List<Mesa> mesas = new ArrayList<>();
        String sql = "SELECT * FROM mesas ORDER BY numero";
        try (Statement st = Conexion.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                mesas.add(new Mesa(
                        rs.getInt("id"),
                        rs.getInt("numero"),
                        rs.getInt("capacidad"),
                        rs.getString("estado")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener mesas: " + e.getMessage());
        }
        return mesas;
    }

    public boolean actualizarEstado(int idMesa, String nuevoEstado) {
        String sql = "UPDATE mesas SET estado = ? WHERE id = ?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idMesa);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar mesa: " + e.getMessage());
            return false;
        }
    }

    public boolean insertar(Mesa mesa) {
        String sql = "INSERT INTO mesas (numero, capacidad, estado) VALUES (?, ?, 'libre')";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, mesa.getNumero());
            ps.setInt(2, mesa.getCapacidad());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar mesa: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizar(Mesa mesa) {
        String sql = "UPDATE mesas SET numero = ?, capacidad = ? WHERE id = ?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, mesa.getNumero());
            ps.setInt(2, mesa.getCapacidad());
            ps.setInt(3, mesa.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar mesa: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id) {
        String sql = "DELETE FROM mesas WHERE id = ?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar mesa: " + e.getMessage());
            return false;
        }
    }
}