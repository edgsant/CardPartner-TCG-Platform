<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <title>Card Partner - News</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/newsStyle.css?v=1">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="news">
                <div class="barra_news">
                    <h2 class="titulo-seccion">NEWS</h2>
                </div>
                    
                <a href="#" class="noticia-principal" style="text-decoration: none; color: inherit;">
                    <img src="${pageContext.request.contextPath}/img/newOpSet.PNG" alt="Nuevo OP Set">
                    <div class="info-principal">
                        <h3>Nuevo OP anunciado</h3>
                        <p>Se han revelado nuevas cartas que cambiarán completamente el meta competitivo actual.</p>
                    </div>
                </a>

                <section class="lista-noticias">

                    <a href="#" class="noticia" style="text-decoration: none; color: inherit;">
                        <img src="${pageContext.request.contextPath}/img/genteJugando.jpg" alt="Jugadores">
                        <div>
                            <h4>Problemas con el competitivo en Valladolid</h4>
                            <p>Los torneos proponen premios desproporcionados con el pago de inscripción.</p>
                        </div>
                    </a>

                    <a href="#" class="noticia" style="text-decoration: none; color: inherit;">
                        <img src="${pageContext.request.contextPath}/img/topDeck.jpg" alt="Top Decks">
                        <div>
                            <h4>Top decks del mes</h4>
                            <p>Análisis del meta actual.</p>
                        </div>
                    </a>

                    <a href="#" class="noticia" style="text-decoration: none; color: inherit;">
                        <img src="${pageContext.request.contextPath}/img/torneo.jpg" alt="Torneo Internacional">
                        <div>
                            <h4>Gran torneo internacional anunciado</h4>
                            <p>Participación global con apariciones de antiguos campeones de grandes torneos.</p>
                        </div>
                    </a>

                </section>
            </main>

        </div>
    </div>
</body>
</html>