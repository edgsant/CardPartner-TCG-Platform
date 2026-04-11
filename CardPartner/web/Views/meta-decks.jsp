<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Usuario"%>
<%@page import="modelo.Carta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // SIMULACIÓN (MOCK) - Mazos del META actual
    // ==========================================
    
    List<Mazo> listaMeta = new ArrayList<>();
    
    // Usuario administrador para los mazos oficiales
    Usuario admin = new Usuario(0, "Administrador", "admin@cardpartner.com", "", null);
    
    // Líderes del Meta
    Carta liderSakazuki = new Carta("OP05-041", "Sakazuki", "L", "Azul/Negro", "Líder", 5, 5000, "", "/img/lider-sakazuki.jpg");
    Carta liderKatakuri = new Carta("OP03-099", "Katakuri", "L", "Amarillo", "Líder", 5, 5000, "", "/img/lider-katakuri.jpg");
    Carta liderEnel = new Carta("OP05-098", "Enel", "L", "Amarillo", "Líder", 5, 5000, "", "/img/lider-enel.jpg");

    // Construcción de los mazos meta
    listaMeta.add(new Mazo(101, "Sakazuki Control", "El Tier 0 del formato OP-05. Control absoluto.", "Público", admin, liderSakazuki));
    listaMeta.add(new Mazo(102, "Katakuri Big Mom", "Estrategia Midrange agresiva con manipulación de vidas.", "Público", admin, liderKatakuri));
    listaMeta.add(new Mazo(103, "Enel Sky Island", "Resistencia infinita gracias a su habilidad de curación.", "Público", admin, liderEnel));
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    
    <title>Card Partner - META Decks</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/metaDecksStyle.css?v=4">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-mazos">
                
                <div class="barra-busqueda">
                    <h2 class="titulo-seccion">META DECKS</h2>
                    <div class="acciones-derecha">
                        <div class="filtros">
                            <input type="text" placeholder="Buscar líder o color...">
                            <a href="#modal-filtros" class="btn-filtro" style="text-decoration: none; display: inline-block;">Filtros ⚙️</a>
                        </div>
                        <a href="${pageContext.request.contextPath}/Views/deck-builder.jsp" class="btn-crear">+ Crear Mazo</a>
                    </div>
                </div>

                <h3 class="subtitulo-mazos">Los mejores mazos del formato oficial</h3>
                <section class="galeria-mazos">
                    
                    <%
                        // BUCLE PARA MOSTRAR LOS MAZOS DEL META
                        for(Mazo m : listaMeta) {
                            String img = (m.getLider() != null) ? m.getLider().getImagenUrl() : "/img/logo.png";
                    %>
                        <a href="${pageContext.request.contextPath}/Views/deck-view.jsp?id=<%= m.getId() %>" class="mazo-item">
                            <div class="mazo-imagen">
                                <img src="${pageContext.request.contextPath}<%= img %>" alt="Líder Meta">
                            </div>
                            <div class="mazo-info">
                                <h3><%= m.getNombre() %></h3>
                                <p class="mazo-meta">Colores: <%= m.getLider().getColor() %></p>
                                <p class="mazo-autor">Creador: <%= m.getCreador().getNombreUsuario() %></p>
                                <span class="mazo-tipo"><%= m.getDescripcion() %></span>
                            </div>
                        </a>
                    <%
                        }
                    %>

                </section>

            </main>

        </div>
    </div>

    <div id="modal-filtros" class="modal-overlay">
        <div class="modal-content">
            <a href="#" class="btn-cerrar">&times;</a>
            <h2 class="titulo-seccion" style="font-size: 24px; margin-bottom: 20px; text-align: left;">Filtros de Búsqueda</h2>

            <form action="#" style="text-align: left;">
                <div style="margin-bottom: 15px;">
                    <label style="font-weight: bold; display: block; margin-bottom: 5px;">Color de la carta:</label>
                    <select style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid #ccc; font-family: 'Poppins', sans-serif; font-size: 14px;">
                        <option>Todos los colores</option>
                        <option>Rojo</option>
                        <option>Verde</option>
                        <option>Azul</option>
                        <option>Morado</option>
                        <option>Negro</option>
                        <option>Amarillo</option>
                    </select>
                </div>

                <div style="margin-bottom: 15px;">
                    <label style="font-weight: bold; display: block; margin-bottom: 5px;">Rareza:</label>
                    <div style="display: flex; gap: 10px; flex-wrap: wrap; font-size: 14px;">
                        <label><input type="checkbox"> Common (C)</label>
                        <label><input type="checkbox"> Uncommon (UC)</label>
                        <label><input type="checkbox"> Rare (R)</label>
                        <label><input type="checkbox"> Super Rare (SR)</label>
                        <label><input type="checkbox"> Secret Rare (SEC)</label>
                    </div>
                </div>

                <div style="margin-bottom: 25px;">
                    <label style="font-weight: bold; display: block; margin-bottom: 5px;">Coste de Don!!:</label>
                    <input type="range" min="1" max="10" style="width: 100%;">
                    <div style="display: flex; justify-content: space-between; font-size: 12px; color: #666;">
                        <span>1</span><span>10</span>
                    </div>
                </div>

                <a href="#" class="btn-filtro" style="width: 100%; background: #333; color: white; text-align: center; display: block; text-decoration: none;">APLICAR FILTROS</a>
            </form>
        </div>
    </div>
</body>
</html>