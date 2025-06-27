package Controller;

import DAO.EmpleadoDAO;
import DAO.UsuarioDAO;
import Model.EmpleadoDTO;
import Model.UsuarioDTO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "EmpleadoDetalle", urlPatterns = {"/empleado_detalle"})
public class EmpleadoDetalleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            int id = Integer.parseInt(request.getParameter("id"));

            EmpleadoDAO empleadoDAO = new EmpleadoDAO(conn);
            UsuarioDAO usuarioDAO = new UsuarioDAO(conn);

            EmpleadoDTO empleado = empleadoDAO.obtenerEmpleadoPorId(id);
            UsuarioDTO usuario = usuarioDAO.obtenerPorIdEmpleado(id);

            if (empleado == null) {
                response.sendRedirect("empleado.jsp?mensaje=no_encontrado");
                return;
            }

            request.setAttribute("empleado", empleado);
            request.setAttribute("usuario", usuario);

            request.getRequestDispatcher("empleado_detalle.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("empleado.jsp?mensaje=error_detalle");
        }
    }
}
