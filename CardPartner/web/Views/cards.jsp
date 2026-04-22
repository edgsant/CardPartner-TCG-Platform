<%@page import="java.util.List"%>
<%@page import="modelo.Carta"%>
<%@page import="datos.CartaDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    CartaDAO cartaDao = new CartaDAO();
    List<Carta> listaCartas = cartaDao.filtrarCartas(null, null, null, null, 20, 0);
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Card Partner - Cartas</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cardsStyle.css?v=<%= System.currentTimeMillis()%>">
        <style>
            .carta-item img {
                width: 100%;
                display: block;
                min-height: 250px;
                background-color: #333;
                object-fit: contain;
            }
            /* Si no carga nada, esto ayuda a ver si el contenedor existe */
            .contenedor-img {
                background: #222;
                min-height: 250px;
            }
        </style>
    </head>
    <body>

        <div class="fondo-rosa">
            <div class="contenedor-principal">
                <jsp:include page="/components/cabecera.jsp" />

                <main class="contenido-cartas">
                    <div class="barra-busqueda">
                        <h2 class="titulo-seccion">CARDS</h2>
                    </div>

                    <section class="galeria-cartas">
                        <%
                            if (listaCartas != null && !listaCartas.isEmpty()) {
                                for (Carta c : listaCartas) {
                                    String imgRaw = (c.getImagen() != null) ? c.getImagen().trim() : "";
                                    String srcFinal = "";

                                    if (!imgRaw.isEmpty() && imgRaw.toLowerCase().startsWith("http")) {
                                        // Usamos otro proxy alternativo por si wsrv.nl falla
                                        // Este proxy (image.weserv.nl) es muy robusto
                                        srcFinal = "https://images.weserv.nl/?url=" + imgRaw;
                                    } else {
                                        srcFinal = request.getContextPath() + "/img/logo.png";
                                    }
                        %>
                        <a href="${pageContext.request.contextPath}/Views/card-view.jsp?id=<%= c.getIdCarta()%>" class="carta-item">
                            <div class="contenedor-img">
                                <img src="<%= srcFinal %>" 
                                     alt="<%= c.getNombreCarta() %>" 
                                     loading="lazy"
                                     referrerpolicy="no-referrer"
                                     onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
                            </div>

                            <div class="carta-info">
                                <h3><%= c.getNombreCarta() %></h3>
                                <p><strong><%= c.getIdCarta() %></strong> | <%= c.getTipo() %></p>
                            </div>
                        </a>
                        <%
                                }
                            } else {
                        %>
                        <p style="color: white; text-align: center; width: 100%;">No hay cartas disponibles.</p>
                        <%
                            }
                        %>
                    </section>
                </main>
            </div>
        </div>
    </body>
</html>