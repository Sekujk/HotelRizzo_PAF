package Controller.servicios;

import DAO.ServicioDAO;
import Utils.Conexion;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/servicio_activar")
public class ServicioActivarServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            int id = Integer.parseInt(request.getParameter("id"));
            ServicioDAO dao = new ServicioDAO(conn);
            dao.cambiarEstado(id, true);
            response.sendRedirect("servicio?mensaje=activado");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("servicio?mensaje=error_activar");
        }
    }
}
