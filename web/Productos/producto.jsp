<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Model.ProductoDTO" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");
    
    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    List<ProductoDTO> productos = (List<ProductoDTO>) request.getAttribute("productos");
    long activos = (long) request.getAttribute("activos");
    int total = (int) request.getAttribute("total");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Productos - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/productos/producto.css">
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
        <a href="logout" class="btn-logout">
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
        <div class="dashboard-header">
            <h1><i class="fas fa-shopping-cart"></i> Gestión de Productos</h1>
            <p class="dashboard-subtitle">Panel de administración de productos del hotel</p>
        </div>

        <div class="stats-grid">
            <div class="stat-card info">
                <div class="stat-icon">
                    <i class="fas fa-list"></i>
                </div>
                <div class="stat-content">
                    <h3 id="stat-total"><%= total %></h3>
                    <p>Total Productos</p>
                </div>
            </div>

            <div class="stat-card success">
                <div class="stat-icon">
                    <i class="fas fa-check-circle"></i>
                </div>
                <div class="stat-content">
                    <h3 id="stat-activos"><%= activos %></h3>
                    <p>Disponibles</p>
                </div>
            </div>

            <div class="stat-card warning">
                <div class="stat-icon">
                    <i class="fas fa-exclamation-triangle"></i>
                </div>
                <div class="stat-content">
                    <h3 id="stat-bajo-stock">${bajoStock}</h3>
                    <p>Bajo Stock</p>
                </div>
            </div>
        </div>

        <div class="actions-bar">
            <div class="left-actions">
                <a href="dashboard" class="btn btn-secondary">
                    <i class="fas fa-arrow-left"></i> Volver al Panel
                </a>
                <% if ("Administrador".equals(rolUsuario) || "Gerente".equals(rolUsuario)) { %>
                <a href="producto_crear" class="btn btn-primary">
                    <i class="fas fa-plus"></i> Nuevo Producto
                </a>
                <% } %>
            </div>
            <div class="search-box">
                <i class="fas fa-search"></i>
                <input type="text" id="searchInput" placeholder="Buscar por nombre de producto...">
            </div>
        </div>

        <div class="table-container">
            <table id="productosTable">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Nombre</th>
                        <th>Precio (S/.)</th>
                        <th>Stock</th>
                        <th>Stock Mínimo</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (productos != null && !productos.isEmpty()) { 
                        int index = 1;
                        for (ProductoDTO p : productos) { 
                            String estadoClase = p.getStock() <= p.getStockMinimo() ? "bajo-stock" : 
                                              (p.isActivo() ? "activo" : "inactivo");
                    %>
                    <tr data-nombre="<%= p.getNombre().toLowerCase() %>" 
                        data-estado="<%= estadoClase %>">
                        <td><%= index++ %></td>
                        <td><strong><%= p.getNombre() %></strong></td>
                        <td>S/. <%= String.format("%.2f", p.getPrecioUnitario()) %></td>
                        <td class="<%= p.getStock() <= p.getStockMinimo() ? "text-danger" : "" %>">
                            <%= p.getStock() %>
                        </td>
                        <td><%= p.getStockMinimo() %></td>
                        <td>
                            <span class="status-badge <%= estadoClase %>">
                                <%= estadoClase.equals("bajo-stock") ? "Bajo Stock" : 
                                    (p.isActivo() ? "Activo" : "Inactivo") %>
                            </span>
                        </td>
                        <td class="actions-cell">
                            <div class="action-buttons">
                                <a class="btn-action view" href="producto_detalle?id=<%= p.getIdProducto() %>" title="Ver Detalles">
                                    <i class="fas fa-eye"></i>
                                </a>
                                <% if ("Administrador".equals(rolUsuario) || "Gerente".equals(rolUsuario)) { %>
                                <a class="btn-action edit" href="producto_editar?id=<%= p.getIdProducto() %>" title="Editar">
                                    <i class="fas fa-edit"></i>
                                </a>
                                <% if (p.isActivo()) { %>
                                <form method="post" action="producto_desactivar" class="inline-form">
                                    <input type="hidden" name="id" value="<%= p.getIdProducto() %>">
                                    <button type="submit" class="btn-action deactivate" title="Desactivar" onclick="return confirm('¿Desactivar este producto?');">
                                        <i class="fas fa-toggle-on"></i>
                                    </button>
                                </form>
                                <% } else { %>
                                <form method="post" action="producto_activar" class="inline-form">
                                    <input type="hidden" name="id" value="<%= p.getIdProducto() %>">
                                    <button type="submit" class="btn-action activate" title="Activar" onclick="return confirm('¿Activar este producto?');">
                                        <i class="fas fa-toggle-off"></i>
                                    </button>
                                </form>
                                <% } %>
                                <%  } %>
                            </div>
                        </td>
                    </tr>
                    <% } 
                    } else { %>
                    <tr class="no-data">
                        <td colspan="7">
                            <div class="empty-state">
                                <i class="fas fa-box-open"></i>
                                <p>No hay productos registrados</p>
                            </div>
                        </td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
</footer>

<script src="${pageContext.request.contextPath}/js/producto.js"></script>
</body>
</html>