<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute("usuarioLogueado") != null) {
        response.sendRedirect("dashboard");
        return;
    }

    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Hotel Rizzo | Sistema de Gestión</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="robots" content="noindex, nofollow">
    <link rel="icon" href="images/favicon.ico" type="image/x-icon">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/auth/index.css">
</head>
<body>
    <div class="minimal-container">
        <main class="minimal-main">
            <div class="minimal-card">
                <img src="images/logo-rizzo.svg" alt="Hotel Rizzo" class="minimal-logo" width="160">
                
                <h1 class="minimal-title">Sistema de Gestión <span>Hotel Rizzo</span></h1>
                
                <p class="minimal-description">
                    Acceso al panel de administración de reservas, habitaciones y servicios del hotel.
                </p>
                
                <div class="minimal-features">
                    <div class="feature">
                        <svg viewBox="0 0 24 24" width="20" height="20">
                            <path fill="currentColor" d="M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M10,17L5,12L6.41,10.58L10,14.17L17.59,6.58L19,8L10,17Z" />
                        </svg>
                        <span>Gestión de reservas</span>
                    </div>
                    <div class="feature">
                        <svg viewBox="0 0 24 24" width="20" height="20">
                            <path fill="currentColor" d="M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M10,17L5,12L6.41,10.58L10,14.17L17.59,6.58L19,8L10,17Z" />
                        </svg>
                        <span>Control de habitaciones</span>
                    </div>
                    <div class="feature">
                        <svg viewBox="0 0 24 24" width="20" height="20">
                            <path fill="currentColor" d="M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2M10,17L5,12L6.41,10.58L10,14.17L17.59,6.58L19,8L10,17Z" />
                        </svg>
                        <span>Reportes y estadísticas</span>
                    </div>
                </div>
                
                <a href="login.jsp" class="minimal-btn">Iniciar Sesión</a>
                
                <div class="minimal-footer">
                    <div class="system-status">
                        <span class="status-dot" id="statusDot"></span>
                        <span id="statusText">Sistema operativo</span>
                    </div>
                    <div class="datetime" id="datetime"></div>
                </div>
            </div>
        </main>
        
        <div class="minimal-background">
            <img src="images/hotel.jpg" alt="Hotel Rizzo" loading="lazy">
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Actualizar fecha y hora
            function updateDateTime() {
                const now = new Date();
                const options = {
                    weekday: 'short',
                    day: '2-digit',
                    month: 'short',
                    year: 'numeric',
                    hour: '2-digit',
                    minute: '2-digit'
                };
                document.getElementById('datetime').textContent = now.toLocaleDateString('es-PE', options);
            }

            // Actualizar estado del sistema
            function updateSystemStatus() {
                const isOnline = navigator.onLine;
                const dot = document.getElementById('statusDot');
                const statusText = document.getElementById('statusText');
                
                dot.style.backgroundColor = isOnline ? '#10b981' : '#ef4444';
                statusText.textContent = isOnline ? 'Sistema operativo' : 'Sin conexión';
            }

            // Inicialización
            updateDateTime();
            updateSystemStatus();
            setInterval(updateDateTime, 60000);
            
            // Event listeners
            window.addEventListener('online', updateSystemStatus);
            window.addEventListener('offline', updateSystemStatus);
        });
    </script>
</body>
</html>