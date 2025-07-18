package Controller.productos;

import DAO.ProductoDAO;
import Model.ProductoDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import Utils.Conexion;

@WebServlet(name = "ProductoCrearServlet", urlPatterns = {"/producto_crear"})
public class ProductoCrearServlet extends HttpServlet {

    // Mostrar el formulario de creación (GET)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("Productos/producto_crear.jsp").forward(request, response);
    }

    // Procesar el formulario de creación (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String descripcion = request.getParameter("descripcion");
        double precio = Double.parseDouble(request.getParameter("precio"));
        int stock = Integer.parseInt(request.getParameter("stock"));
        int stockMinimo = Integer.parseInt(request.getParameter("stockMinimo"));

        try (Connection conn = Conexion.getConnection()) {
            ProductoDAO productoDAO = new ProductoDAO(conn);

            ProductoDTO producto = new ProductoDTO();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecioUnitario(precio);
            producto.setStock(stock);
            producto.setStockMinimo(stockMinimo);
            producto.setActivo(true);

            productoDAO.registrar(producto);

            response.sendRedirect("producto?mensaje=creado");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("producto?mensaje=error_crear");
        }
    }
}