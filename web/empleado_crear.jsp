<%@page import="Utils.Conexion"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.RolesDTO" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");
    List<RolesDTO> roles = (List<RolesDTO>) request.getAttribute("roles");
    String usuarioGenerado = (String) request.getAttribute("usuario_generado");
    String claveGenerada = (String) request.getAttribute("clave_generada");
    
    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registrar Empleado - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/empleados/empleado_crear.css">
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
        <div class="form-container">
            <div class="form-header">
                <h1><i class="fas fa-user-plus"></i> Registrar Nuevo Empleado</h1>
                <p class="form-subtitle">Complete los datos del nuevo empleado</p>
            </div>

            <% if (usuarioGenerado != null && claveGenerada != null) { %>
            <div class="alert alert-success">
                <h4><i class="fas fa-key"></i> Credenciales Generadas</h4>
                <p><strong>Usuario:</strong> <%= usuarioGenerado %></p>
                <p><strong>Contraseña:</strong> <%= claveGenerada %></p>
                <p class="text-muted">Guarde esta información en un lugar seguro</p>
            </div>
            <% } %>

            <form action="empleado_crear" method="post" class="empleado-form">
                <div class="form-row">
                    <div class="form-group">
                        <label for="nombre">Nombre</label>
                        <input type="text" id="nombre" name="nombre" required placeholder="Ej. Juan">
                    </div>
                    <div class="form-group">
                        <label for="apellido">Apellido</label>
                        <input type="text" id="apellido" name="apellido" required placeholder="Ej. Pérez">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="dni">DNI</label>
                        <input type="text" id="dni" name="dni" required placeholder="Ej. 12345678">
                    </div>
                    <div class="form-group">
                        <label for="telefono">Teléfono</label>
                        <input type="text" id="telefono" name="telefono" placeholder="Ej. 987654321">
                    </div>
                </div>

                <div class="form-group">
                    <label for="correo">Correo Electrónico</label>
                    <input type="email" id="correo" name="correo" placeholder="Ej. empleado@hotelrizzo.com">
                </div>

                <div class="form-group">
                    <label for="direccion">Dirección</label>
                    <input type="text" id="direccion" name="direccion" placeholder="Ej. Av. Principal 123">
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="fecha_nacimiento">Fecha de Nacimiento</label>
                        <input type="date" id="fecha_nacimiento" name="fecha_nacimiento" required>
                    </div>
                    <div class="form-group">
                        <label for="genero">Género</label>
                        <select id="genero" name="genero">
                            <option value="M">Masculino</option>
                            <option value="F">Femenino</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="id_rol">Rol</label>
                        <select id="id_rol" name="id_rol" required>
                            <% for (RolesDTO rol : roles) { %>
                                <option value="<%= rol.getIdRol() %>"><%= rol.getNombreRol() %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="fecha_contratacion">Fecha de Contratación</label>
                        <input type="date" id="fecha_contratacion" name="fecha_contratacion" required>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="salario">Salario (S/)</label>
                        <input type="number" id="salario" name="salario" step="0.01" required placeholder="0.00">
                    </div>
                    <div class="form-group">
                        <label for="turno">Turno</label>
                        <select id="turno" name="turno">
                            <option value="Mañana">Mañana</option>
                            <option value="Tarde">Tarde</option>
                            <option value="Noche">Noche</option>
                        </select>
                    </div>
                </div>

                <div class="form-actions">
                    <a href="empleado" class="btn btn-secondary">
                        <i class="fas fa-times"></i> Cancelar
                    </a>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Registrar Empleado
                    </button>
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