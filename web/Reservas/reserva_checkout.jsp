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
    String habitaciones = (String) request.getAttribute("habitaciones");
    List<ConsumoServicioDTO> serviciosConsumidos = (List<ConsumoServicioDTO>) request.getAttribute("serviciosConsumidos");
    List<ConsumoProductoDTO> productosConsumidos = (List<ConsumoProductoDTO>) request.getAttribute("productosConsumidos");
    List<PagoDTO> pagos = (List<PagoDTO>) request.getAttribute("pagos");
    List<ComprobanteDTO> comprobantes = (List<ComprobanteDTO>) request.getAttribute("comprobantes");
    
    // Totales
    BigDecimal totalServicios = (BigDecimal) request.getAttribute("totalServicios");
    BigDecimal totalProductos = (BigDecimal) request.getAttribute("totalProductos");
    BigDecimal subtotalConsumos = (BigDecimal) request.getAttribute("subtotalConsumos");
    BigDecimal subtotalGeneral = (BigDecimal) request.getAttribute("subtotalGeneral");
    BigDecimal impuestosConsumos = (BigDecimal) request.getAttribute("impuestosConsumos");
    BigDecimal impuestosGenerales = (BigDecimal) request.getAttribute("impuestosGenerales");
    BigDecimal totalGeneral = (BigDecimal) request.getAttribute("totalGeneral");
    BigDecimal totalPagado = (BigDecimal) request.getAttribute("totalPagado");
    BigDecimal saldoPendiente = (BigDecimal) request.getAttribute("saldoPendiente");
    
    Boolean tieneComprobante = (Boolean) request.getAttribute("tieneComprobante");
    
    String error = (String) request.getAttribute("error");
    String mensaje = (String) session.getAttribute("mensaje");
    if (mensaje != null) session.removeAttribute("mensaje");
    
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    if (reserva == null) {
        response.sendRedirect("reservas");
        return;
    }
    
    // Valores por defecto si son null
    if (totalServicios == null) totalServicios = BigDecimal.ZERO;
    if (totalProductos == null) totalProductos = BigDecimal.ZERO;
    if (subtotalConsumos == null) subtotalConsumos = BigDecimal.ZERO;
    if (subtotalGeneral == null) subtotalGeneral = BigDecimal.ZERO;
    if (impuestosConsumos == null) impuestosConsumos = BigDecimal.ZERO;
    if (impuestosGenerales == null) impuestosGenerales = BigDecimal.ZERO;
    if (totalGeneral == null) totalGeneral = BigDecimal.ZERO;
    if (totalPagado == null) totalPagado = BigDecimal.ZERO;
    if (saldoPendiente == null) saldoPendiente = BigDecimal.ZERO;
    if (tieneComprobante == null) tieneComprobante = false;
    
    boolean tieneSaldoPendiente = saldoPendiente.compareTo(BigDecimal.ZERO) > 0;
    boolean puedeHacerCheckout = !tieneSaldoPendiente || rolUsuario.equals("Gerente") || rolUsuario.equals("Administrador");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Check-Out - Reserva <%= reserva.getNumeroReserva() %></title>
    <link rel="stylesheet" href="css/reservas/reserva_crear.css">
    <link rel="stylesheet" href="css/utils/base.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .checkout-container {
            max-width: 1200px;
            margin: 0 auto;
        }
        
        .checkout-header {
            background: linear-gradient(135deg, #f44336, #d32f2f);
            color: white;
            border-radius: 12px;
            padding: 25px;
            margin-bottom: 25px;
            box-shadow: 0 4px 20px rgba(244, 67, 54, 0.3);
        }
        
        .header-content {
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
        }
        
        .header-info h2 {
            margin: 0;
            font-size: 1.8em;
        }
        
        .header-info p {
            margin: 5px 0 0 0;
            opacity: 0.9;
        }
        
        .status-badge {
            background: rgba(255, 255, 255, 0.2);
            padding: 8px 16px;
            border-radius: 20px;
            font-weight: bold;
            backdrop-filter: blur(10px);
        }
        
        .checkout-grid {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 30px;
            margin-bottom: 30px;
        }
        
        .main-content-area {
            display: flex;
            flex-direction: column;
            gap: 25px;
        }
        
        .sidebar-area {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }
        
        .section-card {
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            border: 1px solid #e0e0e0;
        }
        
        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #e0e0e0;
        }
        
        .section-header h3 {
            margin: 0;
            color: #333;
            font-size: 1.2em;
        }
        
        .section-total {
            background: linear-gradient(135deg, #4CAF50, #45a049);
            color: white;
            padding: 8px 15px;
            border-radius: 20px;
            font-weight: bold;
        }
        
        .consumo-item {
            background: #f8f9fa;
            padding: 15px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            margin-bottom: 10px;
        }
        
        .consumo-details {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
        }
        
        .consumo-info h4 {
            margin: 0 0 8px 0;
            color: #333;
            font-size: 1em;
        }
        
        .consumo-meta {
            font-size: 0.85em;
            color: #666;
            line-height: 1.4;
        }
        
        .consumo-price {
            text-align: right;
            min-width: 80px;
        }
        
        .precio-total {
            font-weight: bold;
            color: #2e7d32;
        }
        
        .resumen-financiero {
            background: linear-gradient(135deg, #2196f3, #1976d2);
            color: white;
        }
        
        .resumen-financiero .section-header {
            border-bottom-color: rgba(255,255,255,0.3);
        }
        
        .resumen-financiero h3 {
            color: white;
        }
        
        .resumen-item {
            display: flex;
            justify-content: space-between;
            padding: 8px 0;
            border-bottom: 1px solid rgba(255,255,255,0.2);
        }
        
        .resumen-item:last-child {
            border-bottom: none;
            font-size: 1.2em;
            font-weight: bold;
            margin-top: 10px;
            padding-top: 15px;
            border-top: 2px solid rgba(255,255,255,0.3);
        }
        
        .saldo-pendiente {
            background: #ff9800;
            color: white;
            padding: 15px;
            border-radius: 8px;
            margin: 15px 0;
            text-align: center;
        }
        
        .saldo-cero {
            background: #4CAF50;
        }
        
        .pago-item {
            background: #e8f5e8;
            border: 1px solid #4CAF50;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 10px;
        }
        
        .pago-header {
            display: flex;
            justify-content: space-between;
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .pago-details {
            font-size: 0.9em;
            color: #666;
        }
        
        .comprobante-item {
            background: #e3f2fd;
            border: 1px solid #2196f3;
            padding: 12px;
            border-radius: 6px;
            margin-bottom: 10px;
        }
        
        .form-pago {
            background: #f8f9fa;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            padding: 20px;
            margin-top: 20px;
        }
        
        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 15px;
            margin-bottom: 15px;
        }
        
        .form-group-full {
            grid-column: span 2;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
            color: #333;
        }
        
        .form-group input, .form-group select, .form-group textarea {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 0.9em;
        }
        
        .checkout-actions {
            background: white;
            border-radius: 10px;
            padding: 25px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            text-align: center;
        }
        
        .btn {
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            font-weight: 500;
            transition: all 0.3s ease;
            cursor: pointer;
            font-size: 1em;
            margin: 0 5px;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #f44336, #d32f2f);
            color: white;
        }
        
        .btn-primary:hover {
            background: linear-gradient(135deg, #d32f2f, #b71c1c);
            transform: translateY(-2px);
        }
        
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        
        .btn-success {
            background: linear-gradient(135deg, #4CAF50, #45a049);
            color: white;
        }
        
        .btn-warning {
            background: #ff9800;
            color: white;
        }
        
        .btn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
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
        
        .alert-warning {
            background: #fff3cd;
            border: 1px solid #ffeaa7;
            color: #856404;
        }
        
        .no-items {
            text-align: center;
            padding: 30px;
            color: #666;
        }
        
        .no-items i {
            font-size: 2em;
            margin-bottom: 10px;
            opacity: 0.5;
        }
        
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }
        
        .modal-content {
            background-color: white;
            margin: 5% auto;
            padding: 30px;
            border-radius: 10px;
            width: 90%;
            max-width: 600px;
            max-height: 80vh;
            overflow-y: auto;
        }
        
        .modal-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
            padding-bottom: 15px;
            border-bottom: 2px solid #e0e0e0;
        }
        
        .close {
            font-size: 28px;
            font-weight: bold;
            cursor: pointer;
        }
        
        @media (max-width: 768px) {
            .checkout-grid {
                grid-template-columns: 1fr;
            }
            
            .header-content {
                flex-direction: column;
                text-align: center;
            }
            
            .form-row {
                grid-template-columns: 1fr;
            }
            
            .btn {
                width: 100%;
                margin: 5px 0;
                justify-content: center;
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
        </nav>
    </aside>

    <main class="main-content">
        <div class="dashboard-header">
            <h1><i class="fas fa-door-closed"></i> Check-Out de Huésped</h1>
            <p class="dashboard-subtitle">Resumen final y facturación</p>
        </div>

        <div class="checkout-container">
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

            <!-- Header de Checkout -->
            <div class="checkout-header">
                <div class="header-content">
                    <div class="header-info">
                        <h2><i class="fas fa-calendar-check"></i> Reserva <%= reserva.getNumeroReserva() %></h2>
                        <p><strong>Huésped:</strong> <%= reserva.getNombreCliente() %></p>
                        <p><strong>Habitaciones:</strong> <%= habitaciones != null ? habitaciones : "No asignadas" %></p>
                        <p><strong>Estadía:</strong> <%= reserva.getFechaEntrada().format(dateFormatter) %> - <%= reserva.getFechaSalida().format(dateFormatter) %></p>
                    </div>
                    <div class="status-badge">
                        <i class="fas fa-door-open"></i> <%= reserva.getEstado() %>
                    </div>
                </div>
            </div>

            <!-- Grid Principal -->
            <div class="checkout-grid">
                <!-- Área Principal -->
                <div class="main-content-area">
                    
                    <!-- Servicios Consumidos -->
                    <div class="section-card">
                        <div class="section-header">
                            <h3><i class="fas fa-concierge-bell"></i> Servicios Consumidos</h3>
                            <div class="section-total">S/. <%= String.format("%.2f", totalServicios) %></div>
                        </div>
                        
                        <% if (serviciosConsumidos != null && !serviciosConsumidos.isEmpty()) { %>
                            <% for (ConsumoServicioDTO consumo : serviciosConsumidos) { %>
                            <div class="consumo-item">
                                <div class="consumo-details">
                                    <div class="consumo-info">
                                        <h4><%= consumo.getNombreServicio() %></h4>
                                        <div class="consumo-meta">
                                            <div><i class="fas fa-calendar"></i> <%= consumo.getFechaConsumo().format(dateFormatter) %> <%= consumo.getHoraConsumo().format(timeFormatter) %></div>
                                            <div><i class="fas fa-sort-numeric-up"></i> Cantidad: <%= consumo.getCantidad() %></div>
                                            <% if (consumo.getObservaciones() != null && !consumo.getObservaciones().trim().isEmpty()) { %>
                                            <div><i class="fas fa-comment"></i> <%= consumo.getObservaciones() %></div>
                                            <% } %>
                                        </div>
                                    </div>
                                    <div class="consumo-price">
                                        <div class="precio-total">S/. <%= String.format("%.2f", consumo.getSubtotal()) %></div>
                                    </div>
                                </div>
                            </div>
                            <% } %>
                        <% } else { %>
                            <div class="no-items">
                                <i class="fas fa-concierge-bell"></i>
                                <p>No hay servicios consumidos</p>
                            </div>
                        <% } %>
                    </div>

                    <!-- Productos Consumidos -->
                    <div class="section-card">
                        <div class="section-header">
                            <h3><i class="fas fa-shopping-cart"></i> Productos del Minibar</h3>
                            <div class="section-total" style="background: linear-gradient(135deg, #3b82f6, #2563eb);">S/. <%= String.format("%.2f", totalProductos) %></div>
                        </div>
                        
                        <% if (productosConsumidos != null && !productosConsumidos.isEmpty()) { %>
                            <% for (ConsumoProductoDTO consumo : productosConsumidos) { %>
                            <div class="consumo-item">
                                <div class="consumo-details">
                                    <div class="consumo-info">
                                        <h4><%= consumo.getNombreProducto() %></h4>
                                        <div class="consumo-meta">
                                            <div><i class="fas fa-calendar"></i> <%= consumo.getFechaConsumo().format(dateFormatter) %> <%= consumo.getHoraConsumo().format(timeFormatter) %></div>
                                            <div><i class="fas fa-sort-numeric-up"></i> Cantidad: <%= consumo.getCantidad() %></div>
                                            <% if (consumo.getObservaciones() != null && !consumo.getObservaciones().trim().isEmpty()) { %>
                                            <div><i class="fas fa-comment"></i> <%= consumo.getObservaciones() %></div>
                                            <% } %>
                                        </div>
                                    </div>
                                    <div class="consumo-price">
                                        <div class="precio-total">S/. <%= String.format("%.2f", consumo.getPrecioUnitario().multiply(new BigDecimal(consumo.getCantidad()))) %></div>
                                    </div>
                                </div>
                            </div>
                            <% } %>
                        <% } else { %>
                            <div class="no-items">
                                <i class="fas fa-shopping-cart"></i>
                                <p>No hay productos consumidos</p>
                            </div>
                        <% } %>
                    </div>

                    <!-- Pagos Realizados -->
                    <div class="section-card">
                        <div class="section-header">
                            <h3><i class="fas fa-credit-card"></i> Pagos Realizados</h3>
                            <div class="section-total" style="background: linear-gradient(135deg, #10b981, #059669);">S/. <%= String.format("%.2f", totalPagado) %></div>
                        </div>
                        
                        <% if (pagos != null && !pagos.isEmpty()) { %>
                            <% for (PagoDTO pago : pagos) { %>
                            <div class="pago-item">
                                <div class="pago-header">
                                    <span>S/. <%= String.format("%.2f", pago.getMonto()) %></span>
                                    <span><%= pago.getMetodoPago() %></span>
                                </div>
                                <div class="pago-details">
                                    <div><i class="fas fa-calendar"></i> <%= pago.getFechaPago().format(dateTimeFormatter) %></div>
                                    <% if (pago.getNumeroOperacion() != null && !pago.getNumeroOperacion().trim().isEmpty()) { %>
                                    <div><i class="fas fa-hashtag"></i> <%= pago.getNumeroOperacion() %></div>
                                    <% } %>
                                    <div><i class="fas fa-user"></i> <%= pago.getNombreEmpleado() %></div>
                                </div>
                            </div>
                            <% } %>
                            
                            <!-- Botón para agregar más pagos -->
                            <% if (tieneSaldoPendiente) { %>
                            <button type="button" class="btn btn-success" onclick="mostrarFormularioPago()">
                                <i class="fas fa-plus"></i> Agregar Pago
                            </button>
                            <% } %>
                        <% } else { %>
                            <div class="no-items">
                                <i class="fas fa-credit-card"></i>
                                <p>No hay pagos registrados</p>
                                <button type="button" class="btn btn-success" onclick="mostrarFormularioPago()">
                                    <i class="fas fa-plus"></i> Registrar Primer Pago
                                </button>
                            </div>
                        <% } %>
                    </div>

                    <!-- Comprobantes -->
                    <% if (comprobantes != null && !comprobantes.isEmpty()) { %>
                    <div class="section-card">
                        <div class="section-header">
                            <h3><i class="fas fa-file-invoice"></i> Comprobantes Emitidos</h3>
                        </div>
                        
                        <% for (ComprobanteDTO comprobante : comprobantes) { %>
                        <div class="comprobante-item">
                            <div style="display: flex; justify-content: space-between; align-items: center;">
                                <div>
                                    <strong><%= comprobante.getTipoComprobante() %> <%= comprobante.getNumeroCompleto() %></strong><br>
                                    <small><i class="fas fa-calendar"></i> <%= comprobante.getFechaEmision().format(dateTimeFormatter) %></small><br>
                                    <small><i class="fas fa-dollar-sign"></i> S/. <%= String.format("%.2f", comprobante.getTotal()) %></small>
                                </div>
                                <div>
                                    <span style="background: #4CAF50; color: white; padding: 4px 8px; border-radius: 4px; font-size: 0.8em;">
                                        <%= comprobante.getEstado() %>
                                    </span>
                                </div>
                            </div>
                        </div>
                        <% } %>
                    </div>
                    <% } %>
                </div>

                <!-- Sidebar -->
                <div class="sidebar-area">
                    <!-- Resumen Financiero -->
                    <div class="section-card resumen-financiero">
                        <div class="section-header">
                            <h3><i class="fas fa-calculator"></i> Resumen Financiero</h3>
                        </div>
                        
                        <div class="resumen-item">
                            <span>Hospedaje:</span>
                            <span>S/. <%= String.format("%.2f", reserva.getMontoTotal()) %></span>
                        </div>
                        
                        <div class="resumen-item">
                            <span>Servicios:</span>
                            <span>S/. <%= String.format("%.2f", totalServicios) %></span>
                        </div>
                        
                        <div class="resumen-item">
                            <span>Productos:</span>
                            <span>S/. <%= String.format("%.2f", totalProductos) %></span>
                        </div>
                        
                        <div class="resumen-item">
                            <span>Subtotal:</span>
                            <span>S/. <%= String.format("%.2f", subtotalGeneral) %></span>
                        </div>
                        
                        <div class="resumen-item">
                            <span>IGV (18%):</span>
                            <span>S/. <%= String.format("%.2f", impuestosGenerales) %></span>
                        </div>
                        
                        <div class="resumen-item">
                            <span>TOTAL:</span>
                            <span>S/. <%= String.format("%.2f", totalGeneral) %></span>
                        </div>
                    </div>

                    <!-- Estado de Pago -->
                    <div class="section-card">
                        <div class="section-header">
                            <h3><i class="fas fa-money-check"></i> Estado de Pago</h3>
                        </div>
                        
                        <div style="margin-bottom: 15px;">
                            <strong>Total a Pagar:</strong> S/. <%= String.format("%.2f", totalGeneral) %><br>
                            <strong>Total Pagado:</strong> S/. <%= String.format("%.2f", totalPagado) %>
                        </div>
                        
                        <div class="saldo-pendiente <%= tieneSaldoPendiente ? "" : "saldo-cero" %>">
                            <% if (tieneSaldoPendiente) { %>
                                <i class="fas fa-exclamation-triangle"></i>
                                <strong>Saldo Pendiente</strong><br>
                                S/. <%= String.format("%.2f", saldoPendiente) %>
                            <% } else { %>
                                <i class="fas fa-check-circle"></i>
                                <strong>Totalmente Pagado</strong><br>
                                Listo para Check-Out
                            <% } %>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Acciones de Check-Out -->
            <div class="checkout-actions">
                <h3><i class="fas fa-clipboard-check"></i> Finalizar Check-Out</h3>
                
                <% if (tieneSaldoPendiente) { %>
                <div class="alert alert-warning">
                    <i class="fas fa-exclamation-triangle"></i>
                    <span>Hay un saldo pendiente de S/. <%= String.format("%.2f", saldoPendiente) %>. 
                    <% if (puedeHacerCheckout) { %>
                        Como <%= rolUsuario %>, puede proceder con el check-out.
                    <% } else { %>
                        Debe completar el pago antes del check-out.
                    <% } %>
                    </span>
                </div>
                <% } %>
                
                <div style="margin: 20px 0;">
                    <a href="reserva_servicios?id=<%= reserva.getIdReserva() %>" class="btn btn-secondary">
                        <i class="fas fa-concierge-bell"></i> Ver Servicios
                    </a>
                    
                    <% if (tieneSaldoPendiente) { %>
                    <button type="button" class="btn btn-success" onclick="mostrarFormularioPago()">
                        <i class="fas fa-credit-card"></i> Registrar Pago
                    </button>
                    <% } %>
                    
                    <% if (puedeHacerCheckout) { %>
                    <button type="button" class="btn btn-primary" onclick="mostrarFormularioCheckout()">
                        <i class="fas fa-door-closed"></i> Realizar Check-Out
                    </button>
                    <% } else { %>
                    <button type="button" class="btn btn-primary" disabled title="Complete el pago para proceder">
                        <i class="fas fa-door-closed"></i> Realizar Check-Out
                    </button>
                    <% } %>
                    
                    <a href="reservas" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Volver a Reservas
                    </a>
                </div>
            </div>
        </div>
    </main>
</div>

<!-- Modal para Agregar Pago -->
<div id="modalPago" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3><i class="fas fa-credit-card"></i> Registrar Pago</h3>
            <span class="close" onclick="cerrarModal('modalPago')">&times;</span>
        </div>
        
        <form method="post" action="reserva_checkout" id="formPago">
            <input type="hidden" name="action" value="procesar_pago">
            <input type="hidden" name="id_reserva" value="<%= reserva.getIdReserva() %>">
            
            <div class="form-row">
                <div class="form-group">
                    <label for="monto">Monto a Pagar *</label>
                    <input type="number" name="monto" id="monto" step="0.01" min="0.01" 
                           max="<%= saldoPendiente %>" value="<%= String.format("%.2f", saldoPendiente) %>" required>
                    <small>Saldo pendiente: S/. <%= String.format("%.2f", saldoPendiente) %></small>
                </div>
                
                <div class="form-group">
                    <label for="metodo_pago">Método de Pago *</label>
                    <select name="metodo_pago" id="metodo_pago" required>
                        <option value="">Seleccione...</option>
                        <option value="Efectivo">Efectivo</option>
                        <option value="Tarjeta de Crédito">Tarjeta de Crédito</option>
                        <option value="Tarjeta de Débito">Tarjeta de Débito</option>
                        <option value="Transferencia">Transferencia Bancaria</option>
                        <option value="Yape">Yape</option>
                        <option value="Plin">Plin</option>
                    </select>
                </div>
            </div>
            
            <div class="form-row">
                <div class="form-group form-group-full">
                    <label for="numero_operacion">Número de Operación</label>
                    <input type="text" name="numero_operacion" id="numero_operacion" 
                           placeholder="Opcional - Para transferencias, tarjetas, etc.">
                </div>
            </div>
            
            <div class="form-row">
                <div class="form-group form-group-full">
                    <label for="observaciones_pago">Observaciones</label>
                    <textarea name="observaciones" id="observaciones_pago" rows="3" 
                              placeholder="Observaciones adicionales sobre el pago (opcional)"></textarea>
                </div>
            </div>
            
            <div style="text-align: center; margin-top: 20px;">
                <button type="button" class="btn btn-secondary" onclick="cerrarModal('modalPago')">
                    <i class="fas fa-times"></i> Cancelar
                </button>
                <button type="submit" class="btn btn-success">
                    <i class="fas fa-check"></i> Registrar Pago
                </button>
            </div>
        </form>
    </div>
</div>

<!-- Modal para Check-Out -->
<div id="modalCheckout" class="modal">
    <div class="modal-content">
        <div class="modal-header">
            <h3><i class="fas fa-door-closed"></i> Confirmar Check-Out</h3>
            <span class="close" onclick="cerrarModal('modalCheckout')">&times;</span>
        </div>
        
        <div style="margin-bottom: 20px;">
            <div class="alert alert-warning">
                <i class="fas fa-exclamation-triangle"></i>
                <span><strong>¡Atención!</strong> Esta acción finalizará la reserva y no se puede deshacer.</span>
            </div>
        </div>
        
        <form method="post" action="reserva_checkout" id="formCheckout">
            <input type="hidden" name="action" value="finalizar_checkout">
            <input type="hidden" name="id_reserva" value="<%= reserva.getIdReserva() %>">
            
            <!-- Comprobante -->
            <div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin-bottom: 20px;">
                <h4 style="margin: 0 0 15px 0;"><i class="fas fa-file-invoice"></i> Comprobante de Pago</h4>
                
                <div class="form-row">
                    <div class="form-group">
                        <label>
                            <input type="checkbox" name="generar_comprobante" value="true" id="checkComprobante" onchange="toggleComprobante()">
                            Generar comprobante de pago
                        </label>
                    </div>
                </div>
                
                <div id="datosComprobante" style="display: none;">
                    <div class="form-row">
                        <div class="form-group">
                            <label for="tipo_comprobante">Tipo de Comprobante *</label>
                            <select name="tipo_comprobante" id="tipo_comprobante" onchange="toggleFacturaFields()">
                                <option value="Boleta">Boleta de Venta</option>
                                <option value="Factura">Factura</option>
                            </select>
                        </div>
                    </div>
                    
                    <div id="datosFactura" style="display: none;">
                        <div class="form-row">
                            <div class="form-group">
                                <label for="ruc_cliente">RUC *</label>
                                <input type="text" name="ruc_cliente" id="ruc_cliente" 
                                       pattern="[0-9]{11}" maxlength="11" 
                                       placeholder="20123456789">
                            </div>
                            
                            <div class="form-group">
                                <label for="razon_social">Razón Social *</label>
                                <input type="text" name="razon_social" id="razon_social" 
                                       placeholder="Empresa S.A.C.">
                            </div>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group form-group-full">
                                <label for="direccion_cliente">Dirección</label>
                                <input type="text" name="direccion_cliente" id="direccion_cliente" 
                                       placeholder="Dirección fiscal de la empresa">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Observaciones del Check-Out -->
            <div class="form-row">
                <div class="form-group form-group-full">
                    <label for="observaciones_checkout"><i class="fas fa-comment"></i> Observaciones del Check-Out</label>
                    <textarea name="observaciones" id="observaciones_checkout" rows="4" 
                              placeholder="Registre cualquier observación sobre el check-out, estado de las habitaciones, objetos olvidados, etc. (opcional)"></textarea>
                </div>
            </div>
            
            <!-- Resumen Final -->
            <div style="background: linear-gradient(135deg, #2196f3, #1976d2); color: white; padding: 15px; border-radius: 8px; margin: 20px 0;">
                <h4 style="margin: 0 0 10px 0;"><i class="fas fa-clipboard-list"></i> Resumen Final</h4>
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px; font-size: 0.9em;">
                    <div><strong>Huésped:</strong> <%= reserva.getNombreCliente() %></div>
                    <div><strong>Habitaciones:</strong> <%= habitaciones %></div>
                    <div><strong>Total General:</strong> S/. <%= String.format("%.2f", totalGeneral) %></div>
                    <div><strong>Total Pagado:</strong> S/. <%= String.format("%.2f", totalPagado) %></div>
                    <div><strong>Saldo:</strong> 
                        <span style="<%= tieneSaldoPendiente ? "color: #ff9800;" : "color: #4CAF50;" %>">
                            S/. <%= String.format("%.2f", saldoPendiente) %>
                        </span>
                    </div>
                </div>
            </div>
            
            <div style="text-align: center; margin-top: 25px;">
                <button type="button" class="btn btn-secondary" onclick="cerrarModal('modalCheckout')">
                    <i class="fas fa-times"></i> Cancelar
                </button>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-check"></i> Confirmar Check-Out
                </button>
            </div>
        </form>
    </div>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
</footer>

<script>
// Función para mostrar modal de pago
function mostrarFormularioPago() {
    document.getElementById('modalPago').style.display = 'block';
    document.getElementById('monto').focus();
}

// Función para mostrar modal de checkout
function mostrarFormularioCheckout() {
    document.getElementById('modalCheckout').style.display = 'block';
}

// Función para cerrar modales
function cerrarModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

// Cerrar modal al hacer clic fuera
window.onclick = function(event) {
    const modales = document.querySelectorAll('.modal');
    modales.forEach(modal => {
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    });
}

// Toggle de campos de comprobante
function toggleComprobante() {
    const checkbox = document.getElementById('checkComprobante');
    const datosComprobante = document.getElementById('datosComprobante');
    
    if (checkbox.checked) {
        datosComprobante.style.display = 'block';
    } else {
        datosComprobante.style.display = 'none';
        document.getElementById('datosFactura').style.display = 'none';
    }
}

// Toggle de campos de factura
function toggleFacturaFields() {
    const tipoComprobante = document.getElementById('tipo_comprobante').value;
    const datosFactura = document.getElementById('datosFactura');
    const rucInput = document.getElementById('ruc_cliente');
    const razonSocialInput = document.getElementById('razon_social');
    
    if (tipoComprobante === 'Factura') {
        datosFactura.style.display = 'block';
        rucInput.required = true;
        razonSocialInput.required = true;
    } else {
        datosFactura.style.display = 'none';
        rucInput.required = false;
        razonSocialInput.required = false;
    }
}

// Validación del formulario de pago
document.getElementById('formPago').addEventListener('submit', function(e) {
    const monto = parseFloat(document.getElementById('monto').value);
    const saldoPendiente = <%= saldoPendiente %>;
    
    if (monto <= 0) {
        e.preventDefault();
        alert('El monto debe ser mayor a cero');
        return;
    }
    
    if (monto > saldoPendiente) {
        e.preventDefault();
        alert('El monto no puede ser mayor al saldo pendiente (S/. ' + saldoPendiente.toFixed(2) + ')');
        return;
    }
    
    const metodoPago = document.getElementById('metodo_pago').value;
    if (!metodoPago) {
        e.preventDefault();
        alert('Debe seleccionar un método de pago');
        return;
    }
    
    // Confirmación
    const confirmacion = 'Registrar pago de S/. ' + monto.toFixed(2) + ' con ' + metodoPago + '?';
    if (!confirm(confirmacion)) {
        e.preventDefault();
        return;
    }
});

// Validación del formulario de checkout
document.getElementById('formCheckout').addEventListener('submit', function(e) {
    const generarComprobante = document.getElementById('checkComprobante').checked;
    
    if (generarComprobante) {
        const tipoComprobante = document.getElementById('tipo_comprobante').value;
        
        if (tipoComprobante === 'Factura') {
            const ruc = document.getElementById('ruc_cliente').value.trim();
            const razonSocial = document.getElementById('razon_social').value.trim();
            
            if (!ruc || ruc.length !== 11 || !/^[0-9]+$/.test(ruc)) {
                e.preventDefault();
                alert('Para factura debe ingresar un RUC válido de 11 dígitos');
                document.getElementById('ruc_cliente').focus();
                return;
            }
            
            if (!razonSocial) {
                e.preventDefault();
                alert('Para factura debe ingresar la razón social');
                document.getElementById('razon_social').focus();
                return;
            }
        }
    }
    
    // Confirmación final
    const huespedNombre = '<%= reserva.getNombreCliente() %>';
    const habitaciones = '<%= habitaciones %>';
    const total = '<%= String.format("%.2f", totalGeneral) %>';
    const saldo = '<%= String.format("%.2f", saldoPendiente) %>';
    
    let mensaje = 'CONFIRMAR CHECK-OUT\n\n';
    mensaje += 'Huésped: ' + huespedNombre + '\n';
    mensaje += 'Habitaciones: ' + habitaciones + '\n';
    mensaje += 'Total: S/. ' + total + '\n';
    mensaje += 'Saldo pendiente: S/. ' + saldo + '\n\n';
    
    if (generarComprobante) {
        const tipoComp = document.getElementById('tipo_comprobante').value;
        mensaje += 'Se generará: ' + tipoComp + '\n\n';
    }
    
    mensaje += 'Esta acción finalizará la reserva y no se puede deshacer.\n';
    mensaje += '¿Proceder con el check-out?';
    
    if (!confirm(mensaje)) {
        e.preventDefault();
        return;
    }
});

// Auto-calcular monto completo
document.getElementById('metodo_pago').addEventListener('change', function() {
    if (this.value === 'Efectivo') {
        // Para efectivo, sugerir el monto exacto
        document.getElementById('monto').value = '<%= String.format("%.2f", saldoPendiente) %>';
    }
});

// Formateo de RUC
document.getElementById('ruc_cliente').addEventListener('input', function() {
    this.value = this.value.replace(/[^0-9]/g, '').substring(0, 11);
});
</script>

</body>
</html>