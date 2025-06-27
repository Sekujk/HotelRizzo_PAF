<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.EmpleadoDTO" %>
<%@ page import="Model.RolesDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    EmpleadoDTO empleado = (EmpleadoDTO) request.getAttribute("empleado");
    List<RolesDTO> roles = (List<RolesDTO>) request.getAttribute("roles");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Empleado</title>
    <link rel="stylesheet" href="css/empleado_crear.css"/>
</head>
<body>
<div class="form-container">
    <h2>Editar Empleado</h2>
    <form action="empleado_editar" method="post">
        <input type="hidden" name="id_empleado" value="<%= empleado.getIdEmpleado() %>">

        <label>Nombre:</label>
        <input type="text" name="nombre" value="<%= empleado.getNombre() %>" required>

        <label>Apellido:</label>
        <input type="text" name="apellido" value="<%= empleado.getApellido() %>" required>

        <label>DNI:</label>
        <input type="text" name="dni" value="<%= empleado.getDni() %>" required>

        <label>Teléfono:</label>
        <input type="text" name="telefono" value="<%= empleado.getTelefono() %>">

        <label>Correo:</label>
        <input type="email" name="correo" value="<%= empleado.getCorreo() %>">

        <label>Dirección:</label>
        <input type="text" name="direccion" value="<%= empleado.getDireccion() %>">

        <label>Fecha de Nacimiento:</label>
        <input type="date" name="fecha_nacimiento"
               value="<%= empleado.getFechaNacimiento() != null ? sdf.format(empleado.getFechaNacimiento()) : "" %>"
               required>

        <label>Género:</label>
        <select name="genero">
            <option value="M" <%= "M".equals(empleado.getGenero()) ? "selected" : "" %>>Masculino</option>
            <option value="F" <%= "F".equals(empleado.getGenero()) ? "selected" : "" %>>Femenino</option>
        </select>

        <label>Rol:</label>
        <select name="id_rol">
            <% for (RolesDTO rol : roles) { %>
                <option value="<%= rol.getIdRol() %>" <%= rol.getIdRol() == empleado.getIdRol() ? "selected" : "" %>>
                    <%= rol.getNombreRol() %>
                </option>
            <% } %>
        </select>

        <label>Fecha de Contratación:</label>
        <input type="date" name="fecha_contratacion"
               value="<%= empleado.getFechaContratacion() != null ? sdf.format(empleado.getFechaContratacion()) : "" %>"
               required>

        <label>Salario:</label>
        <input type="number" step="0.01" name="salario" value="<%= empleado.getSalario() %>" required>

        <label>Turno:</label>
        <select name="turno">
            <option value="Mañana" <%= "Mañana".equals(empleado.getTurno()) ? "selected" : "" %>>Mañana</option>
            <option value="Tarde" <%= "Tarde".equals(empleado.getTurno()) ? "selected" : "" %>>Tarde</option>
            <option value="Noche" <%= "Noche".equals(empleado.getTurno()) ? "selected" : "" %>>Noche</option>
        </select>

        <button type="submit">Guardar Cambios</button>
    </form>

    <a class="btn-volver" href="empleado">← Volver a lista de empleados</a>
</div>
</body>
</html>
