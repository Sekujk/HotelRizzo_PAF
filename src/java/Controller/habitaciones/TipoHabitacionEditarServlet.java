package Controller.habitaciones;

import DAO.TipoHabitacionDAO;
import Model.TipoHabitacionDTO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "TipoHabitacionEditarServlet", urlPatterns = {"/tipoHabitacion_editar"})
public class TipoHabitacionEditarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");

        try (Connection conn = Conexion.getConnection()) {
            TipoHabitacionDAO dao = new TipoHabitacionDAO(conn);
            int id = Integer.parseInt(idParam);
            TipoHabitacionDTO tipo = dao.obtenerPorId(id);
            request.setAttribute("tipo", tipo);
            request.getRequestDispatcher("TipoHabitaciones/tipoHabitacion_editar.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("tipoHabitacion?mensaje=error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        double precioBase = Double.parseDouble(request.getParameter("precioBase"));
        int capacidad = Integer.parseInt(request.getParameter("capacidadPersonas"));
        boolean activo = Boolean.parseBoolean(request.getParameter("activo"));

        TipoHabitacionDTO tipo = new TipoHabitacionDTO();
        tipo.setIdTipo(id);
        tipo.setNombre(nombre);
        tipo.setDescripcion(descripcion);
        tipo.setPrecioBase(precioBase);
        tipo.setCapacidadPersonas(capacidad);
        tipo.setActivo(activo);

        try (Connection conn = Conexion.getConnection()) {
            TipoHabitacionDAO dao = new TipoHabitacionDAO(conn);
            dao.actualizar(tipo);
            response.sendRedirect("tipoHabitacion?mensaje=actualizado");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("tipoHabitacion?mensaje=error");
        }
    }
}
