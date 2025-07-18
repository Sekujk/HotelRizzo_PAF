package DAO;

import Model.HabitacionDTO;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDAO {

    private Connection connection;

    public HabitacionDAO(Connection connection) {
        this.connection = connection;
    }

    public void insertar(HabitacionDTO h) throws SQLException {
        String sql = "INSERT INTO Habitaciones (numero, piso, id_tipo, estado, observaciones, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, h.getNumero());
            ps.setInt(2, h.getPiso());
            ps.setInt(3, h.getIdTipo());
            ps.setString(4, h.getEstado());
            ps.setString(5, h.getObservaciones());
            ps.setTimestamp(6, Timestamp.valueOf(h.getCreatedAt()));
            ps.executeUpdate();
        }
    }

    public boolean actualizar(HabitacionDTO h) throws SQLException {
        // Validar que no se editen habitaciones ocupadas o en mantenimiento
        String sqlConsulta = "SELECT estado FROM Habitaciones WHERE id_habitacion = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlConsulta)) {
            ps.setInt(1, h.getIdHabitacion());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String estadoActual = rs.getString("estado");
                if ("Ocupada".equalsIgnoreCase(estadoActual) || "Mantenimiento".equalsIgnoreCase(estadoActual)) {
                    return false;
                }
            } else {
                return false;
            }
        }

        String sqlUpdate = "UPDATE Habitaciones SET numero = ?, piso = ?, id_tipo = ?, observaciones = ? WHERE id_habitacion = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlUpdate)) {
            ps.setString(1, h.getNumero());
            ps.setInt(2, h.getPiso());
            ps.setInt(3, h.getIdTipo());
            ps.setString(4, h.getObservaciones());
            ps.setInt(5, h.getIdHabitacion());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarDefinitivo(int idHabitacion) throws SQLException {
        String sql = "DELETE FROM Habitaciones WHERE id_habitacion = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idHabitacion);
            return stmt.executeUpdate() > 0;
        }
    }

    public HabitacionDTO obtenerPorId(int id) throws SQLException {
        String sql = "SELECT h.*, t.nombre AS nombre_tipo FROM Habitaciones h "
                + "JOIN TipoHabitacion t ON h.id_tipo = t.id_tipo WHERE id_habitacion = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                HabitacionDTO h = new HabitacionDTO();
                h.setIdHabitacion(rs.getInt("id_habitacion"));
                h.setNumero(rs.getString("numero"));
                h.setPiso(rs.getInt("piso"));
                h.setIdTipo(rs.getInt("id_tipo"));
                h.setNombreTipo(rs.getString("nombre_tipo"));
                h.setEstado(rs.getString("estado"));
                h.setObservaciones(rs.getString("observaciones"));
                h.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return h;
            }
        }
        return null;
    }

    public List<HabitacionDTO> listarTodos() throws SQLException {
        List<HabitacionDTO> lista = new ArrayList<>();
        String sql = "SELECT h.*, t.nombre AS nombre_tipo FROM Habitaciones h "
                + "JOIN TipoHabitacion t ON h.id_tipo = t.id_tipo ORDER BY h.numero ASC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HabitacionDTO h = new HabitacionDTO();
                h.setIdHabitacion(rs.getInt("id_habitacion"));
                h.setNumero(rs.getString("numero"));
                h.setPiso(rs.getInt("piso"));
                h.setIdTipo(rs.getInt("id_tipo"));
                h.setNombreTipo(rs.getString("nombre_tipo"));
                h.setEstado(rs.getString("estado"));
                h.setObservaciones(rs.getString("observaciones"));
                h.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                lista.add(h);
            }
        }
        return lista;
    }

    /**
     * ✅ MÉTODO CORREGIDO: Lista habitaciones disponibles para un rango de
     * fechas específico. Ahora maneja correctamente las fechas consecutivas y
     * la lógica de superposición.
     */
    public List<HabitacionDTO> listarDisponibles(LocalDate entrada, LocalDate salida) {
        List<HabitacionDTO> disponibles = new ArrayList<>();

        String sql = """
            SELECT h.*, t.nombre AS nombre_tipo 
            FROM Habitaciones h
            JOIN TipoHabitacion t ON h.id_tipo = t.id_tipo
            WHERE h.estado = 'Disponible'
              AND h.activo = 1
              AND NOT EXISTS (
                  SELECT 1 FROM ReservaHabitaciones rh
                  JOIN Reservas r ON rh.id_reserva = r.id_reserva
                  WHERE rh.id_habitacion = h.id_habitacion
                    AND r.estado IN ('En proceso', 'Pendiente', 'Confirmada', 'CheckIn')
                    AND (
                         -- ✅ CORREGIDO: Detecta superposición real de fechas
                         (rh.fecha_entrada < ? AND rh.fecha_salida > ?) OR  -- La reserva existente envuelve nuestra entrada
                         (rh.fecha_entrada >= ? AND rh.fecha_entrada < ?)   -- La reserva existente empieza dentro de nuestro rango
                    )
              )
            ORDER BY h.numero
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // ✅ Parámetros corregidos para detectar superposición correctamente
            stmt.setDate(1, Date.valueOf(salida));   // Para condición: reserva empieza antes de nuestra salida
            stmt.setDate(2, Date.valueOf(entrada));  // Para condición: reserva termina después de nuestra entrada
            stmt.setDate(3, Date.valueOf(entrada));  // Para condición: reserva empieza en/después de nuestra entrada
            stmt.setDate(4, Date.valueOf(salida));   // Para condición: reserva empieza antes de nuestra salida

            System.out.println("🔍 CONSULTANDO DISPONIBILIDAD:");
            System.out.println("📅 Rango solicitado: " + entrada + " → " + salida);
            System.out.println("📝 Excluiremos reservas que:");
            System.out.println("   - Empiecen antes del " + salida + " Y terminen después del " + entrada);
            System.out.println("   - O empiecen entre " + entrada + " y " + salida);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                HabitacionDTO h = new HabitacionDTO();
                h.setIdHabitacion(rs.getInt("id_habitacion"));
                h.setNumero(rs.getString("numero"));
                h.setPiso(rs.getInt("piso"));
                h.setIdTipo(rs.getInt("id_tipo"));
                h.setNombreTipo(rs.getString("nombre_tipo"));
                h.setEstado(rs.getString("estado"));
                h.setObservaciones(rs.getString("observaciones"));
                h.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                disponibles.add(h);

                System.out.println("✅ Habitación disponible: " + h.getNumero() + " (" + h.getNombreTipo() + ")");
            }

            System.out.println("📊 Total habitaciones disponibles: " + disponibles.size());

            // ✅ DEBUGGING: Mostrar por qué algunas habitaciones no están disponibles
            if (disponibles.size() < 5) { // Si hay pocas disponibles, mostrar debug
                mostrarHabitacionesNoDisponibles(entrada, salida);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar disponibilidad: " + e.getMessage());
            e.printStackTrace();
        }
        return disponibles;
    }

    /**
     * ✅ MÉTODO DE DEBUGGING: Muestra por qué las habitaciones no están
     * disponibles.
     */
    private void mostrarHabitacionesNoDisponibles(LocalDate entrada, LocalDate salida) {
        String sql = """
            SELECT h.numero, h.estado, rh.fecha_entrada, rh.fecha_salida, r.numero_reserva, r.estado as estado_reserva
            FROM Habitaciones h
            LEFT JOIN ReservaHabitaciones rh ON h.id_habitacion = rh.id_habitacion
            LEFT JOIN Reservas r ON rh.id_reserva = r.id_reserva
            WHERE h.activo = 1
              AND (
                  h.estado != 'Disponible' 
                  OR (
                      r.estado IN ('En proceso', 'Pendiente', 'Confirmada', 'CheckIn')
                      AND (
                          (rh.fecha_entrada < ? AND rh.fecha_salida > ?) OR
                          (rh.fecha_entrada >= ? AND rh.fecha_entrada < ?)
                      )
                  )
              )
            ORDER BY h.numero
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(salida));
            ps.setDate(2, Date.valueOf(entrada));
            ps.setDate(3, Date.valueOf(entrada));
            ps.setDate(4, Date.valueOf(salida));

            ResultSet rs = ps.executeQuery();
            System.out.println("\n❌ HABITACIONES NO DISPONIBLES Y MOTIVOS:");

            while (rs.next()) {
                String numero = rs.getString("numero");
                String estado = rs.getString("estado");
                Date fechaEntrada = rs.getDate("fecha_entrada");
                Date fechaSalida = rs.getDate("fecha_salida");
                String numeroReserva = rs.getString("numero_reserva");
                String estadoReserva = rs.getString("estado_reserva");

                if (!"Disponible".equals(estado)) {
                    System.out.println("   🏠 " + numero + " - Estado: " + estado);
                } else if (numeroReserva != null) {
                    System.out.println("   🏠 " + numero + " - Reservada: " + numeroReserva
                            + " (" + fechaEntrada + " → " + fechaSalida + ") Estado: " + estadoReserva);
                }
            }
            System.out.println("   ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        } catch (SQLException e) {
            System.out.println("❌ Error en debugging: " + e.getMessage());
        }
    }

    /**
     * ✅ MÉTODO MEJORADO: Muestra todas las reservas que afectan una habitación
     * específica. Útil para debugging y verificar por qué una habitación no
     * está disponible.
     */
    public void mostrarReservasDeHabitacion(int idHabitacion, LocalDate desde, LocalDate hasta) {
        String sql = """
            SELECT r.numero_reserva, rh.fecha_entrada, rh.fecha_salida, r.estado
            FROM ReservaHabitaciones rh
            JOIN Reservas r ON rh.id_reserva = r.id_reserva
            WHERE rh.id_habitacion = ?
              AND r.estado IN ('En proceso', 'Pendiente', 'Confirmada', 'CheckIn')
              AND (
                   (rh.fecha_entrada < ? AND rh.fecha_salida > ?) OR
                   (rh.fecha_entrada >= ? AND rh.fecha_entrada < ?)
              )
            ORDER BY rh.fecha_entrada
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ps.setDate(2, Date.valueOf(hasta));
            ps.setDate(3, Date.valueOf(desde));
            ps.setDate(4, Date.valueOf(desde));
            ps.setDate(5, Date.valueOf(hasta));

            ResultSet rs = ps.executeQuery();
            System.out.println("🔍 Reservas que afectan habitación " + idHabitacion + ":");

            boolean tieneReservas = false;
            while (rs.next()) {
                tieneReservas = true;
                System.out.println("   📋 " + rs.getString("numero_reserva")
                        + " | " + rs.getDate("fecha_entrada")
                        + " → " + rs.getDate("fecha_salida")
                        + " | Estado: " + rs.getString("estado"));
            }

            if (!tieneReservas) {
                System.out.println("   ✅ No hay reservas que conflicten");
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar reservas de habitación: " + e.getMessage());
        }
    }

    /**
     * ✅ MÉTODO MEJORADO: Cambia el estado de una habitación con validaciones.
     */
    public boolean cambiarEstado(int idHabitacion, String nuevoEstado) throws SQLException {
        // Validaciones de estado
        String sqlConsulta = "SELECT estado FROM Habitaciones WHERE id_habitacion = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlConsulta)) {
            ps.setInt(1, idHabitacion);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String estadoActual = rs.getString("estado");

                // Validar transiciones válidas
                if ("Ocupada".equalsIgnoreCase(estadoActual) && "Mantenimiento".equalsIgnoreCase(nuevoEstado)) {
                    System.out.println("❌ No se puede cambiar habitación ocupada a mantenimiento");
                    return false;
                }

                // Log del cambio
                if (!estadoActual.equalsIgnoreCase(nuevoEstado)) {
                    System.out.println("🔄 Habitación " + idHabitacion + ": " + estadoActual + " → " + nuevoEstado);
                }
            } else {
                System.out.println("❌ Habitación " + idHabitacion + " no encontrada");
                return false;
            }
        }

        String sqlUpdate = "UPDATE Habitaciones SET estado = ? WHERE id_habitacion = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlUpdate)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idHabitacion);
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("✅ Estado actualizado correctamente");
                return true;
            } else {
                System.out.println("❌ No se pudo actualizar el estado");
                return false;
            }
        }
    }

    /**
     * ✅ MÉTODO ADICIONAL: Obtiene estadísticas de ocupación.
     */
    public void mostrarEstadisticasOcupacion() throws SQLException {
        String sql = """
            SELECT 
                estado,
                COUNT(*) as cantidad,
                ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Habitaciones WHERE activo = 1), 1) as porcentaje
            FROM Habitaciones 
            WHERE activo = 1
            GROUP BY estado
            ORDER BY cantidad DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            System.out.println("📊 ESTADÍSTICAS DE OCUPACIÓN:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            while (rs.next()) {
                String estado = rs.getString("estado");
                int cantidad = rs.getInt("cantidad");
                double porcentaje = rs.getDouble("porcentaje");

                String emoji = switch (estado.toLowerCase()) {
                    case "disponible" ->
                        "🟢";
                    case "ocupada" ->
                        "🔴";
                    case "mantenimiento" ->
                        "🟡";
                    case "limpieza" ->
                        "🔵";
                    default ->
                        "⚪";
                };

                System.out.println(emoji + " " + estado + ": " + cantidad + " (" + porcentaje + "%)");
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }


}
