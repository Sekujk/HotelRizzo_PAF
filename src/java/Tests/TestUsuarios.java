/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Tests;

import Utils.Conexion;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

public class TestUsuarios {
    public static void main(String[] args) {
        // Contraseña común a todos
        String passwordPlano = "admin123";
        String passwordHash = BCrypt.hashpw(passwordPlano, BCrypt.gensalt());

        // Datos de ejemplo: nombre, apellido, dni, id_rol, username
        Object[][] empleados = {
            {"Alejandro", "Seclen", "76867256", 1, "seclenleo"},
        };

        try (Connection conn = Conexion.getConnection()) {
            for (Object[] emp : empleados) {
                String nombre = (String) emp[0];
                String apellido = (String) emp[1];
                String dni = (String) emp[2];
                int rolId = (int) emp[3];
                String username = (String) emp[4];

                // 1. Insertar persona
                String sqlPersona = "INSERT INTO Personas (nombre, apellido, dni, genero) VALUES (?, ?, ?, 'M')";
                PreparedStatement stmtPersona = conn.prepareStatement(sqlPersona);
                stmtPersona.setString(1, nombre);
                stmtPersona.setString(2, apellido);
                stmtPersona.setString(3, dni);
                stmtPersona.executeUpdate();

                // 2. Insertar empleado
                String sqlEmpleado = "INSERT INTO Empleados (id_empleado, id_rol, fecha_contratacion, salario, turno) " +
                                     "VALUES ((SELECT id_persona FROM Personas WHERE dni = ?), ?, GETDATE(), 2500, 'Mañana')";
                PreparedStatement stmtEmpleado = conn.prepareStatement(sqlEmpleado);
                stmtEmpleado.setString(1, dni);
                stmtEmpleado.setInt(2, rolId);
                stmtEmpleado.executeUpdate();

                // 3. Insertar usuario
                String sqlUsuario = "INSERT INTO Usuarios (id_empleado, username, password_hash) " +
                                    "VALUES ((SELECT id_persona FROM Personas WHERE dni = ?), ?, ?)";
                PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario);
                stmtUsuario.setString(1, dni);
                stmtUsuario.setString(2, username);
                stmtUsuario.setString(3, passwordHash);
                stmtUsuario.executeUpdate();

                System.out.println("✔ Usuario creado: " + username);
            }

            System.out.println("✅ Todos los empleados fueron registrados correctamente.");
        } catch (SQLException e) {
            System.err.println("❌ Error al insertar usuarios: " + e.getMessage());
        }
    }
}
