package DAO;

import Model.ReservaHabitacionDTO;
import Utils.Conexion;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ReservaHabitacionDAO {
    private Connection connection;

    public ReservaHabitacionDAO(Connection connection) {
        this.connection = connection;
    }

    // Constructor alternativo que obtiene conexión automáticamente
    public ReservaHabitacionDAO() {
        this.connection = Conexion.getConnection();
    }

    /**
     * ✅ INSERTAR NUEVA RESERVA DE HABITACIÓN
     */
    public boolean insertar(ReservaHabitacionDTO reservaHab) throws SQLException {
        String sql = """
            INSERT INTO ReservaHabitaciones (id_reserva, id_habitacion, fecha_entrada, 
                                           fecha_salida, precio_noche, observaciones)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservaHab.getIdReserva());
            ps.setInt(2, reservaHab.getIdHabitacion());
            ps.setDate(3, Date.valueOf(reservaHab.getFechaEntrada()));
            ps.setDate(4, Date.valueOf(reservaHab.getFechaSalida()));
            ps.setBigDecimal(5, reservaHab.getPrecioNoche());
            ps.setString(6, reservaHab.getObservaciones());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    reservaHab.setIdDetalle(rs.getInt(1));
                }
                System.out.println("✅ Habitación agregada a reserva: " + reservaHab.getNumeroHabitacion());
                return true;
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar reserva de habitación: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    /**
     * ✅ INSERTAR MÚLTIPLES HABITACIONES A UNA RESERVA
     */
    public boolean insertarMultiples(List<ReservaHabitacionDTO> reservasHab) throws SQLException {
        String sql = """
            INSERT INTO ReservaHabitaciones (id_reserva, id_habitacion, fecha_entrada, 
                                           fecha_salida, precio_noche, observaciones)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try {
            connection.setAutoCommit(false); // Iniciar transacción
            
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (ReservaHabitacionDTO reservaHab : reservasHab) {
                    ps.setInt(1, reservaHab.getIdReserva());
                    ps.setInt(2, reservaHab.getIdHabitacion());
                    ps.setDate(3, Date.valueOf(reservaHab.getFechaEntrada()));
                    ps.setDate(4, Date.valueOf(reservaHab.getFechaSalida()));
                    ps.setBigDecimal(5, reservaHab.getPrecioNoche());
                    ps.setString(6, reservaHab.getObservaciones());
                    ps.addBatch();
                }
                
                int[] resultados = ps.executeBatch();
                connection.commit(); // Confirmar transacción
                
                System.out.println("✅ " + resultados.length + " habitaciones agregadas a la reserva");
                return true;
            }
            
        } catch (SQLException e) {
            connection.rollback(); // Revertir en caso de error
            System.out.println("❌ Error al insertar múltiples habitaciones: " + e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true); // Restaurar auto-commit
        }
    }

    /**
     * ✅ ACTUALIZAR RESERVA DE HABITACIÓN
     */
    public boolean actualizar(ReservaHabitacionDTO reservaHab) throws SQLException {
        String sql = """
            UPDATE ReservaHabitaciones SET 
                fecha_entrada = ?, fecha_salida = ?, precio_noche = ?, observaciones = ?
            WHERE id_detalle = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(reservaHab.getFechaEntrada()));
            ps.setDate(2, Date.valueOf(reservaHab.getFechaSalida()));
            ps.setBigDecimal(3, reservaHab.getPrecioNoche());
            ps.setString(4, reservaHab.getObservaciones());
            ps.setInt(5, reservaHab.getIdDetalle());
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Reserva de habitación actualizada exitosamente");
                return true;
            }
        }
        return false;
    }

    /**
     * ✅ ELIMINAR HABITACIÓN DE UNA RESERVA
     */
    public boolean eliminar(int idDetalle) throws SQLException {
        String sql = "DELETE FROM ReservaHabitaciones WHERE id_detalle = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idDetalle);
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Habitación eliminada de la reserva");
                return true;
            }
        }
        return false;
    }

    /**
     * ✅ OBTENER POR ID
     */
    public ReservaHabitacionDTO obtenerPorId(int idDetalle) throws SQLException {
        String sql = """
            SELECT rh.*, h.numero AS numero_habitacion, t.nombre AS nombre_tipo_habitacion
            FROM ReservaHabitaciones rh
            JOIN Habitaciones h ON rh.id_habitacion = h.id_habitacion
            JOIN TipoHabitacion t ON h.id_tipo = t.id_tipo
            WHERE rh.id_detalle = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idDetalle);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapearResultSetAReservaHabitacion(rs);
            }
        }
        return null;
    }

    /**
     * ✅ LISTAR HABITACIONES DE UNA RESERVA
     */
    public List<ReservaHabitacionDTO> listarPorReserva(int idReserva) throws SQLException {
        List<ReservaHabitacionDTO> lista = new ArrayList<>();
        String sql = """
            SELECT rh.*, h.numero AS numero_habitacion, t.nombre AS nombre_tipo_habitacion
            FROM ReservaHabitaciones rh
            JOIN Habitaciones h ON rh.id_habitacion = h.id_habitacion
            JOIN TipoHabitacion t ON h.id_tipo = t.id_tipo
            WHERE rh.id_reserva = ?
            ORDER BY h.numero ASC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAReservaHabitacion(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ LISTAR TODAS LAS RESERVAS DE HABITACIONES
     */
    public List<ReservaHabitacionDTO> listarTodos() throws SQLException {
        List<ReservaHabitacionDTO> lista = new ArrayList<>();
        String sql = """
            SELECT rh.*, h.numero AS numero_habitacion, t.nombre AS nombre_tipo_habitacion,
                   r.numero_reserva
            FROM ReservaHabitaciones rh
            JOIN Habitaciones h ON rh.id_habitacion = h.id_habitacion
            JOIN TipoHabitacion t ON h.id_tipo = t.id_tipo
            JOIN Reservas r ON rh.id_reserva = r.id_reserva
            ORDER BY rh.fecha_entrada DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ReservaHabitacionDTO reservaHab = mapearResultSetAReservaHabitacion(rs);
                // Agregar número de reserva si está disponible
                if (rs.getString("numero_reserva") != null) {
                    // Podrías agregar este campo al DTO si lo necesitas
                }
                lista.add(reservaHab);
            }
        }
        return lista;
    }

    /**
     * ✅ LISTAR HABITACIONES OCUPADAS EN UN RANGO DE FECHAS
     */
    public List<ReservaHabitacionDTO> listarOcupadasEnRango(LocalDate entrada, LocalDate salida) throws SQLException {
        List<ReservaHabitacionDTO> lista = new ArrayList<>();
        String sql = """
            SELECT rh.*, h.numero AS numero_habitacion, t.nombre AS nombre_tipo_habitacion,
                   r.numero_reserva, r.estado
            FROM ReservaHabitaciones rh
            JOIN Habitaciones h ON rh.id_habitacion = h.id_habitacion
            JOIN TipoHabitacion t ON h.id_tipo = t.id_tipo
            JOIN Reservas r ON rh.id_reserva = r.id_reserva
            WHERE r.estado IN ('Pendiente', 'Confirmada', 'CheckIn')
            AND (
                (rh.fecha_entrada < ? AND rh.fecha_salida > ?) OR
                (rh.fecha_entrada >= ? AND rh.fecha_entrada < ?)
            )
            ORDER BY h.numero ASC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(salida));
            ps.setDate(2, Date.valueOf(entrada));
            ps.setDate(3, Date.valueOf(entrada));
            ps.setDate(4, Date.valueOf(salida));
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAReservaHabitacion(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ OBTENER TOTAL DE NOCHES POR RESERVA
     */
    public int obtenerTotalNochesPorReserva(int idReserva) throws SQLException {
        String sql = """
            SELECT SUM(DATEDIFF(day, fecha_entrada, fecha_salida)) as total_noches
            FROM ReservaHabitaciones 
            WHERE id_reserva = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total_noches");
            }
        }
        return 0;
    }

    /**
     * ✅ OBTENER SUBTOTAL POR RESERVA
     */
    public BigDecimal obtenerSubtotalPorReserva(int idReserva) throws SQLException {
        String sql = """
            SELECT SUM(DATEDIFF(day, fecha_entrada, fecha_salida) * precio_noche) as subtotal
            FROM ReservaHabitaciones 
            WHERE id_reserva = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal subtotal = rs.getBigDecimal("subtotal");
                return subtotal != null ? subtotal : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * ✅ VERIFICAR SI UNA HABITACIÓN ESTÁ OCUPADA EN UN RANGO
     */
    public boolean estaOcupadaEnRango(int idHabitacion, LocalDate entrada, LocalDate salida, int excludeReserva) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM ReservaHabitaciones rh
            JOIN Reservas r ON rh.id_reserva = r.id_reserva
            WHERE rh.id_habitacion = ? 
            AND r.id_reserva != ?
            AND r.estado IN ('Pendiente', 'Confirmada', 'CheckIn')
            AND (
                (rh.fecha_entrada < ? AND rh.fecha_salida > ?) OR
                (rh.fecha_entrada >= ? AND rh.fecha_entrada < ?)
            )
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ps.setInt(2, excludeReserva);
            ps.setDate(3, Date.valueOf(salida));
            ps.setDate(4, Date.valueOf(entrada));
            ps.setDate(5, Date.valueOf(entrada));
            ps.setDate(6, Date.valueOf(salida));
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * ✅ OBTENER HABITACIONES MÁS RESERVADAS
     */
    public List<String> obtenerHabitacionesMasReservadas(int limite) throws SQLException {
        List<String> habitaciones = new ArrayList<>();
        String sql = """
            SELECT TOP (?) h.numero, COUNT(*) as total_reservas
            FROM ReservaHabitaciones rh
            JOIN Habitaciones h ON rh.id_habitacion = h.id_habitacion
            JOIN Reservas r ON rh.id_reserva = r.id_reserva
            WHERE r.estado NOT IN ('Cancelada')
            GROUP BY h.numero
            ORDER BY total_reservas DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limite);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String info = rs.getString("numero") + " (" + rs.getInt("total_reservas") + " reservas)";
                habitaciones.add(info);
            }
        }
        return habitaciones;
    }

    /**
     * ✅ MAPEAR RESULTSET A DTO
     */
    private ReservaHabitacionDTO mapearResultSetAReservaHabitacion(ResultSet rs) throws SQLException {
        ReservaHabitacionDTO reservaHab = new ReservaHabitacionDTO();
        
        reservaHab.setIdDetalle(rs.getInt("id_detalle"));
        reservaHab.setIdReserva(rs.getInt("id_reserva"));
        reservaHab.setIdHabitacion(rs.getInt("id_habitacion"));
        reservaHab.setNumeroHabitacion(rs.getString("numero_habitacion"));
        reservaHab.setNombreTipoHabitacion(rs.getString("nombre_tipo_habitacion"));
        
        reservaHab.setFechaEntrada(rs.getDate("fecha_entrada").toLocalDate());
        reservaHab.setFechaSalida(rs.getDate("fecha_salida").toLocalDate());
        reservaHab.setPrecioNoche(rs.getBigDecimal("precio_noche"));
        
        // Los campos calculados se establecen automáticamente por el getter
        reservaHab.setTotalNoches(rs.getInt("total_noches"));
        reservaHab.setSubtotal(rs.getBigDecimal("subtotal"));
        
        reservaHab.setObservaciones(rs.getString("observaciones"));
        
        return reservaHab;
    }

    /**
     * ✅ MOSTRAR ESTADÍSTICAS DE OCUPACIÓN POR TIPO
     */
    public void mostrarEstadisticasOcupacion() throws SQLException {
        String sql = """
            SELECT t.nombre, COUNT(*) as reservas, 
                   AVG(CAST(DATEDIFF(day, rh.fecha_entrada, rh.fecha_salida) AS FLOAT)) as promedio_noches,
                   SUM(DATEDIFF(day, rh.fecha_entrada, rh.fecha_salida) * rh.precio_noche) as ingresos_totales
            FROM ReservaHabitaciones rh
            JOIN Habitaciones h ON rh.id_habitacion = h.id_habitacion
            JOIN TipoHabitacion t ON h.id_tipo = t.id_tipo
            JOIN Reservas r ON rh.id_reserva = r.id_reserva
            WHERE r.estado NOT IN ('Cancelada')
            GROUP BY t.nombre
            ORDER BY reservas DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            System.out.println("📊 ESTADÍSTICAS DE OCUPACIÓN POR TIPO:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%-15s %-10s %-15s %-15s%n", "Tipo", "Reservas", "Prom. Noches", "Ingresos");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            while (rs.next()) {
                String tipo = rs.getString("nombre");
                int reservas = rs.getInt("reservas");
                double promedioNoches = rs.getDouble("promedio_noches");
                BigDecimal ingresos = rs.getBigDecimal("ingresos_totales");
                
                System.out.printf("%-15s %-10d %-15.1f S/. %-10.2f%n", 
                                tipo, reservas, promedioNoches, ingresos);
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }

    /**
     * ✅ CERRAR CONEXIÓN
     */
    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
        }
    }
}