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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/perfilStyle.css?v=9">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-perfil">
                
                <div class="perfil-header">
                    <!-- AvatarServlet cargando la foto -->
                    <div class="perfil-avatar">
                        <img src="${pageContext.request.contextPath}/AvatarServlet?id=<%= usuarioActual.getIdUsuario() %>" alt="Avatar de <%= usuarioActual.getApodoUsuario() %>" onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/default-avatar.png';">
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
                </div> <!-- ¡AQUÍ ESTABA EL DIV QUE ME COMÍ ANTES! -->

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
                    <p style="font-size: 12px; color: #666; margin-bottom: 5px;">Deja este campo vacío si no quieres cambiar tu foto actual.</p>
                    <input type="file" name="avatar" accept="image/*" style="width: 100%; padding: 10px; margin-bottom: 20px; background: #fff; border-radius: 8px; border: 1px dashed #ccc;">
                    
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
                    <input type="password" name="passActual" placeholder="Contraseña actual" required style="width: 100%; padding: 10px; margin-bottom: 15px; border-radius: 8px; border: 1px solid #ccc;">
                    <input type="password" name="passNueva" placeholder="Nueva contraseña" required style="width: 100%; padding: 10px; margin-bottom: 15px; border-radius: 8px; border: 1px solid #ccc;">
                    <button type="submit" class="btn-editar-perfil" style="width: 100%;">Actualizar</button>
                </div>
            </form>
        </div>
    </div>

</body>
</html>