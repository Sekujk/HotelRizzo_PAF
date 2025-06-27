package Controller;

import Utils.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    private static final int MAX_INTENTOS = 5;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String claveIngresada = request.getParameter("clave");

        System.out.println("🔐 Intento de login para: " + usuario);

        try (Connection conn = Conexion.getConnection()) {

            if (conn == null) {
                System.out.println("❌ No se pudo establecer conexión a la base de datos.");
                response.sendRedirect("login.jsp?error=conexion");
                return;
            }

            String sql = """
                SELECT id_usuario, password_hash, activo, bloqueado, intentos_fallidos
                FROM Usuarios
                WHERE username = ?
            """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashAlmacenado = rs.getString("password_hash");
                boolean activo = rs.getBoolean("activo");
                boolean bloqueado = rs.getBoolean("bloqueado");
                int intentosFallidos = rs.getInt("intentos_fallidos");
                int idUsuario = rs.getInt("id_usuario");

                if (!activo || bloqueado) {
                    System.out.println("⚠️ Cuenta inactiva o bloqueada.");
                    response.sendRedirect("login.jsp?error=bloqueado");
                    return;
                }

                if (BCrypt.checkpw(claveIngresada, hashAlmacenado)) {
                    System.out.println("✅ Login exitoso");

                    // Resetear intentos fallidos
                    String resetSql = "UPDATE Usuarios SET intentos_fallidos = 0, ultimo_acceso = GETDATE() WHERE id_usuario = ?";
                    try (PreparedStatement resetStmt = conn.prepareStatement(resetSql)) {
                        resetStmt.setInt(1, idUsuario);
                        resetStmt.executeUpdate();
                    }

                    // Guardar sesión con el nombre correcto
                    HttpSession session = request.getSession();
                    session.setAttribute("usuarioLogueado", usuario); // 👈 Cambiado aquí

                    response.sendRedirect("dashboard");
                } else {
                    intentosFallidos++;
                    System.out.println("❌ Contraseña incorrecta. Intentos: " + intentosFallidos);

                    // Actualizar intentos y bloquear si se excede
                    String updateSql = "UPDATE Usuarios SET intentos_fallidos = ?, bloqueado = ? WHERE id_usuario = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, intentosFallidos);
                        updateStmt.setBoolean(2, intentosFallidos >= MAX_INTENTOS);
                        updateStmt.setInt(3, idUsuario);
                        updateStmt.executeUpdate();
                    }

                    if (intentosFallidos >= MAX_INTENTOS) {
                        response.sendRedirect("login.jsp?error=bloqueado");
                    } else {
                        response.sendRedirect("login.jsp?error=credenciales");
                    }
                }

            } else {
                System.out.println("❌ Usuario no encontrado");
                response.sendRedirect("login.jsp?error=credenciales");
            }

        } catch (Exception e) {
            System.out.println("❌ Excepción en LoginServlet: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=excepcion");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}
