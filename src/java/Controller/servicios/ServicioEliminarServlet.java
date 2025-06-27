package Controller;

import DAO.ServicioDAO;
import Utils.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "ServicioEliminarServlet", urlPatterns = {"/servicio_eliminar"})
public class ServicioEliminarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idServicio = Integer.parseInt(request.getParameter("id"));

        try (Connection conn = Conexion.getConnection()) {
            ServicioDAO servicioDAO = new ServicioDAO(conn);
            boolean eliminado = servicioDAO.eliminar(idServicio); // puede ser físico o lógico

            if (eliminado) {
                response.sendRedirect("servicio?mensaje=eliminado");
            } else {
                response.sendRedirect("servicio?mensaje=error_eliminar");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("servicio?mensaje=error_exception");
        }
    }
}
