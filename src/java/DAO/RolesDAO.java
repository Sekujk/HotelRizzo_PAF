package DAO;

import Model.RolesDTO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolesDAO {
    private final Connection conn;

    public RolesDAO(Connection conn) {
        this.conn = conn;
    }

    public List<RolesDTO> listarRolesActivos() throws SQLException {
        List<RolesDTO> roles = new ArrayList<>();
        String sql = "SELECT id_rol, nombre_rol, descripcion FROM Roles WHERE activo = 1";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                RolesDTO rol = new RolesDTO();
                rol.setIdRol(rs.getInt("id_rol"));
                rol.setNombreRol(rs.getString("nombre_rol"));
                rol.setDescripcion(rs.getString("descripcion"));
                rol.setActivo(true);
                roles.add(rol);
            }
        }
        return roles;
    }
}
