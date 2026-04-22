<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Carta"%>
<%@page import="modelo.ContieneCarta"%>
<%@page import="datos.MazoDAO"%>
<%@page import="datos.CartaDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String idParam = request.getParameter("id");
    Mazo mazoActual = null;
    Carta liderActual = null;
    List<ContieneCarta> listaCartas = null;
    
    MazoDAO mazoDao = new MazoDAO();
    CartaDAO cartaDao = new CartaDAO();

    if(idParam != null && !idParam.isEmpty()) {
        try {
            int idBusqueda = Integer.parseInt(idParam);
            mazoActual = mazoDao.obtenerMazoPorId(idBusqueda);
            if (mazoActual != null) {
                liderActual = cartaDao.obtenerCartaPorId(mazoActual.getIdCartaLider());
                listaCartas = mazoDao.obtenerCartasDeUnMazo(idBusqueda);
            }
        } catch(Exception e) {}
    }

    if (mazoActual == null) {
        response.sendRedirect(request.getContextPath() + "/CatalogoMazosServlet?accion=listar");
        return;
    }

    String imgLiderRaw = (liderActual != null && liderActual.getImagen() != null) ? liderActual.getImagen().trim() : "";
    String srcLider = "";
    if (!imgLiderRaw.isEmpty() && imgLiderRaw.toLowerCase().startsWith("http")) {
        srcLider = "https://images.weserv.nl/?url=" + imgLiderRaw;
    } else {
        srcLider = request.getContextPath() + (imgLiderRaw.isEmpty() ? "/img/logo.png" : "/img/" + imgLiderRaw);
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="referrer" content="no-referrer">
    <title>Card Partner - <%= mazoActual.getNombreMazo() %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/deckViewStyle.css?v=15">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-detalle">
                
                <h2 class="titulo-mazo-grande"><%= mazoActual.getNombreMazo() %></h2>

                <div class="deck-header">
                    
                    <div class="lider-seccion">
                        <img src="<%= srcLider %>" alt="Líder" onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
                        <p>Líder: <%= (liderActual != null) ? liderActual.getNombreCarta() : "Desconocido" %> (<%= mazoActual.getIdCartaLider() %>)</p>
                    </div>

                    <div class="descripcion-seccion">
                        <h3>DESCRIPCIÓN DEL MAZO</h3>
                        <p><%= (mazoActual.getDescripcion() != null) ? mazoActual.getDescripcion() : "Sin descripción." %></p>

                        <div class="tags-info">
                            <span class="tag color-tag"><%= (liderActual != null) ? liderActual.getColor() : "Varios" %></span>
                            <span class="tag winrate-tag">Winrate: <%= mazoActual.getWinrate() %>%</span>
                        </div>

                        <div class="acciones-mazo">
                             <button class="btn-accion">Guardar Mazo</button>
                             <a href="#" class="btn-accion">Copiar / Editar</a>
                             <button class="btn-accion">Comprar Cartas</button>
                        </div>
                    </div>
                </div>

                <h3 class="titulo-cartas">LISTA DE CARTAS</h3>

                <div class="deck-grid">
                    <%
                        if (listaCartas != null && !listaCartas.isEmpty()) {
                            for (ContieneCarta cc : listaCartas) {
                                Carta c = cc.getCarta();
                                String imgCartaRaw = (c.getImagen() != null) ? c.getImagen().trim() : "";
                                String srcCarta = "";
                                if (!imgCartaRaw.isEmpty() && imgCartaRaw.toLowerCase().startsWith("http")) {
                                    srcCarta = "https://images.weserv.nl/?url=" + imgCartaRaw;
                                } else {
                                    srcCarta = request.getContextPath() + (imgCartaRaw.isEmpty() ? "/img/logo.png" : "/img/" + imgCartaRaw);
                                }
                    %>
                        <div class="carta-item">
                            <img src="<%= srcCarta %>" alt="<%= c.getNombreCarta() %>" loading="lazy" onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
                            <span class="badge-cantidad">x<%= cc.getCantidad() %></span>
                        </div>
                    <%
                            }
                        } else {
                    %>
                        <p style="color: white; grid-column: 1/-1; text-align: center;">No hay cartas en este mazo.</p>
                    <%
                        }
                    %>
                </div>

            </main>

        </div>
    </div>
</body>
</html>