<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.ServicioDTO" %>
<%
    ServicioDTO servicio = (ServicioDTO) request.getAttribute("servicio");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Editar Servicio</title>
        <link rel="stylesheet" href="css/servicios/servicio_editar.css">
    </head>
    <body>
        <div class="form-container">
            <h2>Editar Servicio</h2>
            <form action="servicio_editar" method="post">
                <input type="hidden" name="id" value="<%= servicio.getIdServicio()%>">

                <label for="nombre">Nombre del Servicio:</label>
                <input type="text" id="nombre" name="nombre" value="<%= servicio.getNombre()%>" required>

                <label for="descripcion">Descripción:</label>
                <textarea id="descripcion" name="descripcion" rows="4"><%= servicio.getDescripcion()%></textarea>

                <label for="precio">Precio Unitario (S/):</label>
                <input type="number" id="precio" name="precio" step="0.01" min="0" value="<%= servicio.getPrecioUnitario()%>" required>

                <div class="campo-activo">
                    <input type="checkbox" id="activo" name="activo" <%= servicio.isActivo() ? "checked" : ""%>>
                    <span>Servicio activo</span>
                </div>


                <button type="submit" class="btn-guardar">Guardar Cambios</button>
                <a href="servicio_detalle?id=<%= servicio.getIdServicio()%>" class="btn-volver">← Cancelar</a>
            </form>
        </div>
    </body>
</html>
