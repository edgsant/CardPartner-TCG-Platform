<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // 1. RECUPERAR LA LISTA (El Servlet usa "listaMazosComunidad")
    List<Mazo> listaMazos = (List<Mazo>) request.getAttribute("listaMazosComunidad");
    
    // 2. ESCUDO: Si alguien intenta entrar directo, redirigimos al Servlet
    if (listaMazos == null) {
        response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=listar&tipo_mazo=comunidad");
        return; 
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Card Partner - Community Decks</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/communityDeckStyle.css?v=10">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-mazos">
                <div class="barra-busqueda">
                    <h2 class="titulo-seccion">COMMUNITY DECKS</h2>
                    <div class="acciones-derecha">
                        <div class="filtros">
                            <input type="text" placeholder="Buscar líder, color o creador...">
                            <a href="#modal-filtros" class="btn-filtro">Filtros ⚙️</a>
                        </div>
                        <a href="${pageContext.request.contextPath}/Views/deck-builder.jsp" class="btn-crear">+ Crear Mazo</a>
                    </div>
                </div>

                <h3 class="subtitulo-mazos">Mazos creados por los jugadores</h3>
                <section class="galeria-mazos">
                    <%
                        if (!listaMazos.isEmpty()) {
                            for(Mazo m : listaMazos) {
                                Carta lider = m.getCartaLider();
                                
                                // --- LÓGICA DE IMAGEN ---
                                String imgRaw = (lider != null) ? lider.getImagen() : "";
                                String srcFinal;
                                if (imgRaw == null || imgRaw.trim().isEmpty()) {
                                    srcFinal = request.getContextPath() + "/img/logo.png";
                                } else if (imgRaw.toLowerCase().startsWith("http")) {
                                    srcFinal = imgRaw;
                                } else {
                                    srcFinal = request.getContextPath() + "/img/" + imgRaw;
                                }

                                String colorLider = (lider != null && lider.getColor() != null) ? lider.getColor() : "Desconocido";
                    %>
                        <%-- IMPORTANTE: El enlace debe ir al CatalogoMazosServlet --%>
                        <a href="${pageContext.request.contextPath}/CatalogoMazosServlet?accion=detalle&id=<%= m.getIdMazo() %>" class="mazo-item">
                            <div class="mazo-imagen">
                                <img src="<%= srcFinal %>" alt="Líder">
                            </div>
                            <div class="mazo-info">
                                <h3><%= m.getNombreMazo() %></h3>
                                <p class="mazo-meta">Colores: <%= colorLider %></p>
                                <p class="mazo-autor">ID Creador: <%= m.getIdUsuarioCreador() %></p>
                                <span class="mazo-tipo"><%= m.getDescripcion() %></span>
                            </div>
                        </a>
                    <%
                            }
                        } else {
                    %>
                        <p style="color: white; text-align: center; width: 100%;">No hay mazos en la base de datos.</p>
                    <%
                        }
                    %>
                </section>
            </main>
        </div>
    </div>
</body>
</html>