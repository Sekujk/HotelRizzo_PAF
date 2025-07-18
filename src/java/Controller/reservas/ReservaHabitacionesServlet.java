package Controller.reservas;

import DAO.HabitacionDAO;
import DAO.ReservaDAO;
import DAO.TipoHabitacionDAO;
import Model.ClienteDTO;
import Model.HabitacionDTO;
import Model.ReservaDTO;
import Model.TipoHabitacionDTO;
import Utils.Conexion;
import java.time.temporal.ChronoUnit;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ReservaHabitacionesServlet", value = "/reserva_habitaciones")
public class ReservaHabitacionesServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Verificar autenticación
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Verificar que tengamos todos los datos necesarios en sesión
        ClienteDTO cliente = (ClienteDTO) session.getAttribute("clienteReserva");
        LocalDate fechaEntrada = (LocalDate) session.getAttribute("fechaEntrada");
        LocalDate fechaSalida = (LocalDate) session.getAttribute("fechaSalida");
        Integer numHuespedes = (Integer) session.getAttribute("numHuespedes");
        
        if (cliente == null || fechaEntrada == null || fechaSalida == null || numHuespedes == null) {
            System.out.println("❌ Datos incompletos en sesión, redirigiendo al inicio");
            response.sendRedirect("reserva_crear");
            return;
        }
        
        System.out.println("🏨 Mostrando selección de habitaciones");
        System.out.println("   - Cliente: " + cliente.getNombre() + " " + cliente.getApellido());
        System.out.println("   - Fechas: " + fechaEntrada + " al " + fechaSalida);
        System.out.println("   - Huéspedes: " + numHuespedes);
        
        mostrarHabitacionesDisponibles(request, response);
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
        System.out.println("🔍 Acción recibida: " + action);
        
        if ("finalizar_reserva".equals(action)) {
            finalizarReserva(request, response);
        } else {
            response.sendRedirect("reserva_habitaciones");
        }
    }
    
    /**
     * Mostrar habitaciones disponibles para las fechas seleccionadas
     */
    private void mostrarHabitacionesDisponibles(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        LocalDate fechaEntrada = (LocalDate) session.getAttribute("fechaEntrada");
        LocalDate fechaSalida = (LocalDate) session.getAttribute("fechaSalida");
        Integer numHuespedes = (Integer) session.getAttribute("numHuespedes");
        
        Connection connection = null;
        try {
            connection = Conexion.getConnection();
            
            // Obtener habitaciones disponibles usando tu método existente
            HabitacionDAO habitacionDAO = new HabitacionDAO(connection);
            List<HabitacionDTO> habitacionesDisponibles = habitacionDAO.listarDisponibles(fechaEntrada, fechaSalida);
            
            // Filtrar por capacidad según número de huéspedes
            TipoHabitacionDAO tipoDAO = new TipoHabitacionDAO(connection);
            List<HabitacionDTO> habitacionesAptas = new ArrayList<>();
            
            for (HabitacionDTO habitacion : habitacionesDisponibles) {
                try {
                    TipoHabitacionDTO tipo = tipoDAO.obtenerPorId(habitacion.getIdTipo());
                    if (tipo != null && tipo.getCapacidadPersonas() >= numHuespedes) {
                        habitacionesAptas.add(habitacion);
                    }
                } catch (SQLException e) {
                    System.err.println("❌ Error obteniendo tipo para habitación " + habitacion.getNumero() + ": " + e.getMessage());
                }
            }
            
            // Obtener tipos de habitación para mostrar información adicional
            List<TipoHabitacionDTO> tiposHabitacion = tipoDAO.listar();
            
            System.out.println("🏠 Habitaciones disponibles encontradas: " + habitacionesAptas.size());
            
            request.setAttribute("habitacionesDisponibles", habitacionesAptas);
            request.setAttribute("tiposHabitacion", tiposHabitacion);
            request.setAttribute("step", "habitaciones");
            
            request.getRequestDispatcher("Reservas/reserva_habitaciones.jsp").forward(request, response);
            
        } catch (SQLException e) {
            System.err.println("❌ Error buscando habitaciones: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al buscar habitaciones disponibles: " + e.getMessage());
            request.getRequestDispatcher("Reservas/reserva_habitaciones.jsp").forward(request, response);
            
        } finally {
            if (connection != null) {
                try { connection.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }
    
    /**
     * Finalizar la creación de la reserva
     */
    private void finalizarReserva(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Obtener datos de la sesión
        ClienteDTO cliente = (ClienteDTO) session.getAttribute("clienteReserva");
        LocalDate fechaEntrada = (LocalDate) session.getAttribute("fechaEntrada");
        LocalDate fechaSalida = (LocalDate) session.getAttribute("fechaSalida");
        Integer numHuespedes = (Integer) session.getAttribute("numHuespedes");
        String observaciones = (String) session.getAttribute("observaciones");
        
        // Obtener ID del empleado logueado (necesitas implementar esto según tu sistema de autenticación)
        Integer idEmpleado = (Integer) session.getAttribute("empleadoId");
        if (idEmpleado == null) {
            // Valor por defecto si no tienes empleado en sesión
            idEmpleado = 1; // Cambiar por la lógica correcta de tu sistema
        }
        
        // Obtener habitaciones seleccionadas
        String[] habitacionesSeleccionadas = request.getParameterValues("habitaciones");
        
        if (habitacionesSeleccionadas == null || habitacionesSeleccionadas.length == 0) {
            request.setAttribute("error", "Debe seleccionar al menos una habitación");
            mostrarHabitacionesDisponibles(request, response);
            return;
        }
        
        System.out.println("💾 Finalizando reserva:");
        System.out.println("   - Cliente: " + cliente.getDni());
        System.out.println("   - Habitaciones: " + String.join(", ", habitacionesSeleccionadas));
        
        Connection connection = null;
        try {
            connection = Conexion.getConnection();
            connection.setAutoCommit(false);
            
            // Calcular total
            BigDecimal subtotal = calcularSubtotal(connection, habitacionesSeleccionadas, fechaEntrada, fechaSalida);
            BigDecimal impuestos = subtotal.multiply(new BigDecimal("0.18")); // IGV 18%
            BigDecimal total = subtotal.add(impuestos);
            
            // Crear la reserva principal
            ReservaDTO reserva = new ReservaDTO();
            reserva.setIdCliente(cliente.getId());
            reserva.setIdEmpleado(idEmpleado);
            reserva.setFechaEntrada(fechaEntrada);
            reserva.setFechaSalida(fechaSalida);
            reserva.setNumHuespedes(numHuespedes);
            reserva.setEstado("Confirmada");
            reserva.setSubtotal(subtotal);
            reserva.setImpuestos(impuestos);
            reserva.setMontoTotal(total);
            reserva.setObservaciones(observaciones);
            
            // Generar número de reserva temporal (el trigger lo sobrescribirá)
            String numeroTemporal = "RES-" + System.currentTimeMillis();
            reserva.setNumeroReserva(numeroTemporal);
            
            ReservaDAO reservaDAO = new ReservaDAO(connection);
            boolean reservaCreada = reservaDAO.insertar(reserva);
            
            if (!reservaCreada || reserva.getIdReserva() == 0) {
                throw new SQLException("Error al crear la reserva principal");
            }
            
            // Insertar detalles de habitaciones
            for (String habitacionIdStr : habitacionesSeleccionadas) {
                try {
                    int idHabitacion = Integer.parseInt(habitacionIdStr);
                    double precioNoche = obtenerPrecioHabitacion(connection, idHabitacion);
                    
                    boolean detalleInsertado = insertarDetalleHabitacion(
                        connection, 
                        reserva.getIdReserva(), 
                        idHabitacion, 
                        fechaEntrada, 
                        fechaSalida, 
                        precioNoche
                    );
                    
                    if (!detalleInsertado) {
                        throw new SQLException("Error al insertar detalle de habitación " + idHabitacion);
                    }
                    
                } catch (NumberFormatException e) {
                    throw new SQLException("ID de habitación inválido: " + habitacionIdStr);
                }
            }
            
            connection.commit();
            
            System.out.println("✅ Reserva creada exitosamente con ID: " + reserva.getIdReserva());
            System.out.println("   - Número: " + reserva.getNumeroReserva());
            System.out.println("   - Total: S/. " + total);
            
            // Limpiar datos de sesión
            session.removeAttribute("clienteReserva");
            session.removeAttribute("fechaEntrada");
            session.removeAttribute("fechaSalida");
            session.removeAttribute("numHuespedes");
            session.removeAttribute("observaciones");
            
            // Redirigir con mensaje de éxito
            session.setAttribute("mensaje", "Reserva creada exitosamente. Número: " + reserva.getNumeroReserva());
            response.sendRedirect("reservas");
            
        } catch (SQLException e) {
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            
            System.err.println("❌ Error creando reserva: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al crear la reserva: " + e.getMessage());
            mostrarHabitacionesDisponibles(request, response);
            
        } finally {
            if (connection != null) {
                try { 
                    connection.setAutoCommit(true);
                    connection.close(); 
                } catch (SQLException e) { /* ignore */ }
            }
        }
    }
    
    /**
     * Calcular el subtotal de la reserva
     */
    private BigDecimal calcularSubtotal(Connection connection, String[] habitacionesIds, 
                                       LocalDate fechaEntrada, LocalDate fechaSalida) throws SQLException {
        
        BigDecimal subtotal = BigDecimal.ZERO;
        long dias = java.time.temporal.ChronoUnit.DAYS.between(fechaEntrada, fechaSalida);
        
        HabitacionDAO habitacionDAO = new HabitacionDAO(connection);
        TipoHabitacionDAO tipoDAO = new TipoHabitacionDAO(connection);
        
        for (String habitacionIdStr : habitacionesIds) {
            try {
                int habitacionId = Integer.parseInt(habitacionIdStr);
                HabitacionDTO habitacion = habitacionDAO.obtenerPorId(habitacionId);
                
                if (habitacion != null) {
                    TipoHabitacionDTO tipo = tipoDAO.obtenerPorId(habitacion.getIdTipo());
                    if (tipo != null) {
                        BigDecimal precioNoche = new BigDecimal(tipo.getPrecioBase());
                        BigDecimal totalHabitacion = precioNoche.multiply(new BigDecimal(dias));
                        subtotal = subtotal.add(totalHabitacion);
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("❌ ID de habitación inválido: " + habitacionIdStr);
            }
        }
        
        return subtotal;
    }
    
    /**
     * Obtener precio de una habitación específica
     */
    private double obtenerPrecioHabitacion(Connection connection, int idHabitacion) throws SQLException {
        HabitacionDAO habitacionDAO = new HabitacionDAO(connection);
        TipoHabitacionDAO tipoDAO = new TipoHabitacionDAO(connection);
        
        HabitacionDTO habitacion = habitacionDAO.obtenerPorId(idHabitacion);
        if (habitacion != null) {
            TipoHabitacionDTO tipo = tipoDAO.obtenerPorId(habitacion.getIdTipo());
            if (tipo != null) {
                return tipo.getPrecioBase();
            }
        }
        return 0.0;
    }
    
    /**
     * Insertar detalle de habitación en ReservaHabitaciones
     */
    private boolean insertarDetalleHabitacion(Connection connection, int idReserva, int idHabitacion,
                                             LocalDate fechaEntrada, LocalDate fechaSalida, 
                                             double precioNoche) throws SQLException {
        
        String sql = """
            INSERT INTO ReservaHabitaciones (
                id_reserva, id_habitacion, fecha_entrada, fecha_salida, 
                precio_noche, observaciones
            ) VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ps.setInt(2, idHabitacion);
            ps.setDate(3, java.sql.Date.valueOf(fechaEntrada));
            ps.setDate(4, java.sql.Date.valueOf(fechaSalida));
            ps.setDouble(5, precioNoche);
            ps.setString(6, null); // observaciones
            
            return ps.executeUpdate() > 0;
        }
    }
}