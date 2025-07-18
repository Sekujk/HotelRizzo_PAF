package Controller.reservas;

import DAO.ReservaDAO;
import DAO.HabitacionDAO;
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
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(name = "ReservaCheckinServlet", value = "/reserva_checkin")
public class ReservaCheckinServlet extends HttpServlet {
    
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
        
        mostrarConfirmacionCheckin(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        realizarCheckin(request, response);
    }
    
    private void mostrarConfirmacionCheckin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idReservaStr = request.getParameter("id");
        
        Connection connection = null;
        try {
            int idReserva = Integer.parseInt(idReservaStr);
            connection = Conexion.getConnection();
            
            ReservaDAO reservaDAO = new ReservaDAO(connection);
            ReservaDTO reserva = reservaDAO.obtenerPorId(idReserva);
            
            if (reserva == null) {
                request.setAttribute("error", "Reserva no encontrada");
                response.sendRedirect("reservas");
                return;
            }
            
            if (!"Confirmada".equals(reserva.getEstado())) {
                request.setAttribute("error", "Solo se puede hacer check-in a reservas confirmadas");
                response.sendRedirect("reservas");
                return;
            }
            
            // Obtener habitaciones de la reserva
            String sqlHabitaciones = """
                SELECT h.numero, th.nombre as tipo_nombre
                FROM ReservaHabitaciones rh
                JOIN Habitaciones h ON rh.id_habitacion = h.id_habitacion
                JOIN TipoHabitacion th ON h.id_tipo = th.id_tipo
                WHERE rh.id_reserva = ?
                ORDER BY h.numero
            """;
            
            StringBuilder habitaciones = new StringBuilder();
            try (PreparedStatement ps = connection.prepareStatement(sqlHabitaciones)) {
                ps.setInt(1, idReserva);
                var rs = ps.executeQuery();
                
                while (rs.next()) {
                    if (habitaciones.length() > 0) habitaciones.append(", ");
                    habitaciones.append(rs.getString("numero"))
                               .append(" (").append(rs.getString("tipo_nombre")).append(")");
                }
            }
            
            request.setAttribute("reserva", reserva);
            request.setAttribute("habitaciones", habitaciones.toString());
            
            request.getRequestDispatcher("Reservas/reserva_checkin.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID de reserva inválido");
            response.sendRedirect("reservas");
        } catch (SQLException e) {
            System.err.println("❌ Error en check-in: " + e.getMessage());
            request.setAttribute("error", "Error al procesar check-in: " + e.getMessage());
            response.sendRedirect("reservas");
        } finally {
            if (connection != null) {
                try { connection.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }
    
    private void realizarCheckin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idReservaStr = request.getParameter("id_reserva");
        String observaciones = request.getParameter("observaciones");
        
        Connection connection = null;
        try {
            int idReserva = Integer.parseInt(idReservaStr);
            
            HttpSession session = request.getSession();
            Integer idEmpleado = (Integer) session.getAttribute("empleadoId");
            if (idEmpleado == null) idEmpleado = 1; // Valor por defecto
            
            connection = Conexion.getConnection();
            connection.setAutoCommit(false);
            
            // 1. Realizar check-in en la reserva
            ReservaDAO reservaDAO = new ReservaDAO(connection);
            boolean checkinRealizado = reservaDAO.realizarCheckin(idReserva, idEmpleado);
            
            if (!checkinRealizado) {
                throw new SQLException("No se pudo realizar el check-in");
            }
            
            // 2. Cambiar estado de habitaciones a "Ocupada"
            String sqlActualizarHabitaciones = """
                UPDATE Habitaciones SET estado = 'Ocupada'
                WHERE id_habitacion IN (
                    SELECT rh.id_habitacion 
                    FROM ReservaHabitaciones rh 
                    WHERE rh.id_reserva = ?
                )
            """;
            
            try (PreparedStatement ps = connection.prepareStatement(sqlActualizarHabitaciones)) {
                ps.setInt(1, idReserva);
                int habitacionesActualizadas = ps.executeUpdate();
                System.out.println("✅ " + habitacionesActualizadas + " habitaciones marcadas como ocupadas");
            }
            
            // 3. Agregar observaciones si las hay
            if (observaciones != null && !observaciones.trim().isEmpty()) {
                String sqlObservaciones = """
                    UPDATE Reservas SET observaciones = 
                    CASE 
                        WHEN observaciones IS NULL OR observaciones = '' 
                        THEN ?
                        ELSE observaciones + CHAR(13) + CHAR(10) + 'Check-in: ' + ?
                    END
                    WHERE id_reserva = ?
                """;
                
                try (PreparedStatement ps = connection.prepareStatement(sqlObservaciones)) {
                    ps.setString(1, "Check-in: " + observaciones);
                    ps.setString(2, observaciones);
                    ps.setInt(3, idReserva);
                    ps.executeUpdate();
                }
            }
            
            connection.commit();
            
            System.out.println("✅ Check-in realizado exitosamente para reserva: " + idReserva);
            
            session.setAttribute("mensaje", "Check-in realizado exitosamente");
            response.sendRedirect("reservas");
            
        } catch (NumberFormatException e) {
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            request.setAttribute("error", "ID de reserva inválido");
            response.sendRedirect("reservas");
        } catch (SQLException e) {
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            System.err.println("❌ Error realizando check-in: " + e.getMessage());
            request.setAttribute("error", "Error al realizar check-in: " + e.getMessage());
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
}