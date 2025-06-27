<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.EmpleadoDTO, Model.UsuarioDTO, Model.RolesDTO" %>
<%
    EmpleadoDTO empleado = (EmpleadoDTO) request.getAttribute("empleado");
    UsuarioDTO usuario = (UsuarioDTO) request.getAttribute("usuario");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle del Empleado</title>
    <link rel="stylesheet" href="css/empleado_detalle.css"/>
</head>
<body>
<div class="form-container">
    <h2>Detalle del Empleado</h2>

    <p><strong>Nombre:</strong> <%= empleado.getNombre() %></p>
    <p><strong>Apellido:</strong> <%= empleado.getApellido() %></p>
    <p><strong>DNI:</strong> <%= empleado.getDni() %></p>
    <p><strong>Teléfono:</strong> <%= empleado.getTelefono() %></p>
    <p><strong>Correo:</strong> <%= empleado.getCorreo() %></p>
    <p><strong>Dirección:</strong> <%= empleado.getDireccion() %></p>
    <p><strong>Fecha Nacimiento:</strong> <%= empleado.getFechaNacimiento() %></p>
    <p><strong>Género:</strong> <%= empleado.getGenero() %></p>
    <p><strong>Rol:</strong> <%= empleado.getNombreRol() %></p>
    <p><strong>Fecha Contratación:</strong> <%= empleado.getFechaContratacion() %></p>
    <p><strong>Salario:</strong> S/ <%= empleado.getSalario() %></p>
    <p><strong>Turno:</strong> <%= empleado.getTurno() %></p>

    <% if (usuario != null) { %>
        <p><strong>Usuario:</strong> <%= usuario.getUsername() %></p>
        <p><strong>Contraseña (hash):</strong> <%= usuario.getPasswordHash() %></p>
    <% } else { %>
        <p><em>Este empleado no tiene cuenta de usuario.</em></p>
    <% } %>

    <div style="margin-top: 20px;">
        <a href="empleado_editar?id=<%= empleado.getIdEmpleado() %>" class="btn-editar">✏️ Editar</a>

        <form action="empleado_eliminar" method="post" style="display:inline;" onsubmit="return confirm('¿Estás seguro de eliminar a este empleado?');">
            <input type="hidden" name="id" value="<%= empleado.getIdEmpleado() %>">
            <button type="submit" class="btn-eliminar">🗑️ Eliminar</button>
        </form>

        <a href="empleado" class="btn-volver">← Volver a lista</a>
    </div>
</div>
</body>
</html>
