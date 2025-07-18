package Controller.habitaciones;

import DAO.TarifaEspecialDAO;
import DAO.TipoHabitacionDAO;
import Model.TarifaEspecialDTO;
import Model.TipoHabitacionDTO;
import Utils.Conexion;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "TarifaEspecialCrearServlet", urlPatterns = {"/tarifaespecial_crear"})
public class TarifaEspecialCrearServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            TipoHabitacionDAO tipoDao = new TipoHabitacionDAO(conn);
            List<TipoHabitacionDTO> tipos = tipoDao.listar();
            request.setAttribute("tipos", tipos);
            request.getRequestDispatcher("TarifaEspeciales/tarifaEspecial_crear.jsp")
                   .forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("tarifaespecial?mensaje=error_carga_tipos");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            TarifaEspecialDTO dto = new TarifaEspecialDTO();
            dto.setNombre(request.getParameter("nombre"));
            dto.setIdTipoHabitacion(Integer.parseInt(request.getParameter("idTipoHabitacion")));
            dto.setFechaInicio(LocalDate.parse(request.getParameter("fechaInicio")));
            dto.setFechaFin(LocalDate.parse(request.getParameter("fechaFin")));
            dto.setPrecioEspecial(Double.parseDouble(request.getParameter("precioEspecial")));
            dto.setTipoTarifa(request.getParameter("tipoTarifa"));
            dto.setActivo(true);

            new TarifaEspecialDAO(conn).insertar(dto);
            response.sendRedirect("tarifaespecial?mensaje=creado");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("tarifaespecial?mensaje=error_crear");
        }
    }
}
