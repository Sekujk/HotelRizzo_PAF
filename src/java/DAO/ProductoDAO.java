package DAO;

import java.sql.*;
import java.util.*;
import Model.ProductoDTO;
import Utils.Conexion;

public class ProductoDAO {
    private Connection conn;

    public ProductoDAO(Connection conn) {
        this.conn = conn;
    }
    
        public long contarProductosActivos() {
        String sql = "SELECT COUNT(*) FROM Productos WHERE activo = 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Obtener el total de productos (activos o no)
    public int contarTotalProductos() {
        String sql = "SELECT COUNT(*) FROM Productos";
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<ProductoDTO> listar() {
    List<ProductoDTO> lista = new ArrayList<>();
    String sql = "SELECT * FROM Productos ORDER BY nombre ASC"; // ya no filtra por activo

    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            ProductoDTO p = new ProductoDTO();
            p.setIdProducto(rs.getInt("id_producto"));
            p.setNombre(rs.getString("nombre"));
            p.setDescripcion(rs.getString("descripcion"));
            p.setPrecioUnitario(rs.getDouble("precio_unitario"));
            p.setStock(rs.getInt("stock"));
            p.setStockMinimo(rs.getInt("stock_minimo"));
            p.setActivo(rs.getBoolean("activo")); // también cargamos estado
            lista.add(p);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return lista;
}

    public boolean registrar(ProductoDTO p) {
        String sql = "INSERT INTO Productos(nombre, descripcion, precio_unitario, stock, stock_minimo, activo) VALUES (?, ?, ?, ?, ?, 1)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getPrecioUnitario());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getStockMinimo());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ProductoDTO obtenerPorId(int id) {
        String sql = "SELECT * FROM Productos WHERE id_producto = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ProductoDTO p = new ProductoDTO();
                    p.setIdProducto(rs.getInt("id_producto"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setPrecioUnitario(rs.getDouble("precio_unitario"));
                    p.setStock(rs.getInt("stock"));
                    p.setStockMinimo(rs.getInt("stock_minimo"));
                    p.setActivo(rs.getBoolean("activo"));
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean actualizar(ProductoDTO p) {
    String sql = "UPDATE Productos SET nombre = ?, descripcion = ?, precio_unitario = ?, stock = ?, stock_minimo = ? WHERE id_producto = ?";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, p.getNombre());
        ps.setString(2, p.getDescripcion());
        ps.setDouble(3, p.getPrecioUnitario());
        ps.setInt(4, p.getStock());
        ps.setInt(5, p.getStockMinimo());
        ps.setInt(6, p.getIdProducto());

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


    public boolean eliminar(int idProducto) {
    String sql = "DELETE FROM Productos WHERE id_producto = ?";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, idProducto);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    
    public int contarStockBajo() {
    String sql = "SELECT COUNT(*) FROM Productos WHERE stock <= stock_minimo AND activo = 1";
    try (PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;
}

    public boolean cambiarEstado(int idProducto, boolean estado) {
    String sql = "UPDATE Productos SET activo = ? WHERE id_producto = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setBoolean(1, estado);
        ps.setInt(2, idProducto);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

}
