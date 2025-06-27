package DAO;

import Model.UsuarioDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {

    private final Connection conn;

    public UsuarioDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean insertarUsuario(UsuarioDTO usuario) throws Exception {
        String sql = "INSERT INTO Usuarios (id_empleado, username, password_hash, activo) VALUES (?, ?, ?, 1)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuario.getIdEmpleado());
            ps.setString(2, usuario.getUsername());
            ps.setString(3, usuario.getPasswordHash());
            return ps.executeUpdate() > 0;
        }
    }

    public UsuarioDTO obtenerPorIdEmpleado(int idEmpleado) throws Exception {
        String sql = "SELECT * FROM Usuarios WHERE id_empleado = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEmpleado);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    UsuarioDTO usuario = new UsuarioDTO();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setIdEmpleado(rs.getInt("id_empleado"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setPasswordHash(rs.getString("password_hash"));
                    return usuario;
                }
            }
        }
        return null;
    }

    public boolean eliminarPorIdEmpleado(int idEmpleado) throws Exception {
        String sql = "DELETE FROM Usuarios WHERE id_empleado = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEmpleado);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean existeUsername(String username) throws Exception {
    String sql = "SELECT COUNT(*) FROM Usuarios WHERE username = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, username);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    }
    return false;
}

}
