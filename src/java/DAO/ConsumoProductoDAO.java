package DAO;

import Model.ConsumoProductoDTO;
import Utils.Conexion;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ConsumoProductoDAO {
    private Connection connection;

    public ConsumoProductoDAO(Connection connection) {
        this.connection = connection;
    }

    // Constructor alternativo que obtiene conexión automáticamente
    public ConsumoProductoDAO() {
        this.connection = Conexion.getConnection();
    }

    /**
     * ✅ INSERTAR NUEVO CONSUMO DE PRODUCTO
     */
    public boolean insertar(ConsumoProductoDTO consumo) throws SQLException {
        // Verificar stock disponible antes de insertar
        if (!verificarStock(consumo.getIdProducto(), consumo.getCantidad())) {
            System.out.println("❌ Stock insuficiente para el producto");
            return false;
        }

        String sql = """
            INSERT INTO ConsumoProductos (id_reserva, id_producto, cantidad, precio_unitario, 
                                        fecha_consumo, hora_consumo, id_empleado, observaciones)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, consumo.getIdReserva());
            ps.setInt(2, consumo.getIdProducto());
            ps.setInt(3, consumo.getCantidad());
            ps.setBigDecimal(4, consumo.getPrecioUnitario());
            ps.setDate(5, Date.valueOf(consumo.getFechaConsumo()));
            ps.setTime(6, Time.valueOf(consumo.getHoraConsumo()));
            ps.setInt(7, consumo.getIdEmpleado());
            ps.setString(8, consumo.getObservaciones());
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Obtener el ID generado
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    consumo.setIdConsumo(rs.getInt(1));
                }
                System.out.println("✅ Consumo de producto registrado: " + consumo.getNombreProducto());
                return true;
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar consumo de producto: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    /**
     * ✅ INSERTAR MÚLTIPLES CONSUMOS DE PRODUCTOS
     */
    public boolean insertarMultiples(List<ConsumoProductoDTO> consumos) throws SQLException {
        String sql = """
            INSERT INTO ConsumoProductos (id_reserva, id_producto, cantidad, precio_unitario, 
                                        fecha_consumo, hora_consumo, id_empleado, observaciones)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try {
            connection.setAutoCommit(false); // Iniciar transacción
            
            // Verificar stock de todos los productos antes de proceder
            for (ConsumoProductoDTO consumo : consumos) {
                if (!verificarStock(consumo.getIdProducto(), consumo.getCantidad())) {
                    throw new SQLException("Stock insuficiente para producto ID: " + consumo.getIdProducto());
                }
            }
            
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (ConsumoProductoDTO consumo : consumos) {
                    ps.setInt(1, consumo.getIdReserva());
                    ps.setInt(2, consumo.getIdProducto());
                    ps.setInt(3, consumo.getCantidad());
                    ps.setBigDecimal(4, consumo.getPrecioUnitario());
                    ps.setDate(5, Date.valueOf(consumo.getFechaConsumo()));
                    ps.setTime(6, Time.valueOf(consumo.getHoraConsumo()));
                    ps.setInt(7, consumo.getIdEmpleado());
                    ps.setString(8, consumo.getObservaciones());
                    ps.addBatch();
                }
                
                int[] resultados = ps.executeBatch();
                connection.commit(); // Confirmar transacción
                
                System.out.println("✅ " + resultados.length + " consumos de productos registrados");
                return true;
            }
            
        } catch (SQLException e) {
            connection.rollback(); // Revertir en caso de error
            System.out.println("❌ Error al insertar múltiples consumos: " + e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true); // Restaurar auto-commit
        }
    }

    /**
     * ✅ ACTUALIZAR CONSUMO DE PRODUCTO
     */
    public boolean actualizar(ConsumoProductoDTO consumo) throws SQLException {
        // Obtener cantidad anterior para ajustar stock
        ConsumoProductoDTO consumoAnterior = obtenerPorId(consumo.getIdConsumo());
        if (consumoAnterior == null) {
            return false;
        }

        // Verificar stock considerando la diferencia
        int diferenciaCantidad = consumo.getCantidad() - consumoAnterior.getCantidad();
        if (diferenciaCantidad > 0 && !verificarStock(consumo.getIdProducto(), diferenciaCantidad)) {
            System.out.println("❌ Stock insuficiente para actualizar el consumo");
            return false;
        }

        String sql = """
            UPDATE ConsumoProductos SET 
                cantidad = ?, precio_unitario = ?, fecha_consumo = ?, 
                hora_consumo = ?, observaciones = ?
            WHERE id_consumo = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, consumo.getCantidad());
            ps.setBigDecimal(2, consumo.getPrecioUnitario());
            ps.setDate(3, Date.valueOf(consumo.getFechaConsumo()));
            ps.setTime(4, Time.valueOf(consumo.getHoraConsumo()));
            ps.setString(5, consumo.getObservaciones());
            ps.setInt(6, consumo.getIdConsumo());
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                // Ajustar stock manualmente si es necesario
                if (diferenciaCantidad != 0) {
                    ajustarStock(consumo.getIdProducto(), -diferenciaCantidad);
                }
                System.out.println("✅ Consumo de producto actualizado exitosamente");
                return true;
            }
        }
        return false;
    }

    /**
     * ✅ ELIMINAR CONSUMO DE PRODUCTO
     */
    public boolean eliminar(int idConsumo) throws SQLException {
        // Obtener información del consumo para ajustar stock
        ConsumoProductoDTO consumo = obtenerPorId(idConsumo);
        if (consumo == null) {
            return false;
        }

        String sql = "DELETE FROM ConsumoProductos WHERE id_consumo = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConsumo);
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                // Devolver stock
                ajustarStock(consumo.getIdProducto(), consumo.getCantidad());
                System.out.println("✅ Consumo de producto eliminado y stock restaurado");
                return true;
            }
        }
        return false;
    }

    /**
     * ✅ OBTENER POR ID
     */
    public ConsumoProductoDTO obtenerPorId(int idConsumo) throws SQLException {
        String sql = """
            SELECT cp.*, r.numero_reserva, p.nombre AS nombre_producto,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM ConsumoProductos cp
            JOIN Reservas r ON cp.id_reserva = r.id_reserva
            JOIN Productos p ON cp.id_producto = p.id_producto
            JOIN Empleados e ON cp.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE cp.id_consumo = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConsumo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapearResultSetAConsumoProducto(rs);
            }
        }
        return null;
    }

    /**
     * ✅ LISTAR CONSUMOS POR RESERVA
     */
    public List<ConsumoProductoDTO> listarPorReserva(int idReserva) throws SQLException {
        List<ConsumoProductoDTO> lista = new ArrayList<>();
        String sql = """
            SELECT cp.*, r.numero_reserva, p.nombre AS nombre_producto,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM ConsumoProductos cp
            JOIN Reservas r ON cp.id_reserva = r.id_reserva
            JOIN Productos p ON cp.id_producto = p.id_producto
            JOIN Empleados e ON cp.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE cp.id_reserva = ?
            ORDER BY cp.fecha_consumo DESC, cp.hora_consumo DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAConsumoProducto(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ LISTAR TODOS LOS CONSUMOS
     */
    public List<ConsumoProductoDTO> listarTodos() throws SQLException {
        List<ConsumoProductoDTO> lista = new ArrayList<>();
        String sql = """
            SELECT cp.*, r.numero_reserva, p.nombre AS nombre_producto,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM ConsumoProductos cp
            JOIN Reservas r ON cp.id_reserva = r.id_reserva
            JOIN Productos p ON cp.id_producto = p.id_producto
            JOIN Empleados e ON cp.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            ORDER BY cp.fecha_consumo DESC, cp.hora_consumo DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAConsumoProducto(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ LISTAR CONSUMOS POR FECHA
     */
    public List<ConsumoProductoDTO> listarPorFecha(LocalDate fecha) throws SQLException {
        List<ConsumoProductoDTO> lista = new ArrayList<>();
        String sql = """
            SELECT cp.*, r.numero_reserva, p.nombre AS nombre_producto,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM ConsumoProductos cp
            JOIN Reservas r ON cp.id_reserva = r.id_reserva
            JOIN Productos p ON cp.id_producto = p.id_producto
            JOIN Empleados e ON cp.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE cp.fecha_consumo = ?
            ORDER BY cp.hora_consumo DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAConsumoProducto(rs));
            }
        }
        return lista;
    }

    /**
     * ✅ OBTENER TOTAL CONSUMOS POR RESERVA
     */
    public BigDecimal obtenerTotalConsumosPorReserva(int idReserva) throws SQLException {
        String sql = """
            SELECT SUM(cantidad * precio_unitario) as total
            FROM ConsumoProductos 
            WHERE id_reserva = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * ✅ OBTENER PRODUCTOS MÁS CONSUMIDOS
     */
    public List<String> obtenerProductosMasConsumidos(int limite) throws SQLException {
        List<String> productos = new ArrayList<>();
        String sql = """
            SELECT TOP (?) p.nombre, SUM(cp.cantidad) as total_vendido,
                   SUM(cp.cantidad * cp.precio_unitario) as ingresos_totales
            FROM ConsumoProductos cp
            JOIN Productos p ON cp.id_producto = p.id_producto
            GROUP BY p.nombre
            ORDER BY total_vendido DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limite);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String info = rs.getString("nombre") + 
                             " (Vendido: " + rs.getInt("total_vendido") + 
                             ", Ingresos: S/. " + rs.getBigDecimal("ingresos_totales") + ")";
                productos.add(info);
            }
        }
        return productos;
    }

    /**
     * ✅ VERIFICAR STOCK DISPONIBLE
     */
    private boolean verificarStock(int idProducto, int cantidadRequerida) throws SQLException {
        String sql = "SELECT stock FROM Productos WHERE id_producto = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                int stockActual = rs.getInt("stock");
                return stockActual >= cantidadRequerida;
            }
        }
        return false;
    }

    /**
     * ✅ AJUSTAR STOCK MANUALMENTE (para correcciones)
     */
    private void ajustarStock(int idProducto, int ajuste) throws SQLException {
        String sql = "UPDATE Productos SET stock = stock + ? WHERE id_producto = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, ajuste);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    /**
     * ✅ OBTENER CONSUMOS DE HOY
     */
    public List<ConsumoProductoDTO> obtenerConsumosHoy() throws SQLException {
        return listarPorFecha(LocalDate.now());
    }

    /**
     * ✅ MAPEAR RESULTSET A DTO
     */
    private ConsumoProductoDTO mapearResultSetAConsumoProducto(ResultSet rs) throws SQLException {
        ConsumoProductoDTO consumo = new ConsumoProductoDTO();
        
        consumo.setIdConsumo(rs.getInt("id_consumo"));
        consumo.setIdReserva(rs.getInt("id_reserva"));
        consumo.setNumeroReserva(rs.getString("numero_reserva"));
        consumo.setIdProducto(rs.getInt("id_producto"));
        consumo.setNombreProducto(rs.getString("nombre_producto"));
        consumo.setCantidad(rs.getInt("cantidad"));
        consumo.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        
        // El subtotal se calcula automáticamente en el DTO
        consumo.setSubtotal(rs.getBigDecimal("subtotal"));
        
        consumo.setFechaConsumo(rs.getDate("fecha_consumo").toLocalDate());
        consumo.setHoraConsumo(rs.getTime("hora_consumo").toLocalTime());
        consumo.setIdEmpleado(rs.getInt("id_empleado"));
        consumo.setNombreEmpleado(rs.getString("nombre_empleado"));
        consumo.setObservaciones(rs.getString("observaciones"));
        
        return consumo;
    }

    /**
     * ✅ MOSTRAR ESTADÍSTICAS DE PRODUCTOS
     */
    public void mostrarEstadisticasProductos() throws SQLException {
        String sql = """
            SELECT p.nombre, 
                   COUNT(cp.id_consumo) as veces_vendido,
                   SUM(cp.cantidad) as total_unidades,
                   SUM(cp.cantidad * cp.precio_unitario) as ingresos_totales,
                   AVG(cp.precio_unitario) as precio_promedio
            FROM Productos p
            LEFT JOIN ConsumoProductos cp ON p.id_producto = cp.id_producto
            GROUP BY p.nombre
            ORDER BY ingresos_totales DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            System.out.println("📊 ESTADÍSTICAS DE PRODUCTOS:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%-20s %-8s %-12s %-12s %-12s%n", 
                            "Producto", "Ventas", "Unidades", "Ingresos", "Precio Prom.");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int ventas = rs.getInt("veces_vendido");
                int unidades = rs.getInt("total_unidades");
                BigDecimal ingresos = rs.getBigDecimal("ingresos_totales");
                BigDecimal precioPromedio = rs.getBigDecimal("precio_promedio");
                
                if (ingresos == null) ingresos = BigDecimal.ZERO;
                if (precioPromedio == null) precioPromedio = BigDecimal.ZERO;
                
                System.out.printf("%-20s %-8d %-12d S/. %-8.2f S/. %-8.2f%n", 
                                nombre, ventas, unidades, ingresos, precioPromedio);
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        }
    }

    /**
     * ✅ CERRAR CONEXIÓN
     */
    public void cerrarConexion() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✅ Conexión cerrada correctamente");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al cerrar conexión: " + e.getMessage());
        }
    }
}