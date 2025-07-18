package Utils;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final Map<String, List<String>> permisosPorRol = new HashMap<>();
    private static final List<String> rutasPublicas = List.of(
            "/login", "/login.jsp", "/LoginServlet", "/logout",
            "/css/", "/js/", "/images/", "/index.jsp"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        permisosPorRol.put("Administrador", List.of(
                "/dashboard",
                "/empleado", "/empleado_crear", "/empleado_editar", "/empleado_eliminar", "/empleado_detalle",
                "/habitaciones", "/habitacion_crear", "/habitacion_editar", "/habitacion_detalle", "/habitacion_mantenimiento", "/habitacion_disponible",
                "/tipoHabitacion_crear", "/tipoHabitacion_editar", "/tipoHabitacion_eliminar", "/tipoHabitacion_detalle", "/tipoHabitacion",
                "/tarifaespecial_crear", "/tarifaespecial_editar", "/tarifaespecial_eliminar", "/tipoHabitacion_detalle", "/tarifaespecial",
                "/producto", "/producto_crear", "/producto_editar", "/producto_eliminar", "/producto_detalle", "/producto_activar", "/producto_desactivar",
                "/servicio", "/servicio_crear", "/servicio_editar", "/servicio_eliminar", "/servicio_detalle", "/servicio_activar", "/servicio_desactivar",
                "/clientes", "/cliente_detalle", "/cliente_editar",
                "/reservas", "/reserva_crear", "/reserva_editar", "/reserva_detalle",
                "/reserva_habitaciones", "/reserva_servicios", "/reserva_checkout", "/reserva_checkin", "/reserva_cancelar"
        ));

        permisosPorRol.put("Recepcionista", List.of(
                "/dashboard",
                "/clientes", "/cliente_detalle",
                "/habitaciones", "/habitacion_detalle",
                "/producto", "/producto_detalle",
                "/servicio", "/servicio_detalle",
                "/reservas", "/reserva_crear", "/reserva_editar", "/reserva_detalle",
                "/reserva_habitaciones", "/reserva_servicios", "/reserva_checkout", "/reserva_checkin", "/reserva_cancelar"
        ));

        permisosPorRol.put("Gerente", List.of(
                "/dashboard",
                "/reporte_general", "/reporte_ingresos"
        ));
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getServletPath();

        // Rutas públicas
        for (String ruta : rutasPublicas) {
            if (path.startsWith(ruta)) {
                chain.doFilter(req, res);
                return;
            }
        }

        HttpSession session = request.getSession(false);

        // Verificar sesión
        if (session == null || session.getAttribute("rol") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String rol = (String) session.getAttribute("rol");
        List<String> rutasPermitidas = permisosPorRol.getOrDefault(rol, new ArrayList<>());

        boolean tienePermiso = rutasPermitidas.contains(path);

        if (!tienePermiso) {
            response.sendRedirect("sin_permiso.jsp");
            return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }
}
