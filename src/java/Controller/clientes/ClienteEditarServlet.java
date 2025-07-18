package Controller.clientes;

import DAO.ClienteDAO;
import DAO.PersonaDAO;
import Model.ClienteDTO;
import Model.PersonaDTO;
import Utils.Conexion;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/cliente_editar")
public class ClienteEditarServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect("clientes");
            return;
        }

        mostrarFormularioEdicion(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        actualizarCliente(request, response);
    }

    private void mostrarFormularioEdicion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idStr = request.getParameter("id");
        Connection connection = null;
        try {
            int idCliente = Integer.parseInt(idStr);
            connection = Conexion.getConnection();

            ClienteDAO clienteDAO = new ClienteDAO(connection);
            ClienteDTO cliente = clienteDAO.buscarPorId(idCliente);

            if (cliente == null) {
                response.sendRedirect("clientes");
                return;
            }

            request.setAttribute("cliente", cliente);
            request.getRequestDispatcher("Clientes/cliente_editar.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("clientes");
        } finally {
            if (connection != null) try { connection.close(); } catch (Exception ignored) {}
        }
    }

    private void actualizarCliente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            HttpSession session = request.getSession(false);
        String idStr = request.getParameter("id_cliente");
        String dni = request.getParameter("dni");
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String telefono = request.getParameter("telefono");
        String correo = request.getParameter("correo");
        String direccion = request.getParameter("direccion");
        String genero = request.getParameter("genero");
        String fechaNacimientoStr = request.getParameter("fecha_nacimiento");

        String tipoCliente = request.getParameter("tipo_cliente");
        String empresa = request.getParameter("empresa");
        String observaciones = request.getParameter("observaciones");

        Connection connection = null;

        try {
            int idCliente = Integer.parseInt(idStr);

            if (dni == null || !dni.matches("\\d{8}")) {
                request.setAttribute("error", "El DNI debe tener 8 dígitos");
                mostrarFormularioEdicion(request, response);
                return;
            }

            if (nombre == null || nombre.trim().isEmpty() || apellido == null || apellido.trim().isEmpty()) {
                request.setAttribute("error", "Nombre y Apellido son obligatorios");
                mostrarFormularioEdicion(request, response);
                return;
            }

            if (correo != null && !correo.trim().isEmpty() &&
                    !correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                request.setAttribute("error", "Correo electrónico inválido");
                mostrarFormularioEdicion(request, response);
                return;
            }

            Date fechaNacimiento = null;
            if (fechaNacimientoStr != null && !fechaNacimientoStr.isEmpty()) {
                try {
                    fechaNacimiento = new SimpleDateFormat("yyyy-MM-dd").parse(fechaNacimientoStr);
                } catch (Exception e) {
                    request.setAttribute("error", "Formato de fecha inválido");
                    mostrarFormularioEdicion(request, response);
                    return;
                }
            }

            connection = Conexion.getConnection();
            ClienteDAO clienteDAO = new ClienteDAO(connection);
            PersonaDAO personaDAO = new PersonaDAO(connection);

            // Verificar que el DNI no exista en otra persona
            ClienteDTO clienteExistente = clienteDAO.buscarPorDni(dni);
            if (clienteExistente != null && clienteExistente.getId() != idCliente) {
                request.setAttribute("error", "El DNI ya está registrado por otro cliente");
                mostrarFormularioEdicion(request, response);
                return;
            }

            // Actualizar Persona
            PersonaDTO persona = new PersonaDTO();
            persona.setId(idCliente);
            persona.setDni(dni.trim());
            persona.setNombre(nombre.trim());
            persona.setApellido(apellido.trim());
            persona.setTelefono(telefono != null ? telefono.trim() : "");
            persona.setCorreo(correo != null ? correo.trim() : "");
            persona.setDireccion(direccion != null ? direccion.trim() : "");
            persona.setGenero(genero != null ? genero : "Otro");
            persona.setFechaNacimiento(fechaNacimiento != null ? fechaNacimiento : new Date());

            boolean personaActualizada = personaDAO.actualizarPersona(persona);

            // Actualizar Cliente
            ClienteDTO cliente = new ClienteDTO();
            cliente.setId(idCliente);
            cliente.setTipoCliente(tipoCliente != null ? tipoCliente : "Individual");
            cliente.setEmpresa(empresa != null ? empresa.trim() : "");
            cliente.setObservaciones(observaciones != null ? observaciones.trim() : "");

            boolean clienteActualizado = actualizarDatosCliente(connection, cliente);

            if (personaActualizada && clienteActualizado) {
                session.setAttribute("mensaje", "Cliente actualizado correctamente");
                response.sendRedirect("clientes");
            } else {
                request.setAttribute("error", "No se pudo actualizar el cliente");
                mostrarFormularioEdicion(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            mostrarFormularioEdicion(request, response);
        } finally {
            if (connection != null) try { connection.close(); } catch (Exception ignored) {}
        }
    }

    private boolean actualizarDatosCliente(Connection conn, ClienteDTO cliente) throws Exception {
        String sql = "UPDATE Clientes SET tipo_cliente = ?, empresa = ?, observaciones = ? WHERE id_cliente = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getTipoCliente());
            stmt.setString(2, cliente.getEmpresa());
            stmt.setString(3, cliente.getObservaciones());
            stmt.setInt(4, cliente.getId());
            return stmt.executeUpdate() > 0;
        }
    }
}
