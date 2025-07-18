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
    <title>Iniciar Sesión | Hotel Rizzo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="css/auth/login.css">
</head>
<body>
    <div class="login-container">
        <div class="login-card">
            <img src="images/logo-rizzo.svg" alt="Hotel Rizzo" class="login-logo" width="140">
            
            <h1 class="login-title">Acceso al Sistema</h1>
            <p class="login-subtitle">Ingrese sus credenciales para continuar</p>
            
            <form action="LoginServlet" method="post" class="login-form">
                <div class="input-group">
                    <label for="usuario">Usuario</label>
                    <input 
                        type="text" 
                        id="usuario" 
                        name="usuario" 
                        placeholder="Ej: admin@hotelrizzo.com" 
                        required
                        autocomplete="username"
                    >
                </div>
                
                <div class="input-group">
                    <label for="clave">Contraseña</label>
                    <input 
                        type="password" 
                        id="clave" 
                        name="clave" 
                        placeholder="Ingrese su contraseña" 
                        required
                        autocomplete="current-password"
                    >
                </div>
                
                <button type="submit" class="login-btn">
                    <span>Ingresar</span>
                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                        <path d="M5 12h14M12 5l7 7-7 7"/>
                    </svg>
                </button>
                
                <% String error = request.getParameter("error");
                   if ("credenciales".equals(error)) { %>
                       <div class="error-message">
                           <svg viewBox="0 0 24 24" width="18" height="18">
                               <path fill="currentColor" d="M12,2C17.53,2 22,6.47 22,12C22,17.53 17.53,22 12,22C6.47,22 2,17.53 2,12C2,6.47 6.47,2 12,2M15.59,7L12,10.59L8.41,7L7,8.41L10.59,12L7,15.59L8.41,17L12,13.41L15.59,17L17,15.59L13.41,12L17,8.41L15.59,7Z"/>
                           </svg>
                           <span>Usuario o contraseña incorrectos</span>
                       </div>
                <% } else if ("conexion".equals(error)) { %>
                       <div class="error-message warning">
                           <svg viewBox="0 0 24 24" width="18" height="18">
                               <path fill="currentColor" d="M13,13H11V7H13M13,17H11V15H13M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2Z"/>
                           </svg>
                           <span>Error al conectar con la base de datos</span>
                       </div>
                <% } else if ("bloqueado".equals(error)) { %>
                       <div class="error-message critical">
                           <svg viewBox="0 0 24 24" width="18" height="18">
                               <path fill="currentColor" d="M12,17A2,2 0 0,0 14,15C14,13.89 13.1,13 12,13A2,2 0 0,0 10,15A2,2 0 0,0 12,17M18,8A2,2 0 0,1 20,10V20A2,2 0 0,1 18,22H6A2,2 0 0,1 4,20V10C4,8.89 4.9,8 6,8H7V6A5,5 0 0,1 12,1A5,5 0 0,1 17,6V8H18M12,3A3,3 0 0,0 9,6V8H15V6A3,3 0 0,0 12,3Z"/>
                           </svg>
                           <span>Cuenta bloqueada por múltiples intentos fallidos</span>
                       </div>
                <% } else if ("excepcion".equals(error)) { %>
                       <div class="error-message">
                           <svg viewBox="0 0 24 24" width="18" height="18">
                               <path fill="currentColor" d="M13,13H11V7H13M13,17H11V15H13M12,2A10,10 0 0,0 2,12A10,10 0 0,0 12,22A10,10 0 0,0 22,12A10,10 0 0,0 12,2Z"/>
                           </svg>
                           <span>Error inesperado. Intente nuevamente más tarde</span>
                       </div>
                <% } %>
            </form>
            
            <a href="index.jsp" class="back-link">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M19 12H5M12 19l-7-7 7-7"/>
                </svg>
                Volver al inicio
            </a>
        </div>
        
        <div class="login-background">
            <img src="images/hotel_login.jpg" alt="Hotel Rizzo" loading="lazy">
        </div>
    </div>
</body>
</html>