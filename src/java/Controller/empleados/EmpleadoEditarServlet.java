package Controller.empleados;

import DAO.EmpleadoDAO;
import DAO.PersonaDAO;
import DAO.RolesDAO;
import Model.EmpleadoDTO;
import Model.PersonaDTO;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet(name = "EmpleadoEditar", urlPatterns = {"/empleado_editar"})
public class EmpleadoEditarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            int id = Integer.parseInt(request.getParameter("id"));

            EmpleadoDAO empleadoDAO = new EmpleadoDAO(conn);
            RolesDAO rolDAO = new RolesDAO(conn);

            EmpleadoDTO empleado = empleadoDAO.obtenerEmpleadoPorId(id);
            List<Model.RolesDTO> roles = rolDAO.listarRolesActivos();

            if (empleado == null) {
                response.sendRedirect("empleado.jsp?mensaje=no_encontrado");
                return;
            }

            request.setAttribute("empleado", empleado);
            request.setAttribute("roles", roles);
            request.getRequestDispatcher("empleado_editar.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("empleado.jsp?mensaje=error_editar");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            request.setCharacterEncoding("UTF-8");

            int id = Integer.parseInt(request.getParameter("id_rol"));

            PersonaDAO personaDAO = new PersonaDAO(conn);
            EmpleadoDAO empleadoDAO = new EmpleadoDAO(conn);

            // Datos de la persona
            PersonaDTO persona = new PersonaDTO();
            persona.setId(id);
            persona.setNombre(request.getParameter("nombre"));
            persona.setApellido(request.getParameter("apellido"));
            persona.setDni(request.getParameter("dni"));
            persona.setTelefono(request.getParameter("telefono"));
            persona.setCorreo(request.getParameter("correo"));
            persona.setDireccion(request.getParameter("direccion"));
            persona.setGenero(request.getParameter("genero"));
            persona.setFechaNacimiento(new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("fecha_nacimiento")));

            // Actualizar persona
            personaDAO.actualizarPersona(persona);

            // Datos del empleado
            EmpleadoDTO empleado = new EmpleadoDTO();
            empleado.setIdEmpleado(id);
            empleado.setIdRol(Integer.parseInt(request.getParameter("id_rol")));
            empleado.setFechaContratacion(new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("fecha_contratacion")));
            empleado.setSalario(Double.parseDouble(request.getParameter("salario")));
            empleado.setTurno(request.getParameter("turno"));

            // Actualizar empleado
            empleadoDAO.actualizarEmpleado(empleado);

            response.sendRedirect("empleado?mensaje=editado");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("empleado.jsp?mensaje=excepcion_editar");
        }
    }
}
