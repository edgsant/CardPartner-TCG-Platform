<%@page import="modelo.Usuario"%>
<%@page import="modelo.Carta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // 1. PATRÓN MVC: RECIBIR DATOS DEL SERVLET
    // ==========================================
    Carta cartaActual = (Carta) request.getAttribute("cartaParaLaVista");
    Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioActivo");

    // 2. ESCUDO DE SEGURIDAD
    if (cartaActual == null) {
        response.sendRedirect(request.getContextPath() + "/CartasServlet?accion=listar");
        return;
    }
    
    // --- LÓGICA DE IMAGEN CON PROXY ---
    String imgRaw = cartaActual.getImagen();
    String srcFinal = "";
    if (imgRaw != null && imgRaw.toLowerCase().trim().startsWith("http")) {
        srcFinal = "https://images.weserv.nl/?url=" + imgRaw.trim();
    } else {
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
                
                <%-- MENSAJES DE WISHLIST --%>
                <% if ("wishlist_ok".equals(request.getParameter("exito"))) { %>
                    <div style="background: #e8f5e9; color: #2e7d32; padding: 10px; border-radius: 8px; margin-bottom: 15px; text-align: center; font-weight: bold;">
                        <i class="fas fa-check-circle"></i> ¡Carta añadida a tu Wishlist!
                    </div>
                <% } else if ("ya_existe".equals(request.getParameter("error"))) { %>
                    <div style="background: #fff3cd; color: #856404; padding: 10px; border-radius: 8px; margin-bottom: 15px; text-align: center; font-weight: bold;">
                        <i class="fas fa-exclamation-triangle"></i> Esta carta ya estaba en tus deseados.
                    </div>
                <% } else if ("wishlist_quitada".equals(request.getParameter("exito"))) { %>
                    <div style="background: #e3f2fd; color: #1565c0; padding: 10px; border-radius: 8px; margin-bottom: 15px; text-align: center; font-weight: bold;">
                        <i class="fas fa-info-circle"></i> Carta eliminada de tus deseados.
                    </div>
                <% } %>
                <div class="layout-carta">
                    <div class="vista-imagen">
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
                            <h3>PRECIO DE MERCADO (OPTCG API)</h3>
                            <%
                                // Si el precio es 0, mostramos que no hay datos. Si no, le ponemos el símbolo de dólar.
                                float precioActual = cartaActual.getPrecio();
                                String textoPrecio = (precioActual > 0) ? "$" + String.format("%.2f", precioActual) : "No disponible";
                            %>
                            <p class="precio-destacado" style="font-size: 1.5rem; font-weight: bold; color: #2e7d32; margin-bottom: 15px;">
                                Normal: <%= textoPrecio %>
                            </p>
                            
                            <div class="acciones-carta-detalle" style="display: flex; flex-direction: column; gap: 15px;">
                                
                                <%-- LÓGICA DEL BOTÓN AÑADIR/QUITAR DESEADOS --%>
                                <% 
                                    boolean enWishlist = (request.getAttribute("enWishlist") != null) ? (Boolean) request.getAttribute("enWishlist") : false;
                                    
                                    if (usuarioLogueado != null) { 
                                        if (enWishlist) {
                                %>
                                            <form action="${pageContext.request.contextPath}/WishlistServlet" method="POST" style="margin:0; width: 100%;">
                                                <input type="hidden" name="accion" value="quitar">
                                                <input type="hidden" name="idCarta" value="<%= cartaActual.getIdCarta() %>">
                                                <button type="submit" class="btn-accion-carta" style="width: 100%; background-color: #d32f2f; color: white; border-color: #b71c1c;">
                                                    <i class="fas fa-heart-broken"></i> Quitar de Deseados
                                                </button>
                                            </form>
                                <%      } else { %>
                                            <form action="${pageContext.request.contextPath}/WishlistServlet" method="POST" style="margin:0; width: 100%;">
                                                <input type="hidden" name="accion" value="añadir">
                                                <input type="hidden" name="idCarta" value="<%= cartaActual.getIdCarta() %>">
                                                <button type="submit" class="btn-accion-carta" style="width: 100%;">
                                                    <i class="fas fa-heart"></i> Añadir a Deseados
                                                </button>
                                            </form>
                                <%      } 
                                    } else { 
                                %>
                                        <div style="text-align: center; padding: 12px; border: 1px dashed #ccc; border-radius: 8px; background-color: #f9f9f9;">
                                            <i class="fas fa-lock" style="color: #666;"></i> 
                                            <a href="${pageContext.request.contextPath}/Views/login.jsp" style="color: #d32f2f; font-weight: bold; text-decoration: none;">Inicia sesión</a> para añadir a deseados
                                        </div>
                                <%  } %>

                                <div style="display: flex; gap: 10px; width: 100%;">
                                    <a href="${pageContext.request.contextPath}/Views/meta-decks.jsp" class="btn-accion-carta" style="flex: 1; text-align: center; text-decoration: none; display: flex; align-items: center; justify-content: center; gap: 8px;">
                                        <i class="fas fa-layer-group"></i> Ver mazos
                                    </a>
                                    <a href="https://www.cardmarket.com/es/OnePiece/Products/Search?searchString=<%= cartaActual.getIdCarta().split("_")[0] %>" target="_blank" class="btn-comprar" style="flex: 1; text-align: center; display: flex; align-items: center; justify-content: center; gap: 8px; margin-top: 0;">
                                        <i class="fas fa-shopping-cart"></i> CardMarket
                                    </a>
                                </div>
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