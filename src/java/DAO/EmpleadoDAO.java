package DAO;

import Model.EmpleadoDTO;
import java.sql.*;
import java.util.*;

public class EmpleadoDAO {

    private Connection conn;

    public EmpleadoDAO(Connection conn) {
        this.conn = conn;
    }

    public List<EmpleadoDTO> listarEmpleados() throws SQLException {
        String sql = "SELECT e.*, p.*, r.nombre_rol FROM Empleados e "
                + "INNER JOIN Personas p ON e.id_empleado = p.id_persona "
                + "INNER JOIN Roles r ON e.id_rol = r.id_rol WHERE p.activo = 1";
        List<EmpleadoDTO> lista = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                EmpleadoDTO emp = new EmpleadoDTO();

                // ID del empleado (mismo que persona)
                emp.setId(rs.getInt("id_persona"));
                emp.setIdEmpleado(rs.getInt("id_empleado")); // ← Nuevo

                // Datos personales heredados
                emp.setNombre(rs.getString("nombre"));
                emp.setApellido(rs.getString("apellido"));
                emp.setDni(rs.getString("dni"));
                emp.setTelefono(rs.getString("telefono"));
                emp.setCorreo(rs.getString("correo"));
                emp.setDireccion(rs.getString("direccion"));
                emp.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
                emp.setGenero(rs.getString("genero"));
                emp.setActivo(rs.getBoolean("activo"));

                // Datos de empleado
                emp.setIdRol(rs.getInt("id_rol"));
                emp.setNombreRol(rs.getString("nombre_rol"));
                emp.setFechaContratacion(rs.getDate("fecha_contratacion"));
                emp.setSalario(rs.getDouble("salario"));
                emp.setTurno(rs.getString("turno"));

                lista.add(emp);
            }
        }
        return lista;
    }

    public boolean insertarEmpleado(int idPersona, EmpleadoDTO empleado) throws SQLException {
        String sql = "INSERT INTO Empleados(id_empleado, id_rol, fecha_contratacion, salario, turno) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPersona);
            stmt.setInt(2, empleado.getIdRol());
            stmt.setDate(3, new java.sql.Date(empleado.getFechaContratacion().getTime()));
            stmt.setDouble(4, empleado.getSalario());
            stmt.setString(5, empleado.getTurno());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean actualizarEmpleado(EmpleadoDTO empleado) throws SQLException {
        String sql = "UPDATE Empleados SET id_rol = ?, fecha_contratacion = ?, salario = ?, turno = ? WHERE id_empleado = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empleado.getIdRol());
            stmt.setDate(2, new java.sql.Date(empleado.getFechaContratacion().getTime()));
            stmt.setDouble(3, empleado.getSalario());
            stmt.setString(4, empleado.getTurno());
            stmt.setInt(5, empleado.getIdEmpleado());
            return stmt.executeUpdate() > 0;
        }
    }

    public long contarEmpleadosActivos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Empleados e "
                + "INNER JOIN Personas p ON e.id_empleado = p.id_persona "
                + "WHERE p.activo = 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }

    public int contarTotalEmpleados() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Empleados";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public EmpleadoDTO obtenerEmpleadoPorId(int id) throws SQLException {
        String sql = "SELECT e.*, p.*, r.nombre_rol FROM Empleados e "
                + "INNER JOIN Personas p ON e.id_empleado = p.id_persona "
                + "INNER JOIN Roles r ON e.id_rol = r.id_rol "
                + "WHERE e.id_empleado = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    EmpleadoDTO emp = new EmpleadoDTO();
                    emp.setId(rs.getInt("id_persona")); // ID persona
                    emp.setIdEmpleado(rs.getInt("id_empleado")); // ID empleado (por claridad)
                    emp.setNombre(rs.getString("nombre"));
                    emp.setApellido(rs.getString("apellido"));
                    emp.setDni(rs.getString("dni"));
                    emp.setTelefono(rs.getString("telefono"));
                    emp.setCorreo(rs.getString("correo"));
                    emp.setDireccion(rs.getString("direccion"));
                    emp.setFechaNacimiento(rs.getDate("fecha_nacimiento"));
                    emp.setGenero(rs.getString("genero"));
                    emp.setActivo(rs.getBoolean("activo"));
                    emp.setIdRol(rs.getInt("id_rol"));
                    emp.setNombreRol(rs.getString("nombre_rol"));
                    emp.setFechaContratacion(rs.getDate("fecha_contratacion"));
                    emp.setSalario(rs.getDouble("salario"));
                    emp.setTurno(rs.getString("turno"));
                    return emp;
                }
            }
        }
        return null;
    }
    
    public boolean eliminarEmpleado(int idEmpleado) throws SQLException {
    String sql = "DELETE FROM Empleados WHERE id_empleado = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, idEmpleado);
        return stmt.executeUpdate() > 0;
    }
}

}
