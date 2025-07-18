package Controller.empleados;

import DAO.EmpleadoDAO;
import DAO.PersonaDAO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "EmpleadoDesactivar", urlPatterns = {"/empleado_desactivar"})
public class EmpleadoDesactivarServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            int id = Integer.parseInt(request.getParameter("id"));

            PersonaDAO personaDAO = new PersonaDAO(conn);
            EmpleadoDAO empleadoDAO = new EmpleadoDAO(conn);

            boolean actualizadoPersona = personaDAO.cambiarEstadoActivo(id, false);
            boolean actualizadoEmpleado = empleadoDAO.cambiarEstadoActivo(id, false);

            if (actualizadoPersona && actualizadoEmpleado) {
                response.sendRedirect("empleado?mensaje=desactivado");
            } else {
                response.sendRedirect("empleado?mensaje=error_desactivar");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("empleado?mensaje=error_desactivar");
        }
    }
}
