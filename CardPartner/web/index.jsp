<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // Recogemos la lista del Servlet por si acaso, 
    // aunque para los 3 mazos de abajo usaremos tus links fijos.
    List<Mazo> mazosRecomendados = (List<Mazo>) request.getAttribute("mazosRecomendados");
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
                            
                            <%-- MAZO 1: HARDCODEADO --%>
                            <a href="https://onepiece-cardgame.dev/deckbuilder" class="item-mazo" target="_blank">
                                <img src="${pageContext.request.contextPath}/img/st08.jpg" alt=ST LUFFY">
                                <span>Mazo Luffy</span>
                            </a>

                            <%-- MAZO 2: HARDCODEADO --%>
                            <a href="https://nakamadecks.com/" class="item-mazo" target="_blank">
                                <img src="${pageContext.request.contextPath}/img/st11.jpg" alt="ST UTA">
                                <span>Mazo Yamato</span>
                            </a>

                            <%-- MAZO 3: HARDCODEADO --%>
                            <a href="https://www.egmanevents.com/one-piece" class="item-mazo" target="_blank">
                                <img src="${pageContext.request.contextPath}/img/st20.jpg" alt="ST BarbaNegra">
                                <span>Mazo Marshall D. Teach</span>
                            </a>

                        </div>
                        <div class="footer-mazos">
                            <%-- Este botón sí te lleva a tu sección interna de comunidad --%>
                            <a href="${pageContext.request.contextPath}/Views/community-deck.jsp" class="btn-ver-mas">Ver todos</a>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>

</body>
</html>