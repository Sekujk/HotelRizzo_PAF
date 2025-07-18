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
    ClienteDTO cliente = (ClienteDTO) request.getAttribute("cliente");
    String nombreEmpleado = (String) request.getAttribute("nombreEmpleado");
    List<HabitacionDetalleDTO> habitaciones = (List<HabitacionDetalleDTO>) request.getAttribute("habitaciones");
    List<ConsumoServicioDTO> serviciosConsumidos = (List<ConsumoServicioDTO>) request.getAttribute("serviciosConsumidos");
    List<ConsumoProductoDTO> productosConsumidos = (List<ConsumoProductoDTO>) request.getAttribute("productosConsumidos");
    List<PagoDTO> pagos = (List<PagoDTO>) request.getAttribute("pagos");
    List<ComprobanteDTO> comprobantes = (List<ComprobanteDTO>) request.getAttribute("comprobantes");

    // Totales
    BigDecimal totalServicios = (BigDecimal) request.getAttribute("totalServicios");
    BigDecimal totalProductos = (BigDecimal) request.getAttribute("totalProductos");
    BigDecimal totalGeneral = (BigDecimal) request.getAttribute("totalGeneral");
    BigDecimal totalPagado = (BigDecimal) request.getAttribute("totalPagado");
    BigDecimal saldoPendiente = (BigDecimal) request.getAttribute("saldoPendiente");

    String error = (String) request.getAttribute("error");
    String mensaje = (String) session.getAttribute("mensaje");
    if (mensaje != null) {
        session.removeAttribute("mensaje");
    }

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    if (reserva == null) {
        response.sendRedirect("reservas");
        return;
    }

    // Valores por defecto si son null
    if (totalServicios == null) {
        totalServicios = BigDecimal.ZERO;
    }
    if (totalProductos == null) {
        totalProductos = BigDecimal.ZERO;
    }
    if (totalGeneral == null) {
        totalGeneral = BigDecimal.ZERO;
    }
    if (totalPagado == null) {
        totalPagado = BigDecimal.ZERO;
    }
    if (saldoPendiente == null) {
        saldoPendiente = BigDecimal.ZERO;
    }

    boolean puedeEditar = ("Gerente".equals(rolUsuario) || "Administrador".equals(rolUsuario))
            && ("Confirmada".equals(reserva.getEstado()) || "Pendiente".equals(reserva.getEstado()));
    boolean puedeCheckIn = "Confirmada".equals(reserva.getEstado());
    boolean puedeCheckOut = "CheckIn".equals(reserva.getEstado());
    boolean puedeAgregarConsumos = "CheckIn".equals(reserva.getEstado());
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Detalle Reserva <%= reserva.getNumeroReserva()%> - Hotel Rizzo</title>
        <link rel="stylesheet" href="css/utils/base.css">
        <link rel="stylesheet" href="css/reservas/reserva_crear.css">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <style>
            .detalle-container {
                max-width: 1000px;
                margin: 0 auto;
                padding: 20px;
            }

            .reserva-header {
                background: linear-gradient(135deg, #2196f3, #1976d2);
                color: white;
                border-radius: 10px;
                padding: 20px;
                margin-bottom: 20px;
            }

            .info-card {
                background: white;
                border-radius: 8px;
                padding: 20px;
                margin-bottom: 20px;
                box-shadow: 0 2px 5px rgba(0,0,0,0.1);
                border: 1px solid #e0e0e0;
            }

            .card-header {
                border-bottom: 2px solid #e0e0e0;
                padding-bottom: 10px;
                margin-bottom: 15px;
            }

            .card-header h3 {
                margin: 0;
                color: #333;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            .info-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                gap: 15px;
            }

            .info-item {
                padding: 10px;
                background: #f8f9fa;
                border-radius: 5px;
                border-left: 4px solid #2196f3;
            }

            .info-label {
                font-size: 0.85em;
                color: #666;
                margin-bottom: 4px;
                font-weight: 600;
            }

            .info-value {
                font-size: 1em;
                color: #333;
                font-weight: 500;
            }

            .habitacion-item {
                background: #f0f8ff;
                border: 1px solid #2196f3;
                border-radius: 5px;
                padding: 15px;
                margin-bottom: 10px;
            }

            .habitacion-numero {
                font-size: 1.1em;
                font-weight: bold;
                color: #1976d2;
            }

            .habitacion-precio {
                font-weight: bold;
                color: #2e7d32;
            }

            .consumo-item {
                background: #f8f9fa;
                border: 1px solid #e0e0e0;
                border-radius: 5px;
                padding: 12px;
                margin-bottom: 8px;
            }

            .pago-item {
                background: #e8f5e8;
                border: 1px solid #4CAF50;
                border-radius: 5px;
                padding: 12px;
                margin-bottom: 8px;
            }

            .btn {
                padding: 10px 15px;
                border: none;
                border-radius: 5px;
                text-decoration: none;
                display: inline-flex;
                align-items: center;
                gap: 8px;
                font-weight: 500;
                cursor: pointer;
                margin: 5px;
            }

            .btn-primary {
                background: #2196f3;
                color: white;
            }
            .btn-success {
                background: #4CAF50;
                color: white;
            }
            .btn-warning {
                background: #ff9800;
                color: white;
            }
            .btn-danger {
                background: #f44336;
                color: white;
            }
            .btn-secondary {
                background: #6c757d;
                color: white;
            }

            .btn:hover {
                opacity: 0.9;
                transform: translateY(-1px);
            }

            .actions-header {
                display: flex;
                gap: 10px;
                justify-content: center;
                margin-bottom: 20px;
                flex-wrap: wrap;
            }

            .alert {
                padding: 12px;
                margin-bottom: 15px;
                border-radius: 5px;
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

            .estado-badge {
                background: rgba(255, 255, 255, 0.2);
                padding: 8px 15px;
                border-radius: 15px;
                font-weight: bold;
                display: inline-flex;
                align-items: center;
                gap: 8px;
            }

            .resumen-financiero {
                background: linear-gradient(135deg, #4CAF50, #45a049);
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
                font-size: 1.1em;
                font-weight: bold;
                margin-top: 8px;
                padding-top: 12px;
                border-top: 2px solid rgba(255,255,255,0.3);
            }

            .no-data {
                text-align: center;
                padding: 20px;
                color: #666;
            }

            @media (max-width: 768px) {
                .info-grid {
                    grid-template-columns: 1fr;
                }

                .actions-header {
                    flex-direction: column;
                    align-items: center;
                }

                .btn {
                    width: 100%;
                    max-width: 300px;
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
                        <span class="user-name"><%= usuario%></span>
                        <span class="user-role"><%= rolUsuario%></span>
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
                    <h1><i class="fas fa-file-alt"></i> Detalle de Reserva</h1>
                    <p class="dashboard-subtitle">Información completa de la reserva</p>
                </div>

                <div class="detalle-container">
                    <!-- Mensajes -->
                    <% if (mensaje != null) {%>
                    <div class="alert alert-success">
                        <i class="fas fa-check-circle"></i>
                        <span><%= mensaje%></span>
                    </div>
                    <% } %>

                    <% if (error != null) {%>
                    <div class="alert alert-error">
                        <i class="fas fa-exclamation-circle"></i>
                        <span><%= error%></span>
                    </div>
                    <% }%>

                    <!-- Header de la Reserva -->
                    <div class="reserva-header">
                        <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 15px;">
                            <div>
                                <h2><i class="fas fa-calendar-check"></i> Reserva <%= reserva.getNumeroReserva()%></h2>
                                <p><strong>Cliente:</strong> <%= cliente != null ? cliente.getNombre() + " " + cliente.getApellido() : "Cliente no encontrado"%></p>
                                <p><strong>Empleado:</strong> <%= nombreEmpleado%></p>
                                <% if (reserva.getCreatedAt() != null) {%>
                                <p><strong>Creada:</strong> <%= reserva.getCreatedAt().format(dateTimeFormatter)%></p>
                                <% } %>
                            </div>
                            <div class="estado-badge">
                                <%
                                    String estadoIcon = "";
                                    switch (reserva.getEstado()) {
                                        case "Pendiente":
                                            estadoIcon = "fas fa-clock";
                                            break;
                                        case "Confirmada":
                                            estadoIcon = "fas fa-check";
                                            break;
                                        case "CheckIn":
                                            estadoIcon = "fas fa-door-open";
                                            break;
                                        case "CheckOut":
                                            estadoIcon = "fas fa-door-closed";
                                            break;
                                        case "Cancelada":
                                            estadoIcon = "fas fa-times";
                                            break;
                                        default:
                                            estadoIcon = "fas fa-info";
                                    }
                                %>
                                <i class="<%= estadoIcon%>"></i>
                                <%= reserva.getEstado()%>
                            </div>
                        </div>
                    </div>

                    <!-- Botones de Acción -->
                    <div class="actions-header">
                        <a href="reservas" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Volver
                        </a>

                        <% if (puedeEditar) {%>
                        <a href="reserva_editar?id=<%= reserva.getIdReserva()%>" class="btn btn-warning">
                            <i class="fas fa-edit"></i> Editar
                        </a>
                        <% } %>

                        <% if (puedeCheckIn) {%>
                        <a href="reserva_checkin?id=<%= reserva.getIdReserva()%>" class="btn btn-success">
                            <i class="fas fa-door-open"></i> Check-In
                        </a>
                        <% } %>

                        <% if (puedeCheckOut) {%>
                        <a href="reserva_checkout?id=<%= reserva.getIdReserva()%>" class="btn btn-danger">
                            <i class="fas fa-door-closed"></i> Check-Out
                        </a>
                        <% }%>

                        <a href="reserva_detalle?id=<%= reserva.getIdReserva()%>&pdf=true" class="btn btn-warning" target="_blank">
                            <i class="fas fa-file-pdf"></i> PDF
                        </a>
                    </div>

                    <!-- Información General -->
                    <div class="info-card">
                        <div class="card-header">
                            <h3><i class="fas fa-info-circle"></i> Información General</h3>
                        </div>

                        <div class="info-grid">
                            <div class="info-item">
                                <div class="info-label">ID Reserva</div>
                                <div class="info-value"><%= reserva.getIdReserva()%></div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Fecha de Entrada</div>
                                <div class="info-value"><%= reserva.getFechaEntrada().format(dateFormatter)%></div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Fecha de Salida</div>
                                <div class="info-value"><%= reserva.getFechaSalida().format(dateFormatter)%></div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Huéspedes</div>
                                <div class="info-value"><%= reserva.getNumHuespedes()%> personas</div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Noches</div>
                                <div class="info-value"><%= reserva.getTotalNoches()%> noches</div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Estado</div>
                                <div class="info-value"><%= reserva.getEstado()%></div>
                            </div>
                        </div>

                        <% if (reserva.getObservaciones() != null && !reserva.getObservaciones().trim().isEmpty()) {%>
                        <div style="margin-top: 15px; padding: 12px; background: #fff3cd; border-radius: 5px;">
                            <strong><i class="fas fa-comment"></i> Observaciones:</strong><br>
                            <%= reserva.getObservaciones()%>
                        </div>
                        <% } %>
                    </div>

                    <!-- Información del Cliente -->
                    <% if (cliente != null) {%>
                    <div class="info-card">
                        <div class="card-header">
                            <h3><i class="fas fa-user"></i> Cliente</h3>
                        </div>

                        <div class="info-grid">
                            <div class="info-item">
                                <div class="info-label">Nombre Completo</div>
                                <div class="info-value"><%= cliente.getNombre()%> <%= cliente.getApellido()%></div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">DNI</div>
                                <div class="info-value"><%= cliente.getDni()%></div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Teléfono</div>
                                <div class="info-value"><%= cliente.getTelefono() != null ? cliente.getTelefono() : "No registrado"%></div>
                            </div>
                            <div class="info-item">
                                <div class="info-label">Correo</div>
                                <div class="info-value"><%= cliente.getCorreo() != null ? cliente.getCorreo() : "No registrado"%></div>
                            </div>
                        </div>
                    </div>
                    <% } %>

                    <!-- Habitaciones -->
                    <div class="info-card">
                        <div class="card-header">
                            <h3><i class="fas fa-bed"></i> Habitaciones Asignadas</h3>
                        </div>

                        <% if (habitaciones != null && !habitaciones.isEmpty()) { %>
                        <% for (HabitacionDetalleDTO hab : habitaciones) {%>
                        <div class="habitacion-item">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px;">
                                <div class="habitacion-numero">Habitación <%= hab.getNumeroHabitacion()%></div>
                                <div class="habitacion-precio">S/. <%= String.format("%.2f", hab.getPrecioNoche())%>/noche</div>
                            </div>
                            <div style="font-size: 0.9em; color: #666;">
                                <strong>Tipo:</strong> <%= hab.getTipoHabitacion()%> |
                                <strong>Capacidad:</strong> <%= hab.getCapacidadPersonas()%> personas
                            </div>
                        </div>
                        <% } %>
                        <% } else { %>
                        <div class="no-data">
                            <i class="fas fa-bed"></i>
                            <p>No hay habitaciones asignadas</p>
                        </div>
                        <% } %>
                    </div>

                    <!-- Servicios Consumidos -->
                    <% if (serviciosConsumidos != null && !serviciosConsumidos.isEmpty()) {%>
                    <div class="info-card">
                        <div class="card-header">
                            <h3><i class="fas fa-concierge-bell"></i> Servicios Consumidos - S/. <%= String.format("%.2f", totalServicios)%></h3>
                        </div>

                        <% for (ConsumoServicioDTO servicio : serviciosConsumidos) {%>
                        <div class="consumo-item">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 5px;">
                                <div><strong><%= servicio.getNombreServicio()%></strong></div>
                                <div><strong>S/. <%= String.format("%.2f", servicio.getSubtotal())%></strong></div>
                            </div>
                            <div style="font-size: 0.85em; color: #666;">
                                Cantidad: <%= servicio.getCantidad()%> | 
                                Fecha: <%= servicio.getFechaConsumo().format(dateFormatter)%>
                            </div>
                        </div>
                        <% } %>
                    </div>
                    <% } %>

                    <!-- Productos Consumidos -->
                    <% if (productosConsumidos != null && !productosConsumidos.isEmpty()) {%>
                    <div class="info-card">
                        <div class="card-header">
                            <h3><i class="fas fa-shopping-cart"></i> Productos Consumidos - S/. <%= String.format("%.2f", totalProductos)%></h3>
                        </div>

                        <% for (ConsumoProductoDTO producto : productosConsumidos) {%>
                        <div class="consumo-item">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 5px;">
                                <div><strong><%= producto.getNombreProducto()%></strong></div>
                                <div><strong>S/. <%= String.format("%.2f", producto.getPrecioUnitario().multiply(new BigDecimal(producto.getCantidad())))%></strong></div>
                            </div>
                            <div style="font-size: 0.85em; color: #666;">
                                Cantidad: <%= producto.getCantidad()%> | 
                                Fecha: <%= producto.getFechaConsumo().format(dateFormatter)%>
                            </div>
                        </div>
                        <% } %>
                    </div>
                    <% } %>

                    <!-- Pagos -->
                    <% if (pagos != null && !pagos.isEmpty()) {%>
                    <div class="info-card">
                        <div class="card-header">
                            <h3><i class="fas fa-credit-card"></i> Pagos Realizados - S/. <%= String.format("%.2f", totalPagado)%></h3>
                        </div>

                        <% for (PagoDTO pago : pagos) {%>
                        <div class="pago-item">
                            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 5px;">
                                <div><strong>S/. <%= String.format("%.2f", pago.getMonto())%></strong></div>
                                <div><%= pago.getMetodoPago()%></div>
                            </div>
                            <div style="font-size: 0.85em; color: #666;">
                                <%= pago.getFechaPago().format(dateTimeFormatter)%>
                                <% if (pago.getNumeroOperacion() != null && !pago.getNumeroOperacion().trim().isEmpty()) {%>
                                | Op: <%= pago.getNumeroOperacion()%>
                                <% } %>
                            </div>
                        </div>
                        <% } %>
                    </div>
                    <% }%>

                    <!-- Resumen Financiero -->
                    <div class="info-card resumen-financiero">
                        <div class="card-header">
                            <h3 style="color: white;"><i class="fas fa-calculator"></i> Resumen Financiero</h3>
                        </div>

                        <div class="resumen-item">
                            <span>Subtotal Hospedaje:</span>
                            <span>S/. <%= String.format("%.2f", reserva.getSubtotal())%></span>
                        </div>

                        <% if (totalServicios.compareTo(BigDecimal.ZERO) > 0) {%>
                        <div class="resumen-item">
                            <span>Servicios:</span>
                            <span>S/. <%= String.format("%.2f", totalServicios)%></span>
                        </div>
                        <% } %>

                        <% if (totalProductos.compareTo(BigDecimal.ZERO) > 0) {%>
                        <div class="resumen-item">
                            <span>Productos:</span>
                            <span>S/. <%= String.format("%.2f", totalProductos)%></span>
                        </div>
                        <% }%>

                        <div class="resumen-item">
                            <span>IGV Hospedaje (18%):</span>
                            <span>S/. <%= String.format("%.2f", reserva.getImpuestos())%></span>
                        </div>
                        <div class="resumen-item">
                            <span>IGV Consumos (18%):</span>
                            <span>S/. <%= String.format("%.2f", ((java.math.BigDecimal) request.getAttribute("impuestosConsumos"))) %></span>
                        </div>

                        <div class="resumen-item">
                            <span>TOTAL:</span>
                            <span>S/. <%= String.format("%.2f", totalGeneral)%></span>
                        </div>

                        <div class="resumen-item">
                            <span>PAGADO:</span>
                            <span>S/. <%= String.format("%.2f", totalPagado)%></span>
                        </div>

                        <div class="resumen-item">
                            <span>SALDO:</span>
                            <span>S/. <%= String.format("%.2f", saldoPendiente)%></span>
                        </div>
                    </div>

                    <!-- Check-In/Check-Out Info -->
                    <% if (reserva.getFechaCheckin() != null || reserva.getFechaCheckout() != null) { %>
                    <div class="info-card">
                        <div class="card-header">
                            <h3><i class="fas fa-door-open"></i> Check-In / Check-Out</h3>
                        </div>

                        <div class="info-grid">
                            <% if (reserva.getFechaCheckin() != null) {%>
                            <div class="info-item">
                                <div class="info-label">Check-In Realizado</div>
                                <div class="info-value"><%= reserva.getFechaCheckin().format(dateTimeFormatter)%></div>
                            </div>
                            <% } %>

                            <% if (reserva.getFechaCheckout() != null) {%>
                            <div class="info-item">
                                <div class="info-label">Check-Out Realizado</div>
                                <div class="info-value"><%= reserva.getFechaCheckout().format(dateTimeFormatter)%></div>
                            </div>
                            <% } %>
                        </div>
                    </div>
                    <% }%>
                </div>
            </main>
        </div>

        <footer class="footer">
            <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
        </footer>

    </body>
</html>