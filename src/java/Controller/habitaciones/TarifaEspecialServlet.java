package Controller.habitaciones;

import DAO.TarifaEspecialDAO;
import Model.TarifaEspecialDTO;
import Utils.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "TarifaEspecialServlet", urlPatterns = {"/tarifaespecial"})
public class TarifaEspecialServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            TarifaEspecialDAO dao = new TarifaEspecialDAO(conn);
            List<TarifaEspecialDTO> tarifas = dao.listarTodos();
            long activas = tarifas.stream().filter(TarifaEspecialDTO::isActivo).count();

            request.setAttribute("tarifas", tarifas);
            request.setAttribute("activas", activas);
            request.setAttribute("total", tarifas.size());

            String mensaje = request.getParameter("mensaje");
            if (mensaje != null) request.setAttribute("mensaje", mensaje);

            request.getRequestDispatcher("TarifaEspeciales/tarifaEspecial.jsp")
                   .forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("dashboard?mensaje=error_tarifa");
        }
    }
}
