package DAO;

import Model.PersonaDTO;
import java.sql.*;

public class PersonaDAO {
    private Connection conn;

    public PersonaDAO(Connection conn) {
        this.conn = conn;
    }

    public int insertarPersona(PersonaDTO persona) throws SQLException {
        String sql = "INSERT INTO Personas(nombre, apellido, dni, telefono, correo, direccion, fecha_nacimiento, genero) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, persona.getNombre());
            stmt.setString(2, persona.getApellido());
            stmt.setString(3, persona.getDni());
            stmt.setString(4, persona.getTelefono());
            stmt.setString(5, persona.getCorreo());
            stmt.setString(6, persona.getDireccion());
            stmt.setDate(7, new java.sql.Date(persona.getFechaNacimiento().getTime()));
            stmt.setString(8, persona.getGenero());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }
    
    public boolean actualizarPersona(PersonaDTO persona) throws SQLException {
    String sql = "UPDATE Personas SET nombre = ?, apellido = ?, dni = ?, telefono = ?, correo = ?, direccion = ?, genero = ?, fecha_nacimiento = ? WHERE id_persona = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, persona.getNombre());
        stmt.setString(2, persona.getApellido());
        stmt.setString(3, persona.getDni());
        stmt.setString(4, persona.getTelefono());
        stmt.setString(5, persona.getCorreo());
        stmt.setString(6, persona.getDireccion());
        stmt.setString(7, persona.getGenero());
        stmt.setDate(8, new java.sql.Date(persona.getFechaNacimiento().getTime()));
        stmt.setInt(9, persona.getId());
        return stmt.executeUpdate() > 0;
    }
}

    public boolean eliminarPersona(int idPersona) throws SQLException {
    String sql = "DELETE FROM Personas WHERE id_persona = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, idPersona);
        return stmt.executeUpdate() > 0;
    }
}

    public boolean existeDni(String dni) throws Exception {
    String sql = "SELECT COUNT(*) FROM Personas WHERE dni = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, dni);
        ResultSet rs = stmt.executeQuery();
        return rs.next() && rs.getInt(1) > 0;
    }
}

    
}