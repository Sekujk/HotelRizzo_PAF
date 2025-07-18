package Controller.productos;

import DAO.ProductoDAO;
import Utils.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/producto_activar")
public class ProductoActivarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idProducto = Integer.parseInt(request.getParameter("id"));

        try (Connection conn = Conexion.getConnection()) {
            ProductoDAO dao = new ProductoDAO(conn);
            dao.cambiarEstado(idProducto, true);
            response.sendRedirect("producto");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error al activar el producto.");
            request.getRequestDispatcher("producto.jsp").forward(request, response);
        }
    }
}
