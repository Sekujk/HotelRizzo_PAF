package Controller;

import DAO.EmpleadoDAO;
import Model.EmpleadoDTO;
import Utils.Conexion;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "empleado", urlPatterns = {"/empleado"})
public class EmpleadoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            EmpleadoDAO empleadoDAO = new EmpleadoDAO(conn);

            // Obtener datos
            List<EmpleadoDTO> empleados = empleadoDAO.listarEmpleados();
            long activos = empleadoDAO.contarEmpleadosActivos();
            int total = empleadoDAO.contarTotalEmpleados();

            // Enviar datos a la vista
            request.setAttribute("empleados", empleados);
            request.setAttribute("activos", activos);
            request.setAttribute("total", total);

            // Leer mensaje si viene en la URL (?mensaje=exito, etc.)
            String mensaje = request.getParameter("mensaje");
            if (mensaje != null) {
                request.setAttribute("mensaje", mensaje);
            }

            request.getRequestDispatcher("empleado.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp?mensaje=error_empleado");
        }
    }
}
