package Controller.reservas;

import DAO.ClienteDAO;
import DAO.PersonaDAO;
import DAO.TipoHabitacionDAO;
import Model.ClienteDTO;
import Model.PersonaDTO;
import Model.TipoHabitacionDTO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Date;

@WebServlet(name = "ReservaCrearServlet", value = "/reserva_crear")
public class ReservaCrearServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Verificar autenticación
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        System.out.println("🏨 Iniciando proceso de nueva reserva");
        
        // Paso 1: Verificar DNI del cliente
        String step = request.getParameter("step");
        if (step == null || "cliente".equals(step)) {
            mostrarBusquedaCliente(request, response);
        } else if ("reserva".equals(step)) {
            mostrarFormularioReserva(request, response);
        } else {
            response.sendRedirect("reserva_crear");
        }
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
        
        if ("buscar_cliente".equals(action)) {
            buscarCliente(request, response);
        } else if ("crear_cliente".equals(action)) {
            crearCliente(request, response);
        } else if ("crear_reserva".equals(action)) {
            crearReserva(request, response);
        } else {
            response.sendRedirect("reserva_crear");
        }
    }
    
    /**
     * Mostrar formulario de búsqueda de cliente
     */
    private void mostrarBusquedaCliente(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("👤 Mostrando búsqueda de cliente");
        request.setAttribute("step", "cliente");
        request.getRequestDispatcher("Reservas/reserva_crear.jsp").forward(request, response);
    }
    
    /**
     * Buscar cliente por DNI usando tus DAOs existentes
     */
    private void buscarCliente(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String dni = request.getParameter("dni");
        if (dni == null || dni.trim().isEmpty()) {
            request.setAttribute("error", "El DNI es requerido");
            request.setAttribute("step", "cliente");
            request.getRequestDispatcher("Reservas/reserva_crear.jsp").forward(request, response);
            return;
        }
        
        dni = dni.trim();
        System.out.println("🔍 Buscando cliente con DNI: " + dni);
        
        Connection connection = null;
        try {
            connection = Conexion.getConnection();
            
            // Usar tu ClienteDAO existente
            ClienteDAO clienteDAO = new ClienteDAO(connection);
            ClienteDTO cliente = clienteDAO.buscarPorDni(dni);
            
            if (cliente != null) {
                // Cliente encontrado, continuar con reserva
                System.out.println("✅ Cliente encontrado: " + cliente.getNombre() + " " + cliente.getApellido());
                request.getSession().setAttribute("clienteReserva", cliente);
                response.sendRedirect("reserva_crear?step=reserva");
            } else {
                // Verificar si existe como persona pero no como cliente
                PersonaDAO personaDAO = new PersonaDAO(connection);
                try {
                    if (personaDAO.existeDni(dni)) {
                        // Persona existe, necesitamos convertirla a cliente
                        System.out.println("⚠️ Persona existe pero no es cliente");
                        request.setAttribute("error", "Esta persona existe pero no está registrada como cliente. Contacte al administrador.");
                        request.setAttribute("step", "cliente");
                        request.getRequestDispatcher("Reservas/reserva_crear.jsp").forward(request, response);
                    } else {
                        // Cliente no existe, mostrar formulario de registro
                        System.out.println("❌ Cliente no encontrado, mostrando formulario de registro");
                        request.setAttribute("step", "nuevo_cliente");
                        request.setAttribute("dni", dni);
                        request.getRequestDispatcher("Reservas/reserva_crear.jsp").forward(request, response);
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error verificando DNI: " + e.getMessage());
                    request.setAttribute("error", "Error al verificar DNI: " + e.getMessage());
                    request.setAttribute("step", "cliente");
                    request.getRequestDispatcher("Reservas/reserva_crear.jsp").forward(request, response);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error buscando cliente: " + e.getMessage());
            request.setAttribute("error", "Error al buscar cliente: " + e.getMessage());
            request.setAttribute("step", "cliente");
            request.getRequestDispatcher("Reservas/reserva_crear.jsp").forward(request, response);
            
        } finally {
            if (connection != null) {
                try { connection.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }
    
    /**
     * Crear nuevo cliente usando tus DAOs existentes
     */
    private void crearCliente(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Obtener datos del formulario
        String dni = request.getParameter("dni");
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String telefono = request.getParameter("telefono");
        String correo = request.getParameter("correo");
        String direccion = request.getParameter("direccion");
        String genero = request.getParameter("genero");
        
        // Validaciones básicas
        if (dni == null || nombre == null || apellido == null || 
            dni.trim().isEmpty() || nombre.trim().isEmpty() || apellido.trim().isEmpty()) {
            request.setAttribute("error", "DNI, nombre y apellido son requeridos");
            request.setAttribute("step", "nuevo_cliente");
            request.setAttribute("dni", dni);
            request.getRequestDispatcher("Reservas/reserva_crear.jsp").forward(request, response);
            return;
        }
        
        System.out.println("👤 Creando nuevo cliente: " + nombre + " " + apellido);
        
        Connection connection = null;
        try {
            connection = Conexion.getConnection();
            connection.setAutoCommit(false); // Transacción
            
            // Crear persona usando tu PersonaDAO
            PersonaDTO persona = new PersonaDTO();
            persona.setDni(dni.trim());
            persona.setNombre(nombre.trim());
            persona.setApellido(apellido.trim());
            persona.setTelefono(telefono != null ? telefono.trim() : null);
            persona.setCorreo(correo != null ? correo.trim() : null);
            persona.setDireccion(direccion != null ? direccion.trim() : null);
            persona.setGenero(genero != null ? genero.trim() : "");
            persona.setFechaNacimiento(new Date()); // Fecha actual por defecto
            persona.setActivo(true);
            
            PersonaDAO personaDAO = new PersonaDAO(connection);
            int idPersona = personaDAO.insertarPersona(persona);
            
            if (idPersona <= 0) {
                throw new SQLException("Error al crear persona");
            }
            
            // Crear cliente usando tu ClienteDAO
            ClienteDTO cliente = new ClienteDTO();
            cliente.setId(idPersona);
            cliente.setNombre(nombre.trim());
            cliente.setApellido(apellido.trim());
            cliente.setDni(dni.trim());
            cliente.setTelefono(telefono);
            cliente.setCorreo(correo);
            cliente.setDireccion(direccion);
            cliente.setGenero(genero);
            cliente.setFechaNacimiento(new Date());
            cliente.setActivo(true);
            cliente.setTipoCliente("Regular");
            cliente.setFechaRegistro(new Date());
            
            ClienteDAO clienteDAO = new ClienteDAO(connection);
            if (!clienteDAO.insertar(cliente)) {
                throw new SQLException("Error al crear cliente");
            }
            
            connection.commit();
            
            System.out.println("✅ Cliente creado exitosamente: " + persona.getNombre() + " " + persona.getApellido());
            
            // Obtener cliente completo para sesión
            cliente = clienteDAO.buscarPorDni(dni.trim());
            
            // Guardar en sesión y continuar con reserva
            request.getSession().setAttribute("clienteReserva", cliente);
            response.sendRedirect("reserva_crear?step=reserva");
            
        } catch (SQLException e) {
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            
            System.err.println("❌ Error creando cliente: " + e.getMessage());
            request.setAttribute("error", "Error al crear cliente: " + e.getMessage());
            request.setAttribute("step", "nuevo_cliente");
            request.setAttribute("dni", dni);
            request.getRequestDispatcher("Reservas/reserva_crear.jsp").forward(request, response);
            
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
     * Mostrar formulario de reserva
     */
    private void mostrarFormularioReserva(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Verificar que tengamos cliente en sesión
        ClienteDTO cliente = (ClienteDTO) request.getSession().getAttribute("clienteReserva");
        
        if (cliente == null) {
            System.out.println("⚠️ No hay cliente en sesión, redirigiendo");
            response.sendRedirect("reserva_crear");
            return;
        }
        
        System.out.println("🏨 Mostrando formulario de reserva para: " + cliente.getNombre());
        
        Connection connection = null;
        try {
            connection = Conexion.getConnection();
            
            // Obtener tipos de habitación usando tu DAO existente
            TipoHabitacionDAO tipoDAO = new TipoHabitacionDAO(connection);
            List<TipoHabitacionDTO> tiposHabitacion = tipoDAO.listar();
            
            // Filtrar solo los activos
            tiposHabitacion.removeIf(tipo -> !tipo.isActivo());
            
            System.out.println("📋 Tipos de habitación cargados: " + tiposHabitacion.size());
            for (TipoHabitacionDTO tipo : tiposHabitacion) {
                System.out.println("   - " + tipo.getNombre() + " (S/. " + tipo.getPrecioBase() + ")");
            }
            
            request.setAttribute("tiposHabitacion", tiposHabitacion);
            request.setAttribute("step", "reserva");
            request.getRequestDispatcher("Reservas/reserva_crear.jsp").forward(request, response);
            
        } catch (SQLException e) {
            System.err.println("❌ Error cargando datos para reserva: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error al cargar datos: " + e.getMessage());
            request.setAttribute("step", "reserva");
            request.getRequestDispatcher("Reservas/reserva_crear.jsp").forward(request, response);
            
        } finally {
            if (connection != null) {
                try { connection.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }
    
    /**
     * Crear reserva (paso final)
     */
    private void crearReserva(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Obtener cliente de la sesión
        ClienteDTO cliente = (ClienteDTO) request.getSession().getAttribute("clienteReserva");
        if (cliente == null) {
            response.sendRedirect("reserva_crear");
            return;
        }
        
        // Obtener datos del formulario
        String fechaEntradaStr = request.getParameter("fecha_entrada");
        String fechaSalidaStr = request.getParameter("fecha_salida");
        String numHuespedesStr = request.getParameter("num_huespedes");
        String observaciones = request.getParameter("observaciones");
        
        System.out.println("📅 Datos recibidos:");
        System.out.println("   - Entrada: " + fechaEntradaStr);
        System.out.println("   - Salida: " + fechaSalidaStr);
        System.out.println("   - Huéspedes: " + numHuespedesStr);
        System.out.println("   - Observaciones: " + observaciones);
        
        try {
            LocalDate fechaEntrada = LocalDate.parse(fechaEntradaStr);
            LocalDate fechaSalida = LocalDate.parse(fechaSalidaStr);
            int numHuespedes = Integer.parseInt(numHuespedesStr);
            
            // Validaciones
            if (fechaSalida.isBefore(fechaEntrada) || fechaSalida.equals(fechaEntrada)) {
                throw new IllegalArgumentException("La fecha de salida debe ser posterior a la fecha de entrada");
            }
            
            if (fechaEntrada.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("La fecha de entrada no puede ser anterior a hoy");
            }
            
            if (numHuespedes <= 0 || numHuespedes > 10) {
                throw new IllegalArgumentException("Número de huéspedes inválido");
            }
            
            System.out.println("📅 Creando reserva: " + fechaEntrada + " al " + fechaSalida + " para " + numHuespedes + " huéspedes");
            
            // Guardar datos de reserva en sesión
            request.getSession().setAttribute("fechaEntrada", fechaEntrada);
            request.getSession().setAttribute("fechaSalida", fechaSalida);
            request.getSession().setAttribute("numHuespedes", numHuespedes);
            request.getSession().setAttribute("observaciones", observaciones);
            
            System.out.println("✅ Datos guardados en sesión, redirigiendo a selección de habitaciones");
            
            // Redirigir a selección de habitaciones
            response.sendRedirect("reserva_habitaciones");
            
        } catch (DateTimeParseException e) {
            System.err.println("❌ Error de formato de fecha: " + e.getMessage());
            request.setAttribute("error", "Formato de fecha inválido");
            mostrarFormularioReserva(request, response);
        } catch (NumberFormatException e) {
            System.err.println("❌ Error en número de huéspedes: " + e.getMessage());
            request.setAttribute("error", "Número de huéspedes inválido");
            mostrarFormularioReserva(request, response);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ Error de validación: " + e.getMessage());
            request.setAttribute("error", e.getMessage());
            mostrarFormularioReserva(request, response);
        } catch (Exception e) {
            System.err.println("❌ Error inesperado: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            mostrarFormularioReserva(request, response);
        }
    }
}