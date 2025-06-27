<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.ProductoDTO" %>
<%
    ProductoDTO producto = (ProductoDTO) request.getAttribute("producto");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle del Producto</title>
    <link rel="stylesheet" href="css/productos/producto_detalle.css"/>
</head>
<body>
<div class="container">
    <h2>Detalle del Producto</h2>

    <div class="detalle-producto">
        <p><strong>Nombre:</strong> <%= producto.getNombre() %></p>
        <p><strong>Descripción:</strong> <%= producto.getDescripcion() %></p>
        <p><strong>Precio Unitario:</strong> S/ <%= String.format("%.2f", producto.getPrecioUnitario()) %></p>
        <p><strong>Stock:</strong> <%= producto.getStock() %></p>
        <p><strong>Stock Mínimo:</strong> <%= producto.getStockMinimo() %></p>
        <p><strong>Estado:</strong> <%= producto.isActivo() ? "Activo" : "Inactivo" %></p>
    </div>

    <div class="acciones">
        <a href="producto_editar?id=<%= producto.getIdProducto() %>" class="btn-editar">✏️ Editar</a>

        <form action="producto_eliminar" method="post" onsubmit="return confirm('¿Deseas eliminar este producto?');" style="display:inline;">
            <input type="hidden" name="id" value="<%= producto.getIdProducto() %>">
            <button type="submit" class="btn-eliminar">🗑️ Eliminar</button>
        </form>

        <a href="producto" class="btn-volver">← Volver a lista</a>
    </div>
</div>
</body>
</html>
