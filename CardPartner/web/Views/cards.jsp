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

    // Recuperamos los filtros aplicados para mantenerlos visibles y usarlos en la paginación
    String colorSel = (request.getAttribute("colorSeleccionado") != null) ? (String)request.getAttribute("colorSeleccionado") : "";
    String tipoSel = (request.getAttribute("tipoSeleccionado") != null) ? (String)request.getAttribute("tipoSeleccionado") : "";
    String atributoSel = (request.getAttribute("atributoSeleccionado") != null) ? (String)request.getAttribute("atributoSeleccionado") : "";

    // LÓGICA DE PAGINACIÓN
    int paginaActual = (request.getAttribute("paginaActual") != null) ? (Integer)request.getAttribute("paginaActual") : 1;
    // Si la lista trae 20 cartas (el límite actual del Servlet), asumimos que hay página siguiente
    boolean hayPaginaSiguiente = (listaCartas.size() == 20); 
    
    // Construimos la URL de filtros para que al cambiar de página no se pierda la búsqueda
    String queryFiltros = "&color=" + colorSel + "&tipo=" + tipoSel + "&atributo=" + atributoSel;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <title>Card Partner - Cartas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cardsStyle.css?v=12">
    <style>
        .carta-item img {
            width: 100%;
            display: block;
            min-height: 250px;
            background-color: transparent;
            object-fit: contain;
        }
        .contenedor-img {
            min-height: 250px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        /* Estilos para la paginación */
        .contenedor-paginacion {
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 20px;
            margin-top: 30px;
            width: 100%;
        }
        .btn-paginacion {
            background: #333;
            color: white;
            padding: 10px 20px;
            border-radius: 8px;
            text-decoration: none;
            font-weight: bold;
        }
        .btn-paginacion.disabled {
            background: #555;
            opacity: 0.5;
            cursor: not-allowed;
            pointer-events: none;
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
                        <form action="${pageContext.request.contextPath}/CartasServlet" method="GET" style="display:inline-block;">
                            <input type="hidden" name="accion" value="filtrar">
                            <input type="text" name="atributo" placeholder="Buscar atributo (Strike, Slash...)" value="<%= atributoSel %>">
                            <button type="submit" class="btn-filtro" style="cursor:pointer; border:none;">Buscar</button>
                        </form>
                        <a href="#modal-filtros" class="btn-filtro" style="text-decoration: none; display: inline-block;">Filtros ⚙️</a>
                    </div>
                </div>

                <section class="galeria-cartas">
                    <%
                        if (!listaCartas.isEmpty()) {
                            for(Carta c : listaCartas) {
                                String imgRaw = (c.getImagen() != null) ? c.getImagen().trim() : "";
                                String srcFinal = "";

                                if (!imgRaw.isEmpty() && imgRaw.toLowerCase().startsWith("http")) {
                                    srcFinal = "https://images.weserv.nl/?url=" + imgRaw;
                                } else {
                                    srcFinal = imgRaw.isEmpty() ? request.getContextPath() + "/img/logo.png" : request.getContextPath() + imgRaw;
                                }
                    %>
                        <a href="${pageContext.request.contextPath}/CartasServlet?accion=detalle&id=<%= c.getIdCarta() %>" class="carta-item">
                            <div class="contenedor-img">
                                <img src="<%= srcFinal %>" 
                                     alt="<%= c.getNombreCarta() %>" 
                                     loading="lazy"
                                     referrerpolicy="no-referrer"
                                     onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
                            </div>
                            <div class="carta-info">
                                <h3><%= c.getNombreCarta() %></h3>
                                <p><%= c.getIdCarta() %> | <%= c.getTipo() %></p>
                            </div>
                        </a>
                    <%
                            }
                        } else {
                    %>
                        <p style="color: #333; text-align: center; width: 100%; margin-top: 20px;">No se han encontrado cartas con esos filtros o la base de datos está vacía.</p>
                    <%
                        }
                    %>
                </section>

                <!-- CONTROLES DE PAGINACIÓN -->
                <div class="contenedor-paginacion">
                    <% if (paginaActual > 1) { %>
                        <a href="${pageContext.request.contextPath}/CartasServlet?accion=filtrar<%= queryFiltros %>&pagina=<%= paginaActual - 1 %>" class="btn-paginacion">&laquo; Anterior</a>
                    <% } else { %>
                        <a href="#" class="btn-paginacion disabled">&laquo; Anterior</a>
                    <% } %>

                    <span style="color: #333; font-weight: bold; font-size: 18px;">Página <%= paginaActual %></span>

                    <% if (hayPaginaSiguiente) { %>
                        <a href="${pageContext.request.contextPath}/CartasServlet?accion=filtrar<%= queryFiltros %>&pagina=<%= paginaActual + 1 %>" class="btn-paginacion">Siguiente &raquo;</a>
                    <% } else { %>
                        <a href="#" class="btn-paginacion disabled">Siguiente &raquo;</a>
                    <% } %>
                </div>

            </main>
        </div>
    </div>

    <!-- VENTANA MODAL DE FILTROS -->
    <div id="modal-filtros" class="modal-overlay">
        <div class="modal-content">
            <a href="#" class="btn-cerrar">&times;</a>
            <h2 class="titulo-seccion" style="font-size: 24px; margin-bottom: 20px; text-align: left;">Filtros de Búsqueda</h2>

            <form action="${pageContext.request.contextPath}/CartasServlet" method="GET" style="text-align: left;">
                <input type="hidden" name="accion" value="filtrar">

                <div style="margin-bottom: 15px;">
                    <label style="font-weight: bold; display: block; margin-bottom: 5px;">Atributo de Combate:</label>
                    <input type="text" name="atributo" value="<%= atributoSel %>" placeholder="Ej: Strike, Slash, Ranged..." style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #ccc;">
                </div>

                <div style="margin-bottom: 15px;">
                    <label style="font-weight: bold; display: block; margin-bottom: 5px;">Color de la carta:</label>
                    <select name="color" style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #ccc;">
                        <option value="">Todos los colores</option>
                        <option value="Rojo" <%= colorSel.equals("Rojo") ? "selected" : "" %>>Rojo</option>
                        <option value="Verde" <%= colorSel.equals("Verde") ? "selected" : "" %>>Verde</option>
                        <option value="Azul" <%= colorSel.equals("Azul") ? "selected" : "" %>>Azul</option>
                        <option value="Morado" <%= colorSel.equals("Morado") ? "selected" : "" %>>Morado</option>
                        <option value="Negro" <%= colorSel.equals("Negro") ? "selected" : "" %>>Negro</option>
                        <option value="Amarillo" <%= colorSel.equals("Amarillo") ? "selected" : "" %>>Amarillo</option>
                    </select>
                </div>

                <div style="margin-bottom: 15px;">
                    <label style="font-weight: bold; display: block; margin-bottom: 5px;">Tipo de Carta:</label>
                    <select name="tipo" style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #ccc;">
                        <option value="">Cualquier tipo</option>
                        <option value="LIDER" <%= tipoSel.equals("LIDER") ? "selected" : "" %>>Líder</option>
                        <option value="PERSONAJE" <%= tipoSel.equals("PERSONAJE") ? "selected" : "" %>>Personaje</option>
                        <option value="EVENTO" <%= tipoSel.equals("EVENTO") ? "selected" : "" %>>Evento</option>
                        <option value="ESCENARIO" <%= tipoSel.equals("ESCENARIO") ? "selected" : "" %>>Escenario</option>
                    </select>
                </div>

                <button type="submit" class="btn-filtro" style="width: 100%; background: #333; color: white; border: none; cursor: pointer; padding: 15px; border-radius: 8px; font-weight: bold;">
                    APLICAR FILTROS
                </button>
            </form>
        </div>
    </div>
</body>
</html>