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

@WebServlet(name = "ReservaCancelarServlet", value = "/reserva_cancelar")
public class ReservaCancelarServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String idReservaStr = request.getParameter("id");
        String motivo = request.getParameter("motivo");
        
        if (idReservaStr == null || idReservaStr.trim().isEmpty()) {
            response.sendRedirect("reservas");
            return;
        }
        
        Connection connection = null;
        try {
            int idReserva = Integer.parseInt(idReservaStr);
            connection = Conexion.getConnection();
            connection.setAutoCommit(false);
            
            // Verificar que la reserva se puede cancelar
            ReservaDAO reservaDAO = new ReservaDAO(connection);
            ReservaDTO reserva = reservaDAO.obtenerPorId(idReserva);
            
            if (reserva == null) {
                request.setAttribute("error", "Reserva no encontrada");
                response.sendRedirect("reservas");
                return;
            }
            
            if (!"Pendiente".equals(reserva.getEstado()) && !"Confirmada".equals(reserva.getEstado())) {
                session.setAttribute("error", "Solo se pueden cancelar reservas Pendientes o Confirmadas");
                response.sendRedirect("reservas");
                return;
            }
            
            // 1. Cambiar estado de la reserva a Cancelada
            String motivoCancelacion = motivo != null && !motivo.trim().isEmpty() ? motivo : "Cancelada por el usuario";
            boolean cancelada = reservaDAO.cambiarEstado(idReserva, "Cancelada", motivoCancelacion);
            
            if (!cancelada) {
                throw new SQLException("No se pudo cancelar la reserva");
            }
            
            // 2. Liberar las habitaciones (cambiar estado a Disponible)
            String sqlLiberarHabitaciones = """
                UPDATE Habitaciones SET estado = 'Disponible'
                WHERE id_habitacion IN (
                    SELECT rh.id_habitacion 
                    FROM ReservaHabitaciones rh 
                    WHERE rh.id_reserva = ?
                )
                AND estado = 'Ocupada'
            """;
            
            try (PreparedStatement ps = connection.prepareStatement(sqlLiberarHabitaciones)) {
                ps.setInt(1, idReserva);
                int habitacionesLiberadas = ps.executeUpdate();
                System.out.println("✅ " + habitacionesLiberadas + " habitaciones liberadas");
            }
            
            connection.commit();
            
            System.out.println("✅ Reserva cancelada exitosamente: " + reserva.getNumeroReserva());
            
            session.setAttribute("mensaje", "Reserva " + reserva.getNumeroReserva() + " cancelada exitosamente");
            response.sendRedirect("reservas");
            
        } catch (NumberFormatException e) {
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            session.setAttribute("error", "ID de reserva inválido");
            response.sendRedirect("reservas");
        } catch (SQLException e) {
            if (connection != null) {
                try { connection.rollback(); } catch (SQLException ex) { /* ignore */ }
            }
            System.err.println("❌ Error cancelando reserva: " + e.getMessage());
            session.setAttribute("error", "Error al cancelar reserva: " + e.getMessage());
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