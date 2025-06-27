<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.ProductoDTO" %>
<%
    ProductoDTO producto = (ProductoDTO) request.getAttribute("producto");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Producto</title>
    <link rel="stylesheet" href="css/productos/producto_editar.css">
</head>
<body>
<div class="form-container">
    <h2>Editar Producto</h2>

    <form action="producto_editar" method="post">
        <input type="hidden" name="id" value="<%= producto.getIdProducto() %>">

        <label for="nombre">Nombre:</label>
        <input type="text" id="nombre" name="nombre" required value="<%= producto.getNombre() %>">

        <label for="descripcion">Descripción:</label>
        <textarea id="descripcion" name="descripcion" required><%= producto.getDescripcion() %></textarea>

        <label for="precio">Precio Unitario:</label>
        <input type="number" step="0.01" id="precio" name="precio" required value="<%= producto.getPrecioUnitario() %>">

        <label for="stock">Stock:</label>
        <input type="number" id="stock" name="stock" required value="<%= producto.getStock() %>">

        <label for="stockMinimo">Stock Mínimo:</label>
        <input type="number" id="stockMinimo" name="stockMinimo" required value="<%= producto.getStockMinimo() %>">

        <div class="form-buttons">
            <button type="submit" class="btn-guardar">💾 Guardar Cambios</button>
            <a href="producto" class="btn-volver">← Cancelar</a>
        </div>
    </form>
</div>
</body>
</html>
