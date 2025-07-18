<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.TipoHabitacionDTO" %>
<%@ page session="true" %>
<%
    TipoHabitacionDTO tipo = (TipoHabitacionDTO) request.getAttribute("tipo");
    String rolUsuario = (String) session.getAttribute("rol");
    
    if (rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Tipo Habitación - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/utils/base.css">
    <link rel="stylesheet" href="css/tipohabitaciones/tipohabitacion_editar.css">
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
                <span class="user-name"><%= session.getAttribute("usuarioLogueado") %></span>
                <span class="user-role"><%= rolUsuario %></span>
            </div>
        </div>
        <form action="logout" method="post" class="logout-form">
            <button type="submit" class="btn-logout">
                <i class="fas fa-sign-out-alt"></i>
            </button>
        </form>
    </div>
</header>

<div class="app-container">
    <aside class="sidebar">
        <nav class="nav-menu">
            <div class="nav-section">
                <h3>Gestión Principal</h3>
                <ul>
                    <li><a href="reservas" class="nav-link">
                        <i class="fas fa-calendar-alt"></i>
                        <span>Reservas</span>
                    </a></li>
                    <li><a href="clientes" class="nav-link">
                        <i class="fas fa-id-card"></i>
                        <span>Clientes</span>
                    </a></li>
                    <li><a href="habitaciones" class="nav-link active">
                        <i class="fas fa-bed"></i>
                        <span>Habitaciones</span>
                    </a></li>
                </ul>
            </div>
            
            <div class="nav-section">
                <h3>Servicios</h3>
                <ul>
                    <li><a href="servicio" class="nav-link">
                        <i class="fas fa-concierge-bell"></i>
                        <span>Servicios</span>
                    </a></li>
                    <li><a href="producto" class="nav-link">
                        <i class="fas fa-shopping-cart"></i>
                        <span>Productos</span>
                    </a></li>
                </ul>
            </div>

            <% if ("Administrador".equals(rolUsuario) || "Gerente".equals(rolUsuario)) { %>
            <div class="nav-section">
                <h3>Administración</h3>
                <ul>
                    <li><a href="empleado" class="nav-link">
                        <i class="fas fa-users"></i>
                        <span>Empleados</span>
                    </a></li>
                </ul>
            </div>
            <% } %>
        </nav>
    </aside>

    <main class="main-content">
        <div class="dashboard-header">
            <h1><i class="fas fa-bed"></i> Editar Tipo de Habitación</h1>
            <p class="dashboard-subtitle">Modifique los datos del tipo de habitación</p>
        </div>

        <div class="edit-form-container">
            <form action="tipoHabitacion_editar" method="post" class="edit-form">
                <input type="hidden" name="idTipo" value="<%= tipo.getIdTipo() %>">

                <div class="form-group">
                    <label for="nombre" class="form-label">
                        <i class="fas fa-tag"></i> Nombre del Tipo
                    </label>
                    <input type="text" id="nombre" name="nombre" value="<%= tipo.getNombre() %>" 
                           class="form-input" required placeholder="Ej. Suite Ejecutiva">
                </div>

                <div class="form-group">
                    <label for="descripcion" class="form-label">
                        <i class="fas fa-align-left"></i> Descripción
                    </label>
                    <textarea id="descripcion" name="descripcion" class="form-textarea" 
                              required rows="4" placeholder="Describa las características del tipo de habitación"><%= tipo.getDescripcion() %></textarea>
                </div>

                <div class="form-columns">
                    <div class="form-group">
                        <label for="capacidad" class="form-label">
                            <i class="fas fa-users"></i> Capacidad (personas)
                        </label>
                        <input type="number" id="capacidad" name="capacidad" 
                               value="<%= tipo.getCapacidadPersonas() %>" class="form-input" 
                               required min="1" max="10">
                    </div>

                    <div class="form-group">
                        <label for="precioBase" class="form-label">
                            <i class="fas fa-money-bill-wave"></i> Precio Base (S/)
                        </label>
                        <div class="input-with-icon">
                            <span class="input-icon">S/</span>
                            <input type="number" id="precioBase" name="precioBase" 
                                   value="<%= String.format("%.2f", tipo.getPrecioBase()) %>" 
                                   class="form-input" step="0.01" required min="0">
                        </div>
                    </div>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Guardar Cambios
                    </button>
                    <a href="tipoHabitacion" class="btn btn-secondary">
                        <i class="fas fa-times"></i> Cancelar
                    </a>
                </div>
            </form>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
    <p class="version">Versión 1.0.0</p>
</footer>

</body>
</html>