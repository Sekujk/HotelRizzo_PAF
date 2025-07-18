package Controller.reservas;

import DAO.ReservaDAO;
import Model.ReservaDTO;
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
import java.util.List;

@WebServlet(name = "ReservaServlet", value = "/reservas")
public class ReservaServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Verificar autenticación
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        Connection connection = null;
        try {
            // Obtener conexión
            connection = Conexion.getConnection();
            
            if (connection == null) {
                request.setAttribute("error", "Error de conexión a la base de datos");
                request.getRequestDispatcher("error.jsp").forward(request, response);
                return;
            }
            
            // Crear DAO y obtener datos
            ReservaDAO reservaDAO = new ReservaDAO(connection);
            
            // Obtener todas las reservas
            List<ReservaDTO> reservas = reservaDAO.listarTodos();
            
            // Calcular estadísticas
            long pendientes = reservas.stream()
                .filter(r -> "Pendiente".equals(r.getEstado()))
                .count();
                
            long confirmadas = reservas.stream()
                .filter(r -> "Confirmada".equals(r.getEstado()))
                .count();
                
            long checkedIn = reservas.stream()
                .filter(r -> "CheckIn".equals(r.getEstado()))
                .count();
                
            long checkedOut = reservas.stream()
                .filter(r -> "CheckOut".equals(r.getEstado()))
                .count();
                
            long canceladas = reservas.stream()
                .filter(r -> "Cancelada".equals(r.getEstado()))
                .count();
            
            int total = reservas.size();
            
            // Obtener reservas para check-in hoy
            List<ReservaDTO> checkinHoy = reservaDAO.obtenerCheckinHoy();
            
            // Obtener reservas para check-out hoy
            List<ReservaDTO> checkoutHoy = reservaDAO.obtenerCheckoutHoy();
            
            // Log para debugging
            System.out.println("✅ Cargando gestión de reservas:");
            System.out.println("   📊 Total reservas: " + total);
            System.out.println("   ⏳ Pendientes: " + pendientes);
            System.out.println("   ✅ Confirmadas: " + confirmadas);
            System.out.println("   🏨 Check-ins hoy: " + checkinHoy.size());
            System.out.println("   🚪 Check-outs hoy: " + checkoutHoy.size());
            
            // Enviar datos a la vista
            request.setAttribute("reservas", reservas);
            request.setAttribute("total", total);
            request.setAttribute("pendientes", pendientes);
            request.setAttribute("confirmadas", confirmadas);
            request.setAttribute("checkedIn", checkedIn);
            request.setAttribute("checkedOut", checkedOut);
            request.setAttribute("canceladas", canceladas);
            request.setAttribute("checkinHoy", checkinHoy);
            request.setAttribute("checkoutHoy", checkoutHoy);
            
            // Redirigir a la vista
            request.getRequestDispatcher("Reservas/reservas.jsp").forward(request, response);
            
        } catch (SQLException e) {
            System.err.println("❌ Error en ReservaServlet: " + e.getMessage());
            e.printStackTrace();
            
            request.setAttribute("error", "Error al cargar las reservas: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en ReservaServlet: " + e.getMessage());
            e.printStackTrace();
            
            request.setAttribute("error", "Error inesperado: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
            
        } finally {
            // Cerrar conexión
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("🔒 Conexión cerrada correctamente");
                } catch (SQLException e) {
                    System.err.println("❌ Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Verificar autenticación
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        // Para operaciones POST como filtros o búsquedas
        String action = request.getParameter("action");
        
        System.out.println("🔍 Acción POST recibida: " + action);
        
        if ("buscar".equals(action)) {
            buscarReservas(request, response);
        } else if ("filtrar".equals(action)) {
            filtrarReservas(request, response);
        } else {
            // Por defecto, redirigir al GET
            System.out.println("⚠️ Acción no reconocida, redirigiendo a GET");
            doGet(request, response);
        }
    }
    
    /**
     * Método para buscar reservas por criterio
     */
    private void buscarReservas(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String criterio = request.getParameter("criterio");
        if (criterio == null || criterio.trim().isEmpty()) {
            System.out.println("⚠️ Criterio de búsqueda vacío, mostrando todas las reservas");
            doGet(request, response);
            return;
        }
        
        System.out.println("🔍 Buscando reservas con criterio: '" + criterio + "'");
        
        Connection connection = null;
        try {
            connection = Conexion.getConnection();
            
            if (connection == null) {
                throw new SQLException("No se pudo establecer conexión con la base de datos");
            }
            
            ReservaDAO reservaDAO = new ReservaDAO(connection);
            
            // Buscar reservas
            List<ReservaDTO> reservas = reservaDAO.buscar(criterio.trim());
            
            // Calcular estadísticas de los resultados
            int total = reservas.size();
            long pendientes = reservas.stream()
                .filter(r -> "Pendiente".equals(r.getEstado()))
                .count();
            long confirmadas = reservas.stream()
                .filter(r -> "Confirmada".equals(r.getEstado()))
                .count();
            long checkedIn = reservas.stream()
                .filter(r -> "CheckIn".equals(r.getEstado()))
                .count();
            long checkedOut = reservas.stream()
                .filter(r -> "CheckOut".equals(r.getEstado()))
                .count();
            long canceladas = reservas.stream()
                .filter(r -> "Cancelada".equals(r.getEstado()))
                .count();
                
            System.out.println("📊 Resultados de búsqueda:");
            System.out.println("   📝 Criterio: " + criterio);
            System.out.println("   📋 Encontradas: " + total);
            
            // Enviar resultados
            request.setAttribute("reservas", reservas);
            request.setAttribute("total", total);
            request.setAttribute("pendientes", pendientes);
            request.setAttribute("confirmadas", confirmadas);
            request.setAttribute("checkedIn", checkedIn);
            request.setAttribute("checkedOut", checkedOut);
            request.setAttribute("canceladas", canceladas);
            request.setAttribute("criterio", criterio);
            request.setAttribute("esBusqueda", true);
            
            request.getRequestDispatcher("Reservas/reservas.jsp").forward(request, response);
            
        } catch (SQLException e) {
            System.err.println("❌ Error en búsqueda de reservas: " + e.getMessage());
            e.printStackTrace();
            
            request.setAttribute("error", "Error en la búsqueda: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en búsqueda: " + e.getMessage());
            e.printStackTrace();
            
            request.setAttribute("error", "Error inesperado en la búsqueda: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
            
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("❌ Error al cerrar conexión en búsqueda: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Método para filtrar reservas por estado
     */
    private void filtrarReservas(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String estado = request.getParameter("estado");
        if (estado == null || estado.trim().isEmpty()) {
            System.out.println("⚠️ Estado de filtro vacío, mostrando todas las reservas");
            doGet(request, response);
            return;
        }
        
        System.out.println("🔍 Filtrando reservas por estado: '" + estado + "'");
        
        Connection connection = null;
        try {
            connection = Conexion.getConnection();
            
            if (connection == null) {
                throw new SQLException("No se pudo establecer conexión con la base de datos");
            }
            
            ReservaDAO reservaDAO = new ReservaDAO(connection);
            
            // Filtrar por estado
            List<ReservaDTO> reservas;
            if ("Todos".equals(estado)) {
                reservas = reservaDAO.listarTodos();
                System.out.println("📋 Mostrando todas las reservas");
            } else {
                reservas = reservaDAO.listarPorEstado(estado);
                System.out.println("📋 Filtrando por estado: " + estado);
            }
            
            // Calcular estadísticas
            int total = reservas.size();
            long pendientes = reservas.stream()
                .filter(r -> "Pendiente".equals(r.getEstado()))
                .count();
            long confirmadas = reservas.stream()
                .filter(r -> "Confirmada".equals(r.getEstado()))
                .count();
            long checkedIn = reservas.stream()
                .filter(r -> "CheckIn".equals(r.getEstado()))
                .count();
            long checkedOut = reservas.stream()
                .filter(r -> "CheckOut".equals(r.getEstado()))
                .count();
            long canceladas = reservas.stream()
                .filter(r -> "Cancelada".equals(r.getEstado()))
                .count();
            
            System.out.println("📊 Resultados del filtro:");
            System.out.println("   🏷️ Estado: " + estado);
            System.out.println("   📋 Encontradas: " + total);
            
            // Enviar resultados
            request.setAttribute("reservas", reservas);
            request.setAttribute("total", total);
            request.setAttribute("pendientes", pendientes);
            request.setAttribute("confirmadas", confirmadas);
            request.setAttribute("checkedIn", checkedIn);
            request.setAttribute("checkedOut", checkedOut);
            request.setAttribute("canceladas", canceladas);
            request.setAttribute("estadoFiltro", estado);
            request.setAttribute("esFiltro", true);
            
            request.getRequestDispatcher("Reservas/reservas.jsp").forward(request, response);
            
        } catch (SQLException e) {
            System.err.println("❌ Error en filtro de reservas: " + e.getMessage());
            e.printStackTrace();
            
            request.setAttribute("error", "Error en el filtro: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en filtro: " + e.getMessage());
            e.printStackTrace();
            
            request.setAttribute("error", "Error inesperado en el filtro: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
            
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("❌ Error al cerrar conexión en filtro: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Método para obtener información de depuración
     */
    @Override
    public void init() throws ServletException {
        super.init();
        System.out.println("🚀 ReservaServlet inicializado correctamente");
        System.out.println("📍 Servlet URL: /reservas");
        System.out.println("🏨 Sistema: Hotel Rizzo - Gestión de Reservas");
    }
    
    /**
     * Método para limpiar recursos al destruir el servlet
     */
    @Override
    public void destroy() {
        System.out.println("🛑 ReservaServlet destruido");
        super.destroy();
    }
}