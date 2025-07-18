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

@WebServlet(name = "HabitacionEditarServlet", urlPatterns = {"/habitacion_editar"})
public class HabitacionEditarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));

        try (Connection conn = Conexion.getConnection()) {
            HabitacionDAO habitacionDAO = new HabitacionDAO(conn);
            TipoHabitacionDAO tipoDAO = new TipoHabitacionDAO(conn);

            HabitacionDTO habitacion = habitacionDAO.obtenerPorId(id);
            if (habitacion == null) {
                response.sendRedirect("habitaciones?mensaje=habitacion_no_encontrada");
                return;
            }

            request.setAttribute("habitacion", habitacion);
            request.setAttribute("tipos", tipoDAO.listar());

            request.getRequestDispatcher("Habitaciones/habitacion_editar.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("habitaciones?mensaje=error_editar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            HabitacionDAO dao = new HabitacionDAO(conn);

            int idHabitacion = Integer.parseInt(request.getParameter("id"));
            HabitacionDTO habitacionExistente = dao.obtenerPorId(idHabitacion);

            if (habitacionExistente == null) {
                response.sendRedirect("habitaciones?mensaje=habitacion_no_encontrada");
                return;
            }

            // Actualizamos solo los campos editables desde el formulario:
            habitacionExistente.setNumero(request.getParameter("numero"));
            habitacionExistente.setPiso(Integer.parseInt(request.getParameter("piso")));
            habitacionExistente.setIdTipo(Integer.parseInt(request.getParameter("idTipo")));
            habitacionExistente.setObservaciones(request.getParameter("observaciones"));

            // El estado se mantiene igual, no se modifica desde el formulario
            dao.actualizar(habitacionExistente);

            response.sendRedirect("habitaciones?mensaje=editado");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("habitaciones?mensaje=error_actualizar");
        }
    }
}
