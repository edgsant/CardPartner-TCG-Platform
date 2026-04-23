<%@page import="java.util.List"%>
<%@page import="modelo.Articulo"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Recibimos los datos empaquetados por el Servlet
    List<Articulo> listaNoticias = (List<Articulo>) request.getAttribute("articulos");
    
    // Si entran directamente al JSP sin pasar por el Servlet, los redirigimos
    if (listaNoticias == null) {
        response.sendRedirect(request.getContextPath() + "/ArticuloServlet?tipo=NOTICIA");
        return;
    }
    Articulo noticiaPrincipal = (!listaNoticias.isEmpty()) ? listaNoticias.get(0) : null;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Card Partner - News</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/newsStyle.css?v=<%= System.currentTimeMillis() %>">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            <jsp:include page="/components/cabecera.jsp" />

            <main class="news">
                <div class="barra_news">
                    <h2 class="titulo-seccion">NEWS</h2>
                </div>
                
                <%-- SECCIÓN NOTICIA PRINCIPAL --%>
                <% if (noticiaPrincipal != null) { 
                    // Limpiamos la cadena de posibles espacios en blanco
                    String imgRaw = (noticiaPrincipal.getPortada() != null) ? noticiaPrincipal.getPortada().trim() : "";
                    String srcFinal;
                    
                    if (!imgRaw.isEmpty() && imgRaw.toLowerCase().startsWith("http")) {
                        // IGUAL QUE EN CARDS: Proxy para saltar el hotlinking
                        srcFinal = "https://wsrv.nl/?url=" + imgRaw;
                    } else {
                        // Imagen por defecto o local
                        srcFinal = request.getContextPath() + "/img/logo.png";
                    }

                    String enlace = (noticiaPrincipal.getEnlaceExterno() != null) ? noticiaPrincipal.getEnlaceExterno() : "#";
                %>
                    <a href="<%= enlace %>" class="noticia-principal" target="_blank" style="text-decoration: none; color: inherit;">
                        <img src="<%= srcFinal %>" 
                             alt="<%= noticiaPrincipal.getTitulo() %>" 
                             referrerpolicy="no-referrer"
                             onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
                        <div class="info-principal">
                            <h3><%= noticiaPrincipal.getTitulo() %></h3>
                            <p><%= noticiaPrincipal.getDescripcion() %></p>
                        </div>
                    </a>
                <% } %>

                <%-- SECCIÓN LISTA DE NOTICIAS SECUNDARIAS --%>
                <section class="lista-noticias">
                    <%
                        if (listaNoticias != null && listaNoticias.size() > 1) {
                            for (int i = 1; i < listaNoticias.size(); i++) {
                                Articulo n = listaNoticias.get(i);
                                // Limpiamos la URL de la lista
                                String imgRawList = (n.getPortada() != null) ? n.getPortada().trim() : "";
                                String srcList;
                                
                                if (!imgRawList.isEmpty() && imgRawList.toLowerCase().startsWith("https")) {
                                    srcList = "https://wsrv.nl/?url=" + imgRawList;
                                } else {
                                    srcList = request.getContextPath() + "/img/logo.png";
                                }

                                String enlaceList = (n.getEnlaceExterno() != null) ? n.getEnlaceExterno() : "#";
                    %>
                            <a href="<%= enlaceList %>" class="noticia" target="_blank" style="text-decoration: none; color: inherit;">
                                <img src="<%= srcList %>" 
                                     alt="<%= n.getTitulo() %>" 
                                     referrerpolicy="no-referrer"
                                     onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
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