<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.ClienteDTO" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");
    ClienteDTO cliente = (ClienteDTO) request.getAttribute("cliente");

    if (usuario == null || rolUsuario == null || cliente == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Cliente - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/clientes/cliente_editar.css">
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
                    <li><a href="clientes" class="nav-link active"><i class="fas fa-id-card"></i><span>Clientes</span></a></li>
                    <li><a href="habitaciones" class="nav-link"><i class="fas fa-bed"></i><span>Habitaciones</span></a></li>
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
        <div class="form-container">
            <div class="form-header">
                <h1><i class="fas fa-edit"></i> Editar Cliente</h1>
                <p class="form-subtitle">Modifique los datos del cliente <%= cliente.getNombre() %> <%= cliente.getApellido() %></p>
            </div>

            <form action="cliente_editar" method="post" class="cliente-form">
                <input type="hidden" name="id_cliente" value="<%= cliente.getId() %>">

                <div class="form-group">
                    <label for="dni">DNI</label>
                    <input type="text" id="dni" name="dni" required maxlength="8" pattern="\d{8}"
                           value="<%= cliente.getDni() %>" placeholder="Ej: 12345678">
                </div>

                <div class="form-group">
                    <label for="nombre">Nombres</label>
                    <input type="text" id="nombre" name="nombre" required value="<%= cliente.getNombre() %>">
                </div>

                <div class="form-group">
                    <label for="apellido">Apellidos</label>
                    <input type="text" id="apellido" name="apellido" required value="<%= cliente.getApellido() %>">
                </div>

                <div class="form-group">
                    <label for="telefono">Teléfono</label>
                    <input type="text" id="telefono" name="telefono" value="<%= cliente.getTelefono() %>">
                </div>

                <div class="form-group">
                    <label for="correo">Correo Electrónico</label>
                    <input type="email" id="correo" name="correo" value="<%= cliente.getCorreo() %>">
                </div>

                <div class="form-group">
                    <label for="direccion">Dirección</label>
                    <input type="text" id="direccion" name="direccion" value="<%= cliente.getDireccion() %>">
                </div>

                <div class="form-group">
                    <label for="genero">Género</label>
                    <select name="genero" id="genero">
                        <option value="M" <%= "Masculino".equals(cliente.getGenero()) ? "selected" : "" %>>Masculino</option>
                        <option value="F" <%= "Femenino".equals(cliente.getGenero()) ? "selected" : "" %>>Femenino</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="fecha_nacimiento">Fecha de Nacimiento</label>
                    <input type="date" id="fecha_nacimiento" name="fecha_nacimiento" 
                           value="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(cliente.getFechaNacimiento()) %>">
                </div>

                <div class="form-group">
                    <label for="tipo_cliente">Tipo de Cliente</label>
                    <select id="tipo_cliente" name="tipo_cliente">
                        <option value="Regular" <%= "Regular".equals(cliente.getTipoCliente()) ? "selected" : "" %>>Regular</option>
                        <option value="Corporativo" <%= "Corporativo".equals(cliente.getTipoCliente()) ? "selected" : "" %>>Corporativo</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="empresa">Empresa (si aplica)</label>
                    <input type="text" id="empresa" name="empresa" value="<%= cliente.getEmpresa() %>">
                </div>

                <div class="form-group">
                    <label for="observaciones">Observaciones</label>
                    <textarea id="observaciones" name="observaciones" rows="3"><%= cliente.getObservaciones() != null ? cliente.getObservaciones() : "" %></textarea>
                </div>

                <div class="form-actions">
                    <a href="clientes" class="btn btn-secondary">
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
