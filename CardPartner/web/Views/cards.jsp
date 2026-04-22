<%@page import="java.util.List"%>
<%@page import="modelo.Carta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // PATRÓN MVC: RECIBIR DATOS DEL SERVLET
    // ==========================================
    List<Carta> listaCartas = (List<Carta>) request.getAttribute("listaCartas");
    
    if (listaCartas == null) {
        response.sendRedirect(request.getContextPath() + "/CartasServlet?accion=listar");
        return; 
    }

    // Recuperamos filtros
    String colorSel = (request.getAttribute("colorSeleccionado") != null) ? (String)request.getAttribute("colorSeleccionado") : "";
    String tipoSel = (request.getAttribute("tipoSeleccionado") != null) ? (String)request.getAttribute("tipoSeleccionado") : "";
    String atributoSel = (request.getAttribute("atributoSeleccionado") != null) ? (String)request.getAttribute("atributoSeleccionado") : "";

    // Datos de paginación desde el Servlet
    int paginaActual = (request.getAttribute("paginaActual") != null) ? (Integer)request.getAttribute("paginaActual") : 1;
    int totalPaginas = (request.getAttribute("totalPaginas") != null) ? (Integer)request.getAttribute("totalPaginas") : 1;
    int totalRegistros = (request.getAttribute("totalRegistros") != null) ? (Integer)request.getAttribute("totalRegistros") : 0;
    
    String queryFiltros = "&color=" + colorSel + "&tipo=" + tipoSel + "&atributo=" + atributoSel;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <title>Card Partner - Galería</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cardsStyle.css?v=13">
    <style>
        /* Estilos mejorados para la paginación numérica */
        .contenedor-paginacion {
            display: flex;
            justify-content: center;
            align-items: center;
            flex-wrap: wrap;
            gap: 8px;
            margin: 40px 0;
            padding-bottom: 50px;
        }

        .item-paginacion {
            display: flex;
            align-items: center;
            justify-content: center;
            min-width: 40px;
            height: 40px;
            padding: 0 10px;
            border-radius: 6px;
            text-decoration: none;
            background: #fff;
            color: #333;
            border: 1px solid #ddd;
            font-weight: 600;
            transition: all 0.2s ease;
        }

        .item-paginacion:hover {
            background: #f0f0f0;
            border-color: #333;
        }

        .item-paginacion.active {
            background: #333;
            color: #fff;
            border-color: #333;
        }

        .item-paginacion.disabled {
            opacity: 0.4;
            pointer-events: none;
            background: #eee;
        }

        .puntos-suspensivos {
            padding: 0 5px;
            color: #888;
            font-weight: bold;
        }

        .info-resultados {
            text-align: center;
            color: #666;
            margin-bottom: 20px;
            font-family: 'Poppins', sans-serif;
        }
    </style>
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-cartas">
                <div class="barra-busqueda">
                    <h2 class="titulo-seccion">CARDS</h2>
                    <div class="filtros">
                        <form action="${pageContext.request.contextPath}/CartasServlet" method="GET">
                            <input type="hidden" name="accion" value="filtrar">
                            <input type="text" name="atributo" placeholder="Atributo..." value="<%= atributoSel %>">
                            <button type="submit" class="btn-filtro">Buscar</button>
                        </form>
                        <a href="#modal-filtros" class="btn-filtro">Filtros ⚙️</a>
                    </div>
                </div>

                <p class="info-resultados">
                    Mostrando <%= listaCartas.size() %> de <strong><%= totalRegistros %></strong> cartas disponibles.
                </p>

                <section class="galeria-cartas">
                    <% if (!listaCartas.isEmpty()) {
                        for(Carta c : listaCartas) {
                            String imgRaw = (c.getImagen() != null) ? c.getImagen().trim() : "";
                            String srcFinal = imgRaw.toLowerCase().startsWith("http") ? "https://images.weserv.nl/?url=" + imgRaw : (imgRaw.isEmpty() ? request.getContextPath() + "/img/logo.png" : request.getContextPath() + imgRaw);
                    %>
                        <a href="${pageContext.request.contextPath}/CartasServlet?accion=detalle&id=<%= c.getIdCarta() %>" class="carta-item">
                            <div class="contenedor-img">
                                <img src="<%= srcFinal %>" alt="<%= c.getNombreCarta() %>" loading="lazy" onerror="this.src='${pageContext.request.contextPath}/img/logo.png';">
                            </div>
                            <div class="carta-info">
                                <h3><%= c.getNombreCarta() %></h3>
                                <p><%= c.getIdCarta() %> | <%= c.getTipo() %></p>
                            </div>
                        </a>
                    <% } } else { %>
                        <p style="text-align: center; width: 100%;">No hay resultados.</p>
                    <% } %>
                </section>

                <div class="contenedor-paginacion">
                    
                    <a href="${pageContext.request.contextPath}/CartasServlet?accion=filtrar<%= queryFiltros %>&pagina=<%= paginaActual - 1 %>" 
                       class="item-paginacion <%= (paginaActual <= 1) ? "disabled" : "" %>">&laquo;</a>

                    <%
                        // Lógica para mostrar números (1, 2, 3... total)
                        // Mostramos siempre la primera, la última y un rango alrededor de la actual
                        int rango = 2; 
                        for (int i = 1; i <= totalPaginas; i++) {
                            if (i == 1 || i == totalPaginas || (i >= paginaActual - rango && i <= paginaActual + rango)) {
                    %>
                                <a href="${pageContext.request.contextPath}/CartasServlet?accion=filtrar<%= queryFiltros %>&pagina=<%= i %>" 
                                   class="item-paginacion <%= (i == paginaActual) ? "active" : "" %>">
                                    <%= i %>
                                </a>
                    <%
                            } else if (i == paginaActual - rango - 1 || i == paginaActual + rango + 1) {
                                // Dibujamos puntos suspensivos si nos saltamos páginas
                    %>
                                <span class="puntos-suspensivos">...</span>
                    <%
                            }
                        }
                    %>

                    <a href="${pageContext.request.contextPath}/CartasServlet?accion=filtrar<%= queryFiltros %>&pagina=<%= paginaActual + 1 %>" 
                       class="item-paginacion <%= (paginaActual >= totalPaginas) ? "disabled" : "" %>">&raquo;</a>

                </div>
            </main>
        </div>
    </div>

    <div id="modal-filtros" class="modal-overlay">
        </div>
</body>
</html>