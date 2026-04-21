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

    // Recuperamos los filtros aplicados para que los campos no se vacíen al recargar
    String colorSel = (request.getAttribute("colorSeleccionado") != null) ? (String)request.getAttribute("colorSeleccionado") : "";
    String tipoSel = (request.getAttribute("tipoSeleccionado") != null) ? (String)request.getAttribute("tipoSeleccionado") : "";
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    <title>Card Partner - Cartas</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cardsStyle.css?v=6">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-cartas">
                
                <div class="barra-busqueda">
                    <h2 class="titulo-seccion">CARDS</h2>
                    <div class="filtros">
                        <!-- Buscador rápido por color -->
                        <form action="${pageContext.request.contextPath}/CartasServlet" method="GET" style="display:inline-block;">
                            <input type="hidden" name="accion" value="filtrar">
                            <input type="text" name="color" placeholder="Color..." value="<%= colorSel %>">
                            <button type="submit" class="btn-filtro" style="cursor:pointer; border:none;">Buscar</button>
                        </form>
                        <!-- Este enlace abre el div con id="modal-filtros" gracias al CSS :target -->
                        <a href="#modal-filtros" class="btn-filtro" style="text-decoration: none; display: inline-block;">Filtros ⚙️</a>
                    </div>
                </div>

                <section class="galeria-cartas">
                    <%
                        for(Carta c : listaCartas) {
                            String imgCarta = (c.getImagen() != null) ? c.getImagen() : "/img/logo.png";
                            double precioMercado = Math.round((Math.random() * 60 + 5) * 100.0) / 100.0;
                    %>
                        <a href="${pageContext.request.contextPath}/CartasServlet?accion=detalle&id=<%= c.getIdCarta() %>" class="carta-item">
                            <img src="${pageContext.request.contextPath}<%= imgCarta %>" alt="<%= c.getNombreCarta() %>">
                            <div class="carta-info">
                                <h3><%= c.getNombreCarta() %></h3>
                                <p><%= c.getIdCarta() %> | <%= c.getTipo() %></p>
                                <span class="precio"><%= String.format("%.2f", precioMercado) %> €</span>
                            </div>
                        </a>
                    <%
                        }
                    %>
                </section>
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

                <!-- Botón de envío del formulario -->
                <button type="submit" class="btn-filtro" style="width: 100%; background: #333; color: white; border: none; cursor: pointer; padding: 15px; border-radius: 8px; font-weight: bold;">
                    APLICAR FILTROS
                </button>
            </form>
        </div>
    </div>
</body>
</html>