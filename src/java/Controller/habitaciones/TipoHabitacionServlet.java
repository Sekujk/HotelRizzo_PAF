package Controller.habitaciones;

import DAO.TipoHabitacionDAO;
import Model.TipoHabitacionDTO;
import Utils.Conexion;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "tipohabitacion", urlPatterns = {"/tipoHabitacion"})
public class TipoHabitacionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        try (Connection conn = Conexion.getConnection()) {
            TipoHabitacionDAO tipoDAO = new TipoHabitacionDAO(conn);
            List<TipoHabitacionDTO> tipos = tipoDAO.listar();

            long activos = tipos.stream().filter(TipoHabitacionDTO::isActivo).count();
            int total = tipos.size();

            request.setAttribute("tipos", tipos);
            request.setAttribute("activos", activos);
            request.setAttribute("total", total);

            request.getRequestDispatcher("TipoHabitaciones/tipoHabitacion.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp?mensaje=error_tipohabitacion");
        }
    }
}
