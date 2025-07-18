package Controller.habitaciones;

import DAO.HabitacionDAO;
import Utils.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "HabitacionDisponibleServlet", urlPatterns = {"/habitacion_disponible"})
public class HabitacionDisponibleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idHabitacion = Integer.parseInt(request.getParameter("id"));

            try (Connection conn = Conexion.getConnection()) {
                HabitacionDAO habitacionDAO = new HabitacionDAO(conn);
                boolean actualizado = habitacionDAO.cambiarEstado(idHabitacion, "Disponible");

                if (actualizado) {
                    response.sendRedirect("habitaciones?mensaje=disponible_ok");
                } else {
                    response.sendRedirect("habitaciones?mensaje=error_estado");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("habitaciones?mensaje=error_exception");
        }
    }
}
