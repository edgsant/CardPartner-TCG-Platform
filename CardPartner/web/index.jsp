<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page import="modelo.Articulo"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // 1. ESCUDO MVC: Protección de la vista
    // ==========================================
    // Si la vista no recibe los datos, significa que han entrado directo a index.jsp.
    // Redirigimos obligatoriamente al Servlet para que hable con la BD.
    if (request.getAttribute("mazosRecomendados") == null) {
        response.sendRedirect(request.getContextPath() + "/InicioServlet");
        return;
    }

    // 2. Recogemos las listas dinámicas del Servlet
    List<Mazo> mazosRecomendados = (List<Mazo>) request.getAttribute("mazosRecomendados");
    List<Articulo> guiasRecomendadas = (List<Articulo>) request.getAttribute("guiasRecomendadas");
    
    // Sacamos la primera guía si es que existe
    Articulo guiaPrincipal = (guiasRecomendadas != null && !guiasRecomendadas.isEmpty()) ? guiasRecomendadas.get(0) : null;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CardPartner - Inicio</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/indexStyle.css?v=15">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">

            <jsp:include page="/components/cabecera.jsp" />

            <div class="contenido">
                
                <div class="noticias">
                    <a href="https://optcgsim.com/" class="promo-link" target="_blank">
                        <img src="${pageContext.request.contextPath}/img/Simulador.jpg" alt="Banner Principal">
                    </a>
                </div>

                <div class="columnas-inferiores">
                    
                    <div class="guias">
                        <h2 class="titulo-mazos">Última Guía de Estrategia</h2>
                        <div class="contenido-mazos" style="justify-content: center; align-items: center;">
                            <% if (guiaPrincipal != null) { 
                                // Lógica de proxy para la portada de la guía
                                String imgRawGuia = (guiaPrincipal.getPortada() != null) ? guiaPrincipal.getPortada().trim() : "";
                                String srcGuia = "";
                                if (!imgRawGuia.isEmpty() && imgRawGuia.toLowerCase().startsWith("http")) {
                                    srcGuia = "https://wsrv.nl/?url=" + imgRawGuia;
                                } else {
                                    srcGuia = request.getContextPath() + (imgRawGuia.isEmpty() ? "/img/tutorial.jpg" : imgRawGuia);
                                }
                            %>
                            <a href="<%= guiaPrincipal.getEnlaceExterno() %>" class="item-guia" target="_blank">
                                <img src="<%= srcGuia %>" alt="<%= guiaPrincipal.getTitulo() %>" referrerpolicy="no-referrer">
                                <span class="titulo-guia-index"><%= guiaPrincipal.getTitulo() %></span>
                            </a>
                            <% } else { %>
                            <!-- Fallback por si no hay guías en la base de datos -->
                            <a href="${pageContext.request.contextPath}/ArticuloServlet?tipo=GUIA" class="item-guia">
                                <img src="${pageContext.request.contextPath}/img/tutorial.jpg" alt="Imagen Guía">
                                <span class="titulo-guia-index">Introducción al Juego</span>
                            </a>
                            <% } %>
                        </div>
                        <div class="footer-mazos">
                            <a href="${pageContext.request.contextPath}/ArticuloServlet?tipo=GUIA" class="btn-ver-mas">Ver todas</a>
                        </div>
                    </div>

                    <div class="mazos">
                        <h2 class="titulo-mazos">Nuevos Mazos de la Comunidad</h2>
                        <div class="contenido-mazos">
                            
                            <%
                                if (mazosRecomendados != null && !mazosRecomendados.isEmpty()) {
                                    for(Mazo m : mazosRecomendados) {
                                        Carta lider = m.getCartaLider();
                                        
                                        // Lógica de Proxy de imagen para los mazos del Index
                                        String imgRaw = (lider != null && lider.getImagen() != null) ? lider.getImagen().trim() : "";
                                        String srcFinal = "";
                                        
                                        if (!imgRaw.isEmpty() && imgRaw.toLowerCase().startsWith("http")) {
                                            srcFinal = "https://images.weserv.nl/?url=" + imgRaw;
                                        } else if (imgRaw.isEmpty()) {
                                            srcFinal = request.getContextPath() + "/img/logo.png";
                                        } else {
                                            srcFinal = request.getContextPath() + "/img/" + imgRaw;
                                        }
                                        String nombreLider = (lider != null) ? lider.getNombreCarta() : "Desconocido";
                            %>
                            
                            <a href="${pageContext.request.contextPath}/CatalogoMazosServlet?accion=detalle&id=<%= m.getIdMazo() %>" class="item-mazo">
                                <img src="<%= srcFinal %>" alt="Líder: <%= nombreLider %>" loading="lazy" referrerpolicy="no-referrer" onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
                                <span><%= m.getNombreMazo() %></span>
                            </a>
                            
                            <% 
                                    }
                                } else { 
                            %>
                                <p style="color: #666; font-style: italic; width: 100%; text-align: center; padding: 20px;">No hay mazos recientes publicados.</p>
                            <% } %>

                        </div>
                        <div class="footer-mazos">
                            <a href="${pageContext.request.contextPath}/CatalogoMazosServlet?accion=listar&tipo_mazo=comunidad" class="btn-ver-mas">Ver todos</a>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>

</body>
</html>