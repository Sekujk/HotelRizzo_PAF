<%@page import="Utils.Conexion"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.EmpleadoDTO" %>
<%@ page import="Model.RolesDTO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");
    EmpleadoDTO empleado = (EmpleadoDTO) request.getAttribute("empleado");
    List<RolesDTO> roles = (List<RolesDTO>) request.getAttribute("roles");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    
    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Empleado - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/empleados/empleado_editar.css">
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
                <h1><i class="fas fa-user-edit"></i> Editar Empleado</h1>
                <p class="form-subtitle">Modifique los datos de <%= empleado.getNombreCompleto() %></p>
            </div>
            
            <form action="empleado_editar" method="post" class="empleado-form">
                <input type="hidden" name="id_empleado" value="<%= empleado.getIdEmpleado() %>">
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="nombre">Nombre</label>
                        <input type="text" id="nombre" name="nombre" required 
                               value="<%= empleado.getNombre() %>" placeholder="Ej. Juan">
                    </div>
                    <div class="form-group">
                        <label for="apellido">Apellido</label>
                        <input type="text" id="apellido" name="apellido" required 
                               value="<%= empleado.getApellido() %>" placeholder="Ej. Pérez">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="dni">DNI</label>
                        <input type="text" id="dni" name="dni" required 
                               value="<%= empleado.getDni() %>" placeholder="Ej. 12345678">
                    </div>
                    <div class="form-group">
                        <label for="telefono">Teléfono</label>
                        <input type="text" id="telefono" name="telefono" 
                               value="<%= empleado.getTelefono() %>" placeholder="Ej. 987654321">
                    </div>
                </div>

                <div class="form-group">
                    <label for="correo">Correo Electrónico</label>
                    <input type="email" id="correo" name="correo" 
                           value="<%= empleado.getCorreo() %>" placeholder="Ej. empleado@hotelrizzo.com">
                </div>

                <div class="form-group">
                    <label for="direccion">Dirección</label>
                    <input type="text" id="direccion" name="direccion" 
                           value="<%= empleado.getDireccion() %>" placeholder="Ej. Av. Principal 123">
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="fecha_nacimiento">Fecha de Nacimiento</label>
                        <input type="date" id="fecha_nacimiento" name="fecha_nacimiento" required
                               value="<%= empleado.getFechaNacimiento() != null ? sdf.format(empleado.getFechaNacimiento()) : "" %>">
                    </div>
                    <div class="form-group">
                        <label for="genero">Género</label>
                        <select id="genero" name="genero">
                            <option value="M" <%= "M".equals(empleado.getGenero()) ? "selected" : "" %>>Masculino</option>
                            <option value="F" <%= "F".equals(empleado.getGenero()) ? "selected" : "" %>>Femenino</option>
                        </select>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="id_rol">Rol</label>
                        <select id="id_rol" name="id_rol" required>
                            <% for (RolesDTO rol : roles) { %>
                                <option value="<%= rol.getIdRol() %>" <%= rol.getIdRol() == empleado.getIdRol() ? "selected" : "" %>>
                                    <%= rol.getNombreRol() %>
                                </option>
                            <% } %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="fecha_contratacion">Fecha de Contratación</label>
                        <input type="date" id="fecha_contratacion" name="fecha_contratacion" required
                               value="<%= empleado.getFechaContratacion() != null ? sdf.format(empleado.getFechaContratacion()) : "" %>">
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="salario">Salario (S/)</label>
                        <input type="number" id="salario" name="salario" step="0.01" required 
                               value="<%= empleado.getSalario() %>" placeholder="0.00">
                    </div>
                    <div class="form-group">
                        <label for="turno">Turno</label>
                        <select id="turno" name="turno">
                            <option value="Mañana" <%= "Mañana".equals(empleado.getTurno()) ? "selected" : "" %>>Mañana</option>
                            <option value="Tarde" <%= "Tarde".equals(empleado.getTurno()) ? "selected" : "" %>>Tarde</option>
                            <option value="Noche" <%= "Noche".equals(empleado.getTurno()) ? "selected" : "" %>>Noche</option>
                        </select>
                    </div>
                </div>

                <div class="form-actions">
                    <a href="empleado" class="btn btn-secondary">
                        <i class="fas fa-times"></i> Cancelar
                    </a>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Guardar Cambios
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