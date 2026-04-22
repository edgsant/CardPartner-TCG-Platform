<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // 1. RECUPERAR LA LISTA (El Servlet CatalogoMazosServlet usa este nombre de atributo)
    List<Mazo> listaMazos = (List<Mazo>) request.getAttribute("listaMazosComunidad");
    
    // 2. ESCUDO: Si alguien intenta entrar directo al JSP, redirigimos al Servlet
    if (listaMazos == null) {
        response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=listar&tipo_mazo=comunidad");
        return; 
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    
    <title>Card Partner - Community Decks</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/communityDeckStyle.css?v=13">
    
    <style>
        /* Aseguramos que el contenedor de la imagen tenga consistencia visual */
        .mazo-imagen {
            min-height: 200px;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: rgba(0,0,0,0.05); /* Un fondo sutil por si tarda en cargar */
            overflow: hidden;
        }
        .mazo-imagen img {
            width: 100%;
            height: auto;
            display: block;
            object-fit: contain;
        }
    </style>
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
                            <a href="#modal-filtros" class="btn-filtro" style="text-decoration: none; display: inline-block;">Filtros ⚙️</a>
                        </div>
                        <a href="${pageContext.request.contextPath}/Views/deck-builder.jsp" class="btn-crear">+ Crear Mazo</a>
                    </div>
                </div>

                <h3 class="subtitulo-mazos">Mazos creados por los jugadores</h3>
                
                <section class="galeria-mazos">
                    
                    <%
                        if (listaMazos != null && !listaMazos.isEmpty()) {
                            for(Mazo m : listaMazos) {
                                
                                Carta lider = m.getCartaLider();
                                String imgRaw = (lider != null && lider.getImagen() != null) ? lider.getImagen().trim() : "";
                                String srcFinal = "";

                                // ==========================================
                                // LÓGICA DE PROXY (Igual que en cartas.jsp)
                                // ==========================================
                                if (!imgRaw.isEmpty() && imgRaw.toLowerCase().startsWith("http")) {
                                    // Usamos el proxy de weserv.nl para saltar bloqueos de la web de referencia
                                    srcFinal = "https://images.weserv.nl/?url=" + imgRaw;
                                } else if (imgRaw.isEmpty()) {
                                    srcFinal = request.getContextPath() + "/img/logo.png";
                                } else {
                                    // Imagen local
                                    srcFinal = request.getContextPath() + "/img/" + imgRaw;
                                }

                                String nombreLider = (lider != null) ? lider.getNombreCarta() : "Desconocido";
                                String colorLider = (lider != null && lider.getColor() != null) ? lider.getColor() : "Varios";
                    %>
                        <a href="${pageContext.request.contextPath}/CatalogoMazosServlet?accion=detalle&id=<%= m.getIdMazo() %>" class="mazo-item">
                            <div class="mazo-imagen">
                                <img src="<%= srcFinal %>" 
                                     alt="Líder: <%= nombreLider %>" 
                                     loading="lazy"
                                     referrerpolicy="no-referrer"
                                     onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
                            </div>
                            <div class="mazo-info">
                                <h3><%= m.getNombreMazo() %></h3>
                                <p class="mazo-meta"><strong>Colores:</strong> <%= colorLider %></p>
                                <p class="mazo-autor"><strong>Creador:</strong> Usuario #<%= m.getIdUsuarioCreador() %></p>
                                <span class="mazo-tipo"><%= (m.getDescripcion() != null) ? m.getDescripcion() : "" %></span>
                            </div>
                        </a>
                    <%
                            }
                        } else {
                    %>
                        <p style="color: white; text-align: center; width: 100%; grid-column: 1 / -1;">
                            No se han encontrado mazos en la base de datos.
                        </p>
                    <%
                        }
                    %>
                </section>
            </main>
        </div>
    </div>
</body>
</html>