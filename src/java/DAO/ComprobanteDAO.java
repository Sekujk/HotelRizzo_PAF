package DAO;

import Model.ComprobanteDTO;
import Utils.Conexion;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ComprobanteDAO {
    private Connection connection;

    public ComprobanteDAO(Connection connection) {
        this.connection = connection;
    }

    // Constructor alternativo que obtiene conexión automáticamente
    public ComprobanteDAO() {
        this.connection = Conexion.getConnection();
    }

    /**
     * ✅ INSERTAR NUEVO COMPROBANTE
     */
    public boolean insertar(ComprobanteDTO comprobante) throws SQLException {
    
    // Si numero_completo viene del servlet, úsalo. Si no, generarlo aquí
    if (comprobante.getNumeroCompleto() == null || comprobante.getNumeroCompleto().trim().isEmpty()) {
        // Obtener siguiente número para la serie
        int siguienteNumero = obtenerSiguienteNumero(comprobante.getSerie());
        comprobante.setNumero(siguienteNumero);
        comprobante.setNumeroCompleto(comprobante.getSerie() + "-" + String.format("%08d", siguienteNumero));
    }

    // ✅ INCLUIR numero_completo en el INSERT
    String sql = """
        INSERT INTO Comprobantes (id_reserva, tipo_comprobante, serie, numero, numero_completo,
                                ruc_cliente, razon_social, direccion_cliente,
                                subtotal, impuesto, total, fecha_emision, 
                                id_empleado, estado, observaciones)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;
    
    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setInt(1, comprobante.getIdReserva());
        ps.setString(2, comprobante.getTipoComprobante());
        ps.setString(3, comprobante.getSerie());
        ps.setInt(4, comprobante.getNumero());
        ps.setString(5, comprobante.getNumeroCompleto());  // ✅ AGREGAR ESTA LÍNEA
        ps.setString(6, comprobante.getRucCliente());
        ps.setString(7, comprobante.getRazonSocial());
        ps.setString(8, comprobante.getDireccionCliente());
        ps.setBigDecimal(9, comprobante.getSubtotal());
        ps.setBigDecimal(10, comprobante.getImpuesto());
        ps.setBigDecimal(11, comprobante.getTotal());
        ps.setTimestamp(12, Timestamp.valueOf(comprobante.getFechaEmision()));
        ps.setInt(13, comprobante.getIdEmpleado());
        ps.setString(14, comprobante.getEstado());
        ps.setString(15, comprobante.getObservaciones());
        
        int filasAfectadas = ps.executeUpdate();
        
        if (filasAfectadas > 0) {
            // Obtener el ID generado
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                comprobante.setIdComprobante(rs.getInt(1));
            }
            
            System.out.println("✅ " + comprobante.getTipoComprobante() + " emitida: " + comprobante.getNumeroCompleto());
            return true;
        }
        
    } catch (SQLException e) {
        System.out.println("❌ Error al insertar comprobante: " + e.getMessage());
        e.printStackTrace();
        throw e;
    }
    return false;
}

    /**
     * ✅ ACTUALIZAR COMPROBANTE (solo si no está emitido)
     */
    public boolean actualizar(ComprobanteDTO comprobante) throws SQLException {
        // Validar que no se actualicen comprobantes emitidos
        String sqlConsulta = "SELECT estado FROM Comprobantes WHERE id_comprobante = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlConsulta)) {
            ps.setInt(1, comprobante.getIdComprobante());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String estadoActual = rs.getString("estado");
                if ("Anulado".equalsIgnoreCase(estadoActual)) {
                    System.out.println("❌ No se puede modificar un comprobante anulado");
                    return false;
                }
            } else {
                System.out.println("❌ Comprobante no encontrado: " + comprobante.getIdComprobante());
                return false;
            }
        }

        String sqlUpdate = """
            UPDATE Comprobantes SET 
                ruc_cliente = ?, razon_social = ?, direccion_cliente = ?,
                subtotal = ?, impuesto = ?, total = ?, observaciones = ?
            WHERE id_comprobante = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sqlUpdate)) {
            ps.setString(1, comprobante.getRucCliente());
            ps.setString(2, comprobante.getRazonSocial());
            ps.setString(3, comprobante.getDireccionCliente());
            ps.setBigDecimal(4, comprobante.getSubtotal());
            ps.setBigDecimal(5, comprobante.getImpuesto());
            ps.setBigDecimal(6, comprobante.getTotal());
            ps.setString(7, comprobante.getObservaciones());
            ps.setInt(8, comprobante.getIdComprobante());
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Comprobante actualizado exitosamente");
                return true;
            }
        }
        return false;
    }

    /**
     * ✅ ANULAR COMPROBANTE
     */
    public boolean anular(int idComprobante, String motivo) throws SQLException {
        String sql = """
            UPDATE Comprobantes SET 
                estado = 'Anulado', 
                motivo_anulacion = ?, 
                fecha_anulacion = ?
            WHERE id_comprobante = ? AND estado = 'Emitido'
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(3, idComprobante);
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Comprobante anulado exitosamente");
                return true;
            } else {
                System.out.println("❌ No se pudo anular el comprobante (puede que ya esté anulado)");
                return false;
            }
        }
    }

    /**
     * ✅ OBTENER POR ID
     */
    public ComprobanteDTO obtenerPorId(int idComprobante) throws SQLException {
        String sql = """
            SELECT c.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Comprobantes c
            JOIN Reservas r ON c.id_reserva = r.id_reserva
            JOIN Empleados e ON c.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE c.id_comprobante = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idComprobante);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapearResultSetAComprobante(rs);
            }
        }
        return null;
    }

    /**
     * ✅ OBTENER POR NÚMERO COMPLETO
     */
    public ComprobanteDTO obtenerPorNumeroCompleto(String numeroCompleto) throws SQLException {
        String sql = """
            SELECT c.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Comprobantes c
            JOIN Reservas r ON c.id_reserva = r.id_reserva
            JOIN Empleados e ON c.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE c.numero_completo = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, numeroCompleto);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapearResultSetAComprobante(rs);
            }
        }
        return null;
    }

    /**
     * ✅ LISTAR COMPROBANTES POR RESERVA
     */
    public List<ComprobanteDTO> listarPorReserva(int idReserva) throws SQLException {
        List<ComprobanteDTO> lista = new ArrayList<>();
        String sql = """
            SELECT c.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Comprobantes c
            JOIN Reservas r ON c.id_reserva = r.id_reserva
            JOIN Empleados e ON c.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE c.id_reserva = ?
            ORDER BY c.fecha_emision DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAComprobante(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ LISTAR TODOS LOS COMPROBANTES
     */
    public List<ComprobanteDTO> listarTodos() throws SQLException {
        List<ComprobanteDTO> lista = new ArrayList<>();
        String sql = """
            SELECT c.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Comprobantes c
            JOIN Reservas r ON c.id_reserva = r.id_reserva
            JOIN Empleados e ON c.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            ORDER BY c.fecha_emision DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAComprobante(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ LISTAR COMPROBANTES POR FECHA
     */
    public List<ComprobanteDTO> listarPorFecha(LocalDate fecha) throws SQLException {
        List<ComprobanteDTO> lista = new ArrayList<>();
        String sql = """
            SELECT c.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Comprobantes c
            JOIN Reservas r ON c.id_reserva = r.id_reserva
            JOIN Empleados e ON c.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE CAST(c.fecha_emision AS DATE) = ?
            ORDER BY c.fecha_emision DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAComprobante(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ LISTAR COMPROBANTES POR TIPO
     */
    public List<ComprobanteDTO> listarPorTipo(String tipoComprobante) throws SQLException {
        List<ComprobanteDTO> lista = new ArrayList<>();
        String sql = """
            SELECT c.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Comprobantes c
            JOIN Reservas r ON c.id_reserva = r.id_reserva
            JOIN Empleados e ON c.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE c.tipo_comprobante = ?
            ORDER BY c.fecha_emision DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, tipoComprobante);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAComprobante(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ OBTENER SIGUIENTE NÚMERO PARA UNA SERIE
     */
    private int obtenerSiguienteNumero(String serie) throws SQLException {
        String sql = "SELECT MAX(numero) as ultimo_numero FROM Comprobantes WHERE serie = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, serie);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                Integer ultimoNumero = (Integer) rs.getObject("ultimo_numero");
                return (ultimoNumero != null) ? ultimoNumero + 1 : 1;
            }
        }
        return 1;
    }

    /**
     * ✅ ACTUALIZAR NÚMERO COMPLETO
     */
    private void actualizarNumeroCompleto(int idComprobante, String numeroCompleto) throws SQLException {
        String sql = "UPDATE Comprobantes SET numero_completo = ? WHERE id_comprobante = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, numeroCompleto);
            ps.setInt(2, idComprobante);
            ps.executeUpdate();
        }
    }

    /**
     * ✅ BUSCAR COMPROBANTES
     */
    public List<ComprobanteDTO> buscar(String criterio) throws SQLException {
        List<ComprobanteDTO> lista = new ArrayList<>();
        String sql = """
            SELECT c.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Comprobantes c
            JOIN Reservas r ON c.id_reserva = r.id_reserva
            JOIN Empleados e ON c.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE c.numero_completo LIKE ? 
               OR r.numero_reserva LIKE ?
               OR c.ruc_cliente LIKE ?
               OR c.razon_social LIKE ?
            ORDER BY c.fecha_emision DESC
        """;
        
        String busqueda = "%" + criterio + "%";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ps.setString(3, busqueda);
            ps.setString(4, busqueda);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSetAComprobante(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ OBTENER COMPROBANTES DE HOY
     */
    public List<ComprobanteDTO> obtenerComprobantesHoy() throws SQLException {
        return listarPorFecha(LocalDate.now());
    }

    /**
     * ✅ MAPEAR RESULTSET A DTO
     */
    private ComprobanteDTO mapearResultSetAComprobante(ResultSet rs) throws SQLException {
        ComprobanteDTO comprobante = new ComprobanteDTO();
        
        comprobante.setIdComprobante(rs.getInt("id_comprobante"));
        comprobante.setIdReserva(rs.getInt("id_reserva"));
        comprobante.setNumeroReserva(rs.getString("numero_reserva"));
        comprobante.setTipoComprobante(rs.getString("tipo_comprobante"));
        comprobante.setSerie(rs.getString("serie"));
        comprobante.setNumero(rs.getInt("numero"));
        comprobante.setNumeroCompleto(rs.getString("numero_completo"));
        
        // Datos del cliente
        comprobante.setRucCliente(rs.getString("ruc_cliente"));
        comprobante.setRazonSocial(rs.getString("razon_social"));
        comprobante.setDireccionCliente(rs.getString("direccion_cliente"));
        
        // Montos
        comprobante.setSubtotal(rs.getBigDecimal("subtotal"));
        comprobante.setImpuesto(rs.getBigDecimal("impuesto"));
        comprobante.setTotal(rs.getBigDecimal("total"));
        
        // Control
        comprobante.setFechaEmision(rs.getTimestamp("fecha_emision").toLocalDateTime());
        comprobante.setIdEmpleado(rs.getInt("id_empleado"));
        comprobante.setNombreEmpleado(rs.getString("nombre_empleado"));
        comprobante.setEstado(rs.getString("estado"));
        comprobante.setMotivoAnulacion(rs.getString("motivo_anulacion"));
        
        Timestamp fechaAnulacion = rs.getTimestamp("fecha_anulacion");
        if (fechaAnulacion != null) {
            comprobante.setFechaAnulacion(fechaAnulacion.toLocalDateTime());
        }
        
        comprobante.setObservaciones(rs.getString("observaciones"));
        
        return comprobante;
    }

    /**
     * ✅ MOSTRAR ESTADÍSTICAS DE COMPROBANTES
     */
    public void mostrarEstadisticasComprobantes() throws SQLException {
        String sql = """
            SELECT 
                tipo_comprobante,
                COUNT(*) as cantidad_emitida,
                SUM(CASE WHEN estado = 'Emitido' THEN 1 ELSE 0 END) as emitidos,
                SUM(CASE WHEN estado = 'Anulado' THEN 1 ELSE 0 END) as anulados,
                SUM(CASE WHEN estado = 'Emitido' THEN total ELSE 0 END) as total_facturado,
                AVG(CASE WHEN estado = 'Emitido' THEN total ELSE NULL END) as promedio_facturacion
            FROM Comprobantes
            GROUP BY tipo_comprobante
            ORDER BY total_facturado DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            System.out.println("📊 ESTADÍSTICAS DE COMPROBANTES:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%-12s %-10s %-10s %-10s %-15s %-15s%n", 
                            "Tipo", "Total", "Emitidos", "Anulados", "Facturado", "Promedio");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            while (rs.next()) {
                String tipo = rs.getString("tipo_comprobante");
                int cantidad = rs.getInt("cantidad_emitida");
                int emitidos = rs.getInt("emitidos");
                int anulados = rs.getInt("anulados");
                BigDecimal totalFacturado = rs.getBigDecimal("total_facturado");
                BigDecimal promedio = rs.getBigDecimal("promedio_facturacion");
                
                if (totalFacturado == null) totalFacturado = BigDecimal.ZERO;
                if (promedio == null) promedio = BigDecimal.ZERO;
                
                String emoji = "Boleta".equals(tipo) ? "🧾" : "📄";
                
                System.out.printf("%s %-9s %-10d %-10d %-10d S/. %-10.2f S/. %-10.2f%n", 
                                emoji, tipo, cantidad, emitidos, anulados, totalFacturado, promedio);
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }

    /**
     * ✅ OBTENER RESUMEN DE FACTURACIÓN POR PERÍODO
     */
    public void mostrarResumenFacturacion(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = """
            SELECT 
                tipo_comprobante,
                COUNT(*) as cantidad,
                SUM(subtotal) as total_subtotal,
                SUM(impuesto) as total_impuesto,
                SUM(total) as total_general
            FROM Comprobantes
            WHERE CAST(fecha_emision AS DATE) BETWEEN ? AND ?
            AND estado = 'Emitido'
            GROUP BY tipo_comprobante
            ORDER BY total_general DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = ps.executeQuery();
            
            System.out.println("📈 RESUMEN DE FACTURACIÓN:");
            System.out.println("Período: " + fechaInicio + " al " + fechaFin);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%-12s %-10s %-15s %-15s %-15s%n", 
                            "Tipo", "Cantidad", "Subtotal", "Impuestos", "Total");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            BigDecimal totalGeneral = BigDecimal.ZERO;
            BigDecimal totalImpuestos = BigDecimal.ZERO;
            int totalCantidad = 0;
            
            while (rs.next()) {
                String tipo = rs.getString("tipo_comprobante");
                int cantidad = rs.getInt("cantidad");
                BigDecimal subtotal = rs.getBigDecimal("total_subtotal");
                BigDecimal impuesto = rs.getBigDecimal("total_impuesto");
                BigDecimal total = rs.getBigDecimal("total_general");
                
                totalCantidad += cantidad;
                totalGeneral = totalGeneral.add(total);
                totalImpuestos = totalImpuestos.add(impuesto);
                
                String emoji = "Boleta".equals(tipo) ? "🧾" : "📄";
                
                System.out.printf("%s %-9s %-10d S/. %-10.2f S/. %-10.2f S/. %-10.2f%n", 
                                emoji, tipo, cantidad, subtotal, impuesto, total);
            }
            
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%-12s %-10d %-15s S/. %-10.2f S/. %-10.2f%n", 
                            "TOTALES:", totalCantidad, "", totalImpuestos, totalGeneral);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }

    /**
     * ✅ OBTENER COMPROBANTES POR RANGO DE FECHAS
     */
    public List<ComprobanteDTO> listarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        List<ComprobanteDTO> lista = new ArrayList<>();
        String sql = """
            SELECT c.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Comprobantes c
            JOIN Reservas r ON c.id_reserva = r.id_reserva
            JOIN Empleados e ON c.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE CAST(c.fecha_emision AS DATE) BETWEEN ? AND ?
            ORDER BY c.fecha_emision DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAComprobante(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ VALIDAR SI SE PUEDE EMITIR FACTURA
     */
    public boolean puedeEmitirFactura(String ruc, String razonSocial) {
        // Validaciones básicas para facturación
        if (ruc == null || ruc.trim().isEmpty()) {
            System.out.println("❌ RUC es requerido para emitir factura");
            return false;
        }
        
        if (razonSocial == null || razonSocial.trim().isEmpty()) {
            System.out.println("❌ Razón social es requerida para emitir factura");
            return false;
        }
        
        // Validar formato de RUC (Perú: 11 dígitos)
        if (!ruc.matches("\\d{11}")) {
            System.out.println("❌ RUC debe tener 11 dígitos");
            return false;
        }
        
        return true;
    }

    /**
     * ✅ OBTENER TOTAL FACTURADO POR PERÍODO
     */
    public BigDecimal obtenerTotalFacturado(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = """
            SELECT SUM(total) as total_facturado
            FROM Comprobantes 
            WHERE CAST(fecha_emision AS DATE) BETWEEN ? AND ?
            AND estado = 'Emitido'
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total_facturado");
                return total != null ? total : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * ✅ OBTENER IMPUESTOS RECAUDADOS POR PERÍODO
     */
    public BigDecimal obtenerImpuestosRecaudados(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = """
            SELECT SUM(impuesto) as total_impuestos
            FROM Comprobantes 
            WHERE CAST(fecha_emision AS DATE) BETWEEN ? AND ?
            AND estado = 'Emitido'
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total_impuestos");
                return total != null ? total : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * ✅ VERIFICAR SI EXISTE COMPROBANTE PARA UNA RESERVA
     */
    public boolean existeComprobantePorReserva(int idReserva) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Comprobantes WHERE id_reserva = ? AND estado = 'Emitido'";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    /**
     * ✅ OBTENER ÚLTIMOS COMPROBANTES EMITIDOS
     */
    public List<ComprobanteDTO> obtenerUltimosEmitidos(int limite) throws SQLException {
        List<ComprobanteDTO> lista = new ArrayList<>();
        String sql = """
            SELECT TOP (?) c.*, r.numero_reserva,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM Comprobantes c
            JOIN Reservas r ON c.id_reserva = r.id_reserva
            JOIN Empleados e ON c.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE c.estado = 'Emitido'
            ORDER BY c.fecha_emision DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limite);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAComprobante(rs));
            }
        }
        return lista;
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