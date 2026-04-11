<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // --- 1. CONTROL DE SEGURIDAD ---
    // Recogemos el usuario de la sesión. Si es null (no ha hecho login), lo echamos.
    Usuario usuarioActual = (Usuario) session.getAttribute("usuarioActivo");
    if (usuarioActual == null) {
        response.sendRedirect(request.getContextPath() + "/Views/login.jsp");
        return; // Detenemos la carga de la página
    }
    
    // Formateador para que la fecha quede bonita (Ej: 15/04/2024)
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/perfilStyle.css?v=1">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-perfil">
                
                <div class="perfil-header">
                    <div class="perfil-avatar">
                        <i class="fas fa-user-circle"></i>
                    </div>
                    <div class="perfil-info">
                        <h2 class="perfil-nombre"><%= usuarioActual.getNombreUsuario() %></h2>
                        <p class="perfil-email"><i class="fas fa-envelope"></i> <%= usuarioActual.getEmail() %></p>
                        <p class="perfil-fecha"><i class="fas fa-calendar-alt"></i> Miembro desde: <%= fechaBonita %></p>
                    </div>
                    <div class="perfil-acciones">
                        <button class="btn-editar-perfil"><i class="fas fa-edit"></i> Editar Datos</button>
                        <a href="#modal-password" class="btn-cambiar-pass" style="text-decoration: none;"><i class="fas fa-key"></i> Cambiar Contraseña</a>
                    </div>
                </div>

                <div class="seccion-mazos">
                    <div class="barra-titulo">
                        <h3 class="subtitulo-mazos">Mis Mazos Creados</h3>
                        <a href="${pageContext.request.contextPath}/Views/deck-builder.jsp" class="btn-crear-nuevo">+ Crear Nuevo</a>
                    </div>
                    
                    <div class="galeria-mazos">
                        <%
                            // Recuperamos la lista de mazos que creamos en GuardarMazoServlet
                            List<Mazo> misMazos = (List<Mazo>) session.getAttribute("listaMisMazos");
                            
                            // Si la lista tiene cosas, hacemos un bucle para pintarlos
                            if (misMazos != null && !misMazos.isEmpty()) {
                                for (Mazo m : misMazos) {
                        %>
                                    <div class="mazo-item">
                                        <a href="${pageContext.request.contextPath}/Views/deck-view.jsp" style="text-decoration: none; color: inherit;">
                                            <div class="mazo-imagen">
                                                <img src="${pageContext.request.contextPath}/img/lider-sakazuki.jpg" alt="Líder Mazo">
                                            </div>
                                            <div class="mazo-info">
                                                <h3><%= m.getNombre() %></h3>
                                                <p class="mazo-meta">Privacidad: <%= m.getVisibilidad() %></p>
                                                <span class="mazo-tipo">Personalizado</span>
                                            </div>
                                        </a>
                                        <div class="acciones-carta">
                                            <a href="${pageContext.request.contextPath}/Views/deck-builder.jsp" class="btn-accion btn-editar"><i class="fas fa-pen"></i> Editar</a>
                                            <button class="btn-accion btn-borrar"><i class="fas fa-trash"></i></button>
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

                <div class="seccion-mazos">
                    <h3 class="subtitulo-mazos" style="margin-top: 20px;">Mazos Guardados (Favoritos)</h3>
                    <div class="galeria-mazos">
                        <div class="mazo-item">
                            <a href="${pageContext.request.contextPath}/Views/deck-view.jsp" style="text-decoration: none; color: inherit;">
                                <div class="mazo-imagen">
                                    <img src="${pageContext.request.contextPath}/img/lider-zoro.jpg" alt="Líder Zoro">
                                </div>
                                <div class="mazo-info">
                                    <h3>Zoro Rush (Aggro)</h3>
                                    <p class="mazo-autor">Creador: Comunidad</p>
                                    <span class="mazo-tipo">Aggro</span>
                                </div>
                            </a>
                            <div class="acciones-carta">
                                <button class="btn-accion btn-borrar" style="width: 100%;"><i class="fas fa-heart-broken"></i> Quitar de guardados</button>
                            </div>
                        </div>
                    </div>
                </div>

            </main>
        </div>
    </div>

    <div id="modal-password" class="modal-overlay">
        <div class="modal-content">
            <a href="#" class="btn-cerrar">&times;</a>
            <h2 class="titulo-seccion" style="font-size: 24px; margin-bottom: 20px;"><i class="fas fa-lock"></i> Nueva Contraseña</h2>
            <div class="campo-form">
                <input type="password" placeholder="Contraseña actual" style="width: 100%; padding: 10px; margin-bottom: 15px;">
                <input type="password" placeholder="Nueva contraseña" style="width: 100%; padding: 10px; margin-bottom: 15px;">
                <input type="password" placeholder="Repetir nueva contraseña" style="width: 100%; padding: 10px; margin-bottom: 20px;">
                <button class="btn-editar-perfil" style="width: 100%;">Actualizar</button>
            </div>
        </div>
    </div>

</body>
</html>