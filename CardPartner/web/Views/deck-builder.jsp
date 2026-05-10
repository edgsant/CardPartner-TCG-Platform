<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="modelo.Carta"%>
<%@page import="modelo.Usuario"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // 1. SEGURIDAD Y CARGA DE DATOS
    // ==========================================
    Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioActivo");
    if (usuarioLogueado == null) {
        response.sendRedirect(request.getContextPath() + "/Views/login.jsp");
        return;
    }

    List<Carta> catalogo = (List<Carta>) request.getAttribute("catalogo");
    if (catalogo == null) {
        response.sendRedirect(request.getContextPath() + "/DeckBuilderServlet");
        return;
    }

    // Datos de paginación y filtros
    Integer paginaActual = (Integer) request.getAttribute("paginaActual");
    Integer totalPaginas = (Integer) request.getAttribute("totalPaginas");
    String colorFiltro = (request.getAttribute("colorFiltro") != null) ? (String) request.getAttribute("colorFiltro") : "";
    String tipoFiltro = (request.getAttribute("tipoFiltro") != null) ? (String) request.getAttribute("tipoFiltro") : "";

    // Mazo temporal (Carrito) y Líder
    List<Carta> mazoTemporal = (List<Carta>) session.getAttribute("mazoTemporal");
    if (mazoTemporal == null) mazoTemporal = new ArrayList<>();
    Carta lider = (Carta) session.getAttribute("liderSeleccionado");

    // Recuperar datos para edición/copia (incluyendo el WINRATE FLOAT)
    String nombrePrevio = (session.getAttribute("mazoNombrePrevio") != null) ? (String)session.getAttribute("mazoNombrePrevio") : "";
    String descPrevia = (session.getAttribute("mazoDescripcionPrevio") != null) ? (String)session.getAttribute("mazoDescripcionPrevio") : "";
    String visibilidadPrevia = (session.getAttribute("mazoVisibilidadPrevia") != null) ? (String)session.getAttribute("mazoVisibilidadPrevia") : "Público";
    Float winratePrevio = (session.getAttribute("mazoWinratePrevio") != null) ? (Float)session.getAttribute("mazoWinratePrevio") : 0.0f;

    // Agrupación para la lista lateral
    Map<String, Integer> conteoMazo = new HashMap<>();
    Map<String, Carta> mapaCartasMazo = new HashMap<>();
    for (Carta c : mazoTemporal) {
        String idBase = c.getIdCarta().split("_")[0]; 
        conteoMazo.put(idBase, conteoMazo.getOrDefault(idBase, 0) + 1);
        if(!mapaCartasMazo.containsKey(idBase)) {
            mapaCartasMazo.put(idBase, c);
        }
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <title>Deck Builder - Card Partner</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/deckBuilderStyle.css?v=<%= System.currentTimeMillis() %>">
</head>
<body>
    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <div class="db-container">
                
                <aside class="db-sidebar">
                    <h2 style="margin-top: 0; color: #333; font-family: 'Montserrat', sans-serif;"><i class="fas fa-layer-group" style="color: #e91e63;"></i> Tu Mazo</h2>
                    
                    <% String error = request.getParameter("error");
                       String exito = request.getParameter("exito");
                       if(error != null) {
                           String txt = "Error en la regla del mazo.";
                           if(error.equals("limite_copias")) txt = "Límite: Máximo 4 copias.";
                           if(error.equals("color_invalido")) txt = "Color incompatible con tu Líder.";
                           if(error.equals("mazo_lleno")) txt = "El mazo ya tiene 50 cartas.";
                           if(error.equals("falta_lider")) txt = "Elige primero un Líder.";
                    %>
                        <div class="msg-box msg-error"><i class="fas fa-exclamation-triangle"></i> <%= txt %></div>
                    <% } else if(exito != null) { %>
                        <div class="msg-box msg-success"><i class="fas fa-check-circle"></i> Acción correcta.</div>
                    <% } %>

                    <div style="text-align: center; margin-bottom: 20px; padding-bottom: 20px; border-bottom: 2px dashed #eee;">
                        <h4 style="margin: 0 0 10px 0; color: #888; font-size: 0.85rem; text-transform: uppercase;">Líder</h4>
                        <% if(lider != null) { 
                            String imgLider = (lider.getImagen() != null && lider.getImagen().startsWith("http")) ? "https://images.weserv.nl/?url=" + lider.getImagen() : request.getContextPath() + "/img/logo.png";
                        %>
                            <img src="<%= imgLider %>" alt="Lider" style="width: 120px; border-radius: 10px; box-shadow: 0 4px 10px rgba(0,0,0,0.1);" referrerpolicy="no-referrer">
                            <p style="margin: 10px 0 5px 0; font-weight: bold;"><%= lider.getNombreCarta() %></p>
                            <span style="font-size: 0.7rem; background: #333; color: white; padding: 2px 8px; border-radius: 10px;"><%= lider.getColor() %></span>
                        <% } else { %>
                            <div style="width: 120px; height: 168px; border: 2px dashed #ccc; border-radius: 10px; margin: 0 auto; display: flex; align-items: center; justify-content: center; background: #f9f9f9; color: #aaa; font-size: 0.8rem; padding: 10px; text-align: center;">
                                Selecciona un Líder
                            </div>
                        <% } %>
                    </div>

                    <div style="display: flex; justify-content: space-between; margin-bottom: 10px;">
                        <h4 style="margin: 0; color: #888; font-size: 0.85rem;">CARTAS</h4>
                        <span style="font-weight: bold; <%= mazoTemporal.size() == 50 ? "color:#2e7d32;" : "color:#e91e63;" %>"><%= mazoTemporal.size() %> / 50</span>
                    </div>
                    
                    <div style="max-height: 300px; overflow-y: auto; margin-bottom: 20px;">
                        <% if(conteoMazo.isEmpty()) { %>
                            <p style="text-align: center; color: #ccc; font-style: italic; font-size: 0.9rem; padding: 20px;">Vacío</p>
                        <% } else { 
                            for(Map.Entry<String, Integer> entry : conteoMazo.entrySet()) {
                                Carta cMazo = mapaCartasMazo.get(entry.getKey());
                        %>
                            <div class="mazo-lista-item">
                                <span class="qty">x<%= entry.getValue() %></span>
                                <span style="flex:1; margin: 0 10px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"><%= cMazo.getNombreCarta() %></span>
                                <form action="${pageContext.request.contextPath}/DeckBuilderServlet" method="POST" style="margin:0;">
                                    <input type="hidden" name="accion" value="quitar">
                                    <input type="hidden" name="idCarta" value="<%= cMazo.getIdCarta() %>">
                                    <button type="submit" class="btn-remove"><i class="fas fa-minus"></i></button>
                                </form>
                            </div>
                        <% } } %>
                    </div>

                    <div style="display: flex; flex-direction: column; gap: 10px;">
                        <% if (lider != null && mazoTemporal.size() == 50) { %>
                            <button type="button" onclick="document.getElementById('modal-guardar-mazo').style.display='flex';" style="width: 100%; padding: 12px; background: #4CAF50; color: white; border: none; border-radius: 8px; cursor: pointer; font-weight: bold;">
                                <i class="fas fa-save"></i> FINALIZAR MAZO
                            </button>
                        <% } else { %>
                            <button type="button" disabled style="width: 100%; padding: 12px; background: #e0e0e0; color: #999; border: none; border-radius: 8px; cursor: not-allowed; font-weight: bold;">
                                <i class="fas fa-lock"></i> Mazo Incompleto
                            </button>
                        <% } %>
                        
                        <form action="${pageContext.request.contextPath}/DeckBuilderServlet" method="POST" style="margin:0;">
                            <input type="hidden" name="accion" value="limpiar">
                            <button type="submit" style="width: 100%; padding: 8px; background: transparent; color: #999; border: 1px solid #ccc; border-radius: 8px; cursor: pointer;">Limpiar</button>
                        </form>
                    </div>
                </aside>

                <main class="db-main">
                    
                    <form action="${pageContext.request.contextPath}/DeckBuilderServlet" method="GET" class="db-filtros">
                        <input type="hidden" name="accion" value="listar">
                        
                        <% if (lider == null) { %>
                            <div class="filtro-grupo">
                                <label>1. Filtrar Líder por Color:</label>
                                <select name="color">
                                    <option value="">Cualquier Color</option>
                                    <option value="ROJO" <%= "ROJO".equals(colorFiltro) ? "selected" : "" %>>🔴 Rojo</option>
                                    <option value="VERDE" <%= "VERDE".equals(colorFiltro) ? "selected" : "" %>>🟢 Verde</option>
                                    <option value="AZUL" <%= "AZUL".equals(colorFiltro) ? "selected" : "" %>>🔵 Azul</option>
                                    <option value="AMARILLO" <%= "AMARILLO".equals(colorFiltro) ? "selected" : "" %>>🟡 Amarillo</option>
                                    <option value="MORADO" <%= "MORADO".equals(colorFiltro) ? "selected" : "" %>>🟣 Morado</option>
                                    <option value="NEGRO" <%= "NEGRO".equals(colorFiltro) ? "selected" : "" %>>⚫ Negro</option>
                                </select>
                            </div>
                        <% } else { %>
                            <div class="filtro-grupo">
                                <label>2. Buscar por Tipo:</label>
                                <select name="tipo">
                                    <option value="">Cualquier Tipo</option>
                                    <option value="PERSONAJE" <%= "PERSONAJE".equals(tipoFiltro) ? "selected" : "" %>>Personaje</option>
                                    <option value="EVENTO" <%= "EVENTO".equals(tipoFiltro) ? "selected" : "" %>>Evento</option>
                                    <option value="ESCENARIO" <%= "ESCENARIO".equals(tipoFiltro) ? "selected" : "" %>>Escenario</option>
                                </select>
                            </div>
                            <div style="flex: 1; align-self: center; font-size: 0.8rem; color: #2e7d32; background: #e8f5e9; padding: 10px; border-radius: 8px;">
                                <i class="fas fa-info-circle"></i> Compatible con <%= lider.getNombreCarta() %>
                            </div>
                        <% } %>

                        <button type="submit" style="padding: 11px 25px; background: #333; color: white; border: none; border-radius: 8px; cursor: pointer;">Buscar</button>
                    </form>

                    <div class="db-grid">
                        <% for(Carta c : catalogo) { 
                            String imgC = (c.getImagen() != null && c.getImagen().startsWith("http")) ? "https://images.weserv.nl/?url=" + c.getImagen() : request.getContextPath() + "/img/logo.png";
                        %>
                            <div class="db-card">
                                <span class="badge-tipo"><%= c.getTipo() %></span>
                                <img src="<%= imgC %>" alt="<%= c.getNombreCarta() %>" loading="lazy" referrerpolicy="no-referrer" onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/logo.png';">
                                <p style="font-size: 0.8rem; font-weight: bold; margin-bottom: 2px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"><%= c.getNombreCarta() %></p>
                                <p style="font-size: 0.65rem; color: #999; margin-bottom: 8px;"><%= c.getIdCarta() %></p>
                                
                                <form action="${pageContext.request.contextPath}/DeckBuilderServlet" method="POST" style="margin:0;">
                                    <input type="hidden" name="accion" value="agregar">
                                    <input type="hidden" name="idCarta" value="<%= c.getIdCarta() %>">
                                    <button type="submit" class="btn-add">
                                        <%= "LIDER".equals(c.getTipo()) ? "Elegir" : "Añadir" %>
                                    </button>
                                </form>
                            </div>
                        <% } %>
                    </div>

                    <% if(totalPaginas > 1) { 
                        String urlP = request.getContextPath() + "/DeckBuilderServlet?accion=listar&color=" + colorFiltro + "&tipo=" + tipoFiltro;
                    %>
                        <div class="db-pagination">
                            <a href="<%= urlP %>&page=<%= paginaActual > 1 ? (paginaActual - 1) : 1 %>" class="<%= paginaActual == 1 ? "disabled" : "" %>">Anterior</a>
                            <span>Página <%= paginaActual %> de <%= totalPaginas %></span>
                            <a href="<%= urlP %>&page=<%= paginaActual < totalPaginas ? (paginaActual + 1) : totalPaginas %>" class="<%= paginaActual >= totalPaginas ? "disabled" : "" %>">Siguiente</a>
                        </div>
                    <% } %>
                </main>
            </div>
        </div>
    </div>

    <div id="modal-guardar-mazo" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.6); z-index:9999; align-items:center; justify-content:center;">
        <div style="background:white; padding:30px; border-radius:15px; width:450px; position:relative; box-shadow: 0 10px 30px rgba(0,0,0,0.3);">
            <span onclick="document.getElementById('modal-guardar-mazo').style.display='none';" style="position:absolute; top:15px; right:20px; font-size:28px; cursor:pointer; color:#ccc;">&times;</span>
            <h2 style="margin-top:0;">Guardar Mazo</h2>
            
            <form action="${pageContext.request.contextPath}/GuardarMazoServlet" method="POST">
                <%-- Info oculta --%>
                <% 
                   StringBuilder sb = new StringBuilder();
                   for(Map.Entry<String, Integer> e : conteoMazo.entrySet()) sb.append(e.getKey()).append(":").append(e.getValue()).append(",");
                   String cartasStr = sb.length() > 0 ? sb.substring(0, sb.length()-1) : "";
                %>
                <input type="hidden" name="id_mazo" value="<%= (session.getAttribute("mazoEditandoId") != null) ? session.getAttribute("mazoEditandoId") : 0 %>">
                <input type="hidden" name="lider_mazo" value="<%= (lider != null) ? lider.getIdCarta() : "" %>">
                <input type="hidden" name="lista_cartas" value="<%= cartasStr %>">

                <div style="margin-bottom: 15px;">
                    <label style="display:block; margin-bottom:5px; font-weight:bold;">Nombre:</label>
                    <input type="text" name="nombre_mazo" value="<%= nombrePrevio %>" required style="width:100%; padding:10px; border-radius:8px; border:1px solid #ddd;">
                </div>

                <div style="margin-bottom: 15px;">
                    <label style="display:block; margin-bottom:5px; font-weight:bold;">Descripción:</label>
                    <textarea name="descripcion_mazo" rows="3" style="width:100%; padding:10px; border-radius:8px; border:1px solid #ddd; resize:none;"><%= descPrevia %></textarea>
                </div>

                <div style="margin-bottom: 20px;">
                    <label style="display:block; margin-bottom:5px; font-weight:bold;">Visibilidad:</label>
                    <select name="visibilidad_mazo" id="visibilidad_selector" onchange="toggleWinrate(this.value)" style="width:100%; padding:10px; border-radius:8px; border:1px solid #ddd;">
                        <option value="Público" <%= "Público".equals(visibilidadPrevia) ? "selected" : "" %>>Público</option>
                        <option value="Privado" <%= "Privado".equals(visibilidadPrevia) ? "selected" : "" %>>Privado</option>
                        
                        <%-- OPCIÓN META EXCLUSIVA --%>
                        <% if (usuarioLogueado.getRol() != null && usuarioLogueado.getRol().equalsIgnoreCase("SUPERADMIN")) { %>
                            <option value="Meta" <%= "Meta".equals(visibilidadPrevia) ? "selected" : "" %> style="color: #d32f2f; font-weight: bold;">🏆 Meta (Oficial)</option>
                        <% } %>
                    </select>
                </div>

                <div id="campo_winrate" style="display: <%= "Meta".equals(visibilidadPrevia) ? "block" : "none" %>; margin-bottom: 20px; background: #fff3e0; padding: 15px; border-radius: 8px; border: 1px solid #ffe0b2;">
                    <label style="display:block; margin-bottom:5px; font-weight:bold; color: #e65100;">Porcentaje de Victoria (Winrate):</label>
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <input type="number" step="0.1" name="winrate_mazo" min="0" max="100" value="<%= winratePrevio %>" style="width:100px; padding:10px; border-radius:8px; border:1px solid #ddd;">
                        <span style="font-weight: bold; color: #e65100;">%</span>
                    </div>
                </div>

                <script>
                    // Función para mostrar/ocultar el winrate
                    function toggleWinrate(val) {
                        const campo = document.getElementById('campo_winrate');
                        campo.style.display = (val === 'Meta') ? 'block' : 'none';
                    }
                </script>

                <button type="submit" style="width:100%; padding:15px; background:#4CAF50; color:white; border:none; border-radius:8px; font-weight:bold; cursor:pointer;">
                    <%= (session.getAttribute("mazoEditandoId") != null) ? "ACTUALIZAR" : "CREAR MAZO" %>
                </button>
            </form>
        </div>
    </div>
</body>
</html>