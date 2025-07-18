package Controller.empleados;

import DAO.EmpleadoDAO;
import DAO.PersonaDAO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "EmpleadoActivar", urlPatterns = {"/empleado_activar"})
public class EmpleadoActivarServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            int id = Integer.parseInt(request.getParameter("id"));

            PersonaDAO personaDAO = new PersonaDAO(conn);
            EmpleadoDAO empleadoDAO = new EmpleadoDAO(conn);

            boolean actualizadoPersona = personaDAO.cambiarEstadoActivo(id, true);
            boolean actualizadoEmpleado = empleadoDAO.cambiarEstadoActivo(id, true);

            if (actualizadoPersona && actualizadoEmpleado) {
                response.sendRedirect("empleado?mensaje=activado");
            } else {
                response.sendRedirect("empleado?mensaje=error_activar");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("empleado?mensaje=error_activar");
        }
    }
}
