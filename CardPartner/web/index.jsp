<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page import="datos.MazoDAO"%>
<%@page import="datos.CartaDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // CONEXIÓN REAL A LA BASE DE DATOS
    // ==========================================
    
    MazoDAO mazoDao = new MazoDAO();
    CartaDAO cartaDao = new CartaDAO();
    
    // Pedimos a MariaDB los 3 mazos públicos más recientes (límite 3, salto 0)
    List<Mazo> mazosRecomendados = mazoDao.filtrarMazosPublicos(null, null, null, null, 3, 0);
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CardPartner - Inicio</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/indexStyle.css?v=12">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">

            <jsp:include page="/components/cabecera.jsp" />

            <div class="contenido">
                
                <div class="noticias">
                    <button class="flecha">←</button>
                    <a href="https://optcgsim.com/" class="promo-link" target="_blank">
                        <img src="${pageContext.request.contextPath}/img/Simulador.jpg" alt="Banner Principal">
                    </a>
                    <button class="flecha">→</button>
                </div>

                <div class="columnas-inferiores">
                    
                    <div class="guias">
                        <a href="https://www.youtube.com/watch?v=TeEwfKKWmOY" class="enlace-guia" target="_blank">
                            <h2>GUÍAS</h2>
                            <div class="contenedor-carrusel-guia">
                                <span class="flecha-guia">←</span>
                                <img src="${pageContext.request.contextPath}/img/tutorial.jpg" alt="Imagen Guía">
                                <span class="flecha-guia">→</span>
                            </div>
                        </a>
                    </div>

                    <div class="mazos">
                        <h2 class="titulo-mazos">Nuevos Mazos de la Comunidad</h2>
                        <div class="contenido-mazos">
                            <%
                                // Comprobamos si la base de datos nos ha devuelto mazos
                                if (mazosRecomendados != null && !mazosRecomendados.isEmpty()) {
                                    for(Mazo m : mazosRecomendados) {
                                        // Usamos el CartaDAO para buscar la carta líder de este mazo concreto
                                        Carta lider = cartaDao.obtenerCartaPorId(m.getIdCartaLider());
                                        
                                        // Si la carta tiene imagen en la BD, la usamos. Si no, ponemos una por defecto.
                                        String imgLider = (lider != null && lider.getImagen() != null) ? lider.getImagen() : "/img/logo.png";
                            %>
                                    <a href="${pageContext.request.contextPath}/Views/deck-view.jsp?id=<%= m.getIdMazo() %>" class="item-mazo">
                                        <img src="${pageContext.request.contextPath}<%= imgLider %>" alt="<%= m.getNombreMazo() %>">
                                        <span><%= m.getNombreMazo() %></span>
                                    </a>
                            <%
                                    }
                                } else {
                            %>
                                    <p style="color: white; text-align: center; width: 100%;">Aún no hay mazos creados.</p>
                            <%
                                }
                            %>
                        </div>
                        <div class="footer-mazos">
                            <a href="${pageContext.request.contextPath}/Views/community-deck.jsp" class="btn-ver-mas">Ver todos</a>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>

</body>
</html>