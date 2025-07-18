package Controller.habitaciones;

import DAO.HabitacionDAO;
import Utils.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "HabitacionMantenimientoServlet", urlPatterns = {"/habitacion_mantenimiento"})
public class HabitacionMantenimientoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idHabitacion = Integer.parseInt(request.getParameter("id"));

            try (Connection conn = Conexion.getConnection()) {
                HabitacionDAO habitacionDAO = new HabitacionDAO(conn);
                boolean actualizado = habitacionDAO.cambiarEstado(idHabitacion, "Mantenimiento");

                if (actualizado) {
                    response.sendRedirect("habitaciones?mensaje=mantenimiento_ok");
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
