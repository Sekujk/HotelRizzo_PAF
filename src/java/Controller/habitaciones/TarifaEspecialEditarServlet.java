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

@WebServlet(name = "TarifaEspecialEditarServlet", urlPatterns = {"/tarifaespecial_editar"})
public class TarifaEspecialEditarServlet extends HttpServlet {

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
                List<TipoHabitacionDTO> tipos = new TipoHabitacionDAO(conn).listar();

                if (tarifa == null) {
                    response.sendRedirect("tarifaespecial?mensaje=no_encontrado");
                    return;
                }

                request.setAttribute("tarifa", tarifa);
                request.setAttribute("tipos", tipos);
                request.getRequestDispatcher("TarifaEspeciales/tarifaEspecial_editar.jsp")
                       .forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("tarifaespecial?mensaje=id_invalido");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("tarifaespecial?mensaje=error_cargar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            TarifaEspecialDTO dto = new TarifaEspecialDTO();
            dto.setIdTarifa(Integer.parseInt(request.getParameter("id")));
            dto.setNombre(request.getParameter("nombre"));
            dto.setIdTipoHabitacion(Integer.parseInt(request.getParameter("idTipoHabitacion")));
            dto.setFechaInicio(LocalDate.parse(request.getParameter("fechaInicio")));
            dto.setFechaFin(LocalDate.parse(request.getParameter("fechaFin")));
            dto.setPrecioEspecial(Double.parseDouble(request.getParameter("precioEspecial")));
            dto.setTipoTarifa(request.getParameter("tipoTarifa"));
            dto.setActivo(true);

            new TarifaEspecialDAO(conn).actualizar(dto);
            response.sendRedirect("tarifaespecial?mensaje=actualizado");
        } catch (NumberFormatException e) {
            response.sendRedirect("tarifaespecial?mensaje=id_invalido");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("tarifaespecial?mensaje=error_editar");
        }
    }
}
