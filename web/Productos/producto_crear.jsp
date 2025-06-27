<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Crear Producto</title>
    <link rel="stylesheet" href="css/productos/producto_crear.css">
</head>
<body>
<div class="form-container">
    <h2>Registrar Nuevo Producto</h2>

    <form action="producto_crear" method="post">
        <label for="nombre">Nombre del Producto:</label>
        <input type="text" id="nombre" name="nombre" required>

        <label for="descripcion">Descripción:</label>
        <textarea id="descripcion" name="descripcion" rows="3" required></textarea>

        <label for="precio">Precio Unitario (S/):</label>
        <input type="number" id="precio" name="precio" step="0.01" required>

        <label for="stock">Stock:</label>
        <input type="number" id="stock" name="stock" required>

        <label for="stockMinimo">Stock Mínimo:</label>
        <input type="number" id="stockMinimo" name="stockMinimo" required>

        <button type="submit" class="btn-guardar">Guardar Producto</button>
        <a href="producto" class="btn-volver">← Volver</a>
    </form>
</div>
</body>
</html>