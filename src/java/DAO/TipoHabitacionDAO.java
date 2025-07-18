package DAO;

import Model.TipoHabitacionDTO;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TipoHabitacionDAO {
    private Connection connection;

    public TipoHabitacionDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertar(TipoHabitacionDTO t) throws SQLException {
        String sql = "INSERT INTO TipoHabitacion (nombre, descripcion, precio_base, capacidad_personas, activo, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getDescripcion());
            ps.setDouble(3, t.getPrecioBase());
            ps.setInt(4, t.getCapacidadPersonas());
            ps.setBoolean(5, t.isActivo());
            ps.setTimestamp(6, Timestamp.valueOf(t.getCreatedAt()));
            ps.executeUpdate();
        }
    }

    public void actualizar(TipoHabitacionDTO t) throws SQLException {
        String sql = "UPDATE TipoHabitacion SET nombre = ?, descripcion = ?, precio_base = ?, capacidad_personas = ?, activo = ? WHERE id_tipo = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, t.getNombre());
            ps.setString(2, t.getDescripcion());
            ps.setDouble(3, t.getPrecioBase());
            ps.setInt(4, t.getCapacidadPersonas());
            ps.setBoolean(5, t.isActivo());
            ps.setInt(6, t.getIdTipo());
            ps.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM TipoHabitacion WHERE id_tipo = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public TipoHabitacionDTO obtenerPorId(int id) throws SQLException {
        String sql = "SELECT * FROM TipoHabitacion WHERE id_tipo = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                TipoHabitacionDTO t = new TipoHabitacionDTO();
                t.setIdTipo(rs.getInt("id_tipo"));
                t.setNombre(rs.getString("nombre"));
                t.setDescripcion(rs.getString("descripcion"));
                t.setPrecioBase(rs.getDouble("precio_base"));
                t.setCapacidadPersonas(rs.getInt("capacidad_personas"));
                t.setActivo(rs.getBoolean("activo"));
                t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return t;
            }
        }
        return null;
    }

    public List<TipoHabitacionDTO> listar() throws SQLException {
        List<TipoHabitacionDTO> lista = new ArrayList<>();
        String sql = "SELECT * FROM TipoHabitacion";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TipoHabitacionDTO t = new TipoHabitacionDTO();
                t.setIdTipo(rs.getInt("id_tipo"));
                t.setNombre(rs.getString("nombre"));
                t.setDescripcion(rs.getString("descripcion"));
                t.setPrecioBase(rs.getDouble("precio_base"));
                t.setCapacidadPersonas(rs.getInt("capacidad_personas"));
                t.setActivo(rs.getBoolean("activo"));
                t.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                lista.add(t);
            }
        }
        return lista;
    }

    // =============================================
    // 🆕 MÉTODOS PARA TARIFAS ESPECIALES
    // =============================================

    /**
     * Obtiene el precio correcto para un tipo de habitación en una fecha específica.
     * Si existe una tarifa especial activa para esa fecha, la devuelve.
     * Si no, devuelve el precio base.
     * 
     * @param idTipo ID del tipo de habitación
     * @param fecha Fecha para verificar tarifas especiales
     * @return Precio aplicable (especial o base)
     * @throws SQLException Si hay error en la consulta
     */
    public double obtenerPrecioConTarifa(int idTipo, LocalDate fecha) throws SQLException {
        String sql = """
            SELECT 
                COALESCE(te.precio_especial, th.precio_base) as precio_aplicar
            FROM TipoHabitacion th
            LEFT JOIN TarifasEspeciales te ON th.id_tipo = te.id_tipo_habitacion
                AND te.activo = 1
                AND ? BETWEEN te.fecha_inicio AND te.fecha_fin
            WHERE th.id_tipo = ? AND th.activo = 1
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setInt(2, idTipo);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("precio_aplicar");
            }
            
            // Si no encuentra el tipo de habitación, devolver 0
            return 0.0;
        }
    }

    /**
     * Verifica si hay una tarifa especial activa para un tipo de habitación en una fecha.
     * 
     * @param idTipo ID del tipo de habitación
     * @param fecha Fecha a verificar
     * @return true si hay tarifa especial, false si usa precio base
     * @throws SQLException Si hay error en la consulta
     */
    public boolean tieneTarifaEspecial(int idTipo, LocalDate fecha) throws SQLException {
        String sql = """
            SELECT COUNT(*) as total
            FROM TarifasEspeciales 
            WHERE id_tipo_habitacion = ? 
                AND activo = 1
                AND ? BETWEEN fecha_inicio AND fecha_fin
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idTipo);
            ps.setDate(2, Date.valueOf(fecha));
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
            return false;
        }
    }

    /**
     * Obtiene información detallada sobre la tarifa aplicable (base o especial).
     * 
     * @param idTipo ID del tipo de habitación
     * @param fecha Fecha para verificar tarifas
     * @return Array con [precio, nombre_tarifa, tipo_tarifa] o [precio_base, "Base", "Base"]
     * @throws SQLException Si hay error en la consulta
     */
    public String[] obtenerInfoTarifa(int idTipo, LocalDate fecha) throws SQLException {
        String sql = """
            SELECT 
                COALESCE(te.precio_especial, th.precio_base) as precio_aplicar,
                COALESCE(te.nombre, 'Precio Base') as nombre_tarifa,
                COALESCE(te.tipo_tarifa, 'Base') as tipo_tarifa
            FROM TipoHabitacion th
            LEFT JOIN TarifasEspeciales te ON th.id_tipo = te.id_tipo_habitacion
                AND te.activo = 1
                AND ? BETWEEN te.fecha_inicio AND te.fecha_fin
            WHERE th.id_tipo = ? AND th.activo = 1
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setInt(2, idTipo);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                    String.valueOf(rs.getDouble("precio_aplicar")),
                    rs.getString("nombre_tarifa"),
                    rs.getString("tipo_tarifa")
                };
            }
            
            // Si no encuentra nada, devolver valores por defecto
            return new String[]{"0.0", "No encontrado", "Error"};
        }
    }

    /**
     * Obtiene el mejor precio disponible en un rango de fechas.
     * Útil para mostrar precios "desde X soles" en búsquedas.
     * 
     * @param idTipo ID del tipo de habitación
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return El precio más bajo encontrado en el rango
     * @throws SQLException Si hay error en la consulta
     */
    public double obtenerMejorPrecioEnRango(int idTipo, LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        double precioMinimo = Double.MAX_VALUE;
        
        // Iterar día por día para encontrar el precio más bajo
        LocalDate fechaActual = fechaInicio;
        while (!fechaActual.isAfter(fechaFin)) {
            double precioDelDia = obtenerPrecioConTarifa(idTipo, fechaActual);
            if (precioDelDia > 0 && precioDelDia < precioMinimo) {
                precioMinimo = precioDelDia;
            }
            fechaActual = fechaActual.plusDays(1);
        }
        
        return precioMinimo == Double.MAX_VALUE ? 0.0 : precioMinimo;
    }
}