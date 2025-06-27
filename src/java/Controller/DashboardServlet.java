package Controller;

import DAO.EmpleadoDAO;
import DAO.ProductoDAO;
import DAO.ServicioDAO;
import Utils.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            EmpleadoDAO empleadoDAO = new EmpleadoDAO(conn);
            ProductoDAO productoDAO = new ProductoDAO(conn);
            ServicioDAO servicioDAO = new ServicioDAO(conn);

            long empleadosActivos = empleadoDAO.contarEmpleadosActivos();
            int stockBajo = productoDAO.contarStockBajo();
            long serviciosActivos = servicioDAO.contarActivos();

            request.setAttribute("empleadosActivos", empleadosActivos);
            request.setAttribute("stockBajo", stockBajo);
            request.setAttribute("serviciosActivos", serviciosActivos);

            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=dashboard");
        }
    }
}
