<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.ProductoDTO" %>
<%
    List<ProductoDTO> productos = (List<ProductoDTO>) request.getAttribute("productos");
    long activos = (long) request.getAttribute("activos");
    int total = (int) request.getAttribute("total");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Productos</title>
    <link rel="stylesheet" href="css/productos/producto.css"/>
</head>
<body>
    <div class="container">
        <h2>Dashboard de Productos</h2>
        <a class="btn-volver" href="dashboard">← Volver al Panel</a>

        <div class="resumen">
            <p><strong>Activos:</strong> <%= activos %> / <strong>Total:</strong> <%= total %></p>
        </div>

        <div class="acciones">
            <a class="btn-agregar" href="producto_crear">+ Nuevo Producto</a>
            <input id="buscador-producto" class="buscador" type="text" placeholder="Buscar producto...">
        </div>

        <table class="tabla-productos">
            <thead>
                <tr>
                    <th>Nombre</th>
                    <th>Precio Unitario</th>
                    <th>Stock</th>
                    <th>Stock Mínimo</th>
                </tr>
            </thead>
            <tbody>
                <% for (ProductoDTO p : productos) { %>
                <tr onclick="window.location.href = 'producto_detalle?id=<%= p.getIdProducto() %>'" style="cursor:pointer;">
                    <td><%= p.getNombre() %></td>
                    <td>S/ <%= String.format("%.2f", p.getPrecioUnitario()) %></td>
                    <td><%= p.getStock() %></td>
                    <td><%= p.getStockMinimo() %></td>
                </tr>
                <% } %>
            </tbody>
        </table>
    </div>
</body>
<script src="${pageContext.request.contextPath}/js/producto.js"></script>
</html>
