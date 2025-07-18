package Controller.habitaciones;

import DAO.TipoHabitacionDAO;
import Model.TipoHabitacionDTO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "TipoHabitacionDetalleServlet", urlPatterns = {"/tipoHabitacion_detalle"})
public class TipoHabitacionDetalleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("tipoHabitacion?mensaje=error_id");
            return;
        }

        try (Connection conn = Conexion.getConnection()) {
            TipoHabitacionDAO dao = new TipoHabitacionDAO(conn);
            int id = Integer.parseInt(idParam);
            TipoHabitacionDTO tipo = dao.obtenerPorId(id);

            if (tipo != null) {
                request.setAttribute("tipo", tipo);
                request.getRequestDispatcher("TipoHabitaciones/tipoHabitacion_detalle.jsp").forward(request, response);
            } else {
                response.sendRedirect("tipoHabitacion?mensaje=no_encontrado");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("tipoHabitacion?mensaje=error");
        }
    }
}
