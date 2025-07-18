package Controller.productos;

import DAO.ProductoDAO;
import Model.ProductoDTO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "ProductoDetalle", urlPatterns = {"/producto_detalle"})
public class ProductoDetalleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idProducto;

        try {
            idProducto = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendRedirect("productos?mensaje=id_invalido");
            return;
        }

        try (Connection conn = Conexion.getConnection()) {
            ProductoDAO dao = new ProductoDAO(conn);
            ProductoDTO producto = dao.obtenerPorId(idProducto);

            if (producto == null) {
                response.sendRedirect("productos?mensaje=producto_no_encontrado");
                return;
            }

            request.setAttribute("producto", producto);
            request.getRequestDispatcher("Productos/producto_detalle.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("productos?mensaje=error");
        }
    }
}
