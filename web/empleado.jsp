<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.EmpleadoDTO" %>
<%
    List<EmpleadoDTO> empleados = (List<EmpleadoDTO>) request.getAttribute("empleados");
    long activos = (long) request.getAttribute("activos");
    int total = (int) request.getAttribute("total");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Empleados</title>
        <link rel="stylesheet" href="css/empleado.css"/>
    </head>
    <body>
        <div class="container">
            <h2>Dashboard de Empleados</h2>
            <a class="btn-volver" href="dashboard">← Volver al Panel</a>

            <div class="resumen">
                <p><strong>Activos:</strong> <%= activos%> / <strong>Total:</strong> <%= total%></p>
            </div>

            <div class="acciones">
                <a class="btn-agregar" href="empleado_crear">+ Nuevo Empleado</a>
                <input id="buscador-empleado" class="buscador" type="text" placeholder="Buscar empleado...">

            </div>

            <table class="tabla-empleados">
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>DNI</th>
                        <th>Rol</th>
                        <th>Salario</th>
                        <th>Turno</th>
                    </tr>
                </thead>
                <tbody>
                    <% for (EmpleadoDTO emp : empleados) {%>
                    <tr onclick="window.location.href = 'empleado_detalle?id=<%= emp.getIdEmpleado() %>'" style="cursor:pointer;">
                        <td><%= emp.getNombreCompleto()%></td>
                        <td><%= emp.getDni()%></td>
                        <td><%= emp.getNombreRol()%></td>
                        <td>S/ <%= emp.getSalario()%></td>
                        <td><%= emp.getTurno()%></td>
                    </tr>
                    <% }%>
                </tbody>

            </table>
        </div>
    </body>
    <script src="js/empleado.js"></script>
</html>
