package Controller.habitaciones;

import DAO.TarifaEspecialDAO;
import Model.TarifaEspecialDTO;
import Utils.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "TarifaEspecialDetalleServlet", urlPatterns = {"/tarifaespecial_detalle"})
public class TarifaEspecialDetalleServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect("tarifaespecial?mensaje=sin_id");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            try (Connection conn = Conexion.getConnection()) {
                TarifaEspecialDTO tarifa = new TarifaEspecialDAO(conn).obtenerPorId(id);
                if (tarifa == null) {
                    response.sendRedirect("tarifaespecial?mensaje=no_encontrado");
                    return;
                }
                request.setAttribute("tarifa", tarifa);
                request.getRequestDispatcher("TarifaEspeciales/tarifaEspecial_detalle.jsp")
                       .forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("tarifaespecial?mensaje=id_invalido");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("tarifaespecial?mensaje=error_detalle");
        }
    }
}
