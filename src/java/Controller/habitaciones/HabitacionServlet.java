package Controller.habitaciones;

import DAO.HabitacionDAO;
import Model.HabitacionDTO;
import Utils.Conexion;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "habitacion", urlPatterns = {"/habitaciones"})
public class HabitacionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            HabitacionDAO habitacionDAO = new HabitacionDAO(conn);

            // Lógica para obtener todas las habitaciones
            List<HabitacionDTO> habitaciones = habitacionDAO.listarTodos(); // este método ya lo tenías

            // Cálculos para el resumen
            long activas = habitaciones.stream().filter(HabitacionDTO::isActivo).count();
            int total = habitaciones.size();

            // Atributos para la vista
            request.setAttribute("habitaciones", habitaciones);
            request.setAttribute("activas", activas);
            request.setAttribute("total", total);

            // Leer mensaje opcional desde la URL
            String mensaje = request.getParameter("mensaje");
            if (mensaje != null) {
                request.setAttribute("mensaje", mensaje);
            }

            // Redirigir a la vista correspondiente
            request.getRequestDispatcher("Habitaciones/habitacion.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp?mensaje=error_habitacion");
        }
    }
}
