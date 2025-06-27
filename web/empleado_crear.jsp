<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.RolesDTO" %>
<%
    List<RolesDTO> roles = (List<RolesDTO>) request.getAttribute("roles");
    String usuarioGenerado = (String) request.getAttribute("usuario_generado");
    String claveGenerada = (String) request.getAttribute("clave_generada");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registrar Empleado</title>
    <link rel="stylesheet" href="css/empleado_crear.css"/>
</head>
<body>
<div class="form-container">
    <h2>Registrar Nuevo Empleado</h2>

    <% if (usuarioGenerado != null && claveGenerada != null) { %>
        <div class="credenciales-generadas">
            <p><strong>Usuario generado:</strong> <%= usuarioGenerado %></p>
            <p><strong>Contraseña generada:</strong> <%= claveGenerada %></p>
        </div>
    <% } %>

    <form action="empleado_crear" method="post">
        <label>Nombre:</label>
        <input type="text" name="nombre" required>

        <label>Apellido:</label>
        <input type="text" name="apellido" required>

        <label>DNI:</label>
        <input type="text" name="dni" required>

        <label>Teléfono:</label>
        <input type="text" name="telefono">

        <label>Correo:</label>
        <input type="email" name="correo">

        <label>Dirección:</label>
        <input type="text" name="direccion">

        <label>Fecha de Nacimiento:</label>
        <input type="date" name="fecha_nacimiento" required>

        <label>Género:</label>
        <select name="genero">
            <option value="M">Masculino</option>
            <option value="F">Femenino</option>
        </select>

        <label>Rol:</label>
        <select name="id_rol" required>
            <% for (RolesDTO rol : roles) { %>
                <option value="<%= rol.getIdRol() %>"><%= rol.getNombreRol() %></option>
            <% } %>
        </select>

        <label>Fecha de Contratación:</label>
        <input type="date" name="fecha_contratacion" required>

        <label>Salario:</label>
        <input type="number" step="0.01" name="salario" required>

        <label>Turno:</label>
        <select name="turno">
            <option value="Mañana">Mañana</option>
            <option value="Tarde">Tarde</option>
            <option value="Noche">Noche</option>
        </select>

        <button type="submit">Registrar Empleado</button>
    </form>

    <a class="btn-volver" href="empleado">← Volver a lista de empleados</a>
</div>
</body>
</html>
