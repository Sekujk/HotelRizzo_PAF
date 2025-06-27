package Controller.servicios;

import DAO.ServicioDAO;
import Model.ServicioDTO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "ServicioServlet", urlPatterns = {"/servicio"})
public class ServicioServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            ServicioDAO servicioDAO = new ServicioDAO(conn);
            List<ServicioDTO> servicios = servicioDAO.listarTodos();
            long activos = servicios.stream().filter(ServicioDTO::isActivo).count();

            request.setAttribute("servicios", servicios);
            request.setAttribute("activos", activos);
            request.setAttribute("total", servicios.size());

            request.getRequestDispatcher("Servicios/servicio.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard?error_servicio");
        }
    }
}