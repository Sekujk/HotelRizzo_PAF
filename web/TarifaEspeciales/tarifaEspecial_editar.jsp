<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.TarifaEspecialDTO" %>
<%@ page import="Model.TipoHabitacionDTO" %>
<%@ page import="java.util.List" %>
<%
    TarifaEspecialDTO tarifa = (TarifaEspecialDTO) request.getAttribute("tarifa");
    List<TipoHabitacionDTO> tipos = (List<TipoHabitacionDTO>) request.getAttribute("tipos");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Tarifa Especial</title>
    <link rel="stylesheet" href="../css/habitaciones/habitacion.css">
</head>
<body>
<div class="container">
    <h2>Editar Tarifa Especial</h2>
    <a class="btn-volver" href="../tarifaespecial">← Volver</a>

    <form action="../tarifaespecial_editar" method="post" class="formulario">
        <input type="hidden" name="id" value="<%= tarifa.getIdTarifa() %>">

        <label for="nombre">Nombre:</label>
        <input type="text" id="nombre" name="nombre" required value="<%= tarifa.getNombre() %>">

        <label for="idTipoHabitacion">Tipo de Habitación:</label>
        <select name="idTipoHabitacion" id="idTipoHabitacion" required>
            <% for (TipoHabitacionDTO tipo : tipos) {
                   boolean selected = tipo.getIdTipo() == tarifa.getIdTipoHabitacion();
            %>
                <option value="<%= tipo.getIdTipo() %>" <%= selected ? "selected" : "" %>>
                    <%= tipo.getNombre() %>
                </option>
            <% } %>
        </select>

        <label for="fechaInicio">Fecha de Inicio:</label>
        <input type="date" id="fechaInicio" name="fechaInicio" required value="<%= tarifa.getFechaInicio() %>">

        <label for="fechaFin">Fecha de Fin:</label>
        <input type="date" id="fechaFin" name="fechaFin" required value="<%= tarifa.getFechaFin() %>">

        <label for="precioEspecial">Precio Especial (S/):</label>
        <input type="number" id="precioEspecial" name="precioEspecial" required step="0.01" value="<%= tarifa.getPrecioEspecial() %>">

        <label for="tipoTarifa">Tipo de Tarifa:</label>
        <select name="tipoTarifa" id="tipoTarifa" required>
            <option value="Descuento" <%= "Descuento".equals(tarifa.getTipoTarifa()) ? "selected" : "" %>>Descuento</option>
            <option value="Aumento" <%= "Aumento".equals(tarifa.getTipoTarifa()) ? "selected" : "" %>>Aumento</option>
        </select>

        <button type="submit" class="btn-agregar">Guardar Cambios</button>
    </form>
</div>
</body>
</html>
