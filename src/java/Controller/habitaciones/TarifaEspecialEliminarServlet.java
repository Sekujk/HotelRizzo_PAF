package Controller.habitaciones;

import DAO.TarifaEspecialDAO;
import Utils.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;

@WebServlet(name = "TarifaEspecialEliminarServlet", urlPatterns = {"/tarifaespecial_eliminar"})
public class TarifaEspecialEliminarServlet extends HttpServlet {

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
                new TarifaEspecialDAO(conn).eliminar(id);
                response.sendRedirect("tarifaespecial?mensaje=eliminado");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("tarifaespecial?mensaje=id_invalido");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("tarifaespecial?mensaje=error_eliminar");
        }
    }
}
