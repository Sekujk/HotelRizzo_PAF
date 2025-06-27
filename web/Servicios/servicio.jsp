<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.ServicioDTO" %>
<%
    List<ServicioDTO> servicios = (List<ServicioDTO>) request.getAttribute("servicios");
    long activos = (long) request.getAttribute("activos");
    int total = (int) request.getAttribute("total");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Servicios</title>
    <link rel="stylesheet" href="css/servicios/servicio.css"/>
</head>
<body>
<div class="container">
    <h2>Dashboard de Servicios</h2>
    <a class="btn-volver" href="dashboard">← Volver al Panel</a>

    <div class="resumen">
        <p><strong>Activos:</strong> <%= activos %> / <strong>Total:</strong> <%= total %></p>
    </div>

    <div class="acciones">
        <a class="btn-agregar" href="servicio_crear">+ Nuevo Servicio</a>
        <input id="buscador-servicio" class="buscador" type="text" placeholder="Buscar servicio...">
    </div>

    <table class="tabla-servicios">
        <thead>
            <tr>
                <th>Nombre</th>
                <th>Descripción</th>
                <th>Precio Unitario</th>
                <th>Estado</th>
            </tr>
        </thead>
        <tbody>
            <% for (ServicioDTO s : servicios) { %>
                <tr onclick="window.location.href='servicio_detalle?id=<%= s.getIdServicio() %>'" style="cursor:pointer;">
                    <td><%= s.getNombre() %></td>
                    <td><%= s.getDescripcion() %></td>
                    <td>S/ <%= String.format("%.2f", s.getPrecioUnitario()) %></td>
                    <td><%= s.isActivo() ? "Activo" : "Inactivo" %></td>
                </tr>
            <% } %>
        </tbody>
    </table>
</div>

<script src="${pageContext.request.contextPath}/js/servicio.js"></script>
</body>
</html>
