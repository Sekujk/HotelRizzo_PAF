package Controller.habitaciones;

import DAO.TipoHabitacionDAO;
import Model.TipoHabitacionDTO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "TipoHabitacionCrearServlet", urlPatterns = {"/tipoHabitacion_crear"})
public class TipoHabitacionCrearServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Mostrar el formulario de creación
        request.getRequestDispatcher("/TipoHabitaciones/tipoHabitacion_crear.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Procesar el formulario de creación
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        double precioBase = Double.parseDouble(request.getParameter("precioBase"));
        int capacidad = Integer.parseInt(request.getParameter("capacidad"));

        TipoHabitacionDTO tipo = new TipoHabitacionDTO();
        tipo.setNombre(nombre);
        tipo.setDescripcion(descripcion);
        tipo.setPrecioBase(precioBase);
        tipo.setCapacidadPersonas(capacidad);
        tipo.setActivo(true);

        try (Connection conn = Conexion.getConnection()) {
            TipoHabitacionDAO dao = new TipoHabitacionDAO(conn);
            dao.insertar(tipo);
            response.sendRedirect("tipoHabitacion?mensaje=Tipo de habitación creado exitosamente");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al crear el tipo de habitación: " + e.getMessage());
            request.getRequestDispatcher("/habitaciones/tipoHabitacion_crear.jsp").forward(request, response);
        }
    }
}