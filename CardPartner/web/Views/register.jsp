<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registro - CardPartner</title>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/loginStyle.css?v=3">
</head>
<body> 
    
    <div class="contenedor-login"> 
        
        <header class="login-header">
            <img src="${pageContext.request.contextPath}/img/logo.png" alt="CardPartner Logo">
            <h2>CREAR CUENTA</h2>
            <p>Únete a la comunidad de CardPartner</p>
        </header>

        <% 
            String error = request.getParameter("error");
            if (error != null) { 
                String mensaje = "";
                if ("passwords".equals(error)) {
                    mensaje = "Las contraseñas no coinciden.";
                } else if ("emailexiste".equals(error)) {
                    mensaje = "Ese correo electrónico ya está registrado.";
                } else if ("db".equals(error)) {
                    mensaje = "Error interno del servidor. Inténtalo más tarde.";
                }
        %>
            <div style="background-color: #ffebee; color: #c62828; padding: 10px; border-radius: 5px; text-align: center; margin-bottom: 15px; font-family: 'Poppins', sans-serif; font-size: 0.9em;">
                <i class="fas fa-exclamation-circle"></i> <%= mensaje %>
            </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/RegistroServlet" method="POST" class="login-form">
            
            <div class="input-group">
                <i class="fas fa-user"></i>
                <input type="text" id="nuevo_usuario" name="nuevo_usuario" placeholder="Nombre de usuario" required>
            </div>

            <div class="input-group">
                <i class="fas fa-envelope"></i>
                <input type="email" id="email" name="email" placeholder="Correo electrónico" required>
            </div>

            <div class="input-group">
                <i class="fas fa-lock"></i>
                <input type="password" id="nueva_password" name="nueva_password" placeholder="Contraseña" required>
            </div>

            <div class="input-group">
                <i class="fas fa-lock-open"></i>
                <input type="password" id="repetir_password" name="repetir_password" placeholder="Repite tu contraseña" required>
            </div>

            <button type="submit" class="btn-login">REGISTRARSE</button>
            
            <div class="separador">
                <span>O</span>
            </div>
            
            <button type="button" class="btn-google">
                <i class="fab fa-google"></i> Registrarse con Google
            </button>
            
            <div class="login-footer">
                <p>¿Ya tienes cuenta? <a href="${pageContext.request.contextPath}/Views/login.jsp">Inicia sesión aquí</a></p>
            </div>
            
        </form>

        <a href="${pageContext.request.contextPath}/index.jsp" class="btn-volver-inicio">
            <i class="fas fa-arrow-left"></i> Volver al inicio
        </a>
        
    </div>

</body>
</html>