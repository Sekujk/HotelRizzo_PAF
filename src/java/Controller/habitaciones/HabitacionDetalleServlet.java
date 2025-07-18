package Controller.habitaciones;

import DAO.HabitacionDAO;
import DAO.TipoHabitacionDAO;
import Model.HabitacionDTO;
import Utils.Conexion;

import java.io.IOException;
import java.sql.Connection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "HabitacionDetalleServlet", urlPatterns = {"/habitacion_detalle"})
public class HabitacionDetalleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        try (Connection conn = Conexion.getConnection()) {
            HabitacionDAO dao = new HabitacionDAO(conn);
            HabitacionDTO habitacion = dao.obtenerPorId(id);

            if (habitacion != null) {
                request.setAttribute("habitacion", habitacion);
                request.getRequestDispatcher("Habitaciones/habitacion_detalle.jsp").forward(request, response);
            } else {
                response.sendRedirect("habitaciones?mensaje=not_found");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("habitaciones?mensaje=error");
        }
    }
}
