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
    <title>Detalle de Producto - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/productos/producto_detalle.css">
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
        <div class="detail-container">
            <div class="detail-header">
                <h1><i class="fas fa-shopping-cart"></i> Detalle del Producto</h1>
                <p class="detail-subtitle">Información completa del producto registrado</p>
            </div>
            
            <div class="detail-card">
                <div class="detail-section">
                    <h3>Información Básica</h3>
                    <div class="detail-row">
                        <span class="detail-label">Nombre:</span>
                        <span class="detail-value"><%= producto.getNombre() %></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Descripción:</span>
                        <span class="detail-value"><%= producto.getDescripcion() %></span>
                    </div>
                </div>
                
                <div class="detail-section">
                    <h3>Información de Inventario</h3>
                    <div class="detail-row">
                        <span class="detail-label">Precio Unitario:</span>
                        <span class="detail-value">S/ <%= String.format("%.2f", producto.getPrecioUnitario()) %></span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Stock Actual:</span>
                        <span class="detail-value <%= producto.getStock() < producto.getStockMinimo() ? "text-danger" : "" %>">
                            <%= producto.getStock() %>
                            <% if (producto.getStock() < producto.getStockMinimo()) { %>
                                <i class="fas fa-exclamation-circle" title="Stock por debajo del mínimo"></i>
                            <% } %>
                        </span>
                    </div>
                    <div class="detail-row">
                        <span class="detail-label">Stock Mínimo:</span>
                        <span class="detail-value"><%= producto.getStockMinimo() %></span>
                    </div>
                </div>
                
                <div class="detail-section">
                    <h3>Estado</h3>
                    <div class="detail-row">
                        <span class="detail-label">Estado:</span>
                        <span class="detail-value <%= producto.isActivo() ? "text-success" : "text-danger" %>">
                            <i class="fas <%= producto.isActivo() ? "fa-check-circle" : "fa-times-circle" %>"></i>
                            <%= producto.isActivo() ? "Activo" : "Inactivo" %>
                        </span>
                    </div>
                </div>
            </div>
            <% if ("Administrador".equals(rolUsuario) || "Gerente".equals(rolUsuario)) { %>
            <div class="detail-actions">
                <a href="producto_editar?id=<%= producto.getIdProducto() %>" class="btn btn-primary">
                    <i class="fas fa-edit"></i> Editar Producto
                </a>
                
                <form action="producto_eliminar" method="post" onsubmit="return confirm('¿Está seguro que desea eliminar este producto?');" class="inline-form">
                    <input type="hidden" name="id" value="<%= producto.getIdProducto() %>">
                    <button type="submit" class="btn btn-danger">
                        <i class="fas fa-trash-alt"></i> Eliminar Producto
                    </button>
                </form>
                <% } %>
                <a href="producto" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Volver a la lista
                </a>
            </div>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
</footer>
</body>
</html>