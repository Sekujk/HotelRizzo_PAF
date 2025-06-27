package Controller;

import DAO.EmpleadoDAO;
import DAO.PersonaDAO;
import DAO.UsuarioDAO;
import DAO.RolesDAO;
import Model.EmpleadoDTO;
import Model.PersonaDTO;
import Model.UsuarioDTO;
import Utils.Conexion;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "EmpleadoCrear", urlPatterns = {"/empleado_crear"})
public class EmpleadoCrearServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            cargarRolesYCredenciales(conn, request);
            request.getRequestDispatcher("empleado_crear.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("empleado.jsp?mensaje=error_roles");
        }
    }

    @Override

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (Connection conn = Conexion.getConnection()) {
            conn.setAutoCommit(false); // ⛔ Desactivamos auto-commit para usar transacciones

            request.setCharacterEncoding("UTF-8");

            PersonaDAO personaDAO = new PersonaDAO(conn);
            EmpleadoDAO empleadoDAO = new EmpleadoDAO(conn);
            UsuarioDAO usuarioDAO = new UsuarioDAO(conn);
            RolesDAO rolDAO = new RolesDAO(conn);

            // Validar DNI duplicado (opcional pero recomendado)
            String dni = request.getParameter("dni");
            if (personaDAO.existeDni(dni)) {
                reenviarConError(request, response, conn, "dni_duplicado");
                return;
            }

            // Insertar persona
            PersonaDTO persona = new PersonaDTO();
            persona.setNombre(request.getParameter("nombre"));
            persona.setApellido(request.getParameter("apellido"));
            persona.setDni(dni);
            persona.setTelefono(request.getParameter("telefono"));
            persona.setCorreo(request.getParameter("correo"));
            persona.setDireccion(request.getParameter("direccion"));
            persona.setGenero(request.getParameter("genero"));
            persona.setFechaNacimiento(new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("fecha_nacimiento")));

            int idPersona = personaDAO.insertarPersona(persona);

            // Insertar empleado
            EmpleadoDTO empleado = new EmpleadoDTO();
            empleado.setIdEmpleado(idPersona);
            empleado.setIdRol(Integer.parseInt(request.getParameter("id_rol")));
            empleado.setFechaContratacion(new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("fecha_contratacion")));
            empleado.setSalario(Double.parseDouble(request.getParameter("salario")));
            empleado.setTurno(request.getParameter("turno"));

            if (!empleadoDAO.insertarEmpleado(idPersona, empleado)) {
                conn.rollback(); // 🔁 Revertir si falla
                reenviarConError(request, response, conn, "error_empleado");
                return;
            }

            // Crear usuario
            String username = generarUsername(persona, usuarioDAO);
            String passwordVisible = generarPassword(persona);
            String passwordHash = BCrypt.hashpw(passwordVisible, BCrypt.gensalt());

            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setIdEmpleado(idPersona);
            usuario.setUsername(username);
            usuario.setPasswordHash(passwordHash);

            if (!usuarioDAO.insertarUsuario(usuario)) {
                conn.rollback(); // 🔁 Revertir si falla
                reenviarConError(request, response, conn, "error_usuario");
                return;
            }

            conn.commit(); // ✅ Confirmar todo si no hubo errores

            HttpSession session = request.getSession();
            session.setAttribute("usuario_generado", username);
            session.setAttribute("clave_generada", passwordVisible);

            response.sendRedirect("empleado_crear");

        } catch (Exception e) {
            e.printStackTrace();
            try {
                Conexion.getConnection().rollback(); // En caso de excepción inesperada
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            response.sendRedirect("empleado.jsp?mensaje=excepcion");
        }
    }

    private void cargarRolesYCredenciales(Connection conn, HttpServletRequest request) throws Exception {
        RolesDAO rolDAO = new RolesDAO(conn);
        List<Model.RolesDTO> roles = rolDAO.listarRolesActivos();
        request.setAttribute("roles", roles);

        HttpSession session = request.getSession();
        request.setAttribute("usuario_generado", session.getAttribute("usuario_generado"));
        request.setAttribute("clave_generada", session.getAttribute("clave_generada"));
        session.removeAttribute("usuario_generado");
        session.removeAttribute("clave_generada");
    }

    private void reenviarConError(HttpServletRequest request, HttpServletResponse response, Connection conn, String errorKey)
            throws Exception {
        cargarRolesYCredenciales(conn, request);
        request.setAttribute("mensaje", errorKey);
        request.getRequestDispatcher("empleado_crear.jsp").forward(request, response);
    }

    private String generarUsername(PersonaDTO persona, UsuarioDAO usuarioDAO) throws Exception {
        String nombreParte = safeSubstring(persona.getNombre(), 3);
        String apellidoParte = safeSubstring(persona.getApellido(), 3);
        String dniParte = safeSubstring(persona.getDni(), 4);

        String baseUsername = (nombreParte + apellidoParte + dniParte).toLowerCase().replaceAll("\\s+", "");
        String username = baseUsername;
        int intento = 1;
        while (usuarioDAO.existeUsername(username)) {
            username = baseUsername + intento;
            intento++;
        }
        return username;
    }

    private String generarPassword(PersonaDTO persona) {
        String nombreParte = safeSubstring(persona.getNombre(), 3);
        String correoParte = safeSubstring(persona.getCorreo() != null ? persona.getCorreo() : "correo", 3);
        int randomNum = new Random().nextInt(9000) + 1000;
        return (nombreParte + correoParte + randomNum).toLowerCase();
    }

    private String safeSubstring(String texto, int longitud) {
        return texto != null && texto.length() >= longitud ? texto.substring(0, longitud) : texto;
    }
}
