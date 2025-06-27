<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nuevo Servicio</title>
    <link rel="stylesheet" href="css/servicios/servicio_crear.css">
</head>
<body>
<div class="form-container">
    <h2>Registrar Nuevo Servicio</h2>
    <form action="servicio_crear" method="post">
        <label for="nombre">Nombre del Servicio:</label>
        <input type="text" id="nombre" name="nombre" required>

        <label for="descripcion">Descripción:</label>
        <textarea id="descripcion" name="descripcion" rows="4"></textarea>

        <label for="precio">Precio Unitario (S/):</label>
        <input type="number" id="precio" name="precio" step="0.01" min="0" required>

        <button type="submit" class="btn-registrar">Registrar</button>
        <a href="servicio" class="btn-volver">← Cancelar</a>
    </form>
</div>
</body>
</html>
