<%@page import="modelo.Carta"%>
<%@page import="datos.CartaDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // LECTURA DINÁMICA DE LA BASE DE DATOS
    // ==========================================
    String idParam = request.getParameter("id");
    Carta cartaActual = null;
    
    // Si viene un ID por la URL, buscamos esa carta exacta
    if (idParam != null && !idParam.isEmpty()) {
        CartaDAO cartaDao = new CartaDAO();
        cartaActual = cartaDao.obtenerCartaPorId(idParam);
    }
    
    // Control de errores: Si alguien pone un ID falso en la URL o entra directo
    if (cartaActual == null) {
        cartaActual = new Carta();
        cartaActual.setIdCarta("ERROR");
        cartaActual.setNombreCarta("Carta no encontrada");
        cartaActual.setColor("-");
        cartaActual.setTipo("-");
    }
    
    String imgCarta = (cartaActual.getImagen() != null) ? cartaActual.getImagen() : "/img/logo.png";
    String atributo = (cartaActual.getAtributoCarta() != null) ? cartaActual.getAtributoCarta() : "-";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <title>Card Partner - <%= cartaActual.getNombreCarta() %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cardViewStyle.css?v=4">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-detalle">
                <div class="layout-carta">
                    <div class="vista-imagen">
                        <img src="${pageContext.request.contextPath}<%= imgCarta %>" alt="<%= cartaActual.getNombreCarta() %>">
                    </div>

                    <div class="vista-info">
                        <h2 class="titulo-seccion"><%= cartaActual.getNombreCarta() %> (<%= cartaActual.getIdCarta() %>)</h2>
                        <div class="caja-info">
                            <h3>INFORMACIÓN</h3>
                            <p><strong>Color:</strong> <span class="badge badge-rojo"><%= cartaActual.getColor() %></span></p>
                            <p><strong>Tipo:</strong> <span class="badge badge-tipo"><%= cartaActual.getTipo() %></span></p>
                            <p><strong>Coste:</strong> <%= cartaActual.getCoste() %> | <strong>Poder:</strong> <%= cartaActual.getPoder() %></p>
                            <p><strong>Atributo:</strong> <%= atributo %></p>
                        </div>

                        <div class="caja-precios">
                            <h3>PRECIOS ACTUALES (CardMarket)</h3>
                            <p class="precio-destacado">Normal: 45.50 €</p>
                            
                            <div class="acciones-carta-detalle">
                                <button class="btn-accion-carta"><i class="fas fa-heart"></i> Añadir a Deseados</button>
                                <a href="${pageContext.request.contextPath}/Views/meta-decks.jsp" class="btn-accion-carta" style="text-decoration: none;"><i class="fas fa-layer-group"></i> Ver mazos</a>
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