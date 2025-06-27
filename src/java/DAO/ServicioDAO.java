package DAO;

import Model.ServicioDTO;
import java.sql.*;
import java.util.*;

public class ServicioDAO {

    private Connection conn;

    public ServicioDAO(Connection conn) {
        this.conn = conn;
    }

    public void registrar(ServicioDTO servicio) throws SQLException {
        String sql = "INSERT INTO Servicios (nombre, descripcion, precio_unitario, activo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, servicio.getNombre());
            stmt.setString(2, servicio.getDescripcion());
            stmt.setDouble(3, servicio.getPrecioUnitario());
            stmt.setBoolean(4, servicio.isActivo());
            stmt.executeUpdate();
        }
    }

    public List<ServicioDTO> listarTodos() throws SQLException {
        List<ServicioDTO> lista = new ArrayList<>();
        String sql = "SELECT * FROM Servicios ORDER BY activo DESC, nombre ASC"; // primero activos, luego por nombre
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ServicioDTO servicio = new ServicioDTO();
                servicio.setIdServicio(rs.getInt("id_servicio"));
                servicio.setNombre(rs.getString("nombre"));
                servicio.setDescripcion(rs.getString("descripcion"));
                servicio.setPrecioUnitario(rs.getDouble("precio_unitario"));
                servicio.setActivo(rs.getBoolean("activo"));
                servicio.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                lista.add(servicio);
            }
        }
        return lista;
    }

    public ServicioDTO obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM Servicios WHERE id_servicio = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    ServicioDTO servicio = new ServicioDTO();
                    servicio.setIdServicio(rs.getInt("id_servicio"));
                    servicio.setNombre(rs.getString("nombre"));
                    servicio.setDescripcion(rs.getString("descripcion"));
                    servicio.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    servicio.setActivo(rs.getBoolean("activo"));
                    servicio.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return servicio;
                }
            }
        }
        return null;
    }

    public void actualizar(ServicioDTO servicio) throws SQLException {
        String sql = "UPDATE Servicios SET nombre = ?, descripcion = ?, precio_unitario = ?, activo = ? WHERE id_servicio = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, servicio.getNombre());
            stmt.setString(2, servicio.getDescripcion());
            stmt.setDouble(3, servicio.getPrecioUnitario());
            stmt.setBoolean(4, servicio.isActivo());
            stmt.setInt(5, servicio.getIdServicio());
            stmt.executeUpdate();
        }
    }

    public boolean eliminar(int id) throws SQLException {
        String sql = "DELETE FROM Servicios WHERE id_servicio = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public long contarActivos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Servicios WHERE activo = 1";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    public int contarTotal() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Servicios";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
