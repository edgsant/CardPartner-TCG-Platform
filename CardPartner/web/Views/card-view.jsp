<%@page import="modelo.Carta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // RECIBIMOS LA CARTA DEL SERVLET
    // ==========================================
    Carta cartaActual = (Carta) request.getAttribute("cartaParaLaVista");
    
    if (idParam != null && !idParam.isEmpty()) {
        CartaDAO cartaDao = new CartaDAO();
        cartaActual = cartaDao.obtenerCartaPorId(idParam);
    }
    
    if (cartaActual == null) {
        cartaActual = new Carta();
        cartaActual.setIdCarta("ERROR");
        cartaActual.setNombreCarta("Carta no encontrada");
        cartaActual.setColor("-");
        cartaActual.setTipo("-");
    }
    
    // --- LÓGICA DE IMAGEN CON PROXY ---
    String imgRaw = cartaActual.getImagen();
    String srcFinal = "";

    if (imgRaw != null && imgRaw.toLowerCase().trim().startsWith("http")) {
        // Si es externa, usamos el proxy para evitar el bloqueo de Bandai
        srcFinal = "https://wsrv.nl/?url=" + imgRaw.trim();
    } else {
        // Si es interna o nula, usamos el logo local
        // NOTA: Aquí SÍ incluimos el ContextPath para el logo interno
        srcFinal = request.getContextPath() + "/img/logo.png";
    }
    
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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cardViewStyle.css?v=<%= System.currentTimeMillis() %>">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-detalle">
                <div class="layout-carta">
                    <div class="vista-imagen">
                        <%-- He quitado el ContextPath de aquí porque ya va incluido en srcFinal cuando es necesario --%>
                        <img src="<%= srcFinal %>" 
                             alt="<%= cartaActual.getNombreCarta() %>"
                             referrerpolicy="no-referrer"
                             onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
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