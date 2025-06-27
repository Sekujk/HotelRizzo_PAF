<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page session="true" %>
<%
    String usuario = (String) session.getAttribute("usuarioLogueado");
    if (usuario == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    Long empleadosActivos = (Long) request.getAttribute("empleadosActivos");
    if (empleadosActivos == null) {
        empleadosActivos = 0L;
    }
    Integer stockBajo = (Integer) request.getAttribute("stockBajo");
    if (stockBajo == null) {
        stockBajo = 0;
    }

    Long serviciosActivos = (Long) request.getAttribute("serviciosActivos");
    if (serviciosActivos == null) {
        serviciosActivos = 0L;
    }


%>


<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Panel de Administración - Hotel Rizzo</title>
        <link rel="stylesheet" href="css/dashboard.css">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    </head>
    <body>
        <header class="navbar">
            <div class="nav-container">
                <div class="logo-section">
                    <i class="fas fa-hotel"></i>
                    <span class="logo-text">Hotel Rizzo</span>
                </div>
                <div class="user-section">
                    <div class="user-info">
                        <i class="fas fa-user-circle"></i>
                        <span class="welcome">Bienvenido, <strong><%= usuario%></strong></span>
                    </div>
                    <a href="login.jsp" class="btn-logout">
                        <i class="fas fa-sign-out-alt"></i>
                        Cerrar sesión
                    </a>
                </div>
            </div>
        </header>

        <main class="dashboard-container">
            <section class="welcome-section">
                <div class="welcome-content">
                    <h1>Panel de Administración</h1>
                    <p>Gestiona todo el funcionamiento del hotel desde un solo lugar</p>
                    <div class="stats-quick">
                        <div class="stat-item">
                            <i class="fas fa-calendar-check"></i>
                            <span>Reservas Hoy: <strong>12</strong></span>
                        </div>
                        <div class="stat-item">
                            <i class="fas fa-bed"></i>
                            <span>Ocupación: <strong>85%</strong></span>
                        </div>
                        <div class="stat-item">
                            <i class="fas fa-dollar-sign"></i>
                            <span>Ingresos: <strong>$2,450</strong></span>
                        </div>
                    </div>
                </div>
            </section>

            <section class="modules-section">
                <h2>Módulos de Gestión</h2>
                <div class="grid-modules">
                    <a href="reservas.jsp" class="card reservas">
                        <div class="card-icon">
                            <i class="fas fa-calendar-alt"></i>
                        </div>
                        <div class="card-content">
                            <h3>Reservas</h3>
                            <p>Ver y administrar todas las reservas del hotel</p>
                            <span class="card-badge">24 nuevas</span>
                        </div>
                        <div class="card-arrow">
                            <i class="fas fa-arrow-right"></i>
                        </div>
                    </a>

                    <a href="habitaciones.jsp" class="card habitaciones">
                        <div class="card-icon">
                            <i class="fas fa-bed"></i>
                        </div>
                        <div class="card-content">
                            <h3>Habitaciones</h3>
                            <p>Control de disponibilidad y tipos de habitación</p>
                            <span class="card-badge disponible">15 disponibles</span>
                        </div>
                        <div class="card-arrow">
                            <i class="fas fa-arrow-right"></i>
                        </div>
                    </a>

                    <a href="empleado" class="card empleados">
                        <div class="card-icon">
                            <i class="fas fa-users"></i>
                        </div>
                        <div class="card-content">
                            <h3>Empleados</h3>
                            <p>Registrar y gestionar información de empleados</p>
                            <span class="card-badge"><%= empleadosActivos%> activos</span>
                        </div>
                        <div class="card-arrow">
                            <i class="fas fa-arrow-right"></i>
                        </div>
                    </a>

                    <a href="servicio" class="card servicios">
                        <div class="card-icon">
                            <i class="fas fa-concierge-bell"></i>
                        </div>
                        <div class="card-content">
                            <h3>Servicios</h3>
                            <p>Gestionar servicios adicionales y precios</p>
                            <span class="card-badge"><%= serviciosActivos%> activos</span>
                        </div>
                        <div class="card-arrow">
                            <i class="fas fa-arrow-right"></i>
                        </div>
                    </a>
                    <a href="producto" class="card productos">
                        <div class="card-icon">
                            <i class="fas fa-shopping-cart"></i>
                        </div>
                        <div class="card-content">
                            <h3>Productos</h3>
                            <p>Inventario del minibar y productos del hotel</p>
                            <% if (stockBajo > 0) {%>
                            <span class="card-badge warning"><%= stockBajo%> con stock bajo</span>
                            <% } else { %>
                            <span class="card-badge success">Stock suficiente</span>
                            <% }%>
                        </div>
                        <div class="card-arrow">
                            <i class="fas fa-arrow-right"></i>
                        </div>
                    </a>

                    <a href="reportes.jsp" class="card reportes">
                        <div class="card-icon">
                            <i class="fas fa-chart-line"></i>
                        </div>
                        <div class="card-content">
                            <h3>Reportes</h3>
                            <p>Resumen de ingresos y análisis de actividad</p>
                            <span class="card-badge success">Actualizado</span>
                        </div>
                        <div class="card-arrow">
                            <i class="fas fa-arrow-right"></i>
                        </div>
                    </a>
                </div>
            </section>

            <section class="quick-actions">
                <h2>Acciones Rápidas</h2>
                <div class="quick-actions-grid">
                    <button class="quick-btn">
                        <i class="fas fa-plus"></i>
                        Nueva Reserva
                    </button>
                    <button class="quick-btn">
                        <i class="fas fa-key"></i>
                        Check-in Rápido
                    </button>
                    <button class="quick-btn">
                        <i class="fas fa-door-open"></i>
                        Check-out
                    </button>
                    <button class="quick-btn">
                        <i class="fas fa-print"></i>
                        Imprimir Reporte
                    </button>
                </div>
            </section>
        </main>

        <footer class="footer">
            <div class="footer-content">
                <div class="footer-left">
                    <p>&copy; 2025 Hotel Rizzo - Sistema de Gestión Hotelera</p>
                </div>
                <div class="footer-right">
                    <span>Versión 2.1.0</span>
                    <span>|</span>
                    <a href="#ayuda">Ayuda</a>
                    <span>|</span>
                    <a href="#soporte">Soporte</a>
                </div>
            </div>
        </footer>

        <script>
            // Añadir interactividad básica
            document.querySelectorAll('.card').forEach(card => {
                card.addEventListener('mouseenter', function () {
                    this.style.transform = 'translateY(-8px)';
                });

                card.addEventListener('mouseleave', function () {
                    this.style.transform = 'translateY(0)';
                });
            });

            // Animación para las estadísticas
            document.addEventListener('DOMContentLoaded', function () {
                const stats = document.querySelectorAll('.stat-item');
                stats.forEach((stat, index) => {
                    setTimeout(() => {
                        stat.style.opacity = '1';
                        stat.style.transform = 'translateY(0)';
                    }, index * 200);
                });
            });
        </script>
    </body>
</html>