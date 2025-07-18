package Controller.servicios;

import DAO.ServicioDAO;
import Model.ServicioDTO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "ServicioCrearServlet", urlPatterns = {"/servicio_crear"})
public class ServicioCrearServlet extends HttpServlet {

    // Mostrar el formulario
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("Servicios/servicio_crear.jsp").forward(request, response);
    }

    // Procesar el formulario
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        double precio = Double.parseDouble(request.getParameter("precio"));

        try (Connection conn = Conexion.getConnection()) {
            ServicioDAO dao = new ServicioDAO(conn);
            ServicioDTO servicio = new ServicioDTO();
            servicio.setNombre(nombre);
            servicio.setDescripcion(descripcion);
            servicio.setPrecioUnitario(precio);
            servicio.setActivo(true);

            dao.registrar(servicio);
            response.sendRedirect("servicio?mensaje=creado");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("servicio?mensaje=error_crear");
        }
    }
}
