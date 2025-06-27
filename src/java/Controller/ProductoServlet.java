package Controller;

import DAO.ProductoDAO;
import Model.ProductoDTO;
import Utils.Conexion;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "producto", urlPatterns = {"/producto"})
public class ProductoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            ProductoDAO productoDAO = new ProductoDAO(conn);

            List<ProductoDTO> productos = productoDAO.listar();
            long activos = productoDAO.contarProductosActivos();
            int total = productoDAO.contarTotalProductos();

            request.setAttribute("productos", productos);
            request.setAttribute("activos", activos);
            request.setAttribute("total", total);

            String mensaje = request.getParameter("mensaje");
            if (mensaje != null) {
                request.setAttribute("mensaje", mensaje);
            }

            request.getRequestDispatcher("Productos/producto.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard.jsp?mensaje=error_producto");
        }
    }
}
