<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page import="modelo.ContieneCarta"%>
<%@page import="modelo.Comentario"%>
<%@page import="modelo.Valoracion"%>
<%@page import="controladores.GeminiAPI"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // 1. RECOGER LOS DATOS QUE ENVÍA EL SERVLET
    Mazo mazoActual = (Mazo) request.getAttribute("mazoParaLaVista");
    List<ContieneCarta> listaCartas = (List<ContieneCarta>) request.getAttribute("cartasDelMazo");
    modelo.Usuario usuarioLogueado = (modelo.Usuario) session.getAttribute("usuarioActivo");
    boolean esMiMazo = (usuarioLogueado != null && mazoActual.getIdUsuarioCreador() == usuarioLogueado.getIdUsuario());
    
    // ESCUDO DE SEGURIDAD
    if (mazoActual == null) {
        response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=listar");
        return;
    }
    Carta liderActual = mazoActual.getCartaLider();

    // LÓGICA DE PROXY DE IMAGEN
    String imgLiderRaw = (liderActual != null && liderActual.getImagen() != null) ? liderActual.getImagen().trim() : "";
    String srcFinal = "";
    if (!imgLiderRaw.isEmpty() && imgLiderRaw.toLowerCase().startsWith("http")) {
        srcFinal = "https://images.weserv.nl/?url=" + imgLiderRaw;
    } else if (imgLiderRaw.isEmpty()) {
        srcFinal = request.getContextPath() + "/img/logo.png";
    } else {
        srcFinal = request.getContextPath() + imgLiderRaw;
    }
    String nombreLider = (liderActual != null) ? liderActual.getNombreCarta() : "Desconocido";

    // ----------------------------------------------------
    // RECOGER DATOS SOCIALES (Comentarios y Votos)
    // ----------------------------------------------------
    List<Comentario> comentarios = (List<Comentario>) request.getAttribute("comentariosMazo");
    Double notaMedia = (Double) request.getAttribute("notaMedia");
    Integer totalVotos = (Integer) request.getAttribute("totalVotos");
    Valoracion miVoto = (Valoracion) request.getAttribute("miVoto");

    if (notaMedia == null) notaMedia = 0.0;
    if (totalVotos == null) totalVotos = 0;
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="referrer" content="no-referrer">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
        <title>Card Partner - <%= mazoActual.getNombreMazo()%></title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/deckViewStyle.css?v=21">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    </head>
    <body>
        <div class="fondo-rosa">
            <div class="contenedor-principal">
                <jsp:include page="/components/cabecera.jsp" />
                <main class="contenido-detalle">
                    <h2 class="titulo-mazo-grande"><%= mazoActual.getNombreMazo()%></h2>
                    <div class="deck-header">
                        <div class="lider-seccion">
                            <img src="<%= srcFinal%>" alt="Líder" onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
                            <p><strong>Líder:</strong> <%= nombreLider%> (<%= mazoActual.getIdCartaLider()%>)</p>
                        </div>
                        <div class="descripcion-seccion">
                            <h3>DESCRIPCIÓN DEL MAZO</h3>
                            <p><%= mazoActual.getDescripcion()%></p>

                            <a href="?accion=detalle&id=<%= mazoActual.getIdMazo() %>&traducirMazo=true" style="font-size: 0.8rem; color: #e83e8c; text-decoration: none; display: inline-block; margin-bottom: 10px;">
                                <i class="fas fa-language"></i> Traducir estrategia al inglés (IA)
                            </a>
                            <% if ("true".equals(request.getParameter("traducirMazo"))) { %>
                                <div style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin-bottom: 15px; font-style: italic; border-left: 4px solid #e83e8c;">
                                    <%= GeminiAPI.traducir(mazoActual.getDescripcion(), "MAZO") %>
                                </div>
                            <% } %>

                            <p><strong>Privacidad:</strong> <%= (mazoActual.isEsPublico()) ? "Público" : "Privado"%></p>
                            
                            <!-- SECCIÓN VALORACIONES -->
                            <div class="social-rating">
                                <div>
                                    <strong>Nota Media:</strong> <span class="estrellas"><i class="fas fa-star"></i> <%= String.format("%.1f", notaMedia) %></span> 
                                    <span style="font-size: 0.8rem; color: #ccc;">(<%= totalVotos %> votos)</span>
                                </div>
                                <% if (usuarioLogueado != null) { %>
                                    <form action="${pageContext.request.contextPath}/ValoracionServlet" method="POST" class="form-votar">
                                        <input type="hidden" name="idMazo" value="<%= mazoActual.getIdMazo() %>">
                                        <select name="puntuacion" required>
                                            <option value="" disabled <%= (miVoto == null) ? "selected" : "" %>>Valora este mazo...</option>
                                            <option value="5" <%= (miVoto != null && miVoto.getPuntuacion() == 5.0) ? "selected" : "" %>>5 - ¡Increíble!</option>
                                            <option value="4" <%= (miVoto != null && miVoto.getPuntuacion() == 4.0) ? "selected" : "" %>>4 - Muy bueno</option>
                                            <option value="3" <%= (miVoto != null && miVoto.getPuntuacion() == 3.0) ? "selected" : "" %>>3 - Normal</option>
                                            <option value="2" <%= (miVoto != null && miVoto.getPuntuacion() == 2.0) ? "selected" : "" %>>2 - Flojo</option>
                                            <option value="1" <%= (miVoto != null && miVoto.getPuntuacion() == 1.0) ? "selected" : "" %>>1 - Malo</option>
                                        </select>
                                        <button type="submit" class="btn-social">Votar</button>
                                    </form>
                                <% } %>
                            </div>

                            <div class="acciones-mazo">
                                <a href="${pageContext.request.contextPath}/UsuarioServlet?accion=guardar_favorito&id=<%= mazoActual.getIdMazo()%>" class="btn-accion-mazo" style="text-decoration: none;">
                                    <i class="fas fa-heart"></i> Guardar Mazo
                                </a>
                                <% if (esMiMazo) {%>
                                <a href="${pageContext.request.contextPath}/DeckBuilderServlet?accion=editar&id=<%= mazoActual.getIdMazo()%>" class="btn-accion-mazo" style="text-decoration: none;">
                                    <i class="fas fa-edit"></i> Editar Mi Mazo
                                </a>
                                <a href="${pageContext.request.contextPath}/DeckBuilderServlet?accion=copiar&id=<%= mazoActual.getIdMazo()%>" class="btn-accion-mazo" style="text-decoration: none;">
                                    <i class="fas fa-copy"></i> Crear Copia
                                </a>
                                <% } else {%>
                                <a href="${pageContext.request.contextPath}/DeckBuilderServlet?accion=copiar&id=<%= mazoActual.getIdMazo()%>" class="btn-accion-mazo" style="text-decoration: none;">
                                    <i class="fas fa-copy"></i> Copiar Mazo
                                </a>
                                <% } %>
                                <a href="https://www.cardmarket.com/es/OnePiece" target="_blank" class="btn-accion-mazo" style="text-decoration: none;">
                                    <i class="fas fa-shopping-cart"></i> Comprar Cartas
                                </a>
                            </div>
                        </div>
                    </div>

                    <h3 class="titulo-cartas">LISTA DE CARTAS</h3>
                    <div class="deck-grid">
                        <%
                            if (listaCartas != null && !listaCartas.isEmpty()) {
                                for (ContieneCarta cc : listaCartas) {
                                    Carta c = cc.getCarta();
                                    if (c.getIdCarta().equals(mazoActual.getIdCartaLider())) {
                                        continue;
                                    }
                                    String imgCartaRaw = (c.getImagen() != null) ? c.getImagen().trim() : "";
                                    String srcCarta = "";
                                    if (!imgCartaRaw.isEmpty() && imgCartaRaw.toLowerCase().startsWith("http")) {
                                        srcCarta = "https://images.weserv.nl/?url=" + imgCartaRaw;
                                    } else {
                                        srcCarta = request.getContextPath() + (imgCartaRaw.isEmpty() ? "/img/logo.png" : imgCartaRaw);
                                    }
                        %>
                        <div class="carta-item">
                            <img src="<%= srcCarta%>" alt="<%= c.getNombreCarta()%>" loading="lazy"
                                  onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
                            <span class="badge-cantidad">x<%= cc.getCantidad()%></span>
                        </div>
                        <%
                                }
                            } else {
                        %>
                        <p style="color: white; grid-column: 1/-1; text-align: center;">No hay cartas en este mazo.</p>
                        <% } %>
                    </div>

                    <!-- SECCIÓN COMENTARIOS -->
                    <div class="seccion-comentarios">
                        <h3 style="border-bottom: 2px solid #e83e8c; padding-bottom: 10px;">Comentarios (<%= (comentarios != null) ? comentarios.size() : 0 %>)</h3>
                        
                        <% if (usuarioLogueado != null) { %>
                            <form action="${pageContext.request.contextPath}/ComentarioServlet" method="POST" class="form-comentar">
                                <input type="hidden" name="accion" value="crear">
                                <input type="hidden" name="idMazo" value="<%= mazoActual.getIdMazo() %>">
                                <textarea name="contenido" required placeholder="¿Qué opinas de este mazo? Escribe tu comentario aquí..."></textarea>
                                <button type="submit" class="btn-social">Publicar Comentario</button>
                            </form>
                        <% } else { %>
                            <p style="color: #ccc; font-style: italic;"><a href="${pageContext.request.contextPath}/Views/login.jsp" style="color: #e83e8c;">Inicia sesión</a> para dejar un comentario.</p>
                        <% } %>

                        <div class="lista-comentarios">
                            <% if (comentarios != null && !comentarios.isEmpty()) { 
                                for(Comentario c : comentarios) { 
                                    // Limpiamos la fecha para que no salgan los milisegundos feos
                                    String fechaLimpia = (c.getFechaCreacion() != null) ? c.getFechaCreacion().toString().substring(0, 16) : "";
                            %>
                                <div class="comentario-item">
                                    <div class="comentario-header" style="display: flex; align-items: center; gap: 10px; margin-bottom: 5px;">
                                        <img src="${pageContext.request.contextPath}/AvatarServlet?id=<%= c.getUsuarioAutor().getIdUsuario() %>" 
                                             alt="Avatar de <%= c.getUsuarioAutor().getApodoUsuario() %>" 
                                             style="width: 35px; height: 35px; border-radius: 50%; object-fit: cover; border: 1px solid #ccc; background-color: #fff;">
                                             
                                        <strong style="margin-right: auto;"><%= c.getUsuarioAutor().getApodoUsuario() %></strong>
                                        
                                        <span style="font-size: 0.85em; color: #777;"><%= fechaLimpia %></span>
                                    </div>
                                    <p class="comentario-texto"><%= c.getContenido() %></p>

                                    <a href="?accion=detalle&id=<%= mazoActual.getIdMazo() %>&traducirCom=<%= c.getIdComentario() %>" style="font-size: 0.75rem; color: #007bff; text-decoration: none;">
                                        <i class="fas fa-language"></i> Traducir
                                    </a>
                                    <% if (String.valueOf(c.getIdComentario()).equals(request.getParameter("traducirCom"))) { %>
                                        <div style="background: #e9ecef; padding: 10px; border-radius: 6px; margin-top: 5px; font-size: 0.9rem;">
                                            <%= GeminiAPI.traducir(c.getContenido(), "COMENTARIO") %>
                                        </div>
                                    <% } %>

                                    <%-- Si el comentario es mío, muestro botón de borrar --%>
                                    <% if (usuarioLogueado != null && usuarioLogueado.getIdUsuario() == c.getUsuarioAutor().getIdUsuario()) { %>
                                        <form action="${pageContext.request.contextPath}/ComentarioServlet" method="POST" onsubmit="return confirm('¿Seguro que quieres borrar tu comentario?');">
                                            <input type="hidden" name="accion" value="borrar">
                                            <input type="hidden" name="idMazo" value="<%= mazoActual.getIdMazo() %>">
                                            <input type="hidden" name="idComentario" value="<%= c.getIdComentario() %>">
                                            <button type="submit" class="btn-borrar-com"><i class="fas fa-trash"></i> Eliminar</button>
                                        </form>
                                    <% } %>
                                </div>
                            <%  }
                               } else { %>
                                <p style="color: #ccc; margin-top: 15px;">Aún no hay comentarios. ¡Sé el primero en opinar!</p>
                            <% } %>
                        </div>
                    </div>

                </main>
            </div>
        </div>
    </body>
</html>