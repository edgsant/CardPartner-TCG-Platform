<%@page import="java.util.Date"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="modelo.Mazo"%>
<%@page import="modelo.Usuario"%>
<%@page import="modelo.Carta"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    // ==========================================
    // SIMULACIÓN (MOCK) - A borrar cuando haya BD
    // En el futuro: List<Mazo> listaMazos = daoMazo.obtenerMazosPublicos();
    // ==========================================
    
    List<Mazo> listaMazos = new ArrayList<>();
    
    // 1. Creamos un par de usuarios falsos
    Usuario u1 = new Usuario(1, "Sergio Otero", "sergio@uva.es", "1234", new Date());
    Usuario u2 = new Usuario(2, "Álvaro López", "alvaro@uva.es", "1234", new Date());
    
    // 2. Creamos un par de cartas Líder falsas
    Carta liderZoro = new Carta("OP01-001", "Zoro", "L", "Rojo", "Líder", 5, 5000, "", "/img/lider-zoro.jpg");
    Carta liderEnel = new Carta("OP05-098", "Enel", "L", "Amarillo", "Líder", 5, 5000, "", "/img/lider-enel.jpg");
    Carta liderLaw = new Carta("OP01-002", "Trafalgar Law", "L", "Verde/Rojo", "Líder", 5, 5000, "", "/img/lider-sakazuki.jpg"); // Reutilizamos imagen para la demo
    
    // 3. Creamos los mazos uniéndolo todo
    listaMazos.add(new Mazo(1, "Zoro Rush (Aggro)", "Mazo rápido y agresivo", "Público", u1, liderZoro));
    listaMazos.add(new Mazo(2, "Enel Sky Island", "Mazo Midrange curativo", "Público", u2, liderEnel));
    listaMazos.add(new Mazo(3, "Law Supernovas", "Control de mesa", "Público", u1, liderLaw));
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Montserrat:wght@400;700&family=Poppins:wght@400;600&display=swap" rel="stylesheet">
    
    <title>Card Partner - Community Decks</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/communityDeckStyle.css?v=4">
</head>
<body>

    <div class="fondo-rosa">
        <div class="contenedor-principal">
            
            <jsp:include page="/components/cabecera.jsp" />

            <main class="contenido-mazos">
                
                <div class="barra-busqueda">
                    <h2 class="titulo-seccion">COMMUNITY DECKS</h2>
                    <div class="acciones-derecha">
                        <div class="filtros">
                            <input type="text" placeholder="Buscar líder, color o creador...">
                            <a href="#modal-filtros" class="btn-filtro" style="text-decoration: none; display: inline-block;">Filtros ⚙️</a>
                        </div>
                        <a href="${pageContext.request.contextPath}/Views/deck-builder.jsp" class="btn-crear">+ Crear Mazo</a>
                    </div>
                </div>

                <h3 class="subtitulo-mazos">Mazos creados por los jugadores</h3>
                <section class="galeria-mazos">
                    
                    <%
                        // BUCLE MÁGICO: Dibuja una caja por cada mazo en la lista
                        for(Mazo m : listaMazos) {
                            // Extraemos la información de forma segura
                            String imgLider = (m.getLider() != null) ? m.getLider().getImagenUrl() : "/img/logo.png";
                            String colorLider = (m.getLider() != null) ? m.getLider().getColor() : "Variado";
                            String nombreCreador = (m.getCreador() != null) ? m.getCreador().getNombreUsuario() : "Desconocido";
                    %>
                        <a href="${pageContext.request.contextPath}/Views/deck-view.jsp?id=<%= m.getId() %>" class="mazo-item">
                            <div class="mazo-imagen">
                                <img src="${pageContext.request.contextPath}<%= imgLider %>" alt="Líder del mazo">
                            </div>
                            <div class="mazo-info">
                                <h3><%= m.getNombre() %></h3>
                                <p class="mazo-meta">Colores: <%= colorLider %></p>
                                <p class="mazo-autor">Creador: <%= nombreCreador %></p>
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