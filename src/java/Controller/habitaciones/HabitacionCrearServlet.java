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

@WebServlet(name = "HabitacionCrearServlet", urlPatterns = {"/habitacion_crear"})
public class HabitacionCrearServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            TipoHabitacionDAO tipoDAO = new TipoHabitacionDAO(conn);
            request.setAttribute("tipos", tipoDAO.listar());
            request.getRequestDispatcher("Habitaciones/habitacion_crear.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("habitaciones?mensaje=error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try (Connection conn = Conexion.getConnection()) {
            HabitacionDAO dao = new HabitacionDAO(conn);

            HabitacionDTO nueva = new HabitacionDTO();
            nueva.setNumero(request.getParameter("numero"));
            nueva.setPiso(Integer.parseInt(request.getParameter("piso")));
            nueva.setEstado(request.getParameter("estado"));
            nueva.setIdTipo(Integer.parseInt(request.getParameter("idTipoHabitacion")));
            nueva.setActivo(true);

            dao.insertar(nueva);
            response.sendRedirect("habitaciones?mensaje=creado");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("habitaciones?mensaje=error_crear");
        }
    }
}
