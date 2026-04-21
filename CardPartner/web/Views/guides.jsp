<%@page import="java.util.List"%>
<%@page import="modelo.Articulo"%>
<%@page import="datos.ArticuloDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // CONEXIÓN REAL A LA BD: GUÍAS
    // ==========================================
    ArticuloDAO articuloDao = new ArticuloDAO();
    List<Articulo> listaGuias = articuloDao.obtenerPorCategoria("GUIA", 4, 0); 
    
    Articulo guiaPrincipal = (listaGuias != null && !listaGuias.isEmpty()) ? listaGuias.get(0) : null;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <title>Card Partner - Guides</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/guidesStyle.css?v=2">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="guides">
                <div class="barra_guides">
                    <h2 class="titulo-seccion">GUIDES</h2>
                </div>
                
                <% if (guiaPrincipal != null) { 
                    String imgPortada = (guiaPrincipal.getPortada() != null) ? guiaPrincipal.getPortada() : "/img/logo.png";
                    String enlace = (guiaPrincipal.getEnlaceExterno() != null) ? guiaPrincipal.getEnlaceExterno() : "#";
                %>
                    <a href="<%= enlace %>" target="_blank" class="guia-principal" style="text-decoration: none; color: inherit;">
                        <img src="${pageContext.request.contextPath}<%= imgPortada %>" alt="<%= guiaPrincipal.getTitulo() %>">
                        <div class="info-principal">
                            <h3><%= guiaPrincipal.getTitulo() %></h3>
                            <p><%= guiaPrincipal.getDescripcion() %></p>
                        </div>
                    </a>
                <% } else { %>
                    <p style="color: white; text-align: center;">No hay guías publicadas en este momento.</p>
                <% } %>
                
                <section class="lista-guias">
                    <%
                        if (listaGuias != null && listaGuias.size() > 1) {
                            for (int i = 1; i < listaGuias.size(); i++) {
                                Articulo g = listaGuias.get(i);
                                String imgList = (g.getPortada() != null) ? g.getPortada() : "/img/logo.png";
                                String enlaceList = (g.getEnlaceExterno() != null) ? g.getEnlaceExterno() : "#";
                    %>
                            <a href="<%= enlaceList %>" class="guia" style="text-decoration: none; color: inherit;" target="_blank">
                                <img src="${pageContext.request.contextPath}<%= imgList %>" alt="<%= g.getTitulo() %>">
                                <div>
                                    <h4><%= g.getTitulo() %></h4>
                                    <p><%= g.getDescripcion() %></p>
                                </div>
                            </a>
                    <%
                            }
                        }
                    %>
                </section>
            </main>

        </div>
    </div>
</body>
</html>