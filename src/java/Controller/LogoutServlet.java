package Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Obtener sesión actual sin crear una nueva
        HttpSession session = request.getSession(false);

        if (session != null) {
            String usuario = (String) session.getAttribute("usuarioLogueado");
            session.invalidate();
            System.out.println("🔓 Sesión cerrada para: " + usuario);
        } else {
            System.out.println("⚠️ No había sesión activa al intentar cerrar sesión.");
        }

        // Redirigir al login
        response.sendRedirect("LoginServlet?logout=ok");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Para cualquier GET también se cierra sesión
        doPost(request, response);
    }
}
