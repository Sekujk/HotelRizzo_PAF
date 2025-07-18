package Controller.habitaciones;

import DAO.TipoHabitacionDAO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "TipoHabitacionEliminarServlet", urlPatterns = {"/tipoHabitacion_eliminar"})
public class TipoHabitacionEliminarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        try (Connection conn = Conexion.getConnection()) {
            TipoHabitacionDAO dao = new TipoHabitacionDAO(conn);
            dao.eliminar(id);
            response.sendRedirect("tipoHabitacion?mensaje=eliminado");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("tipoHabitacion?mensaje=error");
        }
    }
}
