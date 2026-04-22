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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/guidesStyle.css?v=<%= System.currentTimeMillis() %>">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="guides">
                <div class="barra_guias">
                    <h2 class="titulo-seccion">STRATEGY GUIDES</h2>
                </div>
                
                <section class="galeria-guias">
                    <%
                        if (listaGuias != null && !listaGuias.isEmpty()) {
                            for (Articulo g : listaGuias) {
                                
                                // 1. LIMPIEZA Y LÓGICA DE PROXY PARA CADA GUÍA
                                String imgRaw = (g.getPortada() != null) ? g.getPortada().trim() : "";
                                String srcFinal;
                                
                                // Verificamos si la imagen es una URL externa
                                if (!imgRaw.isEmpty() && imgRaw.toLowerCase().startsWith("http")) {
                                    srcFinal = "https://wsrv.nl/?url=" + imgRaw;
                                } else {
                                    // Si es local o vacía, usamos el logo o la ruta relativa
                                    srcFinal = request.getContextPath() + (imgRaw.isEmpty() ? "/img/logo.png" : imgRaw);
                                }
                                
                                String enlace = (g.getEnlaceExterno() != null) ? g.getEnlaceExterno() : "#";
                    %>
                            <a href="<%= enlace %>" class="guia-card" style="text-decoration: none; color: inherit;" target="_blank">
                                <div class="contenedor-img-guia">
                                    <img src="<%= srcFinal %>" 
                                         alt="<%= g.getTitulo() %>"
                                         referrerpolicy="no-referrer"
                                         onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
                                </div>
                                <div class="info-guia">
                                    <span class="badge-categoria">GUÍA</span>
                                    <h3><%= g.getTitulo() %></h3>
                                    <p><%= g.getDescripcion() %></p>
                                    <span class="leer-mas">Leer guía completa <i class="fas fa-external-link-alt"></i></span>
                                </div>
                            </a>
                    <%
                            }
                        } else {
                    %>
                        <p style="color: white; text-align: center; width: 100%; grid-column: 1 / -1;">
                            No hay guías estratégicas disponibles en este momento.
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