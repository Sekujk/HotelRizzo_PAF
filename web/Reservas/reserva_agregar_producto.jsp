<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
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
    List<ProductoDTO> productosDisponibles = (List<ProductoDTO>) request.getAttribute("productosDisponibles");
    
    String error = (String) request.getAttribute("error");
    String mensaje = (String) session.getAttribute("mensaje");
    if (mensaje != null) session.removeAttribute("mensaje");
    
    if (reserva == null) {
        response.sendRedirect("reservas");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Agregar Producto - Reserva <%= reserva.getNumeroReserva() %></title>
    <link rel="stylesheet" href="css/reservas/reserva_crear.css">
    <link rel="stylesheet" href="css/utils/base.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        .total-field {
            background: linear-gradient(135deg, #3b82f6, #2563eb) !important;
            color: white !important;
            font-weight: bold !important;
            font-size: 1.1em !important;
        }

        .form-description {
            background: #f8f9fa;
            border: 1px solid #e0e0e0;
            padding: 10px;
            border-radius: 4px;
            font-style: italic;
            color: #666;
        }

        .stock-info {
            background: #e8f5e8;
            border: 1px solid #4CAF50;
            padding: 10px;
            border-radius: 4px;
            color: #2e7d32;
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .stock-warning {
            background: #fff3cd;
            border: 1px solid #ffc107;
            color: #856404;
        }

        .stock-danger {
            background: #f8d7da;
            border: 1px solid #dc3545;
            color: #721c24;
        }

        .form-help {
            color: #666;
            font-size: 0.85em;
            margin-top: 4px;
        }

        .debug-info {
            font-size: 0.9em;
            color: #666;
            margin-top: 20px;
            padding: 10px;
            background: #f0f0f0;
            border-radius: 5px;
        }

        .span-2 {
            grid-column: span 2;
        }

        .btn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            background: #ccc !important;
        }

        .product-info {
            background: #e3f2fd;
            border: 1px solid #2196f3;
            border-radius: 4px;
            padding: 12px;
            margin-top: 10px;
        }

        .product-details {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 10px;
            font-size: 0.9em;
        }

        .product-detail {
            display: flex;
            justify-content: space-between;
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
            <h1><i class="fas fa-shopping-cart"></i> Agregar Producto</h1>
            <p class="dashboard-subtitle">Reserva: <%= reserva.getNumeroReserva() %> - <%= reserva.getNombreCliente() %></p>
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
        <div class="form-container">
            <div class="form-header">
                <h2><i class="fas fa-info-circle"></i> Información de la Reserva</h2>
            </div>
            
            <div class="product-info">
                <div class="product-details">
                    <div class="product-detail">
                        <span><strong>Estado:</strong></span>
                        <span><%= reserva.getEstado() %></span>
                    </div>
                    <div class="product-detail">
                        <span><strong>Huéspedes:</strong></span>
                        <span><%= reserva.getNumHuespedes() %></span>
                    </div>
                    <div class="product-detail">
                        <span><strong>Empleado:</strong></span>
                        <span><%= usuario %></span>
                    </div>
                    <div class="product-detail">
                        <span><strong>Fecha:</strong></span>
                        <span><%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()) %></span>
                    </div>
                </div>
            </div>
        </div>

        <!-- Formulario -->
        <div class="form-container">
            <div class="form-header">
                <h2><i class="fas fa-plus-circle"></i> Seleccionar Producto del Minibar</h2>
            </div>

            <form method="post" action="reserva_servicios" class="form-grid" id="formProducto">
                <input type="hidden" name="action" value="agregar_producto">
                <input type="hidden" name="id_reserva" value="<%= reserva.getIdReserva() %>">

                <!-- Selección de Producto -->
                <div class="form-group span-2">
                    <label for="id_producto" class="form-label">
                        <i class="fas fa-shopping-cart"></i> Producto *
                    </label>
                    <select name="id_producto" id="id_producto" class="form-input" required onchange="actualizarProducto()">
                        <option value="">Seleccione un producto del minibar...</option>
                        <% if (productosDisponibles != null && !productosDisponibles.isEmpty()) { %>
                            <% for (ProductoDTO producto : productosDisponibles) { %>
                            <option value="<%= producto.getIdProducto() %>" 
                                    data-precio="<%= producto.getPrecioUnitario() %>"
                                    data-stock="<%= producto.getStock() %>"
                                    data-stockminimo="<%= producto.getStockMinimo() %>"
                                    data-descripcion="<%= producto.getDescripcion() != null ? producto.getDescripcion() : "" %>">
                                <%= producto.getNombre() %> - S/. <%= String.format("%.2f", producto.getPrecioUnitario()) %> 
                                (Stock: <%= producto.getStock() %> unidades)
                            </option>
                            <% } %>
                        <% } else { %>
                            <option value="">No hay productos disponibles en el minibar</option>
                        <% } %>
                    </select>
                </div>

                <!-- Cantidad -->
                <div class="form-group">
                    <label for="cantidad" class="form-label">
                        <i class="fas fa-sort-numeric-up"></i> Cantidad *
                    </label>
                    <input type="number" name="cantidad" id="cantidad" class="form-input" 
                           min="1" max="1" value="1" required onchange="calcularTotal()" oninput="validarCantidad()">
                    <small class="form-help">Máximo disponible: <span id="stock-disponible">0</span> unidades</small>
                </div>

                <!-- Precio Unitario (readonly) -->
                <div class="form-group">
                    <label for="precio_unitario" class="form-label">
                        <i class="fas fa-tag"></i> Precio Unitario
                    </label>
                    <input type="text" id="precio_unitario" class="form-input" readonly placeholder="S/. 0.00">
                </div>

                <!-- Total (calculado) -->
                <div class="form-group span-2">
                    <label for="total" class="form-label">
                        <i class="fas fa-calculator"></i> Total a Cobrar
                    </label>
                    <input type="text" id="total" class="form-input total-field" readonly placeholder="S/. 0.00">
                </div>

                <!-- Información del Producto -->
                <div class="form-group span-2" id="info-producto" style="display: none;">
                    <div id="producto-info" class="product-info">
                        <div class="product-details">
                            <div class="product-detail">
                                <span><strong>Descripción:</strong></span>
                                <span id="producto-descripcion">-</span>
                            </div>
                            <div class="product-detail">
                                <span><strong>Stock actual:</strong></span>
                                <span id="producto-stock">0</span>
                            </div>
                            <div class="product-detail">
                                <span><strong>Stock mínimo:</strong></span>
                                <span id="producto-stock-minimo">0</span>
                            </div>
                            <div class="product-detail">
                                <span><strong>Estado del stock:</strong></span>
                                <span id="producto-estado-stock">-</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Alerta de stock -->
                <div class="form-group span-2" id="alerta-stock" style="display: none;">
                    <div id="stock-alert" class="stock-info">
                        <i class="fas fa-exclamation-triangle"></i>
                        <span id="stock-mensaje">Stock disponible</span>
                    </div>
                </div>

                <!-- Observaciones -->
                <div class="form-group span-2">
                    <label for="observaciones" class="form-label">
                        <i class="fas fa-comment"></i> Observaciones
                    </label>
                    <textarea name="observaciones" id="observaciones" class="form-input" 
                              rows="3" placeholder="Observaciones adicionales sobre el consumo (opcional)"></textarea>
                </div>

                <!-- Botones -->
                <div class="form-actions span-2">
                    <a href="reserva_servicios?id=<%= reserva.getIdReserva() %>" class="btn btn-secondary">
                        <i class="fas fa-arrow-left"></i> Cancelar
                    </a>
                    <button type="submit" class="btn btn-primary" id="btn-agregar" disabled>
                        <i class="fas fa-plus"></i> Agregar al Consumo
                    </button>
                </div>
            </form>
        </div>

        <!-- Debug Info -->
        <% if (productosDisponibles != null) { %>
        <div class="debug-info">
            <strong>Debug:</strong> Se encontraron <%= productosDisponibles.size() %> productos disponibles en el minibar.
            <% if (productosDisponibles.size() == 0) { %>
            <br><em>Nota: Verifique que existan productos activos con stock > 0 en la base de datos.</em>
            <% } %>
        </div>
        <% } %>
    </main>
</div>

<footer class="footer">
    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
</footer>

<script>
function actualizarProducto() {
    const select = document.getElementById('id_producto');
    const precioInput = document.getElementById('precio_unitario');
    const cantidadInput = document.getElementById('cantidad');
    const stockDisponible = document.getElementById('stock-disponible');
    const infoProducto = document.getElementById('info-producto');
    const alertaStock = document.getElementById('alerta-stock');
    const btnAgregar = document.getElementById('btn-agregar');
    
    if (select.value) {
        const option = select.options[select.selectedIndex];
        const precio = parseFloat(option.dataset.precio);
        const stock = parseInt(option.dataset.stock);
        const stockMinimo = parseInt(option.dataset.stockminimo);
        const descripcion = option.dataset.descripcion;
        
        // Actualizar precio
        precioInput.value = 'S/. ' + precio.toFixed(2);
        
        // Actualizar stock
        stockDisponible.textContent = stock;
        cantidadInput.max = stock;
        cantidadInput.value = Math.min(1, stock);
        
        // Mostrar información del producto
        document.getElementById('producto-descripcion').textContent = descripcion || 'Sin descripción';
        document.getElementById('producto-stock').textContent = stock + ' unidades';
        document.getElementById('producto-stock-minimo').textContent = stockMinimo + ' unidades';
        
        // Estado del stock
        let estadoStock = '';
        let claseAlerta = 'stock-info';
        let mensaje = '';
        
        if (stock <= 0) {
            estadoStock = '❌ Sin stock';
            claseAlerta = 'stock-info stock-danger';
            mensaje = 'Producto sin stock disponible';
            btnAgregar.disabled = true;
        } else if (stock <= stockMinimo) {
            estadoStock = '⚠️ Stock bajo';
            claseAlerta = 'stock-info stock-warning';
            mensaje = 'Advertencia: Stock por debajo del mínimo recomendado';
            btnAgregar.disabled = false;
        } else {
            estadoStock = '✅ Stock normal';
            claseAlerta = 'stock-info';
            mensaje = 'Stock disponible: ' + stock + ' unidades';
            btnAgregar.disabled = false;
        }
        
        document.getElementById('producto-estado-stock').textContent = estadoStock;
        document.getElementById('stock-alert').className = claseAlerta;
        document.getElementById('stock-mensaje').textContent = mensaje;
        
        // Mostrar secciones
        infoProducto.style.display = 'block';
        alertaStock.style.display = 'block';
        
        calcularTotal();
    } else {
        // Limpiar todo
        precioInput.value = 'S/. 0.00';
        cantidadInput.max = 1;
        cantidadInput.value = 1;
        stockDisponible.textContent = '0';
        infoProducto.style.display = 'none';
        alertaStock.style.display = 'none';
        btnAgregar.disabled = true;
        calcularTotal();
    }
}

function calcularTotal() {
    const select = document.getElementById('id_producto');
    const cantidadInput = document.getElementById('cantidad');
    const totalInput = document.getElementById('total');
    
    if (select.value && cantidadInput.value) {
        const option = select.options[select.selectedIndex];
        const precio = parseFloat(option.dataset.precio);
        const cantidad = parseInt(cantidadInput.value);
        
        const total = precio * cantidad;
        totalInput.value = 'S/. ' + total.toFixed(2);
    } else {
        totalInput.value = 'S/. 0.00';
    }
}

function validarCantidad() {
    const select = document.getElementById('id_producto');
    const cantidadInput = document.getElementById('cantidad');
    
    if (select.value) {
        const option = select.options[select.selectedIndex];
        const stock = parseInt(option.dataset.stock);
        const cantidad = parseInt(cantidadInput.value);
        
        if (cantidad > stock) {
            cantidadInput.value = stock;
            alert('La cantidad no puede exceder el stock disponible (' + stock + ' unidades)');
        }
        
        if (cantidad <= 0) {
            cantidadInput.value = 1;
        }
        
        calcularTotal();
    }
}

// Validación antes de enviar
document.getElementById('formProducto').addEventListener('submit', function(e) {
    const producto = document.getElementById('id_producto').value;
    const cantidad = parseInt(document.getElementById('cantidad').value);
    
    if (!producto) {
        e.preventDefault();
        alert('Debe seleccionar un producto del minibar');
        return;
    }
    
    if (!cantidad || cantidad <= 0) {
        e.preventDefault();
        alert('La cantidad debe ser mayor a 0');
        return;
    }
    
    const option = document.getElementById('id_producto').options[document.getElementById('id_producto').selectedIndex];
    const stock = parseInt(option.dataset.stock);
    
    if (cantidad > stock) {
        e.preventDefault();
        alert('La cantidad (' + cantidad + ') no puede exceder el stock disponible (' + stock + ' unidades)');
        return;
    }
    
    if (stock <= 0) {
        e.preventDefault();
        alert('El producto seleccionado no tiene stock disponible');
        return;
    }
    
    // Confirmación final
    const nombreProducto = option.text.split(' - ')[0];
    const total = document.getElementById('total').value;
    
    if (!confirm('¿Confirmar el consumo de ' + cantidad + ' unidad(es) de ' + nombreProducto + ' por un total de ' + total + '?')) {
        e.preventDefault();
        return;
    }
});

// Auto-calcular al cambiar cantidad
document.getElementById('cantidad').addEventListener('input', validarCantidad);
</script>

</body>
</html>