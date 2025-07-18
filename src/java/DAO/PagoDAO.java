package DAO;

import Model.PagoDTO;
import Utils.Conexion;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class PagoDAO {
    private Connection connection;

    public PagoDAO(Connection connection) {
        this.connection = connection;
    }

    // Constructor alternativo que obtiene conexión automáticamente
    public PagoDAO() {
        this.connection = Conexion.getConnection();
    }

    /**
     * ✅ INSERTAR NUEVO PAGO
     */
    public boolean insertar(PagoDTO pago) throws SQLException {
        String sql = """
            INSERT INTO Pagos (numero_comprobante, id_reserva, monto, metodo_pago, 
                             numero_operacion, fecha_pago, id_empleado, tipo_pago, 
                             estado, observaciones)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pago.getNumeroComprobante());
            ps.setInt(2, pago.getIdReserva());
            ps.setBigDecimal(3, pago.getMonto());
            ps.setString(4, pago.getMetodoPago());
            ps.setString(5, pago.getNumeroOperacion());
            ps.setTimestamp(6, Timestamp.valueOf(pago.getFechaPago()));
            ps.setInt(7, pago.getIdEmpleado());
            ps.setString(8, pago.getTipoPago());
            ps.setString(9, pago.getEstado());
            ps.setString(10, pago.getObservaciones());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    pago.setIdPago(rs.getInt(1));
                }
                System.out.println("✅ Pago registrado exitosamente: S/. " + pago.getMonto());
                return true;
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar pago: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    /**
     * ✅ INSERTAR MÚLTIPLES PAGOS (para pagos divididos)
     */
    public boolean insertarMultiples(List<PagoDTO> pagos) throws SQLException {
        String sql = """
            INSERT INTO Pagos (numero_comprobante, id_reserva, monto, metodo_pago, 
                             numero_operacion, fecha_pago, id_empleado, tipo_pago, 
                             estado, observaciones)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try {
            connection.setAutoCommit(false); // Iniciar transacción
            
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (PagoDTO pago : pagos) {
                    ps.setString(1, pago.getNumeroComprobante());
                    ps.setInt(2, pago.getIdReserva());
                    ps.setBigDecimal(3, pago.getMonto());
                    ps.setString(4, pago.getMetodoPago());
                    ps.setString(5, pago.getNumeroOperacion());
                    ps.setTimestamp(6, Timestamp.valueOf(pago.getFechaPago()));
                    ps.setInt(7, pago.getIdEmpleado());
                    ps.setString(8, pago.getTipoPago());
                    ps.setString(9, pago.getEstado());
                    ps.setString(10, pago.getObservaciones());
                    ps.addBatch();
                }
                
                int[] resultados = ps.executeBatch();
                connection.commit(); // Confirmar transacción
                
                System.out.println("✅ " + resultados.length + " pagos registrados exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            connection.rollback(); // Revertir en caso de error
            System.out.println("❌ Error al insertar múltiples pagos: " + e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true); // Restaurar auto-commit
        }
    }

    /**
     * ✅ ACTUALIZAR PAGO
     */
    public boolean actualizar(PagoDTO pago) throws SQLException {
        // Validar que no se actualicen pagos completados
        String sqlConsulta = "SELECT estado FROM Pagos WHERE id_pago = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlConsulta)) {
            ps.setInt(1, pago.getIdPago());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String estadoActual = rs.getString("estado");
                if ("Anulado".equalsIgnoreCase(estadoActual)) {
                    System.out.println("❌ No se puede modificar un pago anulado");
                    return false;
                }
            } else {
                System.out.println("❌ Pago no encontrado: " + pago.getIdPago());
                return false;
            }
        }

        String sqlUpdate = """
            UPDATE Pagos SET 
                numero_comprobante = ?, monto = ?, metodo_pago = ?, 
                numero_operacion = ?, tipo_pago = ?, estado = ?, observaciones = ?
            WHERE id_pago = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sqlUpdate)) {
            ps.setString(1, pago.getNumeroComprobante());
            ps.setBigDecimal(2, pago.getMonto());
            ps.setString(3, pago.getMetodoPago());
            ps.setString(4, pago.getNumeroOperacion());
            ps.setString(5, pago.getTipoPago());
            ps.setString(6, pago.getEstado());
            ps.setString(7, pago.getObservaciones());
            ps.setInt(8, pago.getIdPago());
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Pago actualizado exitosamente");
                return true;
            }
        }
        return false;
    }

    /**
     * ✅ ANULAR PAGO
     */
    public boolean anular(int idPago, String motivo) throws SQLException {
        String sql = "UPDATE Pagos SET estado = 'Anulado', observaciones = ? WHERE id_pago = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "ANULADO: " + motivo);
            ps.setInt(2, idPago);
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Pago anulado exitosamente");
                return true;
            }
        }
        return false;
    }

    /**
     * ✅ OBTENER POR ID
     */
    public PagoDTO obtenerPorId(int idPago) throws SQLException {
        String sql = """
            SELECT p.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Pagos p
            JOIN Reservas r ON p.id_reserva = r.id_reserva
            JOIN Empleados e ON p.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE p.id_pago = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idPago);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapearResultSetAPago(rs);
            }
        }
        return null;
    }

    /**
     * ✅ LISTAR PAGOS POR RESERVA
     */
    public List<PagoDTO> listarPorReserva(int idReserva) throws SQLException {
        List<PagoDTO> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Pagos p
            JOIN Reservas r ON p.id_reserva = r.id_reserva
            JOIN Empleados e ON p.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE p.id_reserva = ?
            ORDER BY p.fecha_pago DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAPago(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ LISTAR TODOS LOS PAGOS
     */
    public List<PagoDTO> listarTodos() throws SQLException {
        List<PagoDTO> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Pagos p
            JOIN Reservas r ON p.id_reserva = r.id_reserva
            JOIN Empleados e ON p.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            ORDER BY p.fecha_pago DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAPago(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ LISTAR PAGOS POR FECHA
     */
    public List<PagoDTO> listarPorFecha(LocalDate fecha) throws SQLException {
        List<PagoDTO> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Pagos p
            JOIN Reservas r ON p.id_reserva = r.id_reserva
            JOIN Empleados e ON p.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE CAST(p.fecha_pago AS DATE) = ?
            ORDER BY p.fecha_pago DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAPago(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ LISTAR PAGOS POR MÉTODO DE PAGO
     */
    public List<PagoDTO> listarPorMetodoPago(String metodoPago) throws SQLException {
        List<PagoDTO> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Pagos p
            JOIN Reservas r ON p.id_reserva = r.id_reserva
            JOIN Empleados e ON p.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE p.metodo_pago = ?
            ORDER BY p.fecha_pago DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, metodoPago);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAPago(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ OBTENER TOTAL PAGADO POR RESERVA
     */
    public BigDecimal obtenerTotalPagadoPorReserva(int idReserva) throws SQLException {
        String sql = """
            SELECT SUM(monto) as total_pagado
            FROM Pagos 
            WHERE id_reserva = ? AND estado = 'Completado'
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total_pagado");
                return total != null ? total : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * ✅ OBTENER INGRESOS POR FECHA
     */
    public BigDecimal obtenerIngresosPorFecha(LocalDate fecha) throws SQLException {
        String sql = """
            SELECT SUM(monto) as total_ingresos
            FROM Pagos 
            WHERE CAST(fecha_pago AS DATE) = ? AND estado = 'Completado'
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total_ingresos");
                return total != null ? total : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * ✅ OBTENER INGRESOS POR RANGO DE FECHAS
     */
    public BigDecimal obtenerIngresosPorRango(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = """
            SELECT SUM(monto) as total_ingresos
            FROM Pagos 
            WHERE CAST(fecha_pago AS DATE) BETWEEN ? AND ? 
            AND estado = 'Completado'
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total_ingresos");
                return total != null ? total : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * ✅ OBTENER PAGOS DE HOY
     */
    public List<PagoDTO> obtenerPagosHoy() throws SQLException {
        return listarPorFecha(LocalDate.now());
    }

    /**
     * ✅ VERIFICAR SI UNA RESERVA ESTÁ TOTALMENTE PAGADA
     */
    public boolean estaReservaTotalmentePagada(int idReserva, BigDecimal montoTotal) throws SQLException {
        BigDecimal totalPagado = obtenerTotalPagadoPorReserva(idReserva);
        return totalPagado.compareTo(montoTotal) >= 0;
    }

    /**
     * ✅ OBTENER SALDO PENDIENTE DE UNA RESERVA
     */
    public BigDecimal obtenerSaldoPendiente(int idReserva, BigDecimal montoTotal) throws SQLException {
        BigDecimal totalPagado = obtenerTotalPagadoPorReserva(idReserva);
        BigDecimal saldo = montoTotal.subtract(totalPagado);
        return saldo.compareTo(BigDecimal.ZERO) > 0 ? saldo : BigDecimal.ZERO;
    }

    /**
     * ✅ MAPEAR RESULTSET A DTO
     */
    private PagoDTO mapearResultSetAPago(ResultSet rs) throws SQLException {
        PagoDTO pago = new PagoDTO();
        
        pago.setIdPago(rs.getInt("id_pago"));
        pago.setNumeroComprobante(rs.getString("numero_comprobante"));
        pago.setIdReserva(rs.getInt("id_reserva"));
        pago.setNumeroReserva(rs.getString("numero_reserva"));
        pago.setMonto(rs.getBigDecimal("monto"));
        pago.setMetodoPago(rs.getString("metodo_pago"));
        pago.setNumeroOperacion(rs.getString("numero_operacion"));
        pago.setFechaPago(rs.getTimestamp("fecha_pago").toLocalDateTime());
        pago.setIdEmpleado(rs.getInt("id_empleado"));
        pago.setNombreEmpleado(rs.getString("nombre_empleado"));
        pago.setTipoPago(rs.getString("tipo_pago"));
        pago.setEstado(rs.getString("estado"));
        pago.setObservaciones(rs.getString("observaciones"));
        
        return pago;
    }

    /**
     * ✅ MOSTRAR ESTADÍSTICAS DE PAGOS
     */
    public void mostrarEstadisticasPagos() throws SQLException {
        String sql = """
            SELECT 
                metodo_pago,
                COUNT(*) as cantidad_pagos,
                SUM(monto) as total_monto,
                AVG(monto) as promedio_monto,
                ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Pagos WHERE estado = 'Completado'), 1) as porcentaje
            FROM Pagos 
            WHERE estado = 'Completado'
            GROUP BY metodo_pago
            ORDER BY total_monto DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            System.out.println("📊 ESTADÍSTICAS DE PAGOS POR MÉTODO:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%-15s %-10s %-15s %-15s %-12s%n", 
                            "Método", "Cantidad", "Total", "Promedio", "Porcentaje");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            while (rs.next()) {
                String metodo = rs.getString("metodo_pago");
                int cantidad = rs.getInt("cantidad_pagos");
                BigDecimal total = rs.getBigDecimal("total_monto");
                BigDecimal promedio = rs.getBigDecimal("promedio_monto");
                double porcentaje = rs.getDouble("porcentaje");
                
                String emoji = switch (metodo.toLowerCase()) {
                    case "efectivo" -> "💵";
                    case "tarjeta" -> "💳";
                    case "transferencia" -> "🏦";
                    case "yape" -> "📱";
                    case "plin" -> "📲";
                    default -> "💰";
                };
                
                System.out.printf("%s %-12s %-10d S/. %-10.2f S/. %-10.2f %-10.1f%%%n", 
                                emoji, metodo, cantidad, total, promedio, porcentaje);
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }

    /**
     * ✅ OBTENER RESUMEN DE INGRESOS POR EMPLEADO
     */
    public void mostrarResumenPorEmpleado(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = """
            SELECT per.nombre + ' ' + per.apellido AS nombre_empleado,
                   COUNT(p.id_pago) as total_transacciones,
                   SUM(p.monto) as total_recaudado,
                   AVG(p.monto) as promedio_transaccion
            FROM Pagos p
            JOIN Empleados e ON p.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE CAST(p.fecha_pago AS DATE) BETWEEN ? AND ?
            AND p.estado = 'Completado'
            GROUP BY per.nombre, per.apellido
            ORDER BY total_recaudado DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = ps.executeQuery();
            
            System.out.println("👥 RESUMEN DE RECAUDACIÓN POR EMPLEADO:");
            System.out.println("Período: " + fechaInicio + " al " + fechaFin);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%-25s %-15s %-15s %-15s%n", 
                            "Empleado", "Transacciones", "Total", "Promedio");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            while (rs.next()) {
                String empleado = rs.getString("nombre_empleado");
                int transacciones = rs.getInt("total_transacciones");
                BigDecimal total = rs.getBigDecimal("total_recaudado");
                BigDecimal promedio = rs.getBigDecimal("promedio_transaccion");
                
                System.out.printf("%-25s %-15d S/. %-10.2f S/. %-10.2f%n", 
                                empleado, transacciones, total, promedio);
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }

    /**
     * ✅ OBTENER INGRESOS POR HORARIO (análisis de patrones)
     */
    public void mostrarPatronesDeCobranza() throws SQLException {
        String sql = """
            SELECT 
                CASE 
                    WHEN DATEPART(HOUR, fecha_pago) BETWEEN 6 AND 11 THEN 'Mañana'
                    WHEN DATEPART(HOUR, fecha_pago) BETWEEN 12 AND 17 THEN 'Tarde'
                    WHEN DATEPART(HOUR, fecha_pago) BETWEEN 18 AND 23 THEN 'Noche'
                    ELSE 'Madrugada'
                END as horario,
                COUNT(*) as total_pagos,
                SUM(monto) as ingresos_horario,
                AVG(monto) as promedio_pago
            FROM Pagos
            WHERE estado = 'Completado'
            GROUP BY 
                CASE 
                    WHEN DATEPART(HOUR, fecha_pago) BETWEEN 6 AND 11 THEN 'Mañana'
                    WHEN DATEPART(HOUR, fecha_pago) BETWEEN 12 AND 17 THEN 'Tarde'
                    WHEN DATEPART(HOUR, fecha_pago) BETWEEN 18 AND 23 THEN 'Noche'
                    ELSE 'Madrugada'
                END
            ORDER BY ingresos_horario DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            System.out.println("🕒 PATRONES DE COBRANZA POR HORARIO:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%-12s %-12s %-15s %-15s%n", "Horario", "Pagos", "Ingresos", "Promedio");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            while (rs.next()) {
                String horario = rs.getString("horario");
                int pagos = rs.getInt("total_pagos");
                BigDecimal ingresos = rs.getBigDecimal("ingresos_horario");
                BigDecimal promedio = rs.getBigDecimal("promedio_pago");
                
                String emoji = switch (horario) {
                    case "Mañana" -> "🌅";
                    case "Tarde" -> "☀️";
                    case "Noche" -> "🌙";
                    default -> "🌃";
                };
                
                System.out.printf("%s %-9s %-12d S/. %-10.2f S/. %-10.2f%n", 
                                emoji, horario, pagos, ingresos, promedio);
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }

    /**
     * ✅ BUSCAR PAGOS POR CRITERIO
     */
    public List<PagoDTO> buscar(String criterio) throws SQLException {
        List<PagoDTO> lista = new ArrayList<>();
        String sql = """
            SELECT p.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Pagos p
            JOIN Reservas r ON p.id_reserva = r.id_reserva
            JOIN Empleados e ON p.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE p.numero_comprobante LIKE ? 
               OR r.numero_reserva LIKE ?
               OR p.numero_operacion LIKE ?
               OR per.nombre LIKE ?
               OR per.apellido LIKE ?
            ORDER BY p.fecha_pago DESC
        """;
        
        String busqueda = "%" + criterio + "%";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ps.setString(3, busqueda);
            ps.setString(4, busqueda);
            ps.setString(5, busqueda);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSetAPago(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ GENERAR NÚMERO DE COMPROBANTE AUTOMÁTICO
     */
    public String generarNumeroComprobante(String prefijo) throws SQLException {
        String sql = """
            SELECT MAX(CAST(SUBSTRING(numero_comprobante, LEN(?) + 2, LEN(numero_comprobante)) AS INT)) as ultimo_numero
            FROM Pagos 
            WHERE numero_comprobante LIKE ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String patron = prefijo + "-%";
            ps.setString(1, prefijo);
            ps.setString(2, patron);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Integer ultimoNumero = (Integer) rs.getObject("ultimo_numero");
                int siguienteNumero = (ultimoNumero != null) ? ultimoNumero + 1 : 1;
                return prefijo + "-" + String.format("%06d", siguienteNumero);
            }
        }
        return prefijo + "-000001";
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