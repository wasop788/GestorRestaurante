package com.restaurante.dao;

import com.restaurante.model.LineaPedido;
import com.restaurante.model.Pedido;
import com.restaurante.util.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public Pedido crearPedido(int idMesa, int idUsuario) {
        String sql = "INSERT INTO pedidos (id_mesa, id_usuario, estado) VALUES (?, ?, 'abierto')";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idMesa);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int idGenerado = rs.getInt(1);
                return obtenerPorId(idGenerado);
            }
        } catch (SQLException e) {
            System.err.println("Error al crear pedido: " + e.getMessage());
        }
        return null;
    }

    public Pedido obtenerPorId(int id) {
        String sql = "SELECT * FROM pedidos WHERE id = ?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Pedido p = new Pedido();
                p.setId(rs.getInt("id"));
                p.setIdMesa(rs.getInt("id_mesa"));
                p.setIdUsuario(rs.getInt("id_usuario"));
                p.setEstado(rs.getString("estado"));
                p.setTotal(rs.getDouble("total"));
                Timestamp ts = rs.getTimestamp("fecha_apertura");
                if (ts != null) p.setFechaApertura(ts.toLocalDateTime());
                return p;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pedido: " + e.getMessage());
        }
        return null;
    }

    public Pedido obtenerPedidoAbiertoByMesa(int idMesa) {
        String sql = "SELECT * FROM pedidos WHERE id_mesa = ? AND estado = 'abierto'";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idMesa);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Pedido p = new Pedido();
                p.setId(rs.getInt("id"));
                p.setIdMesa(rs.getInt("id_mesa"));
                p.setIdUsuario(rs.getInt("id_usuario"));
                p.setEstado(rs.getString("estado"));
                p.setTotal(rs.getDouble("total"));
                return p;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener pedido abierto: " + e.getMessage());
        }
        return null;
    }

    public boolean agregarLinea(LineaPedido linea) {
        String sql = "INSERT INTO lineas_pedido (id_pedido, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, linea.getIdPedido());
            ps.setInt(2, linea.getIdProducto());
            ps.setInt(3, linea.getCantidad());
            ps.setDouble(4, linea.getPrecioUnitario());
            ps.setDouble(5, linea.getSubtotal());
            boolean ok = ps.executeUpdate() > 0;
            if (ok) actualizarTotal(linea.getIdPedido());
            return ok;
        } catch (SQLException e) {
            System.err.println("Error al agregar línea: " + e.getMessage());
            return false;
        }
    }

    public List<LineaPedido> obtenerLineas(int idPedido) {
        List<LineaPedido> lineas = new ArrayList<>();
        String sql = "SELECT lp.*, p.nombre FROM lineas_pedido lp " +
                "JOIN productos p ON lp.id_producto = p.id WHERE lp.id_pedido = ?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LineaPedido l = new LineaPedido();
                l.setId(rs.getInt("id"));
                l.setIdPedido(rs.getInt("id_pedido"));
                l.setIdProducto(rs.getInt("id_producto"));
                l.setNombreProducto(rs.getString("nombre"));
                l.setCantidad(rs.getInt("cantidad"));
                l.setPrecioUnitario(rs.getDouble("precio_unitario"));
                l.setSubtotal(rs.getDouble("subtotal"));
                lineas.add(l);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener líneas: " + e.getMessage());
        }
        return lineas;
    }

    public boolean cerrarPedido(int idPedido) {
        String sql = "UPDATE pedidos SET estado = 'pagado', fecha_cierre = NOW() WHERE id = ?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al cerrar pedido: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarPedidoVacio(int idPedido) {
        String sql = "DELETE FROM pedidos WHERE id = ? AND total = 0";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar pedido vacío: " + e.getMessage());
            return false;
        }
    }

    private void actualizarTotal(int idPedido) {
        String sql = "UPDATE pedidos SET total = (SELECT SUM(subtotal) FROM lineas_pedido WHERE id_pedido = ?) WHERE id = ?";
        try (PreparedStatement ps = Conexion.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idPedido);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar total: " + e.getMessage());
        }
    }
}