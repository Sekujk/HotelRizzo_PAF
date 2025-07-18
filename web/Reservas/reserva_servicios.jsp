<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="Model.*" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    String rolUsuario = (String) session.getAttribute("rol");
    
    if (usuario == null || rolUsuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    ReservaDTO reserva = (ReservaDTO) request.getAttribute("reserva");
    List<ConsumoServicioDTO> serviciosConsumidos = (List<ConsumoServicioDTO>) request.getAttribute("serviciosConsumidos");
    List<ConsumoProductoDTO> productosConsumidos = (List<ConsumoProductoDTO>) request.getAttribute("productosConsumidos");
    BigDecimal totalServicios = (BigDecimal) request.getAttribute("totalServicios");
    BigDecimal totalProductos = (BigDecimal) request.getAttribute("totalProductos");
    
    String error = (String) request.getAttribute("error");
    String mensaje = (String) session.getAttribute("mensaje");
    if (mensaje != null) session.removeAttribute("mensaje");
    
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    if (reserva == null) {
        response.sendRedirect("reservas");
        return;
    }
    
    // Valores por defecto si son null
    if (totalServicios == null) totalServicios = BigDecimal.ZERO;
    if (totalProductos == null) totalProductos = BigDecimal.ZERO;
    
    BigDecimal totalConsumos = totalServicios.add(totalProductos);
    BigDecimal totalGeneral = reserva.getMontoTotal().add(totalConsumos);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Servicios y Productos - Reserva <%= reserva.getNumeroReserva() %></title>
    <link rel="stylesheet" href="css/reservas/reserva_crear.css">
    <link rel="stylesheet" href="css/utils/base.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .consumos-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 30px;
            margin-top: 20px;
        }
        
        .consumo-section {
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            border: 1px solid #e0e0e0;
        }
        
        .consumo-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #e0e0e0;
        }
        
        .consumo-header h3 {
            margin: 0;
            color: #333;
            font-size: 1.2em;
        }
        
        .total-consumo {
            background: linear-gradient(135deg, #4CAF50, #45a049);
            color: white;
            padding: 8px 15px;
            border-radius: 20px;
            font-weight: bold;
            font-size: 1.1em;
        }
        
        .consumo-item {
            background: #f8f9fa;
            padding: 15px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            margin-bottom: 15px;
            transition: all 0.3s ease;
        }
        
        .consumo-item:hover {
            border-color: #4CAF50;
            box-shadow: 0 2px 8px rgba(76, 175, 80, 0.2);
            transform: translateY(-2px);
        }
        
        .consumo-details {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
        }
        
        .consumo-info h4 {
            margin: 0 0 8px 0;
            color: #333;
            font-size: 1.1em;
        }
        
        .consumo-meta {
            font-size: 0.9em;
            color: #666;
            line-height: 1.4;
        }
        
        .consumo-meta i {
            margin-right: 5px;
            width: 12px;
        }
        
        .consumo-price {
            text-align: right;
            min-width: 100px;
        }
        
        .precio-unitario {
            font-size: 0.85em;
            color: #666;
            margin-bottom: 5px;
        }
        
        .precio-total {
            font-size: 1.2em;
            font-weight: bold;
            color: #2e7d32;
        }
        
        .total-section {
            background: linear-gradient(135deg, #2196f3, #1976d2);
            color: white;
            padding: 25px;
            border-radius: 12px;
            margin-top: 30px;
            text-align: center;
        }
        
        .total-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        
        .total-item {
            text-align: center;
        }
        
        .total-label {
            font-size: 0.9em;
            opacity: 0.9;
            margin-bottom: 5px;
        }
        
        .total-valor {
            font-size: 1.3em;
            font-weight: bold;
        }
        
        .total-final {
            border-left: 2px solid rgba(255,255,255,0.5);
            padding-left: 20px;
        }
        
        .total-final .total-label {
            font-size: 1em;
        }
        
        .total-final .total-valor {
            font-size: 1.6em;
        }
        
        .no-consumos {
            text-align: center;
            padding: 40px;
            color: #666;
        }
        
        .no-consumos i {
            font-size: 3em;
            margin-bottom: 15px;
            opacity: 0.5;
        }
        
        .btn-agregar {
            background: linear-gradient(135deg, #10b981, #059669);
            color: white;
            border: none;
            padding: 12px 24px;
            border-radius: 8px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: all 0.3s ease;
            font-weight: 500;
        }
        
        .btn-agregar:hover {
            background: linear-gradient(135deg, #059669, #047857);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
        }
        
        .btn-agregar.producto {
            background: linear-gradient(135deg, #3b82f6, #2563eb);
        }
        
        .btn-agregar.producto:hover {
            background: linear-gradient(135deg, #2563eb, #1d4ed8);
            box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
        }
        
        .actions-bar {
            margin-bottom: 30px;
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .reserva-info {
            background: white;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }
        
        .info-item {
            text-align: center;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 8px;
        }
        
        .info-label {
            font-size: 0.9em;
            color: #666;
            margin-bottom: 5px;
        }
        
        .info-valor {
            font-size: 1.1em;
            font-weight: bold;
            color: #333;
        }
        
        .alert {
            padding: 12px;
            margin-bottom: 20px;
            border-radius: 6px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .alert-success {
            background: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
        }
        
        .alert-error {
            background: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
        }
        
        @media (max-width: 768px) {
            .consumos-grid {
                grid-template-columns: 1fr;
                gap: 20px;
            }
            
            .total-grid {
                grid-template-columns: 1fr;
            }
            
            .total-final {
                border-left: none;
                border-top: 2px solid rgba(255,255,255,0.5);
                padding-left: 0;
                padding-top: 15px;
                margin-top: 15px;
            }
            
            .actions-bar {
                flex-direction: column;
                align-items: center;
            }
        }
    </style>
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
                    <li><a href="reservas" class="nav-link active">
                        <i class="fas fa-calendar-alt"></i>
                        <span>Reservas</span>
                    </a></li>
                    <li><a href="clientes" class="nav-link">
                        <i class="fas fa-users"></i>
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
        </nav>
    </aside>

    <main class="main-content">
        <div class="dashboard-header">
            <h1><i class="fas fa-concierge-bell"></i> Servicios y Productos</h1>
            <p class="dashboard-subtitle">Gestión de consumos - Reserva: <%= reserva.getNumeroReserva() %></p>
        </div>

        <!-- Mensajes -->
        <% if (mensaje != null) { %>
        <div class="alert alert-success">
            <i class="fas fa-check-circle"></i>
            <span><%= mensaje %></span>
        </div>
        <% } %>

        <% if (error != null) { %>
        <div class="alert alert-error">
            <i class="fas fa-exclamation-circle"></i>
            <span><%= error %></span>
        </div>
        <% } %>

        <!-- Información de la Reserva -->
        <div class="reserva-info">
            <div class="form-header">
                <h2><i class="fas fa-info-circle"></i> Información de la Reserva</h2>
            </div>
            
            <div class="info-grid">
                <div class="info-item">
                    <div class="info-label">Cliente</div>
                    <div class="info-valor"><%= reserva.getNombreCliente() %></div>
                </div>
                <div class="info-item">
                    <div class="info-label">Estado</div>
                    <div class="info-valor">
                        <% 
                        String estadoClass = "";
                        String estadoIcon = "";
                        switch(reserva.getEstado()) {
                            case "CheckIn": 
                                estadoClass = "style='color: #2196f3;'";
                                estadoIcon = "fas fa-door-open";
                                break;
                            case "Confirmada": 
                                estadoClass = "style='color: #4CAF50;'";
                                estadoIcon = "fas fa-check";
                                break;
                            default: 
                                estadoIcon = "fas fa-info";
                        }
                        %>
                        <span <%= estadoClass %>><i class="<%= estadoIcon %>"></i> <%= reserva.getEstado() %></span>
                    </div>
                </div>
                <div class="info-item">
                    <div class="info-label">Fechas</div>
                    <div class="info-valor">
                        <%= reserva.getFechaEntrada().format(dateFormatter) %> - <%= reserva.getFechaSalida().format(dateFormatter) %>
                    </div>
                </div>
                <div class="info-item">
                    <div class="info-label">Total Habitación</div>
                    <div class="info-valor">S/. <%= String.format("%.2f", reserva.getMontoTotal()) %></div>
                </div>
            </div>
        </div>

        <!-- Botones de Acción -->
        <% if ("CheckIn".equals(reserva.getEstado())) { %>
        <div class="actions-bar">
            <a href="reserva_servicios?action=agregar_servicio&id=<%= reserva.getIdReserva() %>" class="btn-agregar">
                <i class="fas fa-concierge-bell"></i>
                Agregar Servicio
            </a>
            <a href="reserva_servicios?action=agregar_producto&id=<%= reserva.getIdReserva() %>" class="btn-agregar producto">
                <i class="fas fa-shopping-cart"></i>
                Agregar Producto
            </a>
        </div>
        <% } %>

        <!-- Grid de Consumos -->
        <div class="consumos-grid">
            <!-- Servicios Consumidos -->
            <div class="consumo-section">
                <div class="consumo-header">
                    <h3><i class="fas fa-concierge-bell"></i> Servicios Consumidos</h3>
                    <div class="total-consumo">
                        S/. <%= String.format("%.2f", totalServicios) %>
                    </div>
                </div>
                
                <% if (serviciosConsumidos != null && !serviciosConsumidos.isEmpty()) { %>
                    <% for (ConsumoServicioDTO consumo : serviciosConsumidos) { %>
                    <div class="consumo-item">
                        <div class="consumo-details">
                            <div class="consumo-info">
                                <h4><%= consumo.getNombreServicio() %></h4>
                                <div class="consumo-meta">
                                    <div><i class="fas fa-calendar"></i><%= consumo.getFechaConsumo().format(dateFormatter) %></div>
                                    <div><i class="fas fa-clock"></i><%= consumo.getHoraConsumo().format(timeFormatter) %></div>
                                    <div><i class="fas fa-sort-numeric-up"></i>Cantidad: <%= consumo.getCantidad() %></div>
                                    <% if (consumo.getObservaciones() != null && !consumo.getObservaciones().trim().isEmpty()) { %>
                                    <div><i class="fas fa-comment"></i><%= consumo.getObservaciones() %></div>
                                    <% } %>
                                </div>
                            </div>
                            <div class="consumo-price">
                                <div class="precio-unitario">S/. <%= String.format("%.2f", consumo.getPrecioUnitario()) %> c/u</div>
                                <div class="precio-total">S/. <%= String.format("%.2f", consumo.getSubtotal()) %></div>
                            </div>
                        </div>
                    </div>
                    <% } %>
                <% } else { %>
                    <div class="no-consumos">
                        <i class="fas fa-concierge-bell"></i>
                        <p>No hay servicios consumidos</p>
                        <% if ("CheckIn".equals(reserva.getEstado())) { %>
                        <small>Puede agregar servicios usando el botón de arriba</small>
                        <% } %>
                    </div>
                <% } %>
            </div>

            <!-- Productos Consumidos -->
            <div class="consumo-section">
                <div class="consumo-header">
                    <h3><i class="fas fa-shopping-cart"></i> Productos Consumidos</h3>
                    <div class="total-consumo" style="background: linear-gradient(135deg, #3b82f6, #2563eb);">
                        S/. <%= String.format("%.2f", totalProductos) %>
                    </div>
                </div>
                
                <% if (productosConsumidos != null && !productosConsumidos.isEmpty()) { %>
                    <% for (ConsumoProductoDTO consumo : productosConsumidos) { %>
                    <div class="consumo-item">
                        <div class="consumo-details">
                            <div class="consumo-info">
                                <h4><%= consumo.getNombreProducto() %></h4>
                                <div class="consumo-meta">
                                    <div><i class="fas fa-calendar"></i><%= consumo.getFechaConsumo().format(dateFormatter) %></div>
                                    <div><i class="fas fa-clock"></i><%= consumo.getHoraConsumo().format(timeFormatter) %></div>
                                    <div><i class="fas fa-sort-numeric-up"></i>Cantidad: <%= consumo.getCantidad() %></div>
                                    <% if (consumo.getObservaciones() != null && !consumo.getObservaciones().trim().isEmpty()) { %>
                                    <div><i class="fas fa-comment"></i><%= consumo.getObservaciones() %></div>
                                    <% } %>
                                </div>
                            </div>
                            <div class="consumo-price">
                                <div class="precio-unitario">S/. <%= String.format("%.2f", consumo.getPrecioUnitario()) %> c/u</div>
                                <div class="precio-total">S/. <%= String.format("%.2f", consumo.getPrecioUnitario().multiply(new BigDecimal(consumo.getCantidad()))) %></div>
                            </div>
                        </div>
                    </div>
                    <% } %>
                <% } else { %>
                    <div class="no-consumos">
                        <i class="fas fa-shopping-cart"></i>
                        <p>No hay productos consumidos</p>
                        <% if ("CheckIn".equals(reserva.getEstado())) { %>
                        <small>Puede agregar productos del minibar usando el botón de arriba</small>
                        <% } %>
                    </div>
                <% } %>
            </div>
        </div>

        <!-- Total General -->
        <% if (totalConsumos.compareTo(BigDecimal.ZERO) > 0) { %>
        <div class="total-section">
            <h3><i class="fas fa-calculator"></i> Resumen de Consumos</h3>
            <div class="total-grid">
                <div class="total-item">
                    <div class="total-label">Servicios</div>
                    <div class="total-valor">S/. <%= String.format("%.2f", totalServicios) %></div>
                </div>
                <div class="total-item">
                    <div class="total-label">Productos</div>
                    <div class="total-valor">S/. <%= String.format("%.2f", totalProductos) %></div>
                </div>
                <div class="total-item">
                    <div class="total-label">Habitación</div>
                    <div class="total-valor">S/. <%= String.format("%.2f", reserva.getMontoTotal()) %></div>
                </div>
                <div class="total-item total-final">
                    <div class="total-label">TOTAL GENERAL</div>
                    <div class="total-valor">S/. <%= String.format("%.2f", totalGeneral) %></div>
                </div>
            </div>
        </div>
        <% } %>

        <!-- Botones de Navegación -->
        <div class="form-actions" style="margin-top: 30px; text-align: center;">
            <a href="reservas" class="btn btn-secondary">
                <i class="fas fa-arrow-left"></i> Volver a Reservas
            </a>
            
            <% if ("CheckIn".equals(reserva.getEstado())) { %>
            <a href="reserva_checkout?id=<%= reserva.getIdReserva() %>" class="btn btn-primary" style="margin-left: 15px;">
                <i class="fas fa-door-closed"></i> Proceder al Check-Out
            </a>
            <% } %>
        </div>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
</footer>

</body>
</html>