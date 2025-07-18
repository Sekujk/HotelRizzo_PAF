package Controller.productos;

import DAO.ProductoDAO;
import Model.ProductoDTO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "ProductoEditarServlet", urlPatterns = {"/producto_editar"})
public class ProductoEditarServlet extends HttpServlet {
    
    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    try (Connection conn = Conexion.getConnection()) {
        int id = Integer.parseInt(request.getParameter("id"));
        ProductoDAO dao = new ProductoDAO(conn);
        ProductoDTO producto = dao.obtenerPorId(id);

        if (producto != null) {
            request.setAttribute("producto", producto);
            request.getRequestDispatcher("Productos/producto_editar.jsp").forward(request, response);
        } else {
            response.sendRedirect("producto?mensaje=no_encontrado");
        }
    } catch (Exception e) {
        e.printStackTrace();
        response.sendRedirect("producto?mensaje=error_editar");
    }
}


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");
            double precio = Double.parseDouble(request.getParameter("precio"));
            int stock = Integer.parseInt(request.getParameter("stock"));
            int stockMinimo = Integer.parseInt(request.getParameter("stockMinimo"));

            ProductoDTO producto = new ProductoDTO();
            producto.setIdProducto(id);
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecioUnitario(precio);
            producto.setStock(stock);
            producto.setStockMinimo(stockMinimo);

            try (Connection conn = Conexion.getConnection()) {
                ProductoDAO dao = new ProductoDAO(conn);
                dao.actualizar(producto);
                response.sendRedirect("producto?mensaje=actualizado");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("producto?mensaje=error_editar");
        }
    }
}
