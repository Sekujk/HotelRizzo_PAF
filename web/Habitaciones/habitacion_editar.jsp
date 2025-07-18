<%@page import="Utils.Conexion"%>
<%@page import="java.util.List"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.HabitacionDTO" %>
<%@ page import="DAO.TipoHabitacionDAO" %>
<%@ page import="Model.TipoHabitacionDTO" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");

    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    HabitacionDTO hab = (HabitacionDTO) request.getAttribute("habitacion");
    List<TipoHabitacionDTO> tipos = new TipoHabitacionDAO(Conexion.getConnection()).listar();

    if (hab == null) {
        response.sendRedirect("habitaciones?mensaje=error");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Habitación - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/habitaciones/habitacion_editar.css">
    <link rel="stylesheet" href="css/utils/base.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
</head>
<body>

<header class="navbar">
    <div class="logo-section">
        <i class="fas fa-hotel"></i>
        <span class="logo-text">Hotel Rizzo</span>
    </div>
    <div class="user-section">
        <div class="user-info">
            <i class="fas fa-user-circle"></i>
            <div class="user-details">
                <span class="user-name"><%= usuario %></span>
                <span class="user-role"><%= rolUsuario %></span>
            </div>
        </div>
        <a href="login.jsp" class="btn-logout">
            <i class="fas fa-sign-out-alt"></i>
        </a>
    </div>
</header>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <div class="nav-section">
                <h3>Gestión Principal</h3>
                <ul>
                    <li><a href="reservas" class="nav-link"><i class="fas fa-calendar-alt"></i><span>Reservas</span></a></li>
                    <li><a href="clientes" class="nav-link"><i class="fas fa-id-card"></i><span>Clientes</span></a></li>
                    <li><a href="habitaciones" class="nav-link active"><i class="fas fa-bed"></i><span>Habitaciones</span></a></li>
                </ul>
            </div>
            <div class="nav-section">
                <h3>Servicios</h3>
                <ul>
                    <li><a href="servicio" class="nav-link"><i class="fas fa-concierge-bell"></i><span>Servicios</span></a></li>
                    <li><a href="producto" class="nav-link"><i class="fas fa-shopping-cart"></i><span>Productos</span></a></li>
                </ul>
            </div>
            <% if ("Administrador".equals(rolUsuario) || "Gerente".equals(rolUsuario)) { %>
            <div class="nav-section">
                <h3>Administración</h3>
                <ul>
                    <li><a href="empleado" class="nav-link"><i class="fas fa-users"></i><span>Empleados</span></a></li>
                </ul>
            </div>
            <% } %>
        </nav>
    </aside>

    <main class="main-content">
        <div class="edit-container">
            <div class="edit-header">
                <h1><i class="fas fa-edit"></i> Editar Habitación #<%= hab.getNumero() %></h1>
                <p class="edit-subtitle">Modifique los datos necesarios de la habitación</p>
                <a href="habitacion_detalle?id=<%= hab.getIdHabitacion() %>" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Volver al detalle
                </a>
            </div>

            <form action="habitacion_editar" method="post" class="edit-form">
                <input type="hidden" name="id" value="<%= hab.getIdHabitacion() %>">

                <div class="form-grid">
                    <div class="form-group">
                        <label for="numero">Número de Habitación</label>
                        <input type="text" id="numero" name="numero" value="<%= hab.getNumero() %>" required pattern="[0-9]{3}" title="Ingrese un número de 3 dígitos">
                    </div>

                    <div class="form-group">
                        <label for="piso">Piso</label>
                        <input type="number" id="piso" name="piso" value="<%= hab.getPiso() %>" min="1" max="20" required>
                    </div>

                    <div class="form-group">
                        <label for="idTipo">Tipo de Habitación</label>
                        <select id="idTipo" name="idTipo" required>
                            <% for (TipoHabitacionDTO t : tipos) { %>
                            <option value="<%= t.getIdTipo() %>" <%= t.getIdTipo() == hab.getIdTipo() ? "selected" : "" %>><%= t.getNombre() %></option>
                            <% } %>
                        </select>
                    </div>
                </div>

                <div class="form-group">
                    <label for="observaciones">Observaciones</label>
                    <textarea id="observaciones" name="observaciones" rows="4"><%= hab.getObservaciones() != null ? hab.getObservaciones() : "" %></textarea>
                </div>

                <div class="form-actions">
                    <a href="habitaciones" class="btn btn-secondary"><i class="fas fa-times"></i> Cancelar</a>
                    <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Guardar Cambios</button>
                </div>
            </form>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
</footer>

</body>
</html>
