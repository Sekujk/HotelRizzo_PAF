package Controller.servicios;

import DAO.ServicioDAO;
import Model.ServicioDTO;
import Utils.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "ServicioDetalleServlet", urlPatterns = {"/servicio_detalle"})
public class ServicioDetalleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idServicio;
        try {
            idServicio = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect("servicio?mensaje=id_invalido");
            return;
        }

        try (Connection conn = Conexion.getConnection()) {
            ServicioDAO servicioDAO = new ServicioDAO(conn);
            ServicioDTO servicio = servicioDAO.obtenerPorId(idServicio);

            if (servicio != null) {
                request.setAttribute("servicio", servicio);
                request.getRequestDispatcher("Servicios/servicio_detalle.jsp").forward(request, response);
            } else {
                response.sendRedirect("servicio?mensaje=no_encontrado");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("servicio?mensaje=error_detalle");
        }
    }
}
