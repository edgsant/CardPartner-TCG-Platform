<%@page import="java.util.List"%>
<%@page import="modelo.Carta"%>
<%! 
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
    List<Carta> listaCartas = (List<Carta>) request.getAttribute("listaCartas");
    if (listaCartas == null) {
        response.sendRedirect(request.getContextPath() + "/CartasServlet?accion=listar");
        return;
    }

    String nombreSel = (request.getAttribute("nombreBuscado") != null) ? (String)request.getAttribute("nombreBuscado") : "";
    String[] coloresSel = (String[]) request.getAttribute("coloresSeleccionados");
    String[] tiposSel = (String[]) request.getAttribute("tiposSeleccionados");
    String[] atributosSel = (String[]) request.getAttribute("atributosSeleccionados");

    int paginaActual = (request.getAttribute("paginaActual") != null) ? (Integer)request.getAttribute("paginaActual") : 1;
    int totalPaginas = (request.getAttribute("totalPaginas") != null) ? (Integer)request.getAttribute("totalPaginas") : 1;
    int totalRegistros = (request.getAttribute("totalRegistros") != null) ? (Integer)request.getAttribute("totalRegistros") : 0;
    int registrosPorPagina = (request.getAttribute("registrosPorPagina") != null) ? (Integer)request.getAttribute("registrosPorPagina") : 25;

    String queryFiltros = (String) request.getAttribute("queryFiltros");
    if (queryFiltros == null) queryFiltros = "";
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;600&family=Montserrat:wght@400;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <title>Card Partner - Galería</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cardsStyle.css?v=18">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-cartas" style="width: 100%; padding: 30px; box-sizing: border-box; display: flex; flex-direction: column; align-items: center;">
                
                <div style="width: 100%;">
                    
                    <div class="barra-busqueda barra-busqueda-limpia" style="width: 100%; display: block;">
                        <form action="${pageContext.request.contextPath}/CartasServlet" method="GET" class="form-busqueda-limpio" style="width: 100%; display: block;">
                            <input type="hidden" name="accion" value="filtrar">
                            
                            <div class="filtros-horizontal">
                                <input type="text" name="nombre" class="input-buscador" placeholder="Buscar por Nombre, Atributo o Tipo..." value="<%= nombreSel %>">
                                
                                <div class="grupo-selector-size">
                                    <label class="label-size"><i class="fas fa-list"></i> Mostrar:</label>
                                    <select name="size" class="selector-size" onchange="this.form.submit()">
                                        <option value="25" <%= (registrosPorPagina == 25) ? "selected" : "" %>>25 cartas</option>
                                        <option value="50" <%= (registrosPorPagina == 50) ? "selected" : "" %>>50 cartas</option>
                                        <option value="100" <%= (registrosPorPagina == 100) ? "selected" : "" %>>100 cartas</option>
                                        <option value="500" <%= (registrosPorPagina == 500) ? "selected" : "" %>>500 cartas</option>
                                        <option value="2500" <%= (registrosPorPagina == 2500) ? "selected" : "" %>>2500 cartas</option>
                                    </select>
                                </div>

                                <button type="submit" class="btn-filtro"><i class="fas fa-search"></i> Buscar</button>
                                <a href="#modal-filtros" class="btn-filtro" style="text-decoration:none;"><i class="fas fa-sliders-h"></i> Filtros</a>
                            </div>

                            <div id="modal-filtros" class="modal-overlay">
                                <div class="modal-content modal-filtros-avanzados">
                                    <a href="#" class="btn-cerrar">&times;</a>
                                    <h2 class="titulo-seccion titulo-modal-filtros">Filtros Avanzados</h2>
                                    
                                    <div class="campo-form">
                                        <label class="label-filtro-categoria">🎨 Color de la Carta:</label>
                                        <div class="grid-filtros-3">
                                            <label><input type="checkbox" name="color" value="Rojo" <%= contiene(coloresSel, "Rojo") ? "checked" : "" %>> Rojo</label>
                                            <label><input type="checkbox" name="color" value="Verde" <%= contiene(coloresSel, "Verde") ? "checked" : "" %>> Verde</label>
                                            <label><input type="checkbox" name="color" value="Azul" <%= contiene(coloresSel, "Azul") ? "checked" : "" %>> Azul</label>
                                            <label><input type="checkbox" name="color" value="Amarillo" <%= contiene(coloresSel, "Amarillo") ? "checked" : "" %>> Amarillo</label>
                                            <label><input type="checkbox" name="color" value="Negro" <%= contiene(coloresSel, "Negro") ? "checked" : "" %>> Negro</label>
                                            <label><input type="checkbox" name="color" value="Morado" <%= contiene(coloresSel, "Morado") ? "checked" : "" %>> Morado</label>
                                        </div>

                                        <label class="label-filtro-categoria">🃏 Tipo de Carta:</label>
                                        <div class="grid-filtros-2">
                                            <label><input type="checkbox" name="tipo" value="LIDER" <%= contiene(tiposSel, "LIDER") ? "checked" : "" %>> Líder</label>
                                            <label><input type="checkbox" name="tipo" value="PERSONAJE" <%= contiene(tiposSel, "PERSONAJE") ? "checked" : "" %>> Personaje</label>
                                            <label><input type="checkbox" name="tipo" value="EVENTO" <%= contiene(tiposSel, "EVENTO") ? "checked" : "" %>> Evento</label>
                                            <label><input type="checkbox" name="tipo" value="ESCENARIO" <%= contiene(tiposSel, "ESCENARIO") ? "checked" : "" %>> Escenario</label>
                                        </div>

                                        <label class="label-filtro-categoria">⚔️ Atributo:</label>
                                        <div class="grid-filtros-3">
                                            <label><input type="checkbox" name="atributo" value="Strike" <%= contiene(atributosSel, "Strike") ? "checked" : "" %>> Strike</label>
                                            <label><input type="checkbox" name="atributo" value="Slash" <%= contiene(atributosSel, "Slash") ? "checked" : "" %>> Slash</label>
                                            <label><input type="checkbox" name="atributo" value="Special" <%= contiene(atributosSel, "Special") ? "checked" : "" %>> Special</label>
                                            <label><input type="checkbox" name="atributo" value="Ranged" <%= contiene(atributosSel, "Ranged") ? "checked" : "" %>> Ranged</label>
                                            <label><input type="checkbox" name="atributo" value="Wisdom" <%= contiene(atributosSel, "Wisdom") ? "checked" : "" %>> Wisdom</label>
                                        </div>
                                        
                                        <button type="submit" class="btn-filtro btn-aplicar-filtros">Aplicar Filtros</button>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>

                    <p class="info-resultados">
                        Mostrando <%= listaCartas.size() %> de <strong><%= totalRegistros %></strong> cartas disponibles.
                    </p>

                    <section class="galeria-cartas" style="width: 100%;">
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
                            <p class="mensaje-sin-resultados">No hay resultados con estos filtros.</p>
                        <% } %>
                    </section>

                    <div class="contenedor-paginacion">
                        <a href="${pageContext.request.contextPath}/CartasServlet?accion=filtrar<%= queryFiltros %>&pagina=<%= paginaActual - 1 %>" 
                           class="item-paginacion <%= (paginaActual <= 1) ? "disabled" : "" %>">&laquo;</a>

                        <%
                            int rango = 2;
                            for (int i = 1; i <= totalPaginas; i++) {
                                if (i == 1 || i == totalPaginas || (i >= paginaActual - rango && i <= paginaActual + rango)) {
                        %>
                                    <a href="${pageContext.request.contextPath}/CartasServlet?accion=filtrar<%= queryFiltros %>&pagina=<%= i %>" 
                                       class="item-paginacion <%= (i == paginaActual) ? "active" : "" %>"><%= i %></a>
                        <%
                                } else if (i == paginaActual - rango - 1 || i == paginaActual + rango + 1) {
                        %>
                                    <span class="puntos-suspensivos">...</span>
                        <%
                                }
                            }
                        %>

                        <a href="${pageContext.request.contextPath}/CartasServlet?accion=filtrar<%= queryFiltros %>&pagina=<%= paginaActual + 1 %>" 
                           class="item-paginacion <%= (paginaActual >= totalPaginas) ? "disabled" : "" %>">&raquo;</a>
                    </div>
                    
                </div> </main>
        </div>
    </div>

</body>
</html>