<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CardPartner - Inicio</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/indexStyle.css?v=11">
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
                        <h2 class="titulo-mazos">Recomendados: Principiantes</h2>
                        <div class="contenido-mazos">
                            <a href="${pageContext.request.contextPath}/Views/deck-view.jsp" class="item-mazo">
                                <img src="${pageContext.request.contextPath}/img/st20.jpg" alt="ST-20 Marshall D. Teach">
                                <span>ST-20 Marshall D. Teach</span>
                            </a>

                            <a href="${pageContext.request.contextPath}/Views/deck-view.jsp" class="item-mazo">
                                <img src="${pageContext.request.contextPath}/img/st11.jpg" alt="ST-11 UTA">
                                <span>ST-11 UTA</span>
                            </a>

                            <a href="${pageContext.request.contextPath}/Views/deck-view.jsp" class="item-mazo">
                                <img src="${pageContext.request.contextPath}/img/st08.jpg" alt="ST-08 Luffy">
                                <span>ST-08 Luffy</span>
                            </a>
                        </div>
                        <div class="footer-mazos">
                            <a href="${pageContext.request.contextPath}/Views/community-deck.jsp" class="btn-ver-mas">Ver todos los recomendados</a>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    </div>

</body>
</html>