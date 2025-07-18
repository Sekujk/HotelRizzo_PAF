package Controller.clientes;

import DAO.ClienteDAO;
import Model.ClienteDTO;
import Utils.Conexion;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;

@WebServlet("/cliente_detalle")
public class ClienteDetalleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int idCliente = Integer.parseInt(request.getParameter("id"));

        try (Connection conn = Conexion.getConnection()) {
            ClienteDAO clienteDAO = new ClienteDAO(conn);
            ClienteDTO cliente = clienteDAO.buscarPorId(idCliente);

            if (cliente != null) {
                request.setAttribute("cliente", cliente);
                request.getRequestDispatcher("Clientes/cliente_detalle.jsp").forward(request, response);
            } else {
                response.sendRedirect("clientes");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
