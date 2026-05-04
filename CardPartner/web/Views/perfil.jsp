<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page import="modelo.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // 1. CONTROL DE SEGURIDAD (Login)
    // ==========================================
    Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActivo");
    if (usuarioActual == null) {
        response.sendRedirect(request.getContextPath() + "/Views/login.jsp");
        return;
    }
    
    // ==========================================
    // 2. PATRÓN MVC: ESCUDO DE PROTECCIÓN
    // ==========================================
    List<Mazo> misMazos = (List<Mazo>) request.getAttribute("listaMisMazos");
    List<Mazo> misFavoritos = (List<Mazo>) request.getAttribute("listaMisFavoritos");
    
    if (misMazos == null || misFavoritos == null) {
        response.sendRedirect(request.getContextPath() + "/UsuarioServlet?accion=mi_perfil");
        return;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    String fechaBonita = (usuarioActual.getFechaRegistro() != null) ? sdf.format(usuarioActual.getFechaRegistro()) : "Desconocida";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <title>Card Partner - Mi Perfil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/perfilStyle.css?v=11">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-perfil">
                
                <%-- ========================================== --%>
                <%-- MENSAJES DE FEEDBACK (Éxito o Error)       --%>
                <%-- ========================================== --%>
                <% 
                    String exito = request.getParameter("exito");
                    String error = request.getParameter("error");

                    if (exito != null) {
                        String msg = "";
                        if (exito.equals("pass_ok")) msg = "¡Contraseña actualizada con éxito!";
                        else if (exito.equals("perfil_actualizado")) msg = "¡Perfil actualizado correctamente!";
                        else if (exito.equals("mazo_borrado")) msg = "Mazo eliminado correctamente.";
                        else if (exito.equals("favorito_quitado")) msg = "Mazo quitado de favoritos.";
                %>
                    <div style="background-color: #e8f5e9; color: #2e7d32; border: 1px solid #c8e6c9; padding: 15px; border-radius: 8px; margin-bottom: 20px; text-align: center; font-family: 'Poppins', sans-serif; font-weight: 600;">
                        <i class="fas fa-check-circle"></i> <%= msg %>
                    </div>
                <%  } else if (error != null) { 
                        String msg = "Ha ocurrido un error.";
                        if (error.equals("pass_incorrecta")) msg = "La contraseña actual introducida no es correcta.";
                        else if (error.equals("pass_no_coincide")) msg = "Las contraseñas nuevas no coinciden.";
                        else if (error.equals("perfil_fail")) msg = "No se pudo actualizar el perfil.";
                %>
                    <div style="background-color: #ffebee; color: #c62828; border: 1px solid #ffcdd2; padding: 15px; border-radius: 8px; margin-bottom: 20px; text-align: center; font-family: 'Poppins', sans-serif; font-weight: 600;">
                        <i class="fas fa-exclamation-circle"></i> <%= msg %>
                    </div>
                <%  } %>

                <div class="perfil-header">
                    <!-- AvatarServlet cargando la foto con truco anti-caché -->
                    <div class="perfil-avatar">
                        <img src="${pageContext.request.contextPath}/AvatarServlet?id=<%= usuarioActual.getIdUsuario() %>&t=<%= System.currentTimeMillis() %>" alt="Avatar de <%= usuarioActual.getApodoUsuario() %>" onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/default-avatar.png';">
                    </div>
                    
                    <div class="perfil-info">
                        <h2 class="perfil-nombre"><%= usuarioActual.getApodoUsuario() %></h2>
                        <p class="perfil-email"><i class="fas fa-envelope"></i> <%= usuarioActual.getEmail() %></p>
                        <p class="perfil-fecha"><i class="fas fa-calendar-alt"></i> Miembro desde: <%= fechaBonita %></p>
                    </div>
                    
                    <div class="perfil-acciones">
                        <a href="#modal-editar-perfil" class="btn-editar-perfil" style="text-decoration: none; display: inline-block; text-align: center;"><i class="fas fa-edit"></i> Editar Datos</a>
                        <a href="#modal-password" class="btn-cambiar-pass" style="text-decoration: none;"><i class="fas fa-key"></i> Cambiar Contraseña</a>
                    </div>
                </div> 

                <!-- SECCIÓN: MIS MAZOS CREADOS -->
                <div class="seccion-mazos">
                    <div class="barra-titulo">
                        <h3 class="subtitulo-mazos">Mis Mazos Creados</h3>
                        <a href="${pageContext.request.contextPath}/DeckBuilderServlet" class="btn-crear-nuevo">+ Crear Nuevo</a>
                    </div>
                    
                    <div class="galeria-mazos">
                        <%
                            if (!misMazos.isEmpty()) {
                                for (Mazo m : misMazos) {
                                    String visibilidad = m.isEsPublico() ? "Público" : "Privado";
                                    
                                    Carta lider = m.getCartaLider();
                                    String imgRaw = (lider != null && lider.getImagen() != null) ? lider.getImagen().trim() : "";
                                    String imgFinal = (!imgRaw.isEmpty() && imgRaw.toLowerCase().startsWith("http")) 
                                           ? "https://images.weserv.nl/?url=" + imgRaw 
                                           : request.getContextPath() + (imgRaw.isEmpty() ? "/img/logo.png" : imgRaw);
                        %>
                                    <div class="mazo-item">
                                        <a href="${pageContext.request.contextPath}/CatalogoMazosServlet?accion=detalle&id=<%= m.getIdMazo() %>">
                                            <div class="mazo-imagen">
                                                <img src="<%= imgFinal %>" alt="Líder del mazo" loading="lazy" referrerpolicy="no-referrer">
                                            </div>
                                            <div class="mazo-info">
                                                <h3><%= m.getNombreMazo() %></h3>
                                                <p class="mazo-meta">Privacidad: <%= visibilidad %></p>
                                                <span class="mazo-tipo"><%= (lider != null) ? lider.getColor() : "Desconocido" %></span>
                                            </div>
                                        </a>
                                        <div class="acciones-carta">
                                            <a href="${pageContext.request.contextPath}/DeckBuilderServlet?accion=editar&id=<%= m.getIdMazo() %>" class="btn-accion btn-editar" style="text-decoration:none;"><i class="fas fa-pen"></i> Editar</a>
                                            
                                            <form action="${pageContext.request.contextPath}/UsuarioServlet" method="POST" style="margin:0; padding:0; display:inline;" onsubmit="return confirm('¿Seguro que quieres borrar este mazo?');">
                                                <input type="hidden" name="accion" value="borrar_mazo">
                                                <input type="hidden" name="id" value="<%= m.getIdMazo() %>">
                                                <button type="submit" class="btn-accion btn-borrar"><i class="fas fa-trash"></i></button>
                                            </form>
                                        </div>
                                    </div>
                        <%
                                }
                            } else {
                        %>
                                <p style="color: #666; font-style: italic;">Aún no has creado ningún mazo. ¡Anímate en el Deck Builder!</p>
                        <%
                            }
                        %>
                    </div>
                </div>

                <!-- SECCIÓN: MAZOS GUARDADOS (FAVORITOS) -->
                <div class="seccion-mazos">
                    <h3 class="subtitulo-mazos" style="margin-top: 20px;">Mazos Guardados (Favoritos)</h3>
                    <div class="galeria-mazos">
                        <%
                            if (!misFavoritos.isEmpty()) {
                                for (Mazo fav : misFavoritos) {
                                    Carta lider = fav.getCartaLider();
                                    String imgRaw = (lider != null && lider.getImagen() != null) ? lider.getImagen().trim() : "";
                                    String imgFinal = (!imgRaw.isEmpty() && imgRaw.toLowerCase().startsWith("http")) 
                                           ? "https://images.weserv.nl/?url=" + imgRaw 
                                           : request.getContextPath() + (imgRaw.isEmpty() ? "/img/logo.png" : imgRaw);
                        %>
                                    <div class="mazo-item">
                                        <a href="${pageContext.request.contextPath}/CatalogoMazosServlet?accion=detalle&id=<%= fav.getIdMazo() %>">
                                            <div class="mazo-imagen">
                                                <img src="<%= imgFinal %>" alt="Líder Meta" loading="lazy" referrerpolicy="no-referrer">
                                            </div>
                                            <div class="mazo-info">
                                                <h3><%= fav.getNombreMazo() %></h3>
                                                <p class="mazo-autor">Creador ID: <%= fav.getIdUsuarioCreador() %></p>
                                                <span class="mazo-tipo"><%= (lider != null) ? lider.getColor() : "Desconocido" %></span>
                                            </div>
                                        </a>
                                        <div class="acciones-carta">
                                            <form action="${pageContext.request.contextPath}/UsuarioServlet" method="POST" style="margin:0; padding:0; width:100%;">
                                                <input type="hidden" name="accion" value="quitar_favorito">
                                                <input type="hidden" name="id" value="<%= fav.getIdMazo() %>">
                                                <button type="submit" class="btn-accion btn-borrar" style="width: 100%;"><i class="fas fa-heart-broken"></i> Quitar de guardados</button>
                                            </form>
                                        </div>
                                    </div>
                        <%
                                }
                            } else {
                        %>
                                <p style="color: #666; font-style: italic;">No tienes ningún mazo guardado en favoritos.</p>
                        <%
                            }
                        %>
                    </div>
                </div>

            </main>
        </div>
    </div>

    <!-- ========================================== -->
    <!-- MODAL: EDITAR DATOS DEL PERFIL             -->
    <!-- ========================================== -->
    <div id="modal-editar-perfil" class="modal-overlay">
        <div class="modal-content">
            <a href="#" class="btn-cerrar">&times;</a>
            <h2 class="titulo-seccion" style="font-size: 24px; margin-bottom: 20px;"><i class="fas fa-user-edit"></i> Editar Perfil</h2>
            
            <form action="${pageContext.request.contextPath}/UsuarioServlet" method="POST" enctype="multipart/form-data">
                <input type="hidden" name="accion" value="editar_datos">
                
                <div class="campo-form" style="text-align: left;">
                    <label style="font-weight: bold; margin-bottom: 5px; display: block;">Apodo:</label>
                    <input type="text" name="apodo" value="<%= usuarioActual.getApodoUsuario() %>" required style="width: 100%; padding: 10px; margin-bottom: 15px; border-radius: 8px; border: 1px solid #ccc;">
                    
                    <label style="font-weight: bold; margin-bottom: 5px; display: block;">Correo Electrónico:</label>
                    <input type="email" name="email" value="<%= usuarioActual.getEmail() %>" required style="width: 100%; padding: 10px; margin-bottom: 15px; border-radius: 8px; border: 1px solid #ccc;">
                    
                    <label style="font-weight: bold; margin-bottom: 5px; display: block;">Foto de Perfil (Avatar):</label>
                    <p style="font-size: 12px; color: #666; margin-bottom: 5px;">Sube una nueva imagen o marca la casilla para eliminar la actual.</p>
                    <input type="file" name="avatar" accept="image/*" style="width: 100%; padding: 10px; margin-bottom: 10px; background: #fff; border-radius: 8px; border: 1px dashed #ccc;">
                    
                    <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 20px;">
                        <input type="checkbox" id="eliminar_avatar" name="eliminar_avatar" style="width: 18px; height: 18px; cursor: pointer;">
                        <label for="eliminar_avatar" style="font-size: 14px; color: #d32f2f; cursor: pointer; font-weight: 600;">Eliminar foto de perfil actual</label>
                    </div>
                    
                    <button type="submit" class="btn-editar-perfil" style="width: 100%;">Guardar Cambios</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ========================================== -->
    <!-- MODAL: CAMBIAR CONTRASEÑA                  -->
    <!-- ========================================== -->
    <div id="modal-password" class="modal-overlay">
        <div class="modal-content">
            <a href="#" class="btn-cerrar">&times;</a>
            <h2 class="titulo-seccion" style="font-size: 24px; margin-bottom: 20px;"><i class="fas fa-lock"></i> Nueva Contraseña</h2>
            
            <form action="${pageContext.request.contextPath}/UsuarioServlet" method="POST">
                <input type="hidden" name="accion" value="cambiar_password">
                <div class="campo-form">
                    
                    <!-- INPUT 1: Password actual -->
                    <div class="password-wrapper">
                        <input type="password" id="passActual" name="passActual" placeholder="Contraseña actual" required style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #ccc; padding-right: 40px;">
                        <i class="fas fa-eye toggle-password" onclick="togglePassword('passActual', this)"></i>
                    </div>

                    <!-- INPUT 2: Nueva Password -->
                    <div class="password-wrapper">
                        <input type="password" id="passNueva" name="passNueva" placeholder="Nueva contraseña" required style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #ccc; padding-right: 40px;">
                        <i class="fas fa-eye toggle-password" onclick="togglePassword('passNueva', this)"></i>
                    </div>

                    <!-- INPUT 3: Confirmación -->
                    <div class="password-wrapper">
                        <input type="password" id="passConfirmacion" name="passConfirmacion" placeholder="Repite la nueva contraseña" required style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #ccc; padding-right: 40px;">
                        <i class="fas fa-eye toggle-password" onclick="togglePassword('passConfirmacion', this)"></i>
                    </div>
                    
                    <button type="submit" class="btn-editar-perfil" style="width: 100%; margin-top: 10px;">Actualizar</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ========================================== -->
    <!-- SCRIPT PARA MOSTRAR/OCULTAR CONTRASEÑAS    -->
    <!-- ========================================== -->
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