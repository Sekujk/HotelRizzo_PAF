package Controller.servicios;

import DAO.ServicioDAO;
import Model.ServicioDTO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "ServicioEditarServlet", urlPatterns = {"/servicio_editar"})
public class ServicioEditarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));

        try (Connection conn = Conexion.getConnection()) {
            ServicioDAO servicioDAO = new ServicioDAO(conn);
            ServicioDTO servicio = servicioDAO.obtenerPorId(id);

            if (servicio != null) {
                request.setAttribute("servicio", servicio);
                request.getRequestDispatcher("Servicios/servicio_editar.jsp").forward(request, response);
            } else {
                response.sendRedirect("servicio?mensaje=no_encontrado");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("servicio?mensaje=error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int idServicio = Integer.parseInt(request.getParameter("id"));
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");
            double precioUnitario = Double.parseDouble(request.getParameter("precio"));

            ServicioDTO servicio = new ServicioDTO();
            servicio.setIdServicio(idServicio);
            servicio.setNombre(nombre);
            servicio.setDescripcion(descripcion);
            servicio.setPrecioUnitario(precioUnitario);

            try (Connection conn = Conexion.getConnection()) {
                ServicioDAO servicioDAO = new ServicioDAO(conn);
                servicioDAO.actualizar(servicio);
                response.sendRedirect("servicio?mensaje=actualizado");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("servicio?mensaje=error_actualizar");
        }
    }
}
