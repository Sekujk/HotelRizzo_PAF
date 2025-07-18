<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Acceso Denegado</title>
    <style>
        body {
            background-color: #f5f5f5;
            font-family: 'Segoe UI', sans-serif;
            text-align: center;
            padding-top: 80px;
        }
        .denegado-container {
            background-color: white;
            max-width: 500px;
            margin: auto;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 0 15px rgba(0,0,0,0.1);
        }
        h1 {
            color: #d9534f;
        }
        p {
            font-size: 18px;
            color: #333;
        }
        a {
            color: #007bff;
            text-decoration: none;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="denegado-container">
        <h1>Acceso Denegado</h1>
        <p>No tienes permiso para acceder a esta página.</p>
        <a href="index.jsp">Volver al inicio</a>
    </div>
</body>
</html>
