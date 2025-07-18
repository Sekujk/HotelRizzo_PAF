package Controller.reservas;

import DAO.*;
import Model.*;
import Utils.Conexion;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ReservaServiciosServlet", value = "/reserva_servicios")
public class ReservaServiciosServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("agregar_servicio".equals(action)) {
            mostrarFormularioServicio(request, response);
        } else if ("agregar_producto".equals(action)) {
            mostrarFormularioProducto(request, response);
        } else {
            mostrarServiciosReserva(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("agregar_servicio".equals(action)) {
            agregarServicio(request, response);
        } else if ("agregar_producto".equals(action)) {
            agregarProducto(request, response);
        }
    }
    
    /**
     * Mostrar servicios y productos de una reserva
     */
    private void mostrarServiciosReserva(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idReservaStr = request.getParameter("id");
        if (idReservaStr == null) {
            response.sendRedirect("reservas");
            return;
        }
        
        try {
            int idReserva = Integer.parseInt(idReservaStr);
            Connection conn = Conexion.getConnection();
            
            // Obtener reserva
            ReservaDAO reservaDAO = new ReservaDAO(conn);
            ReservaDTO reserva = reservaDAO.obtenerPorId(idReserva);
            
            if (reserva == null) {
                request.setAttribute("error", "Reserva no encontrada");
                response.sendRedirect("reservas");
                return;
            }
            
            // Obtener servicios consumidos
            ConsumoServicioDAO consumoServicioDAO = new ConsumoServicioDAO(conn);
            List<ConsumoServicioDTO> serviciosConsumidos = consumoServicioDAO.listarPorReserva(idReserva);
            BigDecimal totalServicios = consumoServicioDAO.obtenerTotalConsumosPorReserva(idReserva);
            
            // Obtener productos consumidos
            ConsumoProductoDAO consumoProductoDAO = new ConsumoProductoDAO(conn);
            List<ConsumoProductoDTO> productosConsumidos = consumoProductoDAO.listarPorReserva(idReserva);
            BigDecimal totalProductos = consumoProductoDAO.obtenerTotalConsumosPorReserva(idReserva);
            
            request.setAttribute("reserva", reserva);
            request.setAttribute("serviciosConsumidos", serviciosConsumidos);
            request.setAttribute("productosConsumidos", productosConsumidos);
            request.setAttribute("totalServicios", totalServicios != null ? totalServicios : BigDecimal.ZERO);
            request.setAttribute("totalProductos", totalProductos != null ? totalProductos : BigDecimal.ZERO);
            
            request.getRequestDispatcher("Reservas/reserva_servicios.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            response.sendRedirect("reservas");
        }
    }
    
    /**
     * Mostrar formulario para agregar servicio - CONSULTA DIRECTA
     */
    private void mostrarFormularioServicio(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idReservaStr = request.getParameter("id");
        if (idReservaStr == null) {
            response.sendRedirect("reservas");
            return;
        }
        
        try {
            int idReserva = Integer.parseInt(idReservaStr);
            Connection conn = Conexion.getConnection();
            
            // Obtener reserva
            ReservaDAO reservaDAO = new ReservaDAO(conn);
            ReservaDTO reserva = reservaDAO.obtenerPorId(idReserva);
            
            if (reserva == null || !"CheckIn".equals(reserva.getEstado())) {
                request.setAttribute("error", "Solo se pueden agregar servicios a reservas en Check-In");
                response.sendRedirect("reserva_servicios?id=" + idReserva);
                return;
            }
            
            // ✅ CONSULTA DIRECTA PARA SERVICIOS
            List<ServicioDTO> servicios = new ArrayList<>();
            String sql = "SELECT * FROM Servicios WHERE activo = 1 ORDER BY nombre";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ServicioDTO servicio = new ServicioDTO();
                    servicio.setIdServicio(rs.getInt("id_servicio"));
                    servicio.setNombre(rs.getString("nombre"));
                    servicio.setDescripcion(rs.getString("descripcion"));
                    servicio.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    servicio.setActivo(rs.getBoolean("activo"));
                    servicios.add(servicio);
                }
            }
            
            System.out.println("📋 DEBUG: Servicios encontrados: " + servicios.size());
            for (ServicioDTO s : servicios) {
                System.out.println("   - " + s.getNombre() + " (S/. " + s.getPrecioUnitario() + ")");
            }
            
            request.setAttribute("reserva", reserva);
            request.setAttribute("serviciosDisponibles", servicios);
            
            request.getRequestDispatcher("Reservas/reserva_agregar_servicio.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            response.sendRedirect("reservas");
        }
    }
    
    /**
     * Mostrar formulario para agregar producto - CONSULTA DIRECTA
     */
    private void mostrarFormularioProducto(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idReservaStr = request.getParameter("id");
        if (idReservaStr == null) {
            response.sendRedirect("reservas");
            return;
        }
        
        try {
            int idReserva = Integer.parseInt(idReservaStr);
            Connection conn = Conexion.getConnection();
            
            // Obtener reserva
            ReservaDAO reservaDAO = new ReservaDAO(conn);
            ReservaDTO reserva = reservaDAO.obtenerPorId(idReserva);
            
            if (reserva == null || !"CheckIn".equals(reserva.getEstado())) {
                request.setAttribute("error", "Solo se pueden agregar productos a reservas en Check-In");
                response.sendRedirect("reserva_servicios?id=" + idReserva);
                return;
            }
            
            // ✅ CONSULTA DIRECTA PARA PRODUCTOS
            List<ProductoDTO> productos = new ArrayList<>();
            String sql = "SELECT * FROM Productos WHERE activo = 1 AND stock > 0 ORDER BY nombre";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    ProductoDTO producto = new ProductoDTO();
                    producto.setIdProducto(rs.getInt("id_producto"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setDescripcion(rs.getString("descripcion"));
                    producto.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    producto.setStock(rs.getInt("stock"));
                    producto.setStockMinimo(rs.getInt("stock_minimo"));
                    producto.setActivo(rs.getBoolean("activo"));
                    productos.add(producto);
                }
            }
            
            System.out.println("📦 DEBUG: Productos encontrados: " + productos.size());
            for (ProductoDTO p : productos) {
                System.out.println("   - " + p.getNombre() + " (Stock: " + p.getStock() + ", S/. " + p.getPrecioUnitario() + ")");
            }
            
            request.setAttribute("reserva", reserva);
            request.setAttribute("productosDisponibles", productos);
            
            request.getRequestDispatcher("Reservas/reserva_agregar_producto.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            response.sendRedirect("reservas");
        }
    }
    
    /**
     * Agregar servicio a la reserva
     */
    private void agregarServicio(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idReservaStr = request.getParameter("id_reserva");
        String idServicioStr = request.getParameter("id_servicio");
        String cantidadStr = request.getParameter("cantidad");
        String observaciones = request.getParameter("observaciones");
        
        try {
            int idReserva = Integer.parseInt(idReservaStr);
            int idServicio = Integer.parseInt(idServicioStr);
            BigDecimal cantidad = new BigDecimal(cantidadStr);
            
            HttpSession session = request.getSession();
            Integer idEmpleado = (Integer) session.getAttribute("empleadoId");
            if (idEmpleado == null) idEmpleado = 1;
            
            Connection conn = Conexion.getConnection();
            
            // Obtener precio del servicio
            double precioUnitario = 0.0;
            String sqlPrecio = "SELECT precio_unitario FROM Servicios WHERE id_servicio = ? AND activo = 1";
            try (PreparedStatement ps = conn.prepareStatement(sqlPrecio)) {
                ps.setInt(1, idServicio);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    precioUnitario = rs.getDouble("precio_unitario");
                } else {
                    request.setAttribute("error", "Servicio no encontrado");
                    response.sendRedirect("reserva_servicios?action=agregar_servicio&id=" + idReserva);
                    return;
                }
            }
            
            // Crear consumo
            ConsumoServicioDTO consumo = new ConsumoServicioDTO();
            consumo.setIdReserva(idReserva);
            consumo.setIdServicio(idServicio);
            consumo.setCantidad(cantidad);
            consumo.setPrecioUnitario(new BigDecimal(precioUnitario));
            consumo.setFechaConsumo(LocalDate.now());
            consumo.setHoraConsumo(LocalTime.now());
            consumo.setIdEmpleado(idEmpleado);
            consumo.setObservaciones(observaciones);
            
            // Insertar
            ConsumoServicioDAO consumoDAO = new ConsumoServicioDAO(conn);
            boolean insertado = consumoDAO.insertar(consumo);
            
            if (insertado) {
                session.setAttribute("mensaje", "Servicio agregado exitosamente");
            } else {
                request.setAttribute("error", "No se pudo agregar el servicio");
            }
            
            response.sendRedirect("reserva_servicios?id=" + idReserva);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            response.sendRedirect("reserva_servicios?action=agregar_servicio&id=" + idReservaStr);
        }
    }
    
    /**
     * Agregar producto a la reserva
     */
    private void agregarProducto(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idReservaStr = request.getParameter("id_reserva");
        String idProductoStr = request.getParameter("id_producto");
        String cantidadStr = request.getParameter("cantidad");
        String observaciones = request.getParameter("observaciones");
        
        try {
            int idReserva = Integer.parseInt(idReservaStr);
            int idProducto = Integer.parseInt(idProductoStr);
            int cantidad = Integer.parseInt(cantidadStr);
            
            HttpSession session = request.getSession();
            Integer idEmpleado = (Integer) session.getAttribute("empleadoId");
            if (idEmpleado == null) idEmpleado = 1;
            
            Connection conn = Conexion.getConnection();
            
            // Obtener producto y verificar stock
            double precioUnitario = 0.0;
            int stockActual = 0;
            String sqlProducto = "SELECT precio_unitario, stock FROM Productos WHERE id_producto = ? AND activo = 1";
            try (PreparedStatement ps = conn.prepareStatement(sqlProducto)) {
                ps.setInt(1, idProducto);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    precioUnitario = rs.getDouble("precio_unitario");
                    stockActual = rs.getInt("stock");
                } else {
                    request.setAttribute("error", "Producto no encontrado");
                    response.sendRedirect("reserva_servicios?action=agregar_producto&id=" + idReserva);
                    return;
                }
            }
            
            if (stockActual < cantidad) {
                request.setAttribute("error", "Stock insuficiente. Disponible: " + stockActual);
                response.sendRedirect("reserva_servicios?action=agregar_producto&id=" + idReserva);
                return;
            }
            
            // Crear consumo
            ConsumoProductoDTO consumo = new ConsumoProductoDTO();
            consumo.setIdReserva(idReserva);
            consumo.setIdProducto(idProducto);
            consumo.setCantidad(cantidad);
            consumo.setPrecioUnitario(new BigDecimal(precioUnitario));
            consumo.setFechaConsumo(LocalDate.now());
            consumo.setHoraConsumo(LocalTime.now());
            consumo.setIdEmpleado(idEmpleado);
            consumo.setObservaciones(observaciones);
            
            // Insertar (tu DAO maneja el stock automáticamente)
            ConsumoProductoDAO consumoDAO = new ConsumoProductoDAO(conn);
            boolean insertado = consumoDAO.insertar(consumo);
            
            if (insertado) {
                session.setAttribute("mensaje", "Producto agregado exitosamente");
            } else {
                request.setAttribute("error", "No se pudo agregar el producto");
            }
            
            response.sendRedirect("reserva_servicios?id=" + idReserva);
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error: " + e.getMessage());
            response.sendRedirect("reserva_servicios?action=agregar_producto&id=" + idReservaStr);
        }
    }
}