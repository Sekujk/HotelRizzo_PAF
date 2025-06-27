<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.ServicioDTO" %>
<%
    ServicioDTO servicio = (ServicioDTO) request.getAttribute("servicio");
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Detalle del Servicio</title>
        <link rel="stylesheet" href="css/servicios/servicio_detalle.css"/>
    </head>
    <body>
        <div class="container">
            <h2>Detalle del Servicio</h2>

            <div class="detalle-servicio">
                <p><strong>Nombre:</strong> <%= servicio.getNombre()%></p>
                <p><strong>Descripción:</strong> <%= servicio.getDescripcion()%></p>
                <p><strong>Precio Unitario:</strong> S/ <%= String.format("%.2f", servicio.getPrecioUnitario())%></p>
                <p><strong>Estado:</strong> <%= servicio.isActivo() ? "Activo" : "Inactivo"%></p>
            </div>

            <div class="acciones">
                <a href="servicio_editar?id=<%= servicio.getIdServicio()%>" class="btn-editar">✏️ Editar</a>

                <form action="servicio_eliminar" method="post" onsubmit="return confirm('¿Deseas eliminar este servicio?');" style="display:inline;">
                    <input type="hidden" name="id" value="<%= servicio.getIdServicio()%>">
                    <button type="submit" class="btn-eliminar">🗑️ Eliminar</button>
                </form>


                <a href="servicio" class="btn-volver">← Volver a lista</a>
            </div>
        </div>
    </body>
</html>
