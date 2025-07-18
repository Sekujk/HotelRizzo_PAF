package Controller.reservas;

import DAO.*;
import Model.*;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ReservaCheckoutServlet", value = "/reserva_checkout")
public class ReservaCheckoutServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String idReservaStr = request.getParameter("id");
        if (idReservaStr == null || idReservaStr.trim().isEmpty()) {
            response.sendRedirect("reservas");
            return;
        }
        
        mostrarResumenCheckout(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("procesar_pago".equals(action)) {
            procesarPago(request, response);
        } else if ("finalizar_checkout".equals(action)) {
            realizarCheckout(request, response);
        } else {
            response.sendRedirect("reservas");
        }
    }
    
    /**
     * Mostrar resumen completo para checkout
     */
    private void mostrarResumenCheckout(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idReservaStr = request.getParameter("id");
        
        Connection connection = null;
        try {
            int idReserva = Integer.parseInt(idReservaStr);
            connection = Conexion.getConnection();
            
            // Obtener reserva
            ReservaDAO reservaDAO = new ReservaDAO(connection);
            ReservaDTO reserva = reservaDAO.obtenerPorId(idReserva);
            
            if (reserva == null) {
                request.setAttribute("error", "Reserva no encontrada");
                response.sendRedirect("reservas");
                return;
            }
            
            if (!"CheckIn".equals(reserva.getEstado())) {
                request.setAttribute("error", "Solo se puede hacer check-out a reservas en estado Check-In");
                response.sendRedirect("reservas");
                return;
            }
            
            // Obtener consumos
            ConsumoServicioDAO consumoServicioDAO = new ConsumoServicioDAO(connection);
            List<ConsumoServicioDTO> serviciosConsumidos = consumoServicioDAO.listarPorReserva(idReserva);
            BigDecimal totalServicios = consumoServicioDAO.obtenerTotalConsumosPorReserva(idReserva);
            
            ConsumoProductoDAO consumoProductoDAO = new ConsumoProductoDAO(connection);
            List<ConsumoProductoDTO> productosConsumidos = consumoProductoDAO.listarPorReserva(idReserva);
            BigDecimal totalProductos = consumoProductoDAO.obtenerTotalConsumosPorReserva(idReserva);
            
            // Calcular totales
            BigDecimal subtotalConsumos = totalServicios.add(totalProductos);
            BigDecimal subtotalGeneral = reserva.getSubtotal().add(subtotalConsumos);
            BigDecimal impuestosConsumos = subtotalConsumos.multiply(new BigDecimal("0.18"));
            BigDecimal impuestosGenerales = reserva.getImpuestos().add(impuestosConsumos);
            BigDecimal totalGeneral = subtotalGeneral.add(impuestosGenerales);
            
            // Obtener pagos
            List<PagoDTO> pagos = obtenerPagosReserva(connection, idReserva);
            BigDecimal totalPagado = calcularTotalPagado(pagos);
            BigDecimal saldoPendiente = totalGeneral.subtract(totalPagado);
            
            // Obtener comprobantes
            ComprobanteDAO comprobanteDAO = new ComprobanteDAO(connection);
            List<ComprobanteDTO> comprobantes = comprobanteDAO.listarPorReserva(idReserva);
            
            // Establecer atributos
            request.setAttribute("reserva", reserva);
            request.setAttribute("habitaciones", obtenerHabitacionesReserva(connection, idReserva));
            request.setAttribute("serviciosConsumidos", serviciosConsumidos);
            request.setAttribute("productosConsumidos", productosConsumidos);
            request.setAttribute("totalServicios", totalServicios);
            request.setAttribute("totalProductos", totalProductos);
            request.setAttribute("subtotalConsumos", subtotalConsumos);
            request.setAttribute("subtotalGeneral", subtotalGeneral);
            request.setAttribute("impuestosConsumos", impuestosConsumos);
            request.setAttribute("impuestosGenerales", impuestosGenerales);
            request.setAttribute("totalGeneral", totalGeneral);
            request.setAttribute("pagos", pagos);
            request.setAttribute("totalPagado", totalPagado);
            request.setAttribute("saldoPendiente", saldoPendiente);
            request.setAttribute("comprobantes", comprobantes);
            
            request.getRequestDispatcher("Reservas/reserva_checkout.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error en checkout: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al procesar checkout: " + e.getMessage());
            response.sendRedirect("reservas");
        } finally {
            if (connection != null) {
                try { connection.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }
    
    /**
     * Procesar pago de la reserva
     */
    private void procesarPago(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idReservaStr = request.getParameter("id_reserva");
        String montoStr = request.getParameter("monto");
        String metodoPago = request.getParameter("metodo_pago");
        String numeroOperacion = request.getParameter("numero_operacion");
        String observaciones = request.getParameter("observaciones");
        
        Connection connection = null;
        try {
            int idReserva = Integer.parseInt(idReservaStr);
            BigDecimal monto = new BigDecimal(montoStr);
            
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                request.setAttribute("error", "El monto debe ser mayor a cero");
                response.sendRedirect("reserva_checkout?id=" + idReserva);
                return;
            }
            
            HttpSession session = request.getSession();
            Integer idEmpleado = (Integer) session.getAttribute("empleadoId");
            if (idEmpleado == null) idEmpleado = 1;
            
            connection = Conexion.getConnection();
            
            boolean pagoCreado = insertarPago(connection, idReserva, monto, metodoPago, 
                                            numeroOperacion, idEmpleado, observaciones);
            
            if (pagoCreado) {
                session.setAttribute("mensaje", "Pago registrado exitosamente por S/. " + monto);
                System.out.println("✅ Pago registrado: S/. " + monto + " - " + metodoPago);
            } else {
                request.setAttribute("error", "No se pudo registrar el pago");
            }
            
            response.sendRedirect("reserva_checkout?id=" + idReserva);
            
        } catch (Exception e) {
            System.err.println("❌ Error procesando pago: " + e.getMessage());
            request.setAttribute("error", "Error al procesar pago: " + e.getMessage());
            response.sendRedirect("reserva_checkout?id=" + idReservaStr);
        } finally {
            if (connection != null) {
                try { connection.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }
    
    /**
     * Realizar checkout final
     */
    private void realizarCheckout(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    String idReservaStr = request.getParameter("id_reserva");
    String observaciones = request.getParameter("observaciones");
    String generarComprobante = request.getParameter("generar_comprobante");
    String tipoComprobante = request.getParameter("tipo_comprobante");
    String rucCliente = request.getParameter("ruc_cliente");
    String razonSocial = request.getParameter("razon_social");
    String direccionCliente = request.getParameter("direccion_cliente");
    
    Connection connection = null;
    try {
        int idReserva = Integer.parseInt(idReservaStr);
        
        HttpSession session = request.getSession();
        Integer idEmpleado = (Integer) session.getAttribute("empleadoId");
        if (idEmpleado == null) idEmpleado = 1;
        
        connection = Conexion.getConnection();
        connection.setAutoCommit(false);
        
        // 1. Realizar check-out en la reserva
        ReservaDAO reservaDAO = new ReservaDAO(connection);
        boolean checkoutRealizado = reservaDAO.realizarCheckout(idReserva, idEmpleado);
        
        if (!checkoutRealizado) {
            throw new SQLException("No se pudo realizar el check-out");
        }
        
        // 2. ✅ CAMBIO: Habitaciones van directo a "Disponible"
        cambiarEstadoHabitaciones(connection, idReserva, "Disponible");
        
        // 3. Agregar observaciones
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            agregarObservacionesCheckout(connection, idReserva, observaciones);
        }
        
        // 4. Generar comprobante si se solicita
        if ("true".equals(generarComprobante) && tipoComprobante != null) {
            boolean comprobanteGenerado = generarComprobanteCompleto(connection, idReserva, tipoComprobante, 
                                                                   rucCliente, razonSocial, direccionCliente, idEmpleado);
            
            if (comprobanteGenerado) {
                session.setAttribute("mensaje", "Check-out realizado exitosamente y comprobante generado. Habitaciones disponibles para nueva reserva.");
            } else {
                session.setAttribute("mensaje", "Check-out realizado exitosamente, pero no se pudo generar el comprobante. Habitaciones disponibles.");
            }
        } else {
            session.setAttribute("mensaje", "Check-out realizado exitosamente. Habitaciones disponibles para nueva reserva.");
        }
        
        connection.commit();
        System.out.println("✅ Check-out realizado exitosamente para reserva: " + idReserva);
        System.out.println("✅ Habitaciones marcadas como disponibles inmediatamente");
        
        response.sendRedirect("reservas");
        
    } catch (Exception e) {
        if (connection != null) {
            try { connection.rollback(); } catch (SQLException ex) { /* ignore */ }
        }
        System.err.println("❌ Error realizando check-out: " + e.getMessage());
        e.printStackTrace();
        request.setAttribute("error", "Error al realizar check-out: " + e.getMessage());
        response.sendRedirect("reservas");
    } finally {
        if (connection != null) {
            try { 
                connection.setAutoCommit(true);
                connection.close(); 
            } catch (SQLException e) { /* ignore */ }
        }
    }
}
    
    // ===============================================
    // MÉTODOS AUXILIARES
    // ===============================================
    
    /**
     * Obtener pagos de una reserva
     */
    private List<PagoDTO> obtenerPagosReserva(Connection connection, int idReserva) throws SQLException {
        String sql = """
            SELECT p.*, ISNULL(per.nombre + ' ' + per.apellido, 'Sistema') AS nombre_empleado
            FROM Pagos p
            LEFT JOIN Empleados e ON p.id_empleado = e.id_empleado
            LEFT JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE p.id_reserva = ? AND p.estado = 'Completado'
            ORDER BY p.fecha_pago DESC
        """;
        
        List<PagoDTO> pagos = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                PagoDTO pago = new PagoDTO();
                pago.setIdPago(rs.getInt("id_pago"));
                pago.setIdReserva(rs.getInt("id_reserva"));
                pago.setMonto(rs.getBigDecimal("monto"));
                pago.setMetodoPago(rs.getString("metodo_pago"));
                pago.setNumeroOperacion(rs.getString("numero_operacion"));
                pago.setFechaPago(rs.getTimestamp("fecha_pago").toLocalDateTime());
                pago.setTipoPago(rs.getString("tipo_pago"));
                pago.setEstado(rs.getString("estado"));
                pago.setObservaciones(rs.getString("observaciones"));
                pago.setNombreEmpleado(rs.getString("nombre_empleado"));
                pagos.add(pago);
            }
        }
        return pagos;
    }
    
    /**
     * Calcular total pagado
     */
    private BigDecimal calcularTotalPagado(List<PagoDTO> pagos) {
        return pagos.stream()
                   .map(PagoDTO::getMonto)
                   .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Obtener habitaciones de una reserva
     */
    private String obtenerHabitacionesReserva(Connection connection, int idReserva) throws SQLException {
        String sql = """
            SELECT h.numero, th.nombre as tipo_nombre
            FROM ReservaHabitaciones rh
            JOIN Habitaciones h ON rh.id_habitacion = h.id_habitacion
            JOIN TipoHabitacion th ON h.id_tipo = th.id_tipo
            WHERE rh.id_reserva = ?
            ORDER BY h.numero
        """;
        
        StringBuilder habitaciones = new StringBuilder();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                if (habitaciones.length() > 0) habitaciones.append(", ");
                habitaciones.append(rs.getString("numero"))
                           .append(" (").append(rs.getString("tipo_nombre")).append(")");
            }
        }
        return habitaciones.toString();
    }
    
    /**
     * Insertar pago
     */
    private boolean insertarPago(Connection connection, int idReserva, BigDecimal monto, 
                                String metodoPago, String numeroOperacion, int idEmpleado, 
                                String observaciones) throws SQLException {
        String sql = """
            INSERT INTO Pagos (id_reserva, monto, metodo_pago, numero_operacion, 
                             fecha_pago, id_empleado, tipo_pago, estado, observaciones)
            VALUES (?, ?, ?, ?, ?, ?, 'Pago', 'Completado', ?)
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setBigDecimal(2, monto);
            ps.setString(3, metodoPago);
            ps.setString(4, numeroOperacion);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setInt(6, idEmpleado);
            ps.setString(7, observaciones);
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Cambiar estado de habitaciones
     */
    private void cambiarEstadoHabitaciones(Connection connection, int idReserva, String nuevoEstado) throws SQLException {
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
     * Agregar observaciones de checkout
     */
    private void agregarObservacionesCheckout(Connection connection, int idReserva, String observaciones) throws SQLException {
        String sql = """
            UPDATE Reservas SET observaciones = 
            CASE 
                WHEN observaciones IS NULL OR observaciones = '' 
                THEN ?
                ELSE observaciones + CHAR(13) + CHAR(10) + 'Check-out: ' + ?
            END
            WHERE id_reserva = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "Check-out: " + observaciones);
            ps.setString(2, observaciones);
            ps.setInt(3, idReserva);
            ps.executeUpdate();
        }
    }
    
    /**
     * ✅ MÉTODO CORREGIDO: Generar comprobante completo
     */
    private boolean generarComprobanteCompleto(Connection connection, int idReserva, String tipoComprobante, 
                                             String rucCliente, String razonSocial, String direccionCliente, 
                                             int idEmpleado) throws SQLException {
        
        ComprobanteDAO comprobanteDAO = new ComprobanteDAO(connection);
        
        // Validaciones para factura
        if ("Factura".equals(tipoComprobante)) {
            if (rucCliente == null || rucCliente.trim().isEmpty() || 
                razonSocial == null || razonSocial.trim().isEmpty()) {
                System.err.println("❌ No se puede emitir factura: datos de RUC/Razón Social incompletos");
                return false;
            }
        }
        
        // Obtener datos de la reserva
        ReservaDAO reservaDAO = new ReservaDAO(connection);
        ReservaDTO reserva = reservaDAO.obtenerPorId(idReserva);
        
        if (reserva == null) {
            System.err.println("❌ Reserva no encontrada para generar comprobante");
            return false;
        }
        
        // Calcular totales
        ConsumoServicioDAO consumoServicioDAO = new ConsumoServicioDAO(connection);
        ConsumoProductoDAO consumoProductoDAO = new ConsumoProductoDAO(connection);
        
        BigDecimal totalServicios = consumoServicioDAO.obtenerTotalConsumosPorReserva(idReserva);
        BigDecimal totalProductos = consumoProductoDAO.obtenerTotalConsumosPorReserva(idReserva);
        
        BigDecimal subtotalConsumos = totalServicios.add(totalProductos);
        BigDecimal subtotalGeneral = reserva.getSubtotal().add(subtotalConsumos);
        BigDecimal impuestosConsumos = subtotalConsumos.multiply(new BigDecimal("0.18"));
        BigDecimal impuestosGenerales = reserva.getImpuestos().add(impuestosConsumos);
        BigDecimal totalGeneral = subtotalGeneral.add(impuestosGenerales);
        
        // ✅ GENERAR NUMERO_COMPLETO AUTOMÁTICAMENTE
        String serie = "Boleta".equals(tipoComprobante) ? "B001" : "F001";
        int siguienteNumero = obtenerSiguienteNumero(connection, serie);
        String numeroCompleto = serie + "-" + String.format("%08d", siguienteNumero);
        
        // Crear comprobante
        ComprobanteDTO comprobante = new ComprobanteDTO();
        comprobante.setIdReserva(idReserva);
        comprobante.setTipoComprobante(tipoComprobante);
        comprobante.setSerie(serie);
        comprobante.setNumero(siguienteNumero);              // ✅ Establecer número
        comprobante.setNumeroCompleto(numeroCompleto);       // ✅ Establecer numero_completo
        comprobante.setRucCliente(rucCliente);
        comprobante.setRazonSocial(razonSocial);
        comprobante.setDireccionCliente(direccionCliente);
        comprobante.setSubtotal(subtotalGeneral);
        comprobante.setImpuesto(impuestosGenerales);
        comprobante.setTotal(totalGeneral);
        comprobante.setFechaEmision(LocalDateTime.now());
        comprobante.setIdEmpleado(idEmpleado);
        comprobante.setEstado("Emitido");
        comprobante.setObservaciones("Comprobante generado en check-out");
        
        boolean insertado = comprobanteDAO.insertar(comprobante);
        
        if (insertado) {
            System.out.println("✅ " + tipoComprobante + " generada: " + numeroCompleto);
            System.out.println("   - Total: S/. " + totalGeneral);
        }
        
        return insertado;
    }
    
    /**
     * ✅ MÉTODO NUEVO: Obtener siguiente número de comprobante
     */
    private int obtenerSiguienteNumero(Connection connection, String serie) throws SQLException {
        String sql = "SELECT ISNULL(MAX(numero), 0) + 1 FROM Comprobantes WHERE serie = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, serie);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
            return 1; // Primer comprobante de la serie
        }
    }
}