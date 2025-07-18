package Controller.productos;

import DAO.ProductoDAO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "ProductoEliminarServlet", urlPatterns = {"/producto_eliminar"})
public class ProductoEliminarServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("producto?mensaje=id_invalido");
            return;
        }

        int id = Integer.parseInt(idParam);

        try (Connection conn = Conexion.getConnection()) {
            ProductoDAO productoDAO = new ProductoDAO(conn);
            boolean eliminado = productoDAO.eliminar(id);

            if (eliminado) {
                response.sendRedirect("producto?mensaje=eliminado");
            } else {
                response.sendRedirect("producto?mensaje=no_encontrado");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("producto?mensaje=error_eliminar");
        }
    }
}
