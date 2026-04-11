<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <title>Card Partner - Detalle Carta</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cardViewStyle.css?v=3">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-detalle">
                <div class="layout-carta">
                    <div class="vista-imagen">
                        <img src="${pageContext.request.contextPath}/img/op01-120.jpg" alt="Shanks OP01-120">
                    </div>

                    <div class="vista-info">
                        <h2 class="titulo-seccion">Shanks (OP01-120)</h2>
                        <div class="caja-info">
                            <h3>INFORMACIÓN</h3>
                            <p><strong>Expansión:</strong> Romance Dawn [OP-01]</p>
                            <p><strong>Rareza:</strong> SEC (Secret Rare)</p>
                            <p><strong>Color:</strong> <span class="badge badge-rojo">Rojo</span></p>
                            <p><strong>Tipo:</strong> <span class="badge badge-tipo">Personaje</span></p>
                            <p><strong>Arquetipo:</strong> Cuatro Emperadores / Piratas del Pelirrojo</p>
                            <p><strong>Coste:</strong> 9 | <strong>Poder:</strong> 10000</p>
                            <p class="texto-efecto"><em>[Rush] (Este personaje puede atacar en el turno que es jugado.)<br>
                            [Al atacar] Tu oponente no puede activar "Blocker" de personajes con 2000 o menos de poder durante esta batalla.</em></p>
                        </div>

                        <div class="caja-precios">
                            <h3>PRECIOS ACTUALES (CardMarket)</h3>
                            <p class="precio-destacado">Normal: 45.50 €</p>
                            <p>Foil / Paralela: 120.00 €</p>
                            
                            <div class="acciones-carta-detalle">
                                <button class="btn-accion-carta"><i class="fas fa-heart"></i> Añadir a Deseados</button>
                                <a href="meta-decks.jsp" class="btn-accion-carta" style="text-decoration: none;"><i class="fas fa-layer-group"></i> Ver mazos</a>
                                <a href="#" class="btn-comprar"><i class="fas fa-shopping-cart"></i> Comprar</a>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="enlaces-oficiales">
                    <a href="https://asia-en.onepiece-cardgame.com/" target="_blank">Ver en sitio oficial OP Card Game</a>
                </div>
            </main>
        </div>
    </div>
</body>
</html>