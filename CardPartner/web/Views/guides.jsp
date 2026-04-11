<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <title>Card Partner - Guides</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/guidesStyle.css?v=1">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="guides">
                <div class="barra_guides">
                    <h2 class="titulo-seccion">GUIDES</h2>
                </div>
                    
                <a href="https://youtu.be/s8s7vSSb8Tk?si=dyL9eDHnf2KhwyY0" target="_blank" class="guia-principal">
                    <img src="${pageContext.request.contextPath}/img/tutorial_guides.jpg" alt="Guia principal">
                    <div class="info-principal">
                        <h3>Guía completa de One Piece TCG</h3>
                        <p>Aprende las funcionalidades básicas para comenzar a jugar.</p>
                    </div>
                </a>
                
                <section class="lista-guias">

                    <a href="#" class="guia" style="text-decoration: none; color: inherit;">
                        <img src="${pageContext.request.contextPath}/img/luffy-deck-tutorial.jpg" alt="Luffy">
                        <div>
                            <h4>Luffy rampeando hasta el cielo</h4>
                            <p>Aprende todas las mecánicas para jugar lo que quieras en tus primeros turnos.</p>
                        </div>
                    </a>

                    <a href="#" class="guia" style="text-decoration: none; color: inherit;">
                        <img src="${pageContext.request.contextPath}/img/nami-deck-tutorial.jpg" alt="Nami">
                        <div>
                            <h4>A robar se ha dicho</h4>
                            <p>Si juegas este mazo vas a aprender cómo quedarte sin cartas de robo antes de que tu oponente diga basta.</p>
                        </div>
                    </a>

                    <a href="#" class="guia" style="text-decoration: none; color: inherit;">
                        <img src="${pageContext.request.contextPath}/img/vegapunk-deck-tutorial.jpg" alt="Vegapunk">
                        <div>
                            <h4>Vidas infinitas</h4>
                            <p>Bloquear siempre será tu segunda opción una vez sepas este pequeño combo.</p>
                        </div>
                    </a>

                </section>
            </main>

        </div>
    </div>
</body>
</html>