<%@page import="java.util.List"%>
<%@page import="modelo.Articulo"%>
<%@page import="datos.ArticuloDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // CONEXIÓN REAL A LA BD: TORNEOS
    // ==========================================
    ArticuloDAO articuloDao = new ArticuloDAO();
    // Traemos los 4 últimos artículos de la categoría "TORNEO"
    List<Articulo> listaTorneos = articuloDao.obtenerPorCategoria("TORNEO", 4, 0); 
    
    // Separamos el primero para destacarlo
    Articulo torneoPrincipal = (listaTorneos != null && !listaTorneos.isEmpty()) ? listaTorneos.get(0) : null;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <title>Card Partner - Tournaments</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/tournamentsStyle.css?v=2">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="tournaments">
                <div class="barra_torneos">
                    <h2 class="titulo-seccion">TOURNAMENTS</h2>
                </div>
                
                <% if (torneoPrincipal != null) { 
                    String imgPortada = (torneoPrincipal.getPortada() != null) ? torneoPrincipal.getPortada() : "/img/logo.png";
                    String enlace = (torneoPrincipal.getEnlaceExterno() != null) ? torneoPrincipal.getEnlaceExterno() : "#";
                %>
                    <a href="<%= enlace %>" class="torneo-principal" style="text-decoration: none; color: inherit;" target="_blank">
                        <img src="${pageContext.request.contextPath}<%= imgPortada %>" alt="<%= torneoPrincipal.getTitulo() %>">
                        <div class="info-torneo">
                            <h3><%= torneoPrincipal.getTitulo() %></h3>
                            <p><%= torneoPrincipal.getDescripcion() %></p>
                        </div>
                    </a>
                <% } else { %>
                    <p style="color: white; text-align: center;">No hay torneos anunciados en este momento.</p>
                <% } %>

                <section class="lista-torneos">
                    <%
                        if (listaTorneos != null && listaTorneos.size() > 1) {
                            for (int i = 1; i < listaTorneos.size(); i++) {
                                Articulo t = listaTorneos.get(i);
                                String imgList = (t.getPortada() != null) ? t.getPortada() : "/img/logo.png";
                                String enlaceList = (t.getEnlaceExterno() != null) ? t.getEnlaceExterno() : "#";
                    %>
                            <a href="<%= enlaceList %>" class="torneo" style="text-decoration: none; color: inherit;" target="_blank">
                                <img src="${pageContext.request.contextPath}<%= imgList %>" alt="<%= t.getTitulo() %>">
                                <div>
                                    <h4><%= t.getTitulo() %></h4>
                                    <p><%= t.getDescripcion() %></p>
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