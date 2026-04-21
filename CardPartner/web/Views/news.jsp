<%@page import="java.util.List"%>
<%@page import="modelo.Articulo"%>
<%@page import="datos.ArticuloDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // CONEXIÓN REAL A LA BD: NOTICIAS
    // ==========================================
    ArticuloDAO articuloDao = new ArticuloDAO();
    List<Articulo> listaNoticias = articuloDao.obtenerPorCategoria("NOTICIA", 4, 0); // Pedimos las 4 últimas
    
    // Separamos la primera noticia (principal) de las demás
    Articulo noticiaPrincipal = (listaNoticias != null && !listaNoticias.isEmpty()) ? listaNoticias.get(0) : null;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <title>Card Partner - News</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/newsStyle.css?v=2">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="news">
                <div class="barra_news">
                    <h2 class="titulo-seccion">NEWS</h2>
                </div>
                
                <% if (noticiaPrincipal != null) { 
                    String imgPortada = (noticiaPrincipal.getPortada() != null) ? noticiaPrincipal.getPortada() : "/img/logo.png";
                    String enlace = (noticiaPrincipal.getEnlaceExterno() != null) ? noticiaPrincipal.getEnlaceExterno() : "#";
                %>
                    <a href="<%= enlace %>" class="noticia-principal" style="text-decoration: none; color: inherit;" target="_blank">
                        <img src="${pageContext.request.contextPath}<%= imgPortada %>" alt="<%= noticiaPrincipal.getTitulo() %>">
                        <div class="info-principal">
                            <h3><%= noticiaPrincipal.getTitulo() %></h3>
                            <p><%= noticiaPrincipal.getDescripcion() %></p>
                        </div>
                    </a>
                <% } else { %>
                    <p style="color: white; text-align: center;">No hay noticias publicadas en este momento.</p>
                <% } %>

                <section class="lista-noticias">
                    <%
                        // Bucle para el resto de noticias (desde la segunda en adelante)
                        if (listaNoticias != null && listaNoticias.size() > 1) {
                            for (int i = 1; i < listaNoticias.size(); i++) {
                                Articulo n = listaNoticias.get(i);
                                String imgList = (n.getPortada() != null) ? n.getPortada() : "/img/logo.png";
                                String enlaceList = (n.getEnlaceExterno() != null) ? n.getEnlaceExterno() : "#";
                    %>
                            <a href="<%= enlaceList %>" class="noticia" style="text-decoration: none; color: inherit;" target="_blank">
                                <img src="${pageContext.request.contextPath}<%= imgList %>" alt="<%= n.getTitulo() %>">
                                <div>
                                    <h4><%= n.getTitulo() %></h4>
                                    <p><%= n.getDescripcion() %></p>
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