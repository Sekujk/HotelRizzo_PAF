package Controller.clientes;

import DAO.ClienteDAO;
import Model.ClienteDTO;
import Utils.Conexion;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            ClienteDAO clienteDAO = new ClienteDAO(conn);
            List<ClienteDTO> clientes = clienteDAO.listarTodos();

            request.setAttribute("clientes", clientes);
            request.getRequestDispatcher("Clientes/cliente.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
