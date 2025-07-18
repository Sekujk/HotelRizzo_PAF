<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.EmpleadoDTO, Model.UsuarioDTO, Model.RolesDTO" %>
<%@ page session="true" %>
<%
    EmpleadoDTO empleado = (EmpleadoDTO) request.getAttribute("empleado");
    UsuarioDTO usuario = (UsuarioDTO) request.getAttribute("usuario");
    
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
    <title>Detalle del Empleado - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/utils/base.css">
    <link rel="stylesheet" href="css/empleados/empleado_detalle.css">
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
                    <li><a href="habitaciones" class="nav-link">
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
                    <li><a href="empleado" class="nav-link active">
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
            <h1><i class="fas fa-user-tie"></i> Detalle del Empleado</h1>
            <p class="dashboard-subtitle">Información completa del empleado</p>
        </div>

        <div class="detail-container">
            <div class="employee-card">
                <div class="employee-header">
                    <div class="employee-avatar">
                        <i class="fas fa-user-tie"></i>
                    </div>
                    <div class="employee-title">
                        <h2><%= empleado.getNombre() %> <%= empleado.getApellido() %></h2>
                        <p class="employee-position"><%= empleado.getNombreRol() %></p>
                    </div>
                </div>

                <div class="employee-details">
                    <div class="detail-section">
                        <h3><i class="fas fa-id-card"></i> Información Personal</h3>
                        <div class="detail-grid">
                            <div class="detail-item">
                                <span class="detail-label">DNI:</span>
                                <span class="detail-value"><%= empleado.getDni() %></span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Género:</span>
                                <span class="detail-value"><%= empleado.getGenero() %></span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Fecha Nacimiento:</span>
                                <span class="detail-value"><%= empleado.getFechaNacimiento() %></span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Teléfono:</span>
                                <span class="detail-value"><%= empleado.getTelefono() %></span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Correo:</span>
                                <span class="detail-value"><%= empleado.getCorreo() %></span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Dirección:</span>
                                <span class="detail-value"><%= empleado.getDireccion() %></span>
                            </div>
                        </div>
                    </div>

                    <div class="detail-section">
                        <h3><i class="fas fa-briefcase"></i> Información Laboral</h3>
                        <div class="detail-grid">
                            <div class="detail-item">
                                <span class="detail-label">Fecha Contratación:</span>
                                <span class="detail-value"><%= empleado.getFechaContratacion() %></span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Salario:</span>
                                <span class="detail-value">S/ <%= String.format("%.2f", empleado.getSalario()) %></span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Turno:</span>
                                <span class="detail-value"><%= empleado.getTurno() %></span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Estado:</span>
                                <span class="detail-value status-badge <%= empleado.isActivo() ? "active" : "inactive" %>">
                                    <%= empleado.isActivo() ? "Activo" : "Inactivo" %>
                                </span>
                            </div>
                        </div>
                    </div>

                    <% if (usuario != null) { %>
                    <div class="detail-section">
                        <h3><i class="fas fa-user-lock"></i> Cuenta de Usuario</h3>
                        <div class="detail-grid">
                            <div class="detail-item">
                                <span class="detail-label">Nombre de usuario:</span>
                                <span class="detail-value"><%= usuario.getUsername() %></span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Contraseña (hash):</span>
                                <span class="detail-value password-hash"><%= usuario.getPasswordHash() %></span>
                            </div>
                        </div>
                    </div>
                    <% } else { %>
                    <div class="detail-section">
                        <h3><i class="fas fa-user-lock"></i> Cuenta de Usuario</h3>
                        <p class="no-account-message"><i class="fas fa-info-circle"></i> Este empleado no tiene cuenta de usuario.</p>
                    </div>
                    <% } %>
                </div>

                <div class="employee-actions">
                    <a href="empleado_editar?id=<%= empleado.getIdEmpleado() %>" class="btn btn-primary">
                        <i class="fas fa-edit"></i> Editar Empleado
                    </a>
                    
                    <% if (empleado.isActivo()) { %>
                    <form action="empleado_desactivar" method="post" class="inline-form">
                        <input type="hidden" name="id" value="<%= empleado.getIdEmpleado() %>">
                        <button type="submit" class="btn btn-warning">
                            <i class="fas fa-user-slash"></i> Desactivar
                        </button>
                    </form>
                    <% } else { %>
                    <form action="empleado_activar" method="post" class="inline-form">
                        <input type="hidden" name="id" value="<%= empleado.getIdEmpleado() %>">
                        <button type="submit" class="btn btn-success">
                            <i class="fas fa-user-check"></i> Activar
                        </button>
                    </form>
                    <% } %>
                    
                    <form action="empleado_eliminar" method="post" class="inline-form" onsubmit="return confirm('¿Estás seguro de eliminar a este empleado?');">
                        <input type="hidden" name="id" value="<%= empleado.getIdEmpleado() %>">
                        <button type="submit" class="btn btn-danger">
                            <i class="fas fa-trash-alt"></i> Eliminar
                        </button>
                    </form>
                    
                    <a href="empleado" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Volver a la lista
                    </a>
                </div>
            </div>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
    <p class="version">Versión 1.0.0</p>
</footer>

</body>
</html>