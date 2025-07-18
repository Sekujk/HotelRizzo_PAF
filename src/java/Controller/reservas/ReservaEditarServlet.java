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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ReservaEditarServlet", value = "/reserva_editar")
public class ReservaEditarServlet extends HttpServlet {
    
    /**
     * Escapar caracteres especiales para JSON
     */
    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Verificar si es petición AJAX para buscar habitaciones
        if ("buscar_habitaciones".equals(request.getParameter("action"))) {
            buscarHabitacionesDisponibles(request, response);
            return;
        }
        
        // Verificar permisos - Solo Gerente y Administrador pueden editar
        String rolUsuario = (String) session.getAttribute("rol");
        if (!"Gerente".equals(rolUsuario) && !"Administrador".equals(rolUsuario)) {
            session.setAttribute("error", "No tiene permisos para editar reservas");
            response.sendRedirect("reservas");
            return;
        }
        
        String idReservaStr = request.getParameter("id");
        if (idReservaStr == null || idReservaStr.trim().isEmpty()) {
            response.sendRedirect("reservas");
            return;
        }
        
        mostrarFormularioEdicion(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Verificar permisos
        String rolUsuario = (String) session.getAttribute("rol");
        if (!"Gerente".equals(rolUsuario) && !"Administrador".equals(rolUsuario)) {
            session.setAttribute("error", "No tiene permisos para editar reservas");
            response.sendRedirect("reservas");
            return;
        }
        
        actualizarReserva(request, response);
    }
    
    /**
     * NUEVO: Buscar habitaciones disponibles via AJAX
     */
    private void buscarHabitacionesDisponibles(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String fechaEntradaStr = request.getParameter("fecha_entrada");
        String fechaSalidaStr = request.getParameter("fecha_salida");
        String numHuespedesStr = request.getParameter("num_huespedes");
        String idReservaStr = request.getParameter("id_reserva");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            LocalDate fechaEntrada = LocalDate.parse(fechaEntradaStr);
            LocalDate fechaSalida = LocalDate.parse(fechaSalidaStr);
            int numHuespedes = Integer.parseInt(numHuespedesStr);
            int idReserva = Integer.parseInt(idReservaStr);
            
            Connection connection = Conexion.getConnection();
            
            // Buscar habitaciones disponibles
            HabitacionDAO habitacionDAO = new HabitacionDAO(connection);
            List<HabitacionDTO> habitacionesDisponibles = habitacionDAO.listarDisponibles(fechaEntrada, fechaSalida);
            
            // Filtrar por capacidad según número de huéspedes
            TipoHabitacionDAO tipoDAO = new TipoHabitacionDAO(connection);
            List<HabitacionDTO> habitacionesAptas = new ArrayList<>();
            
            for (HabitacionDTO habitacion : habitacionesDisponibles) {
                TipoHabitacionDTO tipo = tipoDAO.obtenerPorId(habitacion.getIdTipo());
                if (tipo != null && tipo.getCapacidadPersonas() >= numHuespedes) {
                    habitacionesAptas.add(habitacion);
                }
            }
            
            // Crear JSON response - CORREGIDO
            StringBuilder json = new StringBuilder();
            json.append("{\"success\": true, \"habitaciones\": [");
            
            for (int i = 0; i < habitacionesAptas.size(); i++) {
                HabitacionDTO hab = habitacionesAptas.get(i);
                TipoHabitacionDTO tipo = tipoDAO.obtenerPorId(hab.getIdTipo());
                
                if (i > 0) json.append(",");
                json.append("{")
                    .append("\"idHabitacion\": ").append(hab.getIdHabitacion()).append(",")
                    .append("\"numero\": \"").append(escapeJson(hab.getNumero())).append("\",")
                    .append("\"tipoHabitacion\": \"").append(escapeJson(tipo.getNombre())).append("\",")
                    .append("\"capacidad\": ").append(tipo.getCapacidadPersonas()).append(",")
                    .append("\"precio\": \"").append(String.format("%.2f", tipo.getPrecioBase())).append("\"")
                    .append("}");
            }
            
            json.append("]}");
            
            System.out.println("🔍 JSON enviado: " + json.toString()); // Debug
            response.getWriter().write(json.toString());
            connection.close();
            
            System.out.println("✅ Habitaciones encontradas para edición: " + habitacionesAptas.size());
            
        } catch (Exception e) {
            System.err.println("❌ Error buscando habitaciones: " + e.getMessage());
            response.getWriter().write("{\"success\": false, \"message\": \"" + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Mostrar formulario de edición
     */
    private void mostrarFormularioEdicion(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idReservaStr = request.getParameter("id");
        
        Connection connection = null;
        try {
            int idReserva = Integer.parseInt(idReservaStr);
            connection = Conexion.getConnection();
            
            // 1. Obtener reserva
            ReservaDAO reservaDAO = new ReservaDAO(connection);
            ReservaDTO reserva = reservaDAO.obtenerPorId(idReserva);
            
            if (reserva == null) {
                request.setAttribute("error", "Reserva no encontrada");
                response.sendRedirect("reservas");
                return;
            }
            
            // 2. Verificar que se puede editar (solo Confirmada, Pendiente)
            if (!"Confirmada".equals(reserva.getEstado()) && !"Pendiente".equals(reserva.getEstado())) {
                request.setAttribute("error", "Solo se pueden editar reservas en estado Confirmada o Pendiente. Estado actual: " + reserva.getEstado());
                response.sendRedirect("reservas");
                return;
            }
            
            // 3. Obtener cliente
            ClienteDAO clienteDAO = new ClienteDAO(connection);
            ClienteDTO cliente = clienteDAO.buscarPorId(reserva.getIdCliente());
            
            // 4. Obtener empleado responsable
            String nombreEmpleado = obtenerNombreEmpleado(connection, reserva.getIdEmpleado());
            
            // 5. Obtener habitaciones asignadas
            List<HabitacionDetalleDTO> habitacionesAsignadas = obtenerHabitacionesReserva(connection, idReserva);
            
            // 6. Verificar si tiene consumos (si tiene, restringir algunos cambios)
            boolean tieneConsumos = verificarConsumos(connection, idReserva);
            
            // 7. Establecer atributos
            request.setAttribute("reserva", reserva);
            request.setAttribute("cliente", cliente);
            request.setAttribute("nombreEmpleado", nombreEmpleado);
            request.setAttribute("habitacionesAsignadas", habitacionesAsignadas);
            request.setAttribute("tieneConsumos", tieneConsumos);
            
            request.getRequestDispatcher("Reservas/reserva_editar.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de reserva inválido");
            response.sendRedirect("reservas");
        } catch (Exception e) {
            System.err.println("❌ Error cargando reserva para edición: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar reserva: " + e.getMessage());
            response.sendRedirect("reservas");
        } finally {
            if (connection != null) {
                try { connection.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }
    
    /**
     * Actualizar la reserva
     */
    private void actualizarReserva(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idReservaStr = request.getParameter("id_reserva");
        String fechaEntradaStr = request.getParameter("fecha_entrada");
        String fechaSalidaStr = request.getParameter("fecha_salida");
        String numHuespedesStr = request.getParameter("num_huespedes");
        String observaciones = request.getParameter("observaciones");
        String descuentoStr = request.getParameter("descuento");
        String motivoCambio = request.getParameter("motivo_cambio");
        
        // Obtener habitaciones seleccionadas (si se cambiaron)
        String[] habitacionesSeleccionadas = request.getParameterValues("habitaciones");
        boolean cambiarHabitaciones = "true".equals(request.getParameter("cambiar_habitaciones"));
        
        Connection connection = null;
        try {
            int idReserva = Integer.parseInt(idReservaStr);
            
            // Validaciones básicas
            LocalDate nuevaFechaEntrada = LocalDate.parse(fechaEntradaStr);
            LocalDate nuevaFechaSalida = LocalDate.parse(fechaSalidaStr);
            int nuevoNumHuespedes = Integer.parseInt(numHuespedesStr);
            BigDecimal descuento = descuentoStr != null && !descuentoStr.trim().isEmpty() ? 
                                 new BigDecimal(descuentoStr) : BigDecimal.ZERO;
            
            // Validar fechas
            if (!nuevaFechaSalida.isAfter(nuevaFechaEntrada)) {
                throw new IllegalArgumentException("La fecha de salida debe ser posterior a la de entrada");
            }
            
            if (nuevaFechaEntrada.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha de entrada no puede ser anterior a hoy");
            }
            
            // Validar huéspedes
            if (nuevoNumHuespedes <= 0 || nuevoNumHuespedes > 20) {
                throw new IllegalArgumentException("Número de huéspedes inválido (1-20)");
            }
            
            // Validar descuento
            if (descuento.compareTo(BigDecimal.ZERO) < 0 || descuento.compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException("El descuento debe estar entre 0% y 100%");
            }
            
            connection = Conexion.getConnection();
            connection.setAutoCommit(false);
            
            // Obtener reserva actual
            ReservaDAO reservaDAO = new ReservaDAO(connection);
            ReservaDTO reservaActual = reservaDAO.obtenerPorId(idReserva);
            
            if (reservaActual == null) {
                throw new SQLException("Reserva no encontrada");
            }
            
            // Verificar disponibilidad si cambiaron fechas o habitaciones
            boolean fechasCambiaron = !nuevaFechaEntrada.equals(reservaActual.getFechaEntrada()) || 
                                    !nuevaFechaSalida.equals(reservaActual.getFechaSalida());
            
            if (fechasCambiaron || cambiarHabitaciones) {
                List<Integer> habitacionesIds = cambiarHabitaciones ? 
                    obtenerIdsHabitacionesSeleccionadas(habitacionesSeleccionadas) :
                    obtenerHabitacionesActuales(connection, idReserva);
                
                if (!verificarDisponibilidadHabitaciones(connection, habitacionesIds, 
                                                        nuevaFechaEntrada, nuevaFechaSalida, idReserva)) {
                    throw new IllegalArgumentException("Las habitaciones no están disponibles en las fechas seleccionadas");
                }
            }
            
            // Calcular nuevo total si cambiaron fechas o habitaciones
            BigDecimal nuevoSubtotal = reservaActual.getSubtotal();
            BigDecimal nuevoImpuesto = reservaActual.getImpuestos();
            BigDecimal nuevoTotal = reservaActual.getMontoTotal();
            
            if (fechasCambiaron || cambiarHabitaciones) {
                List<Integer> habitacionesIds = cambiarHabitaciones ? 
                    obtenerIdsHabitacionesSeleccionadas(habitacionesSeleccionadas) :
                    obtenerHabitacionesActuales(connection, idReserva);
                
                nuevoSubtotal = calcularNuevoSubtotal(connection, habitacionesIds, 
                                                    nuevaFechaEntrada, nuevaFechaSalida);
                
                // Aplicar descuento si hay
                if (descuento.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal porcentajeDescuento = descuento.divide(new BigDecimal("100"));
                    BigDecimal montoDescuento = nuevoSubtotal.multiply(porcentajeDescuento);
                    nuevoSubtotal = nuevoSubtotal.subtract(montoDescuento);
                }
                
                nuevoImpuesto = nuevoSubtotal.multiply(new BigDecimal("0.18")); // IGV 18%
                nuevoTotal = nuevoSubtotal.add(nuevoImpuesto);
            }
            
            // Actualizar reserva principal
            boolean reservaActualizada = actualizarReservaPrincipal(connection, idReserva, 
                nuevaFechaEntrada, nuevaFechaSalida, nuevoNumHuespedes, observaciones,
                nuevoSubtotal, nuevoImpuesto, nuevoTotal);
            
            if (!reservaActualizada) {
                throw new SQLException("Error al actualizar la reserva principal");
            }
            
            // Actualizar habitaciones si es necesario
            if (cambiarHabitaciones && habitacionesSeleccionadas != null && habitacionesSeleccionadas.length > 0) {
                // Eliminar habitaciones actuales
                eliminarHabitacionesReserva(connection, idReserva);
                
                // Insertar nuevas habitaciones
                for (String habitacionIdStr : habitacionesSeleccionadas) {
                    int idHabitacion = Integer.parseInt(habitacionIdStr);
                    double precioNoche = obtenerPrecioHabitacion(connection, idHabitacion);
                    
                    insertarHabitacionReserva(connection, idReserva, idHabitacion, 
                                            nuevaFechaEntrada, nuevaFechaSalida, precioNoche);
                }
            } else if (fechasCambiaron) {
                // Solo actualizar fechas en habitaciones existentes
                actualizarFechasHabitaciones(connection, idReserva, nuevaFechaEntrada, nuevaFechaSalida);
            }
            
            // Registrar el cambio en un log de auditoría
            registrarCambioReserva(connection, idReserva, motivoCambio, 
                                 (Integer) request.getSession().getAttribute("empleadoId"));
            
            connection.commit();
            
            HttpSession session = request.getSession();
            session.setAttribute("mensaje", "Reserva actualizada exitosamente");
            
            System.out.println("✅ Reserva " + reservaActual.getNumeroReserva() + " actualizada exitosamente");
            System.out.println("   - Nuevas fechas: " + nuevaFechaEntrada + " al " + nuevaFechaSalida);
            System.out.println("   - Nuevo total: S/. " + nuevoTotal);
            
            response.sendRedirect("reservas");
            
        } catch (DateTimeParseException e) {
            request.setAttribute("error", "Formato de fecha inválido");
            mostrarFormularioEdicion(request, response);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Datos numéricos inválidos");
            mostrarFormularioEdicion(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            mostrarFormularioEdicion(request, response);
        } catch (Exception e) {
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            
            System.err.println("❌ Error actualizando reserva: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al actualizar reserva: " + e.getMessage());
            mostrarFormularioEdicion(request, response);
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
    // MÉTODOS AUXILIARES (MANTIENEN LA LÓGICA ACTUAL)
    // ===============================================
    
    private String obtenerNombreEmpleado(Connection connection, int idEmpleado) throws SQLException {
        String sql = """
            SELECT p.nombre + ' ' + p.apellido AS nombre_completo
            FROM Empleados e
            JOIN Personas p ON e.id_empleado = p.id_persona
            WHERE e.id_empleado = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("nombre_completo");
            }
        }
        return "Empleado no encontrado";
    }
    
    private List<HabitacionDetalleDTO> obtenerHabitacionesReserva(Connection connection, int idReserva) throws SQLException {
        String sql = """
            SELECT h.id_habitacion, h.numero, th.nombre as tipo_nombre, rh.precio_noche,
                   th.capacidad_personas, th.descripcion, rh.id_detalle
            FROM ReservaHabitaciones rh
            JOIN Habitaciones h ON rh.id_habitacion = h.id_habitacion
            JOIN TipoHabitacion th ON h.id_tipo = th.id_tipo
            WHERE rh.id_reserva = ?
            ORDER BY h.numero
        """;
        
        List<HabitacionDetalleDTO> habitaciones = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                HabitacionDetalleDTO hab = new HabitacionDetalleDTO();
                hab.setIdDetalle(rs.getInt("id_detalle"));
                hab.setIdHabitacion(rs.getInt("id_habitacion"));
                hab.setNumeroHabitacion(rs.getString("numero"));
                hab.setTipoHabitacion(rs.getString("tipo_nombre"));
                hab.setPrecioNoche(rs.getDouble("precio_noche"));
                hab.setCapacidadPersonas(rs.getInt("capacidad_personas"));
                hab.setDescripcionTipo(rs.getString("descripcion"));
                habitaciones.add(hab);
            }
        }
        return habitaciones;
    }
    
    private boolean verificarConsumos(Connection connection, int idReserva) throws SQLException {
        String sql = """
            SELECT COUNT(*) as total FROM (
                SELECT id_reserva FROM ConsumoServicios WHERE id_reserva = ?
                UNION ALL
                SELECT id_reserva FROM ConsumoProductos WHERE id_reserva = ?
            ) consumos
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, idReserva);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        }
        return false;
    }
    
    private boolean verificarDisponibilidadHabitaciones(Connection connection, List<Integer> habitacionesIds,
                                                       LocalDate fechaEntrada, LocalDate fechaSalida, 
                                                       int idReservaExcluir) throws SQLException {
        
        for (int idHabitacion : habitacionesIds) {
            String sql = """
                SELECT COUNT(*) as conflictos
                FROM ReservaHabitaciones rh
                JOIN Reservas r ON rh.id_reserva = r.id_reserva
                WHERE rh.id_habitacion = ? 
                AND r.id_reserva != ?
                AND r.estado IN ('Confirmada', 'CheckIn')
                AND (
                    (rh.fecha_entrada <= ? AND rh.fecha_salida > ?) OR
                    (rh.fecha_entrada < ? AND rh.fecha_salida >= ?) OR
                    (rh.fecha_entrada >= ? AND rh.fecha_salida <= ?)
                )
            """;
            
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idHabitacion);
                ps.setInt(2, idReservaExcluir);
                ps.setDate(3, java.sql.Date.valueOf(fechaEntrada));
                ps.setDate(4, java.sql.Date.valueOf(fechaEntrada));
                ps.setDate(5, java.sql.Date.valueOf(fechaSalida));
                ps.setDate(6, java.sql.Date.valueOf(fechaSalida));
                ps.setDate(7, java.sql.Date.valueOf(fechaEntrada));
                ps.setDate(8, java.sql.Date.valueOf(fechaSalida));
                
                ResultSet rs = ps.executeQuery();
                if (rs.next() && rs.getInt("conflictos") > 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private List<Integer> obtenerIdsHabitacionesSeleccionadas(String[] habitacionesSeleccionadas) {
        List<Integer> ids = new ArrayList<>();
        if (habitacionesSeleccionadas != null) {
            for (String idStr : habitacionesSeleccionadas) {
                try {
                    ids.add(Integer.parseInt(idStr));
                } catch (NumberFormatException e) {
                    System.err.println("❌ ID de habitación inválido: " + idStr);
                }
            }
        }
        return ids;
    }
    
    private List<Integer> obtenerHabitacionesActuales(Connection connection, int idReserva) throws SQLException {
        String sql = "SELECT id_habitacion FROM ReservaHabitaciones WHERE id_reserva = ?";
        List<Integer> habitaciones = new ArrayList<>();
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                habitaciones.add(rs.getInt("id_habitacion"));
            }
        }
        return habitaciones;
    }
    
    private BigDecimal calcularNuevoSubtotal(Connection connection, List<Integer> habitacionesIds,
                                           LocalDate fechaEntrada, LocalDate fechaSalida) throws SQLException {
        
        BigDecimal subtotal = BigDecimal.ZERO;
        long dias = ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
        
        for (int idHabitacion : habitacionesIds) {
            double precioNoche = obtenerPrecioHabitacion(connection, idHabitacion);
            BigDecimal totalHabitacion = new BigDecimal(precioNoche).multiply(new BigDecimal(dias));
            subtotal = subtotal.add(totalHabitacion);
        }
        
        return subtotal;
    }
    
    private double obtenerPrecioHabitacion(Connection connection, int idHabitacion) throws SQLException {
        String sql = """
            SELECT th.precio_base
            FROM Habitaciones h
            JOIN TipoHabitacion th ON h.id_tipo = th.id_tipo
            WHERE h.id_habitacion = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idHabitacion);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("precio_base");
            }
        }
        return 0.0;
    }
    
    private boolean actualizarReservaPrincipal(Connection connection, int idReserva,
                                             LocalDate fechaEntrada, LocalDate fechaSalida, int numHuespedes,
                                             String observaciones, BigDecimal subtotal, BigDecimal impuesto, 
                                             BigDecimal total) throws SQLException {
        
        String sql = """
            UPDATE Reservas SET 
                fecha_entrada = ?, fecha_salida = ?, num_huespedes = ?,
                observaciones = ?, subtotal = ?, impuestos = ?, monto_total = ?
            WHERE id_reserva = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(fechaEntrada));
            ps.setDate(2, java.sql.Date.valueOf(fechaSalida));
            ps.setInt(3, numHuespedes);
            ps.setString(4, observaciones);
            ps.setBigDecimal(5, subtotal);
            ps.setBigDecimal(6, impuesto);
            ps.setBigDecimal(7, total);
            ps.setInt(8, idReserva);
            
            return ps.executeUpdate() > 0;
        }
    }
    
    private void eliminarHabitacionesReserva(Connection connection, int idReserva) throws SQLException {
        String sql = "DELETE FROM ReservaHabitaciones WHERE id_reserva = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.executeUpdate();
        }
    }
    
    private void insertarHabitacionReserva(Connection connection, int idReserva, int idHabitacion,
                                         LocalDate fechaEntrada, LocalDate fechaSalida, double precioNoche) throws SQLException {
        
        String sql = """
            INSERT INTO ReservaHabitaciones (id_reserva, id_habitacion, fecha_entrada, fecha_salida, precio_noche)
            VALUES (?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, idHabitacion);
            ps.setDate(3, java.sql.Date.valueOf(fechaEntrada));
            ps.setDate(4, java.sql.Date.valueOf(fechaSalida));
            ps.setDouble(5, precioNoche);
            ps.executeUpdate();
        }
    }
    
    private void actualizarFechasHabitaciones(Connection connection, int idReserva,
                                            LocalDate fechaEntrada, LocalDate fechaSalida) throws SQLException {
        
        String sql = """
            UPDATE ReservaHabitaciones 
            SET fecha_entrada = ?, fecha_salida = ?
            WHERE id_reserva = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(fechaEntrada));
            ps.setDate(2, java.sql.Date.valueOf(fechaSalida));
            ps.setInt(3, idReserva);
            ps.executeUpdate();
        }
    }
    
    private void registrarCambioReserva(Connection connection, int idReserva, String motivo, Integer idEmpleado) {
        try {
            String sql = """
                INSERT INTO LogCambiosReserva (id_reserva, id_empleado, fecha_cambio, motivo_cambio)
                VALUES (?, ?, GETDATE(), ?)
            """;
            
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, idReserva);
                ps.setInt(2, idEmpleado != null ? idEmpleado : 1);
                ps.setString(3, motivo);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            // Log del cambio es opcional, no fallar por esto
            System.err.println("⚠️ No se pudo registrar el log de cambio: " + e.getMessage());
        }
    }
}