<%@page import="java.util.List"%>
<%@page import="modelo.Articulo"%>
<%@page import="datos.ArticuloDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // CONEXIÓN REAL A LA BD: GUÍAS
    // ==========================================
    ArticuloDAO articuloDao = new ArticuloDAO();
    // Traemos los últimos artículos de la categoría "GUIA"
    // He puesto un límite de 10, puedes ajustarlo según tu diseño
    List<Articulo> listaGuias = articuloDao.obtenerPorCategoria("GUIA", 10, 0);
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
        <title>Card Partner - Guides</title>
        <%-- Mantenemos el sistema de cache busting para el CSS --%>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/guidesStyle.css?v=<%= System.currentTimeMillis()%>">
    </head>
    <body>
        <div class="fondo-rosa">
            <div class="contenedor-principal">

                <jsp:include page="/components/cabecera.jsp" />

                <main class="guides">
                    <div class="barra_guides">
                        <h2 class="titulo-seccion">STRATEGY GUIDES</h2>
                    </div>

                    <%
                        if (listaGuias != null && !listaGuias.isEmpty()) {
                            // 1. Extraemos la primera guía para el formato "Principal"
                            Articulo principal = listaGuias.get(0);
                            String imgPri = (principal.getPortada() != null && principal.getPortada().trim().startsWith("http"))
                                    ? "https://wsrv.nl/?url=" + principal.getPortada().trim()
                                    : request.getContextPath() + "/img/logo.png";
                    %>
                    <a href="<%= principal.getEnlaceExterno()%>" class="guia-principal" target="_blank" style="text-decoration: none; color: inherit;">
                        <img src="<%= imgPri%>" alt="<%= principal.getTitulo()%>" referrerpolicy="no-referrer">
                        <div class="info-principal">
                            <h3><%= principal.getTitulo()%></h3>
                            <p><%= principal.getDescripcion()%></p>
                        </div>
                    </a>

                    <section class="lista-guias">
                        <%
                            for (int i = 1; i < listaGuias.size(); i++) {
                                Articulo g = listaGuias.get(i);
                                String imgSec = (g.getPortada() != null && g.getPortada().trim().startsWith("http"))
                                        ? "https://wsrv.nl/?url=" + g.getPortada().trim()
                                        : request.getContextPath() + "/img/logo.png";
                        %>
                        <a href="<%= g.getEnlaceExterno()%>" class="guia" target="_blank" style="text-decoration: none; color: inherit;">
                            <img src="<%= imgSec%>" alt="<%= g.getTitulo()%>" referrerpolicy="no-referrer">
                            <div>
                                <h4><%= g.getTitulo()%></h4>
                                <p><%= g.getDescripcion()%></p>
                            </div>
                        </a>
                        <% } %>
                    </section>
                    <%
                    } else {
                    %>
                    <p style="color: white; text-align: center;">No hay guías disponibles en este momento.</p>
                    <% }%>
                </main>

            </div>
        </div>
    </body>
</html>