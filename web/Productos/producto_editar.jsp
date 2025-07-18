<%@page import="Utils.Conexion"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="Model.ProductoDTO" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");
    ProductoDTO producto = (ProductoDTO) request.getAttribute("producto");
    
    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Editar Producto - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/productos/producto_editar.css">
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
                    <li><a href="producto" class="nav-link active">
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
        <div class="form-container">
            <div class="form-header">
                <h1><i class="fas fa-edit"></i> Editar Producto</h1>
                <p class="form-subtitle">Modifique los datos del producto <%= producto.getNombre() %></p>
            </div>
            
            <form action="producto_editar" method="post" class="producto-form">
                <input type="hidden" name="id" value="<%= producto.getIdProducto() %>">
                
                <div class="form-group">
                    <label for="nombre">Nombre del Producto</label>
                    <input type="text" id="nombre" name="nombre" required 
                           value="<%= producto.getNombre() %>" placeholder="Nombre del producto">
                </div>
                
                <div class="form-group">
                    <label for="descripcion">Descripción</label>
                    <textarea id="descripcion" name="descripcion" rows="3" required
                              placeholder="Descripción detallada del producto"><%= producto.getDescripcion() %></textarea>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="precio">Precio Unitario (S/)</label>
                        <input type="number" id="precio" name="precio" step="0.01" required 
                               value="<%= producto.getPrecioUnitario() %>" placeholder="0.00">
                    </div>
                    
                    <div class="form-group">
                        <label for="stock">Stock Actual</label>
                        <input type="number" id="stock" name="stock" required 
                               value="<%= producto.getStock() %>" placeholder="0">
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="stockMinimo">Stock Mínimo</label>
                    <input type="number" id="stockMinimo" name="stockMinimo" required 
                           value="<%= producto.getStockMinimo() %>" placeholder="Mínimo requerido">
                </div>
                
                <div class="form-actions">
                    <a href="producto" class="btn btn-secondary">
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