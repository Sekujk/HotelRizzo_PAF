<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.TarifaEspecialDTO" %>
<%
    TarifaEspecialDTO tarifa = (TarifaEspecialDTO) request.getAttribute("tarifa");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Detalle Tarifa Especial</title>
    <link rel="stylesheet" href="../css/habitaciones/habitacion.css">
</head>
<body>
<div class="container">
    <h2>Detalle Tarifa Especial</h2>

    <div class="resumen">
        <p><strong>Nombre:</strong> <%= tarifa.getNombre() %></p>
        <p><strong>Tipo Habitación:</strong> <%= tarifa.getNombreTipoHabitacion() %></p>
        <p><strong>Fecha Inicio:</strong> <%= tarifa.getFechaInicio() %></p>
        <p><strong>Fecha Fin:</strong> <%= tarifa.getFechaFin() %></p>
        <p><strong>Precio Especial:</strong> S/ <%= tarifa.getPrecioEspecial() %></p>
        <p><strong>Tipo Tarifa:</strong> <%= tarifa.getTipoTarifa() %></p>
    </div>

    <div class="acciones">
        <a class="btn-agregar" href="../tarifaespecial_editar?id=<%= tarifa.getIdTarifa() %>">Editar</a>
        <a class="btn-secundario" href="../tarifaespecial_eliminar?id=<%= tarifa.getIdTarifa() %>" onclick="return confirm('¿Eliminar esta tarifa?')">Eliminar</a>
    </div>
</div>
</body>
</html>
