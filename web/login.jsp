<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Iniciar Sesión - Hotel Rizzo</title>
    <link rel="stylesheet" href="css/login_css.css">
</head>
<body>
    <div class="login-box">
        <h2>Acceso al sistema</h2>

        <form action="LoginServlet" method="post">
            <label for="usuario">Usuario</label>
            <input type="text" id="usuario" name="usuario" placeholder="Ingrese su usuario" required>

            <label for="clave">Contraseña</label>
            <input type="password" id="clave" name="clave" placeholder="Ingrese su contraseña" required>

            <button type="submit">Ingresar</button>

            <% String error = request.getParameter("error");
               if ("credenciales".equals(error)) { %>
                   <p class="error">❌ Usuario o contraseña incorrectos.</p>
            <% } else if ("conexion".equals(error)) { %>
                   <p class="error">⚠️ Error al conectar con la base de datos.</p>
            <% } else if ("bloqueado".equals(error)) { %>
                   <p class="error">🔒 Tu cuenta ha sido bloqueada por múltiples intentos fallidos.</p>
            <% } else if ("excepcion".equals(error)) { %>
                   <p class="error">❗ Se produjo un error inesperado. Intenta nuevamente más tarde.</p>
            <% } %>
        </form>

        <a href="index.jsp" class="volver">← Volver al inicio</a>
    </div>
</body>
</html>
