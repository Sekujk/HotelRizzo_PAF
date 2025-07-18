package DAO;

import Model.ClienteDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private final Connection conn;

    public ClienteDAO(Connection conn) {
        this.conn = conn;
    }

    public ClienteDTO buscarPorDni(String dni) throws SQLException {
        String sql = "SELECT p.*, c.fecha_registro, c.tipo_cliente, c.empresa, c.observaciones " +
                     "FROM Personas p JOIN Clientes c ON p.id_persona = c.id_cliente " +
                     "WHERE p.dni = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public ClienteDTO buscarPorId(int idCliente) throws SQLException {
        String sql = "SELECT p.*, c.fecha_registro, c.tipo_cliente, c.empresa, c.observaciones " +
                     "FROM Personas p JOIN Clientes c ON p.id_persona = c.id_cliente " +
                     "WHERE p.id_persona = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public boolean insertar(ClienteDTO c) throws SQLException {
        String sqlCliente = "INSERT INTO Clientes (id_cliente, fecha_registro, tipo_cliente, empresa, observaciones) " +
                            "VALUES (?, GETDATE(), ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sqlCliente)) {
            stmt.setInt(1, c.getId());
            stmt.setString(2, c.getTipoCliente());
            stmt.setString(3, c.getEmpresa());
            stmt.setString(4, c.getObservaciones());
            return stmt.executeUpdate() > 0;
        }
    }

    public List<ClienteDTO> listarTodos() throws SQLException {
        List<ClienteDTO> lista = new ArrayList<>();
        String sql = "SELECT p.*, c.fecha_registro, c.tipo_cliente, c.empresa, c.observaciones " +
                     "FROM Personas p JOIN Clientes c ON p.id_persona = c.id_cliente";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private ClienteDTO mapear(ResultSet rs) throws SQLException {
        ClienteDTO c = new ClienteDTO();

        c.setId(rs.getInt("id_persona"));
        c.setNombre(rs.getString("nombre"));
        c.setApellido(rs.getString("apellido"));
        c.setDni(rs.getString("dni"));
        c.setTelefono(rs.getString("telefono"));
        c.setCorreo(rs.getString("correo"));
        c.setDireccion(rs.getString("direccion"));
        c.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
        c.setGenero(rs.getString("genero"));
        c.setActivo(rs.getBoolean("activo"));

        c.setFechaRegistro(rs.getDate("fecha_registro"));
        c.setTipoCliente(rs.getString("tipo_cliente"));
        c.setEmpresa(rs.getString("empresa"));
        c.setObservaciones(rs.getString("observaciones"));

        return c;
    }
}
