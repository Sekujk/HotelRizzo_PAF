package Controller;

import DAO.EmpleadoDAO;
import DAO.PersonaDAO;
import DAO.UsuarioDAO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "EmpleadoEliminar", urlPatterns = {"/empleado_eliminar"})
public class EmpleadoElliminarServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            int id = Integer.parseInt(request.getParameter("id"));

            UsuarioDAO usuarioDAO = new UsuarioDAO(conn);
            EmpleadoDAO empleadoDAO = new EmpleadoDAO(conn);
            PersonaDAO personaDAO = new PersonaDAO(conn);

            // Eliminar usuario (si existe)
            usuarioDAO.eliminarPorIdEmpleado(id);

            // Eliminar empleado
            empleadoDAO.eliminarEmpleado(id);

            // Eliminar persona de forma física
            personaDAO.eliminarPersona(id);

            response.sendRedirect("empleado?mensaje=eliminado");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("empleado.jsp?mensaje=error_eliminar");
        }
    }
}
