package DAO;

import Model.TarifaEspecialDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TarifaEspecialDAO {
    private Connection connection;

    public TarifaEspecialDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertar(TarifaEspecialDTO t) throws SQLException {
        String sql = "INSERT INTO TarifasEspeciales (id_tipo_habitacion, nombre, fecha_inicio, fecha_fin, precio_especial, tipo_tarifa, activo, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, t.getIdTipoHabitacion());
            ps.setString(2, t.getNombre());
            ps.setDate(3, Date.valueOf(t.getFechaInicio()));
            ps.setDate(4, Date.valueOf(t.getFechaFin()));
            ps.setDouble(5, t.getPrecioEspecial());
            ps.setString(6, t.getTipoTarifa());
            ps.setBoolean(7, t.isActivo());
            ps.setTimestamp(8, Timestamp.valueOf(t.getCreatedAt()));
            ps.executeUpdate();
        }
    }

    public void actualizar(TarifaEspecialDTO t) throws SQLException {
        String sql = "UPDATE TarifasEspeciales SET id_tipo_habitacion = ?, nombre = ?, fecha_inicio = ?, fecha_fin = ?, precio_especial = ?, tipo_tarifa = ?, activo = ? WHERE id_tarifa = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, t.getIdTipoHabitacion());
            ps.setString(2, t.getNombre());
            ps.setDate(3, Date.valueOf(t.getFechaInicio()));
            ps.setDate(4, Date.valueOf(t.getFechaFin()));
            ps.setDouble(5, t.getPrecioEspecial());
            ps.setString(6, t.getTipoTarifa());
            ps.setBoolean(7, t.isActivo());
            ps.setInt(8, t.getIdTarifa());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM TarifasEspeciales WHERE id_tarifa = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public TarifaEspecialDTO obtenerPorId(int id) throws SQLException {
        String sql = "SELECT t.*, th.nombre AS nombre_tipo_habitacion " +
                     "FROM TarifasEspeciales t " +
                     "JOIN TipoHabitacion th ON t.id_tipo_habitacion = th.id_tipo " +
                     "WHERE id_tarifa = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                TarifaEspecialDTO t = new TarifaEspecialDTO();
                t.setIdTarifa(rs.getInt("id_tarifa"));
                t.setIdTipoHabitacion(rs.getInt("id_tipo_habitacion"));
                t.setNombreTipoHabitacion(rs.getString("nombre_tipo_habitacion"));
                t.setNombre(rs.getString("nombre"));
                t.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                t.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                t.setPrecioEspecial(rs.getDouble("precio_especial"));
                t.setTipoTarifa(rs.getString("tipo_tarifa"));
                t.setActivo(rs.getBoolean("activo"));
                t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return t;
            }
        }
        return null;
    }

    public List<TarifaEspecialDTO> listarTodos() throws SQLException {
        List<TarifaEspecialDTO> lista = new ArrayList<>();
        String sql = "SELECT t.*, th.nombre AS nombre_tipo_habitacion " +
                     "FROM TarifasEspeciales t " +
                     "JOIN TipoHabitacion th ON t.id_tipo_habitacion = th.id_tipo " +
                     "ORDER BY t.fecha_inicio ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TarifaEspecialDTO t = new TarifaEspecialDTO();
                t.setIdTarifa(rs.getInt("id_tarifa"));
                t.setIdTipoHabitacion(rs.getInt("id_tipo_habitacion"));
                t.setNombreTipoHabitacion(rs.getString("nombre_tipo_habitacion"));
                t.setNombre(rs.getString("nombre"));
                t.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
                t.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
                t.setPrecioEspecial(rs.getDouble("precio_especial"));
                t.setTipoTarifa(rs.getString("tipo_tarifa"));
                t.setActivo(rs.getBoolean("activo"));
                t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                lista.add(t);
            }
        }
        return lista;
    }
}