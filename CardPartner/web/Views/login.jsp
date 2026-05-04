<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Login - CardPartner</title>
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/loginStyle.css?v=6">
    </head>
    <body>
        <div class="contenedor-login">
            <header class="login-header">
                <img src="${pageContext.request.contextPath}/img/logo.png" alt="Logo">
                <h2>Bienvenido</h2>
                <p>Inicia sesión para continuar</p>
            </header>
            
            <%-- Mensajes de feedback --%>
            <% if (request.getParameter("error") != null) { %>
                <div style="background-color: #ffebee; color: #c62828; padding: 10px; border-radius: 5px; text-align: center; margin-bottom: 15px; font-family: 'Poppins', sans-serif; font-size: 0.9em;">
                    <i class="fas fa-exclamation-circle"></i> 
                    <%= request.getParameter("error").equals("google_fail") ? "Error al iniciar sesión con Google." : "Usuario o contraseña incorrectos." %>
                </div>
            <% } else if (request.getParameter("exito") != null && request.getParameter("exito").equals("registro_ok")) { %>
                <div style="background-color: #e8f5e9; color: #2e7d32; padding: 10px; border-radius: 5px; text-align: center; margin-bottom: 15px; font-family: 'Poppins', sans-serif; font-size: 0.9em;">
                    <i class="fas fa-check-circle"></i> Cuenta creada con éxito. Ya puedes iniciar sesión.
                </div>
            <% } %>

            <form class="login-form" action="${pageContext.request.contextPath}/LoginServlet" method="POST">
                <div class="input-group">
                    <i class="fas fa-user"></i>
                    <input type="text" name="email" placeholder="Usuario o Email" required>
                </div>
                <div class="input-group">
                    <i class="fas fa-lock"></i>
                    <input type="password" id="loginPass" name="password" placeholder="Contraseña" required style="padding-right: 40px;">
                    <i class="fas fa-eye toggle-password" onclick="togglePassword('loginPass', this)"></i>
                </div>
                <div class="input-group" style="display: flex; align-items: center; justify-content: flex-start; gap: 10px; margin-bottom: 20px; border: none; background: transparent; padding: 0;">
                    <input type="checkbox" id="recordar" name="recordar" style="width: 18px; height: 18px; cursor: pointer; margin: 0;">
                    <label for="recordar" style="font-family: 'Poppins', sans-serif; font-size: 0.9em; cursor: pointer; color: #555; margin: 0;">
                        Mantener sesión iniciada
                    </label>
                </div>
                <button type="submit" class="btn-login">ENTRAR</button>
                
                <div class="separador">
                    <span>O</span>
                </div>
                
                <!-- INICIO INTEGRACIÓN GOOGLE -->
            <script src="https://accounts.google.com/gsi/client" async defer></script>
            <div id="g_id_onload"
                 data-client_id="825568955910-g3ogkeqqj3ajurck5gd7bgfci04irrot.apps.googleusercontent.com"
                 data-context="signin"
                 data-ux_mode="popup"
                 data-callback="handleCredentialResponse"
                 data-auto_prompt="false">
            </div>
            
            <!-- AQUÍ ESTÁ LA MAGIA DEL DISEÑO -->
            <div class="g_id_signin"
                 data-type="standard"
                 data-shape="rectangular"
                 data-theme="outline"
                 data-text="continue_with" 
                 data-size="large"
                 data-locale="es"
                 data-width="340"
                 data-logo_alignment="center"
                 style="display: flex; justify-content: center; margin-bottom: 20px;">
            </div>
            
            <script>
              function handleCredentialResponse(response) {
                  var form = document.createElement('form');
                  form.method = 'POST';
                  form.action = '${pageContext.request.contextPath}/GoogleLoginServlet';
            
                  var input = document.createElement('input');
                  input.type = 'hidden';
                  input.name = 'credential';
                  input.value = response.credential;
                  
                  form.appendChild(input);
                  document.body.appendChild(form);
                  form.submit();
              }
            </script>
            <!-- FIN INTEGRACIÓN GOOGLE -->

                <div class="login-footer">
                    <a href="#modal-recuperar">¿Olvidaste tu contraseña?</a>
                    <p>¿No tienes cuenta? <a href="${pageContext.request.contextPath}/Views/register.jsp">Regístrate</a></p>
                </div>
            </form>
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn-volver-inicio">
                <i class="fas fa-arrow-left"></i> Volver al inicio
            </a>
        </div>

        <%-- Modal de recuperación --%>
        <div id="modal-recuperar" class="modal-overlay">
            <div class="modal-content">
                <a href="#" class="btn-cerrar">&times;</a>
                <header class="login-header">
                    <i class="fas fa-key" style="font-size: 40px; color: #333; margin-bottom: 10px;"></i>
                    <h2>Recuperar Clave</h2>
                    <p>Introduce tu email para recibir instrucciones.</p>
                </header>
                <form class="login-form">
                    <div class="input-group">
                        <i class="fas fa-envelope"></i>
                        <input type="email" placeholder="Correo electrónico" required>
                    </div>
                    <button type="submit" class="btn-login">ENVIAR ENLACE</button>
                </form>
            </div>
        </div>

        <script>
            function togglePassword(inputId, iconElement) {
                var input = document.getElementById(inputId);
                if (input.type === "password") {
                    input.type = "text";
                    iconElement.classList.remove("fa-eye");
                    iconElement.classList.add("fa-eye-slash");
                } else {
                    input.type = "password";
                    iconElement.classList.remove("fa-eye-slash");
                    iconElement.classList.add("fa-eye");
                }
            }
        </script>
    </body>
</html>