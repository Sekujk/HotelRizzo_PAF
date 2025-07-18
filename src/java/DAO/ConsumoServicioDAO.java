package DAO;

import Model.ConsumoServicioDTO;
import Utils.Conexion;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ConsumoServicioDAO {
    private Connection connection;

    public ConsumoServicioDAO(Connection connection) {
        this.connection = connection;
    }

    // Constructor alternativo que obtiene conexión automáticamente
    public ConsumoServicioDAO() {
        this.connection = Conexion.getConnection();
    }

    /**
     * ✅ INSERTAR NUEVO CONSUMO DE SERVICIO
     */
    public boolean insertar(ConsumoServicioDTO consumo) throws SQLException {
        String sql = """
            INSERT INTO ConsumoServicios (id_reserva, id_servicio, cantidad, precio_unitario, 
                                        fecha_consumo, hora_consumo, id_empleado, observaciones)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, consumo.getIdReserva());
            ps.setInt(2, consumo.getIdServicio());
            ps.setBigDecimal(3, consumo.getCantidad());
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
                System.out.println("✅ Consumo de servicio registrado exitosamente: " + consumo.getNombreServicio());
                return true;
            }
            
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar consumo de servicio: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    /**
     * ✅ INSERTAR MÚLTIPLES CONSUMOS DE SERVICIOS
     */
    public boolean insertarMultiples(List<ConsumoServicioDTO> consumos) throws SQLException {
        String sql = """
            INSERT INTO ConsumoServicios (id_reserva, id_servicio, cantidad, precio_unitario, 
                                        fecha_consumo, hora_consumo, id_empleado, observaciones)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try {
            connection.setAutoCommit(false); // Iniciar transacción
            
            try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (ConsumoServicioDTO consumo : consumos) {
                    ps.setInt(1, consumo.getIdReserva());
                    ps.setInt(2, consumo.getIdServicio());
                    ps.setBigDecimal(3, consumo.getCantidad());
                    ps.setBigDecimal(4, consumo.getPrecioUnitario());
                    ps.setDate(5, Date.valueOf(consumo.getFechaConsumo()));
                    ps.setTime(6, Time.valueOf(consumo.getHoraConsumo()));
                    ps.setInt(7, consumo.getIdEmpleado());
                    ps.setString(8, consumo.getObservaciones());
                    ps.addBatch();
                }
                
                int[] resultados = ps.executeBatch();
                connection.commit(); // Confirmar transacción
                
                System.out.println("✅ " + resultados.length + " consumos de servicios registrados exitosamente");
                return true;
            }
            
        } catch (SQLException e) {
            connection.rollback(); // Revertir en caso de error
            System.out.println("❌ Error al insertar múltiples consumos de servicios: " + e.getMessage());
            throw e;
        } finally {
            connection.setAutoCommit(true); // Restaurar auto-commit
        }
    }

    /**
     * ✅ ACTUALIZAR CONSUMO DE SERVICIO
     */
    public boolean actualizar(ConsumoServicioDTO consumo) throws SQLException {
        String sql = """
            UPDATE ConsumoServicios SET 
                cantidad = ?, precio_unitario = ?, fecha_consumo = ?, 
                hora_consumo = ?, observaciones = ?
            WHERE id_consumo = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBigDecimal(1, consumo.getCantidad());
            ps.setBigDecimal(2, consumo.getPrecioUnitario());
            ps.setDate(3, Date.valueOf(consumo.getFechaConsumo()));
            ps.setTime(4, Time.valueOf(consumo.getHoraConsumo()));
            ps.setString(5, consumo.getObservaciones());
            ps.setInt(6, consumo.getIdConsumo());
            
            int filasAfectadas = ps.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("✅ Consumo de servicio actualizado exitosamente");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar consumo de servicio: " + e.getMessage());
            throw e;
        }
        return false;
    }

    /**
     * ✅ ELIMINAR CONSUMO DE SERVICIO
     */
    public boolean eliminar(int idConsumo) throws SQLException {
        String sql = "DELETE FROM ConsumoServicios WHERE id_consumo = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConsumo);
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("✅ Consumo de servicio eliminado exitosamente");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al eliminar consumo de servicio: " + e.getMessage());
            throw e;
        }
        return false;
    }

    /**
     * ✅ OBTENER CONSUMO POR ID
     */
    public ConsumoServicioDTO obtenerPorId(int idConsumo) throws SQLException {
        String sql = """
            SELECT cs.*, r.numero_reserva, s.nombre AS nombre_servicio,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM ConsumoServicios cs
            JOIN Reservas r ON cs.id_reserva = r.id_reserva
            JOIN Servicios s ON cs.id_servicio = s.id_servicio
            JOIN Empleados e ON cs.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE cs.id_consumo = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idConsumo);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapearResultSetAConsumoServicio(rs);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener consumo por ID: " + e.getMessage());
            throw e;
        }
        return null;
    }

    /**
     * ✅ LISTAR CONSUMOS POR RESERVA
     */
    public List<ConsumoServicioDTO> listarPorReserva(int idReserva) throws SQLException {
        List<ConsumoServicioDTO> lista = new ArrayList<>();
        String sql = """
            SELECT cs.*, r.numero_reserva, s.nombre AS nombre_servicio,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM ConsumoServicios cs
            JOIN Reservas r ON cs.id_reserva = r.id_reserva
            JOIN Servicios s ON cs.id_servicio = s.id_servicio
            JOIN Empleados e ON cs.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE cs.id_reserva = ?
            ORDER BY cs.fecha_consumo DESC, cs.hora_consumo DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAConsumoServicio(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar consumos por reserva: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    /**
     * ✅ LISTAR TODOS LOS CONSUMOS DE SERVICIOS
     */
    public List<ConsumoServicioDTO> listarTodos() throws SQLException {
        List<ConsumoServicioDTO> lista = new ArrayList<>();
        String sql = """
            SELECT cs.*, r.numero_reserva, s.nombre AS nombre_servicio,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM ConsumoServicios cs
            JOIN Reservas r ON cs.id_reserva = r.id_reserva
            JOIN Servicios s ON cs.id_servicio = s.id_servicio
            JOIN Empleados e ON cs.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            ORDER BY cs.fecha_consumo DESC, cs.hora_consumo DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAConsumoServicio(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar todos los consumos: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    /**
     * ✅ LISTAR CONSUMOS POR FECHA ESPECÍFICA
     */
    public List<ConsumoServicioDTO> listarPorFecha(LocalDate fecha) throws SQLException {
        List<ConsumoServicioDTO> lista = new ArrayList<>();
        String sql = """
            SELECT cs.*, r.numero_reserva, s.nombre AS nombre_servicio,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM ConsumoServicios cs
            JOIN Reservas r ON cs.id_reserva = r.id_reserva
            JOIN Servicios s ON cs.id_servicio = s.id_servicio
            JOIN Empleados e ON cs.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE cs.fecha_consumo = ?
            ORDER BY cs.hora_consumo DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAConsumoServicio(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar consumos por fecha: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    /**
     * ✅ LISTAR CONSUMOS POR SERVICIO ESPECÍFICO
     */
    public List<ConsumoServicioDTO> listarPorServicio(int idServicio) throws SQLException {
        List<ConsumoServicioDTO> lista = new ArrayList<>();
        String sql = """
            SELECT cs.*, r.numero_reserva, s.nombre AS nombre_servicio,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM ConsumoServicios cs
            JOIN Reservas r ON cs.id_reserva = r.id_reserva
            JOIN Servicios s ON cs.id_servicio = s.id_servicio
            JOIN Empleados e ON cs.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE cs.id_servicio = ?
            ORDER BY cs.fecha_consumo DESC, cs.hora_consumo DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idServicio);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAConsumoServicio(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar consumos por servicio: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    /**
     * ✅ LISTAR CONSUMOS POR RANGO DE FECHAS
     */
    public List<ConsumoServicioDTO> listarPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        List<ConsumoServicioDTO> lista = new ArrayList<>();
        String sql = """
            SELECT cs.*, r.numero_reserva, s.nombre AS nombre_servicio,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM ConsumoServicios cs
            JOIN Reservas r ON cs.id_reserva = r.id_reserva
            JOIN Servicios s ON cs.id_servicio = s.id_servicio
            JOIN Empleados e ON cs.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE cs.fecha_consumo BETWEEN ? AND ?
            ORDER BY cs.fecha_consumo DESC, cs.hora_consumo DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapearResultSetAConsumoServicio(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al listar consumos por rango de fechas: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    /**
     * ✅ OBTENER TOTAL DE CONSUMOS POR RESERVA
     */
    public BigDecimal obtenerTotalConsumosPorReserva(int idReserva) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(cantidad * precio_unitario), 0) as total
            FROM ConsumoServicios 
            WHERE id_reserva = ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener total de consumos: " + e.getMessage());
            throw e;
        }
        return BigDecimal.ZERO;
    }

    /**
     * ✅ OBTENER SERVICIOS MÁS CONSUMIDOS
     */
    public List<String> obtenerServiciosMasConsumidos(int limite) throws SQLException {
        List<String> servicios = new ArrayList<>();
        String sql = """
            SELECT TOP (?) s.nombre, 
                   COUNT(cs.id_consumo) as veces_usado,
                   SUM(cs.cantidad) as total_cantidad,
                   SUM(cs.cantidad * cs.precio_unitario) as ingresos_totales
            FROM ConsumoServicios cs
            JOIN Servicios s ON cs.id_servicio = s.id_servicio
            GROUP BY s.nombre
            ORDER BY veces_usado DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limite);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String info = rs.getString("nombre") + 
                             " - Usado: " + rs.getInt("veces_usado") + " veces" +
                             ", Cantidad: " + rs.getBigDecimal("total_cantidad") + 
                             ", Ingresos: S/. " + rs.getBigDecimal("ingresos_totales");
                servicios.add(info);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener servicios más consumidos: " + e.getMessage());
            throw e;
        }
        return servicios;
    }

    /**
     * ✅ OBTENER CONSUMOS DE HOY
     */
    public List<ConsumoServicioDTO> obtenerConsumosHoy() throws SQLException {
        return listarPorFecha(LocalDate.now());
    }

    /**
     * ✅ OBTENER INGRESOS POR SERVICIOS EN RANGO DE FECHAS
     */
    public BigDecimal obtenerIngresosPorRango(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(cantidad * precio_unitario), 0) as total_ingresos
            FROM ConsumoServicios 
            WHERE fecha_consumo BETWEEN ? AND ?
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total_ingresos");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener ingresos por rango: " + e.getMessage());
            throw e;
        }
        return BigDecimal.ZERO;
    }

    /**
     * ✅ OBTENER RESUMEN DE CONSUMOS POR EMPLEADO
     */
    public List<String> obtenerResumenPorEmpleado(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        List<String> resumen = new ArrayList<>();
        String sql = """
            SELECT per.nombre + ' ' + per.apellido AS nombre_empleado,
                   COUNT(cs.id_consumo) as servicios_registrados,
                   SUM(cs.cantidad * cs.precio_unitario) as total_ventas
            FROM ConsumoServicios cs
            JOIN Empleados e ON cs.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE cs.fecha_consumo BETWEEN ? AND ?
            GROUP BY per.nombre, per.apellido
            ORDER BY total_ventas DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(fechaInicio));
            ps.setDate(2, Date.valueOf(fechaFin));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                String info = rs.getString("nombre_empleado") + 
                             " - Servicios: " + rs.getInt("servicios_registrados") +
                             ", Ventas: S/. " + rs.getBigDecimal("total_ventas");
                resumen.add(info);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener resumen por empleado: " + e.getMessage());
            throw e;
        }
        return resumen;
    }

    /**
     * ✅ BUSCAR CONSUMOS DE SERVICIOS
     */
    public List<ConsumoServicioDTO> buscar(String criterio) throws SQLException {
        List<ConsumoServicioDTO> lista = new ArrayList<>();
        String sql = """
            SELECT cs.*, r.numero_reserva, s.nombre AS nombre_servicio,
                   per.nombre + ' ' + per.apellido AS nombre_empleado
            FROM ConsumoServicios cs
            JOIN Reservas r ON cs.id_reserva = r.id_reserva
            JOIN Servicios s ON cs.id_servicio = s.id_servicio
            JOIN Empleados e ON cs.id_empleado = e.id_empleado
            JOIN Personas per ON e.id_empleado = per.id_persona
            WHERE r.numero_reserva LIKE ? 
               OR s.nombre LIKE ?
               OR per.nombre LIKE ?
               OR per.apellido LIKE ?
               OR cs.observaciones LIKE ?
            ORDER BY cs.fecha_consumo DESC, cs.hora_consumo DESC
        """;
        
        String busqueda = "%" + criterio + "%";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, busqueda);
            ps.setString(2, busqueda);
            ps.setString(3, busqueda);
            ps.setString(4, busqueda);
            ps.setString(5, busqueda);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapearResultSetAConsumoServicio(rs));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al buscar consumos: " + e.getMessage());
            throw e;
        }
        return lista;
    }

    /**
     * ✅ MAPEAR RESULTSET A DTO
     */
    private ConsumoServicioDTO mapearResultSetAConsumoServicio(ResultSet rs) throws SQLException {
        ConsumoServicioDTO consumo = new ConsumoServicioDTO();
        
        consumo.setIdConsumo(rs.getInt("id_consumo"));
        consumo.setIdReserva(rs.getInt("id_reserva"));
        consumo.setNumeroReserva(rs.getString("numero_reserva"));
        consumo.setIdServicio(rs.getInt("id_servicio"));
        consumo.setNombreServicio(rs.getString("nombre_servicio"));
        consumo.setCantidad(rs.getBigDecimal("cantidad"));
        consumo.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
        
        // El subtotal se calcula automáticamente en el DTO
        consumo.recalcularSubtotal();
        
        consumo.setFechaConsumo(rs.getDate("fecha_consumo").toLocalDate());
        consumo.setHoraConsumo(rs.getTime("hora_consumo").toLocalTime());
        consumo.setIdEmpleado(rs.getInt("id_empleado"));
        consumo.setNombreEmpleado(rs.getString("nombre_empleado"));
        consumo.setObservaciones(rs.getString("observaciones"));
        
        return consumo;
    }

    /**
     * ✅ MOSTRAR ESTADÍSTICAS DETALLADAS DE SERVICIOS
     */
    public void mostrarEstadisticasServicios() throws SQLException {
        String sql = """
            SELECT s.nombre, 
                   COUNT(cs.id_consumo) as veces_usado,
                   SUM(cs.cantidad) as total_cantidad,
                   SUM(cs.cantidad * cs.precio_unitario) as ingresos_totales,
                   AVG(cs.precio_unitario) as precio_promedio,
                   AVG(cs.cantidad) as cantidad_promedio
            FROM Servicios s
            LEFT JOIN ConsumoServicios cs ON s.id_servicio = cs.id_servicio
            WHERE s.activo = 1
            GROUP BY s.nombre
            ORDER BY ingresos_totales DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            System.out.println("\n📊 ESTADÍSTICAS DETALLADAS DE SERVICIOS:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%-25s %-8s %-12s %-12s %-12s %-12s%n", 
                            "Servicio", "Usos", "Cantidad", "Ingresos", "Precio Prom.", "Cant. Prom.");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int usos = rs.getInt("veces_usado");
                BigDecimal cantidad = rs.getBigDecimal("total_cantidad");
                BigDecimal ingresos = rs.getBigDecimal("ingresos_totales");
                BigDecimal precioPromedio = rs.getBigDecimal("precio_promedio");
                BigDecimal cantidadPromedio = rs.getBigDecimal("cantidad_promedio");
                
                // Manejo de valores null
                if (cantidad == null) cantidad = BigDecimal.ZERO;
                if (ingresos == null) ingresos = BigDecimal.ZERO;
                if (precioPromedio == null) precioPromedio = BigDecimal.ZERO;
                if (cantidadPromedio == null) cantidadPromedio = BigDecimal.ZERO;
                
                System.out.printf("%-25s %-8d %-12.2f S/. %-8.2f S/. %-8.2f %-12.2f%n", 
                                nombre, usos, cantidad, ingresos, precioPromedio, cantidadPromedio);
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        } catch (SQLException e) {
            System.out.println("❌ Error al mostrar estadísticas: " + e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ MOSTRAR PATRONES DE USO POR HORARIO
     */
    public void mostrarPatronesDeUso() throws SQLException {
        String sql = """
            SELECT 
                CASE 
                    WHEN DATEPART(HOUR, hora_consumo) BETWEEN 6 AND 11 THEN 'Mañana (06:00-11:59)'
                    WHEN DATEPART(HOUR, hora_consumo) BETWEEN 12 AND 17 THEN 'Tarde (12:00-17:59)'
                    WHEN DATEPART(HOUR, hora_consumo) BETWEEN 18 AND 23 THEN 'Noche (18:00-23:59)'
                    ELSE 'Madrugada (00:00-05:59)'
                END as horario,
                COUNT(*) as total_servicios,
                SUM(cantidad * precio_unitario) as ingresos_horario,
                AVG(cantidad * precio_unitario) as promedio_consumo
            FROM ConsumoServicios
            GROUP BY 
                CASE 
                    WHEN DATEPART(HOUR, hora_consumo) BETWEEN 6 AND 11 THEN 'Mañana (06:00-11:59)'
                    WHEN DATEPART(HOUR, hora_consumo) BETWEEN 12 AND 17 THEN 'Tarde (12:00-17:59)'
                    WHEN DATEPART(HOUR, hora_consumo) BETWEEN 18 AND 23 THEN 'Noche (18:00-23:59)'
                    ELSE 'Madrugada (00:00-05:59)'
                END
            ORDER BY total_servicios DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            System.out.println("\n🕒 PATRONES DE USO DE SERVICIOS POR HORARIO:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%-25s %-15s %-15s %-15s%n", "Horario", "Total Servicios", "Ingresos", "Promedio");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            while (rs.next()) {
                String horario = rs.getString("horario");
                int totalServicios = rs.getInt("total_servicios");
                BigDecimal ingresos = rs.getBigDecimal("ingresos_horario");
                BigDecimal promedio = rs.getBigDecimal("promedio_consumo");
                
                String emoji = "";
                if (horario.startsWith("Mañana")) emoji = "🌅";
                else if (horario.startsWith("Tarde")) emoji = "☀️";
                else if (horario.startsWith("Noche")) emoji = "🌙";
                else emoji = "🌃";
                
                System.out.printf("%s %-22s %-15d S/. %-10.2f S/. %-10.2f%n", 
                                emoji, horario, totalServicios, ingresos, promedio);
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        } catch (SQLException e) {
            System.out.println("❌ Error al mostrar patrones de uso: " + e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ OBTENER RESUMEN DE VENTAS POR DÍA DE LA SEMANA
     */
    public void mostrarVentasPorDiaSemana() throws SQLException {
        String sql = """
            SELECT 
                CASE DATEPART(WEEKDAY, fecha_consumo)
                    WHEN 1 THEN 'Domingo'
                    WHEN 2 THEN 'Lunes'
                    WHEN 3 THEN 'Martes'
                    WHEN 4 THEN 'Miércoles'
                    WHEN 5 THEN 'Jueves'
                    WHEN 6 THEN 'Viernes'
                    WHEN 7 THEN 'Sábado'
                END as dia_semana,
                COUNT(*) as total_servicios,
                SUM(cantidad * precio_unitario) as ingresos_dia,
                AVG(cantidad * precio_unitario) as promedio_dia
            FROM ConsumoServicios
            GROUP BY DATEPART(WEEKDAY, fecha_consumo)
            ORDER BY 
                CASE DATEPART(WEEKDAY, fecha_consumo)
                    WHEN 2 THEN 1 -- Lunes
                    WHEN 3 THEN 2 -- Martes
                    WHEN 4 THEN 3 -- Miércoles
                    WHEN 5 THEN 4 -- Jueves
                    WHEN 6 THEN 5 -- Viernes
                    WHEN 7 THEN 6 -- Sábado
                    WHEN 1 THEN 7 -- Domingo
                END
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            System.out.println("\n📅 VENTAS DE SERVICIOS POR DÍA DE LA SEMANA:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%-12s %-15s %-15s %-15s%n", "Día", "Total Servicios", "Ingresos", "Promedio");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            while (rs.next()) {
                String dia = rs.getString("dia_semana");
                int totalServicios = rs.getInt("total_servicios");
                BigDecimal ingresos = rs.getBigDecimal("ingresos_dia");
                BigDecimal promedio = rs.getBigDecimal("promedio_dia");
                
                System.out.printf("%-12s %-15d S/. %-10.2f S/. %-10.2f%n", 
                                dia, totalServicios, ingresos, promedio);
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        } catch (SQLException e) {
            System.out.println("❌ Error al mostrar ventas por día de la semana: " + e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ OBTENER TOP SERVICIOS POR INGRESOS
     */
    public void mostrarTopServiciosPorIngresos(int limite) throws SQLException {
        String sql = """
            SELECT TOP (?) s.nombre, s.descripcion,
                   COUNT(cs.id_consumo) as veces_solicitado,
                   SUM(cs.cantidad) as cantidad_total,
                   SUM(cs.cantidad * cs.precio_unitario) as ingresos_totales,
                   AVG(cs.precio_unitario) as precio_promedio
            FROM Servicios s
            JOIN ConsumoServicios cs ON s.id_servicio = cs.id_servicio
            GROUP BY s.nombre, s.descripcion
            ORDER BY ingresos_totales DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limite);
            ResultSet rs = ps.executeQuery();
            
            System.out.println("\n🏆 TOP " + limite + " SERVICIOS POR INGRESOS:");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            int posicion = 1;
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                int veces = rs.getInt("veces_solicitado");
                BigDecimal cantidad = rs.getBigDecimal("cantidad_total");
                BigDecimal ingresos = rs.getBigDecimal("ingresos_totales");
                BigDecimal precioPromedio = rs.getBigDecimal("precio_promedio");
                
                String emoji = switch (posicion) {
                    case 1 -> "🥇";
                    case 2 -> "🥈";
                    case 3 -> "🥉";
                    default -> "🏅";
                };
                
                System.out.println(emoji + " POSICIÓN #" + posicion);
                System.out.println("   📋 Servicio: " + nombre);
                if (descripcion != null && !descripcion.trim().isEmpty()) {
                    System.out.println("   📝 Descripción: " + descripcion);
                }
                System.out.println("   📊 Estadísticas:");
                System.out.println("      • Veces solicitado: " + veces);
                System.out.println("      • Cantidad total: " + cantidad);
                System.out.println("      • Ingresos totales: S/. " + ingresos);
                System.out.println("      • Precio promedio: S/. " + precioPromedio);
                System.out.println("   ─────────────────────────────────────────────────────────────────────");
                
                posicion++;
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        } catch (SQLException e) {
            System.out.println("❌ Error al mostrar top servicios: " + e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ OBTENER SERVICIOS MENOS UTILIZADOS
     */
    public List<String> obtenerServiciosMenosUtilizados(int limite) throws SQLException {
        List<String> servicios = new ArrayList<>();
        String sql = """
            SELECT s.nombre, 
                   COALESCE(COUNT(cs.id_consumo), 0) as veces_usado,
                   COALESCE(SUM(cs.cantidad * cs.precio_unitario), 0) as ingresos_totales
            FROM Servicios s
            LEFT JOIN ConsumoServicios cs ON s.id_servicio = cs.id_servicio
            WHERE s.activo = 1
            GROUP BY s.nombre
            ORDER BY veces_usado ASC, ingresos_totales ASC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            int count = 0;
            while (rs.next() && count < limite) {
                String info = rs.getString("nombre") + 
                             " - Usado: " + rs.getInt("veces_usado") + " veces" +
                             ", Ingresos: S/. " + rs.getBigDecimal("ingresos_totales");
                servicios.add(info);
                count++;
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al obtener servicios menos utilizados: " + e.getMessage());
            throw e;
        }
        return servicios;
    }

    /**
     * ✅ CALCULAR ÍNDICE DE SATISFACCIÓN (basado en repetición de servicios)
     */
    public void mostrarIndiceSatisfaccion() throws SQLException {
        String sql = """
            WITH ServiciosFrecuencia AS (
                SELECT cs.id_servicio, cs.id_reserva, s.nombre,
                       COUNT(*) as veces_por_reserva
                FROM ConsumoServicios cs
                JOIN Servicios s ON cs.id_servicio = s.id_servicio
                GROUP BY cs.id_servicio, cs.id_reserva, s.nombre
            ),
            ServiciosRepetidos AS (
                SELECT nombre, 
                       COUNT(*) as total_reservas,
                       SUM(CASE WHEN veces_por_reserva > 1 THEN 1 ELSE 0 END) as reservas_repetidas
                FROM ServiciosFrecuencia
                GROUP BY nombre
            )
            SELECT nombre,
                   total_reservas,
                   reservas_repetidas,
                   CASE 
                       WHEN total_reservas > 0 
                       THEN ROUND((CAST(reservas_repetidas AS FLOAT) / total_reservas) * 100, 2)
                       ELSE 0 
                   END as indice_satisfaccion
            FROM ServiciosRepetidos
            WHERE total_reservas >= 3  -- Solo servicios con al menos 3 usos
            ORDER BY indice_satisfaccion DESC
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            System.out.println("\n😊 ÍNDICE DE SATISFACCIÓN DE SERVICIOS:");
            System.out.println("(Basado en la repetición de servicios por reserva)");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.printf("%-30s %-15s %-15s %-15s%n", "Servicio", "Total Reservas", "Repetidas", "Satisfacción");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int totalReservas = rs.getInt("total_reservas");
                int repetidas = rs.getInt("reservas_repetidas");
                double satisfaccion = rs.getDouble("indice_satisfaccion");
                
                String emoji = "";
                if (satisfaccion >= 70) emoji = "😍";
                else if (satisfaccion >= 50) emoji = "😊";
                else if (satisfaccion >= 30) emoji = "😐";
                else emoji = "😞";
                
                System.out.printf("%-30s %-15d %-15d %s %-10.1f%%%n", 
                                nombre, totalReservas, repetidas, emoji, satisfaccion);
            }
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        } catch (SQLException e) {
            System.out.println("❌ Error al calcular índice de satisfacción: " + e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ OBTENER ESTADÍSTICAS RESUMIDAS
     */
    public void mostrarResumenGeneral() throws SQLException {
        String sql = """
            SELECT 
                COUNT(*) as total_consumos,
                COUNT(DISTINCT id_reserva) as reservas_con_servicios,
                COUNT(DISTINCT id_servicio) as servicios_diferentes,
                SUM(cantidad * precio_unitario) as ingresos_totales,
                AVG(cantidad * precio_unitario) as promedio_por_consumo,
                MIN(fecha_consumo) as primera_fecha,
                MAX(fecha_consumo) as ultima_fecha
            FROM ConsumoServicios
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                System.out.println("\n📈 RESUMEN GENERAL DE CONSUMO DE SERVICIOS:");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("📊 Estadísticas Generales:");
                System.out.println("   • Total de consumos registrados: " + rs.getInt("total_consumos"));
                System.out.println("   • Reservas que usaron servicios: " + rs.getInt("reservas_con_servicios"));
                System.out.println("   • Servicios diferentes utilizados: " + rs.getInt("servicios_diferentes"));
                System.out.println("   • Ingresos totales por servicios: S/. " + rs.getBigDecimal("ingresos_totales"));
                System.out.println("   • Promedio por consumo: S/. " + rs.getBigDecimal("promedio_por_consumo"));
                System.out.println("   • Período de análisis: " + rs.getDate("primera_fecha") + " al " + rs.getDate("ultima_fecha"));
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al mostrar resumen general: " + e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ VERIFICAR INTEGRIDAD DE DATOS
     */
    public boolean verificarIntegridad() throws SQLException {
        List<String> problemas = new ArrayList<>();
        
        // Verificar consumos sin reserva válida
        String sql1 = """
            SELECT COUNT(*) FROM ConsumoServicios cs 
            LEFT JOIN Reservas r ON cs.id_reserva = r.id_reserva 
            WHERE r.id_reserva IS NULL
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql1)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                problemas.add("Existen " + rs.getInt(1) + " consumos sin reserva válida");
            }
        }
        
        // Verificar consumos sin servicio válido
        String sql2 = """
            SELECT COUNT(*) FROM ConsumoServicios cs 
            LEFT JOIN Servicios s ON cs.id_servicio = s.id_servicio 
            WHERE s.id_servicio IS NULL
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql2)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                problemas.add("Existen " + rs.getInt(1) + " consumos sin servicio válido");
            }
        }
        
        // Verificar consumos con cantidades o precios negativos
        String sql3 = """
            SELECT COUNT(*) FROM ConsumoServicios 
            WHERE cantidad <= 0 OR precio_unitario < 0
        """;
        
        try (PreparedStatement ps = connection.prepareStatement(sql3)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                problemas.add("Existen " + rs.getInt(1) + " consumos con cantidades o precios inválidos");
            }
        }
        
        // Mostrar resultados
        if (problemas.isEmpty()) {
            System.out.println("✅ Verificación de integridad completada: No se encontraron problemas");
            return true;
        } else {
            System.out.println("⚠️ Problemas de integridad encontrados:");
            for (String problema : problemas) {
                System.out.println("   • " + problema);
            }
            return false;
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