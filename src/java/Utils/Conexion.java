/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=rizzo_db";
    private static final String USER = "sa"; // ← Cambia esto si tu usuario es diferente
    private static final String PASSWORD = "sekuren06"; // ← Reemplaza por tu clave real

    public static Connection getConnection() {
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Conexión exitosa a SQL Server.");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver JDBC no encontrado.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a la base de datos pi.");
            e.printStackTrace(); // Imprime el error exacto
        }
        return conn;
    }
}

