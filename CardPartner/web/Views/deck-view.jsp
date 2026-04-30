<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page import="modelo.ContieneCarta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // 1. RECOGER LOS DATOS QUE ENVÍA EL SERVLET (Nada de DAOs aquí)
    Mazo mazoActual = (Mazo) request.getAttribute("mazoParaLaVista");
    List<ContieneCarta> listaCartas = (List<ContieneCarta>) request.getAttribute("cartasDelMazo");

    // 2. ESCUDO DE SEGURIDAD: Si intentan entrar a la vista sin pasar por el Servlet
    if (mazoActual == null) {
        response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=listar");
        return;
    }

    Carta liderActual = mazoActual.getCartaLider();

    // 3. LÓGICA DE PROXY DE IMAGEN (Esto sí está bien en la vista)
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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/deckViewStyle.css?v=20">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <style>
            /* Fix de deformación de cartas */
            .deck-grid {
                display: grid;
                grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
                gap: 20px;
                padding: 20px 0;
            }
            .carta-item {
                position: relative;
                width: 100%;
                aspect-ratio: 2 / 2.8;
                background: rgba(0,0,0,0.1);
                border-radius: 10px;
            }
            .carta-item img {
                width: 100%;
                height: 100%;
                display: block;
                object-fit: contain;
                border-radius: 10px;
            }
            .badge-cantidad {
                position: absolute;
                bottom: 8px;
                right: 8px;
                background: rgba(0, 0, 0, 0.85);
                color: #ffcc00;
                padding: 2px 10px;
                border-radius: 6px;
                font-weight: bold;
                font-size: 1.1rem;
                border: 1px solid #ffcc00;
                z-index: 5;
            }
            .lider-seccion img {
                width: 100%;
                max-width: 300px;
                border-radius: 15px;
                box-shadow: 0 10px 20px rgba(0,0,0,0.3);
            }
        </style>
    </head>
    <body>
        <div class="fondo-rosa">
            <div class="contenedor-principal">
                <jsp:include page="/components/cabecera.jsp" />

                <main class="contenido-detalle">
                    <h2 class="titulo-mazo-grande"><%= mazoActual.getNombreMazo()%></h2>

                    <div class="deck-header">
                        <div class="lider-seccion">
                            <img src="<%= srcFinal%>" 
                                 alt="Líder" 
                                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
                            <p><strong>Líder:</strong> <%= nombreLider%> (<%= mazoActual.getIdCartaLider()%>)</p>
                        </div>

                        <div class="descripcion-seccion">
                            <h3>DESCRIPCIÓN DEL MAZO</h3>
                            <p><%= mazoActual.getDescripcion()%></p>
                            <p><strong>Privacidad:</strong> <%= (mazoActual.isEsPublico()) ? "Público" : "Privado"%></p>

                            <div class="acciones-mazo">
                                <a href="${pageContext.request.contextPath}/UsuarioServlet?accion=guardar_favorito&id=<%= mazoActual.getIdMazo()%>" class="btn-accion-mazo" style="text-decoration: none;">
                                    <i class="fas fa-heart"></i> Guardar Mazo
                                </a>
                                <a href="${pageContext.request.contextPath}/DeckBuilderServlet?accion=copiar&id=<%= mazoActual.getIdMazo()%>" class="btn-accion-mazo" style="text-decoration: none;">
                                    <i class="fas fa-edit"></i> Copiar / Editar
                                </a>
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

                                    // Omitir al líder en la lista de cartas inferior
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
                        <%
                            }
                        %>
                    </div>
                </main>
            </div>
        </div>
    </body>
</html>