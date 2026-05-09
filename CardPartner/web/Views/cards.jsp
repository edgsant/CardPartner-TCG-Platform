<%@page import="java.util.List"%>
<%@page import="modelo.Carta"%>
<%! 
    // Función auxiliar para comprobar si una opción está dentro de los arrays seleccionados
    boolean contiene(String[] array, String valor) {
        if (array == null) return false;
        for (String s : array) {
            if (s.equals(valor)) return true;
        }
        return false;
    }
%>
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

    
    // Recuperamos filtros (ahora son arrays)
    String nombreSel = (request.getAttribute("nombreBuscado") != null) ? (String)request.getAttribute("nombreBuscado") : "";
    String[] coloresSel = (String[]) request.getAttribute("coloresSeleccionados");
    String[] tiposSel = (String[]) request.getAttribute("tiposSeleccionados");
    String[] atributosSel = (String[]) request.getAttribute("atributosSeleccionados");

    // Paginación
    int paginaActual = (request.getAttribute("paginaActual") != null) ? (Integer)request.getAttribute("paginaActual") : 1;
    int totalPaginas = (request.getAttribute("totalPaginas") != null) ? (Integer)request.getAttribute("totalPaginas") : 1;
    int totalRegistros = (request.getAttribute("totalRegistros") != null) ? (Integer)request.getAttribute("totalRegistros") : 0;
    
    // La query completa reconstruida por el Servlet
    String queryFiltros = (String) request.getAttribute("queryFiltros");
    if (queryFiltros == null) queryFiltros = "";
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
                    
                    <form action="${pageContext.request.contextPath}/CartasServlet" method="GET" style="display: contents;">
                        <input type="hidden" name="accion" value="filtrar">
                        
                        <div class="filtros">
                            <input type="text" name="nombre" 
                                placeholder="Nombre, Atributo o Tipo..." 
                                value="<%= nombreSel %>" 
                                style="padding: 10px; border-radius: 6px; border: 1px solid #ccc; font-family: 'Poppins'; width: 250px;">
                            <button type="submit" class="btn-filtro">Buscar</button>
                            <a href="#modal-filtros" class="btn-filtro" style="text-decoration:none;">Filtros ⚙️</a>
                        </div>

                        <div id="modal-filtros" class="modal-overlay">
                            <div class="modal-content" style="max-width: 600px; text-align: left;">
                                <a href="#" class="btn-cerrar">&times;</a>
                                <h2 class="titulo-seccion" style="font-size: 24px; margin-bottom: 20px;">Filtros Avanzados</h2>
                                
                                <div class="campo-form">
                                    <label style="font-weight: bold; margin-bottom: 10px; display: block; color: #d32f2f;">🎨 Color de la Carta:</label>
                                    <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 10px; margin-bottom: 20px;">
                                        <label><input type="checkbox" name="color" value="Rojo" <%= contiene(coloresSel, "Rojo") ? "checked" : "" %>> Rojo</label>
                                        <label><input type="checkbox" name="color" value="Verde" <%= contiene(coloresSel, "Verde") ? "checked" : "" %>> Verde</label>
                                        <label><input type="checkbox" name="color" value="Azul" <%= contiene(coloresSel, "Azul") ? "checked" : "" %>> Azul</label>
                                        <label><input type="checkbox" name="color" value="Amarillo" <%= contiene(coloresSel, "Amarillo") ? "checked" : "" %>> Amarillo</label>
                                        <label><input type="checkbox" name="color" value="Negro" <%= contiene(coloresSel, "Negro") ? "checked" : "" %>> Negro</label>
                                        <label><input type="checkbox" name="color" value="Morado" <%= contiene(coloresSel, "Morado") ? "checked" : "" %>> Morado</label>
                                    </div>

                                    <label style="font-weight: bold; margin-bottom: 10px; display: block; color: #d32f2f;">🃏 Tipo de Carta:</label>
                                    <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 20px;">
                                        <label><input type="checkbox" name="tipo" value="LIDER" <%= contiene(tiposSel, "LIDER") ? "checked" : "" %>> Líder</label>
                                        <label><input type="checkbox" name="tipo" value="PERSONAJE" <%= contiene(tiposSel, "PERSONAJE") ? "checked" : "" %>> Personaje</label>
                                        <label><input type="checkbox" name="tipo" value="EVENTO" <%= contiene(tiposSel, "EVENTO") ? "checked" : "" %>> Evento</label>
                                        <label><input type="checkbox" name="tipo" value="ESCENARIO" <%= contiene(tiposSel, "ESCENARIO") ? "checked" : "" %>> Escenario</label>
                                    </div>

                                    <label style="font-weight: bold; margin-bottom: 10px; display: block; color: #d32f2f;">⚔️ Atributo:</label>
                                    <div style="display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 10px; margin-bottom: 25px;">
                                        <label><input type="checkbox" name="atributo" value="Strike" <%= contiene(atributosSel, "Strike") ? "checked" : "" %>> Strike</label>
                                        <label><input type="checkbox" name="atributo" value="Slash" <%= contiene(atributosSel, "Slash") ? "checked" : "" %>> Slash</label>
                                        <label><input type="checkbox" name="atributo" value="Special" <%= contiene(atributosSel, "Special") ? "checked" : "" %>> Special</label>
                                        <label><input type="checkbox" name="atributo" value="Ranged" <%= contiene(atributosSel, "Ranged") ? "checked" : "" %>> Ranged</label>
                                        <label><input type="checkbox" name="atributo" value="Wisdom" <%= contiene(atributosSel, "Wisdom") ? "checked" : "" %>> Wisdom</label>
                                    </div>
                                    
                                    <button type="submit" class="btn-filtro" style="width: 100%; padding: 12px; font-size: 16px;">Aplicar Filtros</button>
                                </div>
                            </div>
                        </div>
                    </form>
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

    <!-- ========================================== -->
    <!-- MODAL DE FILTROS AVANZADOS                 -->
    <!-- ========================================== -->
    <div id="modal-filtros" class="modal-overlay">
        <div class="modal-content" style="max-width: 500px;">
            <a href="#" class="btn-cerrar">&times;</a>
            <h2 class="titulo-seccion" style="font-size: 24px; margin-bottom: 20px;">Filtros Avanzados</h2>
            
            <form action="${pageContext.request.contextPath}/CartasServlet" method="GET">
                <input type="hidden" name="accion" value="filtrar">
                
                <div class="campo-form" style="text-align: left;">
                    
                    <label style="font-weight: bold; margin-bottom: 5px; display: block;">Color de la Carta:</label>
                    <select name="color" ...>
                         </select>

                    <label style="font-weight: bold; margin-bottom: 5px; display: block;">Tipo de Carta:</label>
                    <select name="tipo" ...>
                         </select>
                    
                    <button type="submit" class="btn-filtro" style="width: 100%; padding: 12px; font-size: 16px;">Aplicar Filtros</button>
                </div>
            </form>
        </div>
    </div>
</body>
</html>