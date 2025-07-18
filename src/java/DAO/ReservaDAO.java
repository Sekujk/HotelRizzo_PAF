package DAO;

import Model.ReservaDTO;
import Utils.Conexion;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ReservaDAO {

    private Connection connection;

    public ReservaDAO(Connection connection) {
        this.connection = connection;
    }

    // Constructor alternativo que obtiene conexión automáticamente
    public ReservaDAO() {
        this.connection = Conexion.getConnection();
    }

    /**
     * ✅ INSERTAR NUEVA RESERVA - CORREGIDO
     */
    public boolean insertar(ReservaDTO reserva) throws SQLException {
        String sql = """
        INSERT INTO Reservas (numero_reserva, id_cliente, id_empleado, fecha_entrada, fecha_salida, 
                            num_huespedes, estado, subtotal, impuestos, monto_total, observaciones)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, reserva.getNumeroReserva()); // Incluir numero_reserva
            ps.setInt(2, reserva.getIdCliente());
            ps.setInt(3, reserva.getIdEmpleado());
            ps.setDate(4, Date.valueOf(reserva.getFechaEntrada()));
            ps.setDate(5, Date.valueOf(reserva.getFechaSalida()));
            ps.setInt(6, reserva.getNumHuespedes());
            ps.setString(7, reserva.getEstado());
            ps.setBigDecimal(8, reserva.getSubtotal());
            ps.setBigDecimal(9, reserva.getImpuestos());
            ps.setBigDecimal(10, reserva.getMontoTotal());
            ps.setString(11, reserva.getObservaciones());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    reserva.setIdReserva(rs.getInt(1));

                    // Obtener el numero_reserva generado por el trigger
                    String sqlNumero = "SELECT numero_reserva FROM Reservas WHERE id_reserva = ?";
                    try (PreparedStatement psNumero = connection.prepareStatement(sqlNumero)) {
                        psNumero.setInt(1, reserva.getIdReserva());
                        ResultSet rsNumero = psNumero.executeQuery();
                        if (rsNumero.next()) {
                            reserva.setNumeroReserva(rsNumero.getString("numero_reserva"));
                        }
                    }
                }
                System.out.println("✅ Reserva creada exitosamente: ID " + reserva.getIdReserva()
                        + ", Número: " + reserva.getNumeroReserva());
                return true;
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar reserva: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    /**
     * ✅ ACTUALIZAR RESERVA EXISTENTE
     */
    public boolean actualizar(ReservaDTO reserva) throws SQLException {
        // Validar que no se actualicen reservas en estados críticos
        String sqlConsulta = "SELECT estado FROM Reservas WHERE id_reserva = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlConsulta)) {
            ps.setInt(1, reserva.getIdReserva());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String estadoActual = rs.getString("estado");
                if ("CheckOut".equalsIgnoreCase(estadoActual) || "Cancelada".equalsIgnoreCase(estadoActual)) {
                    System.out.println("❌ No se puede modificar reserva en estado: " + estadoActual);
                    return false;
                }
            } else {
                System.out.println("❌ Reserva no encontrada: " + reserva.getIdReserva());
                return false;
            }
        }

        String sqlUpdate = """
            UPDATE Reservas SET 
                fecha_entrada = ?, fecha_salida = ?, num_huespedes = ?, 
                subtotal = ?, impuestos = ?, monto_total = ?, observaciones = ?
            WHERE id_reserva = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sqlUpdate)) {
            ps.setDate(1, Date.valueOf(reserva.getFechaEntrada()));
            ps.setDate(2, Date.valueOf(reserva.getFechaSalida()));
            ps.setInt(3, reserva.getNumHuespedes());
            ps.setBigDecimal(4, reserva.getSubtotal());
            ps.setBigDecimal(5, reserva.getImpuestos());
            ps.setBigDecimal(6, reserva.getMontoTotal());
            ps.setString(7, reserva.getObservaciones());
            ps.setInt(8, reserva.getIdReserva());

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Reserva actualizada exitosamente: " + reserva.getNumeroReserva());
                return true;
            }
        }
        return false;
    }

    /**
     * ✅ CAMBIAR ESTADO DE RESERVA
     */
    public boolean cambiarEstado(int idReserva, String nuevoEstado, String motivo) throws SQLException {
        String sql = "UPDATE Reservas SET estado = ?, motivo_cancelacion = ? WHERE id_reserva = ?";

        // Si es cancelación, agregar fecha
        if ("Cancelada".equalsIgnoreCase(nuevoEstado)) {
            sql = "UPDATE Reservas SET estado = ?, motivo_cancelacion = ?, fecha_cancelacion = ? WHERE id_reserva = ?";
        }

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setString(2, motivo);

            if ("Cancelada".equalsIgnoreCase(nuevoEstado)) {
                ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                ps.setInt(4, idReserva);
            } else {
                ps.setInt(3, idReserva);
            }

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Estado de reserva cambiado a: " + nuevoEstado);
                return true;
            }
        }
        return false;
    }

    /**
     * ✅ REALIZAR CHECK-IN
     */
    public boolean realizarCheckin(int idReserva, int idEmpleado) throws SQLException {
        String sql = """
            UPDATE Reservas SET 
                estado = 'CheckIn', 
                fecha_checkin = ?, 
                empleado_checkin = ?
            WHERE id_reserva = ? AND estado = 'Confirmada'
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, idEmpleado);
            ps.setInt(3, idReserva);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                // Cambiar estado de habitaciones a Ocupada
                actualizarEstadoHabitaciones(idReserva, "Ocupada");
                System.out.println("✅ Check-in realizado exitosamente");
                return true;
            }
        }
        return false;
    }

    /**
     * ✅ REALIZAR CHECK-OUT
     */
    public boolean realizarCheckout(int idReserva, int idEmpleado) throws SQLException {
        String sql = """
            UPDATE Reservas SET 
                estado = 'CheckOut', 
                fecha_checkout = ?, 
                empleado_checkout = ?
            WHERE id_reserva = ? AND estado = 'CheckIn'
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(2, idEmpleado);
            ps.setInt(3, idReserva);

            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                // Cambiar estado de habitaciones a Limpieza
                actualizarEstadoHabitaciones(idReserva, "Limpieza");
                System.out.println("✅ Check-out realizado exitosamente");
                return true;
            }
        }
        return false;
    }

    /**
     * ✅ OBTENER RESERVA POR ID
     */
    public ReservaDTO obtenerPorId(int idReserva) throws SQLException {
        String sql = """
            SELECT r.*, 
                   p.nombre + ' ' + p.apellido AS nombre_cliente,
                   e.nombre + ' ' + e.apellido AS nombre_empleado,
                   ec.nombre + ' ' + ec.apellido AS nombre_empleado_checkin,
                   eo.nombre + ' ' + eo.apellido AS nombre_empleado_checkout
            FROM Reservas r
            JOIN Clientes c ON r.id_cliente = c.id_cliente
            JOIN Personas p ON c.id_cliente = p.id_persona
            JOIN Empleados emp ON r.id_empleado = emp.id_empleado
            JOIN Personas e ON emp.id_empleado = e.id_persona
            LEFT JOIN Empleados empc ON r.empleado_checkin = empc.id_empleado
            LEFT JOIN Personas ec ON empc.id_empleado = ec.id_persona
            LEFT JOIN Empleados empo ON r.empleado_checkout = empo.id_empleado
            LEFT JOIN Personas eo ON empo.id_empleado = eo.id_persona
            WHERE r.id_reserva = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearResultSetAReserva(rs);
            }
        }
        return null;
    }

    /**
     * ✅ LISTAR TODAS LAS RESERVAS
     */
    public List<ReservaDTO> listarTodos() throws SQLException {
        List<ReservaDTO> reservas = new ArrayList<>();
        String sql = """
            SELECT r.*, 
                   p.nombre + ' ' + p.apellido AS nombre_cliente,
                   e.nombre + ' ' + e.apellido AS nombre_empleado,
                   ec.nombre + ' ' + ec.apellido AS nombre_empleado_checkin,
                   eo.nombre + ' ' + eo.apellido AS nombre_empleado_checkout
            FROM Reservas r
            JOIN Clientes c ON r.id_cliente = c.id_cliente
            JOIN Personas p ON c.id_cliente = p.id_persona
            JOIN Empleados emp ON r.id_empleado = emp.id_empleado
            JOIN Personas e ON emp.id_empleado = e.id_persona
            LEFT JOIN Empleados empc ON r.empleado_checkin = empc.id_empleado
            LEFT JOIN Personas ec ON empc.id_empleado = ec.id_persona
            LEFT JOIN Empleados empo ON r.empleado_checkout = empo.id_empleado
            LEFT JOIN Personas eo ON empo.id_empleado = eo.id_persona
            ORDER BY r.created_at DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservas.add(mapearResultSetAReserva(rs));
            }
        }
        return reservas;
    }

    /**
     * ✅ LISTAR RESERVAS POR ESTADO
     */
    public List<ReservaDTO> listarPorEstado(String estado) throws SQLException {
        List<ReservaDTO> reservas = new ArrayList<>();
        String sql = """
        SELECT r.*, 
               p.nombre + ' ' + p.apellido AS nombre_cliente,
               e.nombre + ' ' + e.apellido AS nombre_empleado,
               ec.nombre + ' ' + ec.apellido AS nombre_empleado_checkin,
               eo.nombre + ' ' + eo.apellido AS nombre_empleado_checkout
        FROM Reservas r
        JOIN Clientes c ON r.id_cliente = c.id_cliente
        JOIN Personas p ON c.id_cliente = p.id_persona
        JOIN Empleados emp ON r.id_empleado = emp.id_empleado
        JOIN Personas e ON emp.id_empleado = e.id_persona
        LEFT JOIN Empleados empc ON r.empleado_checkin = empc.id_empleado
        LEFT JOIN Personas ec ON empc.id_empleado = ec.id_persona
        LEFT JOIN Empleados empo ON r.empleado_checkout = empo.id_empleado
        LEFT JOIN Personas eo ON empo.id_empleado = eo.id_persona
        WHERE r.estado = ?
        ORDER BY r.fecha_entrada ASC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservas.add(mapearResultSetAReserva(rs));
            }
        }
        return reservas;
    }

    /**
     * ✅ LISTAR RESERVAS POR FECHA
     */
    public List<ReservaDTO> listarPorFecha(LocalDate fecha) throws SQLException {
        List<ReservaDTO> reservas = new ArrayList<>();
        String sql = """
        SELECT r.*, 
               p.nombre + ' ' + p.apellido AS nombre_cliente,
               e.nombre + ' ' + e.apellido AS nombre_empleado,
               ec.nombre + ' ' + ec.apellido AS nombre_empleado_checkin,
               eo.nombre + ' ' + eo.apellido AS nombre_empleado_checkout
        FROM Reservas r
        JOIN Clientes c ON r.id_cliente = c.id_cliente
        JOIN Personas p ON c.id_cliente = p.id_persona
        JOIN Empleados emp ON r.id_empleado = emp.id_empleado
        JOIN Personas e ON emp.id_empleado = e.id_persona
        LEFT JOIN Empleados empc ON r.empleado_checkin = empc.id_empleado
        LEFT JOIN Personas ec ON empc.id_empleado = ec.id_persona
        LEFT JOIN Empleados empo ON r.empleado_checkout = empo.id_empleado
        LEFT JOIN Personas eo ON empo.id_empleado = eo.id_persona
        WHERE ? BETWEEN r.fecha_entrada AND r.fecha_salida
        ORDER BY r.fecha_entrada ASC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservas.add(mapearResultSetAReserva(rs));
            }
        }
        return reservas;
    }

    /**
     * ✅ BUSCAR RESERVAS (por número, cliente, etc.)
     */
    public List<ReservaDTO> buscar(String criterio) throws SQLException {
        List<ReservaDTO> reservas = new ArrayList<>();
        String sql = """
            SELECT r.*, 
                   p.nombre + ' ' + p.apellido AS nombre_cliente,
                   e.nombre + ' ' + e.apellido AS nombre_empleado
            FROM Reservas r
            JOIN Clientes c ON r.id_cliente = c.id_cliente
            JOIN Personas p ON c.id_cliente = p.id_persona
            JOIN Empleados emp ON r.id_empleado = emp.id_empleado
            JOIN Personas e ON emp.id_empleado = e.id_persona
            WHERE r.numero_reserva LIKE ? 
               OR p.nombre LIKE ? 
               OR p.apellido LIKE ?
               OR p.dni LIKE ?
            ORDER BY r.created_at DESC
        """;

        String busqueda = "%" + criterio + "%";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ps.setString(3, busqueda);
            ps.setString(4, busqueda);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservas.add(mapearResultSetAReserva(rs));
            }
        }
        return reservas;
    }

    /**
     * ✅ OBTENER RESERVAS PARA CHECK-IN HOY
     */
    public List<ReservaDTO> obtenerCheckinHoy() throws SQLException {
        return listarPorFechaYEstado(LocalDate.now(), "Confirmada");
    }

    /**
     * ✅ OBTENER RESERVAS PARA CHECK-OUT HOY
     */
    public List<ReservaDTO> obtenerCheckoutHoy() throws SQLException {
        return listarPorFechaYEstado(LocalDate.now(), "CheckIn");
    }

    /**
     * ✅ LISTAR RESERVAS POR FECHA Y ESTADO
     */
    private List<ReservaDTO> listarPorFechaYEstado(LocalDate fecha, String estado) throws SQLException {
        List<ReservaDTO> reservas = new ArrayList<>();
        String sql = """
        SELECT r.*, 
               p.nombre + ' ' + p.apellido AS nombre_cliente,
               e.nombre + ' ' + e.apellido AS nombre_empleado,
               ec.nombre + ' ' + ec.apellido AS nombre_empleado_checkin,
               eo.nombre + ' ' + eo.apellido AS nombre_empleado_checkout
        FROM Reservas r
        JOIN Clientes c ON r.id_cliente = c.id_cliente
        JOIN Personas p ON c.id_cliente = p.id_persona
        JOIN Empleados emp ON r.id_empleado = emp.id_empleado
        JOIN Personas e ON emp.id_empleado = e.id_persona
        LEFT JOIN Empleados empc ON r.empleado_checkin = empc.id_empleado
        LEFT JOIN Personas ec ON empc.id_empleado = ec.id_persona
        LEFT JOIN Empleados empo ON r.empleado_checkout = empo.id_empleado
        LEFT JOIN Personas eo ON empo.id_empleado = eo.id_persona
        WHERE r.fecha_entrada = ? AND r.estado = ?
        ORDER BY r.created_at ASC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ps.setString(2, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservas.add(mapearResultSetAReserva(rs));
            }
        }
        return reservas;
    }

    /**
     * ✅ ACTUALIZAR ESTADOS DE HABITACIONES
     */
    private void actualizarEstadoHabitaciones(int idReserva, String nuevoEstado) throws SQLException {
        String sql = """
            UPDATE Habitaciones SET estado = ?
            WHERE id_habitacion IN (
                SELECT rh.id_habitacion 
                FROM ReservaHabitaciones rh 
                WHERE rh.id_reserva = ?
            )
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idReserva);
            ps.executeUpdate();
        }
    }

    /**
     * ✅ MAPEAR RESULTSET A DTO
     */
    private ReservaDTO mapearResultSetAReserva(ResultSet rs) throws SQLException {
        ReservaDTO reserva = new ReservaDTO();

        reserva.setIdReserva(rs.getInt("id_reserva"));
        reserva.setNumeroReserva(rs.getString("numero_reserva"));
        reserva.setIdCliente(rs.getInt("id_cliente"));
        reserva.setNombreCliente(rs.getString("nombre_cliente"));
        reserva.setIdEmpleado(rs.getInt("id_empleado"));
        reserva.setNombreEmpleado(rs.getString("nombre_empleado"));

        reserva.setFechaEntrada(rs.getDate("fecha_entrada").toLocalDate());
        reserva.setFechaSalida(rs.getDate("fecha_salida").toLocalDate());
        reserva.setNumHuespedes(rs.getInt("num_huespedes"));

        // Check-in y Check-out (pueden ser null)
        Timestamp checkin = rs.getTimestamp("fecha_checkin");
        if (checkin != null) {
            reserva.setFechaCheckin(checkin.toLocalDateTime());
        }

        Timestamp checkout = rs.getTimestamp("fecha_checkout");
        if (checkout != null) {
            reserva.setFechaCheckout(checkout.toLocalDateTime());
        }

        reserva.setEmpleadoCheckin((Integer) rs.getObject("empleado_checkin"));
        reserva.setEmpleadoCheckout((Integer) rs.getObject("empleado_checkout"));

        // ✅ VALIDACIÓN: Solo intentar obtener estos campos si existen en el ResultSet
        try {
            reserva.setNombreEmpleadoCheckin(rs.getString("nombre_empleado_checkin"));
        } catch (SQLException e) {
            // Si la columna no existe, dejar como null
            reserva.setNombreEmpleadoCheckin(null);
        }

        try {
            reserva.setNombreEmpleadoCheckout(rs.getString("nombre_empleado_checkout"));
        } catch (SQLException e) {
            // Si la columna no existe, dejar como null
            reserva.setNombreEmpleadoCheckout(null);
        }

        // Estado y montos
        reserva.setEstado(rs.getString("estado"));
        reserva.setSubtotal(rs.getBigDecimal("subtotal"));
        reserva.setImpuestos(rs.getBigDecimal("impuestos"));
        reserva.setMontoTotal(rs.getBigDecimal("monto_total"));

        // Observaciones y fechas
        reserva.setObservaciones(rs.getString("observaciones"));
        reserva.setMotivoCancelacion(rs.getString("motivo_cancelacion"));

        Timestamp cancelacion = rs.getTimestamp("fecha_cancelacion");
        if (cancelacion != null) {
            reserva.setFechaCancelacion(cancelacion.toLocalDateTime());
        }

        reserva.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        reserva.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return reserva;
    }

    /**
     * ✅ OBTENER TOTAL DE RESERVAS POR MES
     */
    public int obtenerTotalReservasMes(int mes, int año) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM Reservas 
            WHERE MONTH(fecha_entrada) = ? AND YEAR(fecha_entrada) = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, año);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * ✅ OBTENER INGRESOS TOTALES POR MES
     */
    public BigDecimal obtenerIngresosMes(int mes, int año) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(monto_total), 0) FROM Reservas 
            WHERE MONTH(fecha_entrada) = ? AND YEAR(fecha_entrada) = ?
            AND estado NOT IN ('Cancelada')
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, mes);
            ps.setInt(2, año);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * ✅ MOSTRAR ESTADÍSTICAS DE RESERVAS
     */
    public void mostrarEstadisticas() throws SQLException {
        String sql = """
            SELECT 
                estado,
                COUNT(*) as cantidad,
                ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Reservas), 1) as porcentaje,
                COALESCE(SUM(monto_total), 0) as total_ingresos
            FROM Reservas 
            GROUP BY estado
            ORDER BY cantidad DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            System.out.println("📊 ESTADÍSTICAS DE RESERVAS:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%-15s %-10s %-12s %-15s%n", "Estado", "Cantidad", "Porcentaje", "Ingresos");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            while (rs.next()) {
                String estado = rs.getString("estado");
                int cantidad = rs.getInt("cantidad");
                double porcentaje = rs.getDouble("porcentaje");
                BigDecimal ingresos = rs.getBigDecimal("total_ingresos");

                String emoji = switch (estado.toLowerCase()) {
                    case "pendiente" ->
                        "🟡";
                    case "confirmada" ->
                        "🟢";
                    case "checkin" ->
                        "🔵";
                    case "checkout" ->
                        "✅";
                    case "cancelada" ->
                        "❌";
                    default ->
                        "⚪";
                };

                System.out.printf("%s %-12s %-10d %-12.1f%% S/. %-10.2f%n",
                        emoji, estado, cantidad, porcentaje, ingresos);
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }

    /**
     * ✅ VALIDAR DISPONIBILIDAD DE FECHAS
     */
    public boolean validarDisponibilidadFechas(LocalDate entrada, LocalDate salida, int idReserva) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM Reservas 
            WHERE id_reserva != ? 
            AND estado IN ('Pendiente', 'Confirmada', 'CheckIn')
            AND (
                (fecha_entrada < ? AND fecha_salida > ?) OR
                (fecha_entrada >= ? AND fecha_entrada < ?)
            )
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setDate(2, Date.valueOf(salida));
            ps.setDate(3, Date.valueOf(entrada));
            ps.setDate(4, Date.valueOf(entrada));
            ps.setDate(5, Date.valueOf(salida));

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // True si no hay conflictos
            }
        }
        return false;
    }

    public int contarReservasTotales() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Reservas";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
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
