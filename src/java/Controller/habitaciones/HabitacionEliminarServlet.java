package Controller.habitaciones;

import DAO.HabitacionDAO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "HabitacionEliminarServlet", urlPatterns = {"/habitacion_eliminar"})
public class HabitacionEliminarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idHabitacion = Integer.parseInt(request.getParameter("id"));

        try (Connection conn = Conexion.getConnection()) {
            HabitacionDAO dao = new HabitacionDAO(conn);

            boolean eliminado = dao.eliminarDefinitivo(idHabitacion); // borrado permanente

            if (eliminado) {
                response.sendRedirect("habitaciones?mensaje=eliminado");
            } else {
                response.sendRedirect("habitaciones?mensaje=error_eliminar");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("habitaciones?mensaje=error");
        }
    }
}
